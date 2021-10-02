package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Setting;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SettingServiceImpl implements SettingService {

    private static final String SETTING_FILE = "./BillApplication_data/settings.csv";

    private Setting createSetting(CSVRecord record) {
        Setting setting = new Setting();
        setting.setId(record.get("Id"));
        setting.setCompanyName(record.get("CompanyName"));
        setting.setAddress(record.get("Address"));
        setting.setEmail(record.get("Email"));
        setting.setPhone(record.get("Phone"));
        setting.setLogo(record.get("Logo"));
        setting.setSiret(record.get("Siret"));
        setting.setDownloadPath(record.get("DownloadPath"));

        return setting;
    }

    private void writeSetting(CSVPrinter csvPrinter, Setting setting) throws IOException {
        csvPrinter.printRecord(setting.getId(),
                setting.getCompanyName(),
                setting.getAddress(),
                setting.getEmail(),
                setting.getPhone(),
                setting.getLogo(),
                setting.getSiret(),
                setting.getDownloadPath());
    }


    @Override
    public Setting getSetting() {

        List<Setting> settings = new ArrayList<Setting>();
        Setting setting = new Setting();

        try(Reader reader = Files.newBufferedReader(Paths.get(SETTING_FILE));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {

            for(CSVRecord record : csvParser) {
                Setting mission = createSetting(record);
                settings.add(mission);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(settings.size() > 0) {
            return settings.get(0);
        } else {
            return null;
        }


    }

    @Override
    public void setSetting(Setting setting) {

        setting.setId(UUID.randomUUID().toString());

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(SETTING_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "CompanyName", "Address", "Email", "Phone", "Logo", "Siret", "DownloadPath"));) {

                writeSetting(csvPrinter, setting);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
