package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Mission;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MissionsServiceImpl implements MissionService {
    private static final String MISSION_FILE = "./BillApplication_data/missions.csv";

    public Mission createMission(CSVRecord record) throws ParseException {
        Mission mission = new Mission();
        mission.setId(record.get("Id"));
        mission.setNumber(record.get("Number"));
        mission.setAccountId(record.get("AccountId"));
        mission.setType(record.get("Type"));
        mission.setDescription(record.get("Description"));
        mission.setQuantity(Integer.parseInt(record.get("Quantity")));
        mission.setBillId(record.get("BillId"));
        mission.setPrice(Float.parseFloat(record.get("Price")));
        mission.setDate(record.get("Date"));
        mission.setBilled(Boolean.parseBoolean(record.get("Billed")));

        return mission;
    }

    public void writeMission(CSVPrinter csvPrinter, Mission mission) throws IOException {
        System.out.println("BILL ID " + mission.getBillId());
        csvPrinter.printRecord(mission.getId(), mission.getNumber(), mission.getType(), mission.getDescription(), mission.getQuantity(), mission.getAccountId(), mission.getBillId(), mission.getPrice(), mission.getDate(), mission.isBilled());
    }

    @Override
    public List<Mission> getAllMissions() {

        List<Mission> missions = new ArrayList<Mission>();

        try(Reader reader = Files.newBufferedReader(Paths.get(MISSION_FILE));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {

            for(CSVRecord record : csvParser) {
                Mission mission = createMission(record);
                missions.add(mission);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return missions;
    }

    @Override
    public List<Mission> getAllMissionsByAccount(List<Mission> missions, String accountId) {
        List<Mission> missionsToReturn = new ArrayList<Mission>();

        missionsToReturn = missions.stream()
                .filter(mission -> mission.getAccountId().equals(accountId))
                .collect(Collectors.toList());

        return missionsToReturn;
    }

    @Override
    public List<Mission> getAllMissionsByBill(List<Mission> missions, String billId) {
        List<Mission> missionsToReturn = new ArrayList<Mission>();

        missionsToReturn = missions.stream()
                .filter(mission -> mission.getBillId().equals(billId))
                .collect(Collectors.toList());

        return missionsToReturn;
    }

    @Override
    public Mission getMissionById(List<Mission> missions, String id) {
        Mission missionToReturn = missions.stream()
                .filter(mission -> mission.getId().equals(id))
                .collect(Collectors.toList()).get(0);

        return missionToReturn;
    }

    @Override
    public Mission create(Mission mission) {

        mission.setId(UUID.randomUUID().toString());

        try(
                BufferedWriter writer = Files.newBufferedWriter(
                        Paths.get(MISSION_FILE),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        ) {
            writeMission(csvPrinter, mission);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mission;
    }

    @Override
    public void update(List<Mission> missions, List<Mission> missionsToUpdate) {

        List<String> updatedIds = new ArrayList<>();

        missionsToUpdate.forEach(mission -> updatedIds.add(mission.getId()));

        List<Mission> missionsToKeep = missions.stream()
                .filter(miss -> !updatedIds.contains(miss.getId()))
                .collect(Collectors.toList());

        missionsToUpdate.forEach(mission -> {
            if(mission.getBillId().equals("None")) {
                mission.setBilled(false);
            } else {
                mission.setBilled(true);
            }
            missionsToKeep.add(mission);
        });

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(MISSION_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "Number", "Type", "Description", "Quantity", "AccountId", "BillId", "Price", "Date", "Billed"));) {

            for(Mission missionToSave : missionsToKeep) {
                writeMission(csvPrinter, missionToSave);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(List<Mission> missions, Mission mission) {

        String updatedId = mission.getId();

        List<Mission> missionsToKeep = missions.stream()
                .filter(miss -> !miss.getId().equals(updatedId))
                .collect(Collectors.toList());

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(MISSION_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "Number", "Type", "Description", "Quantity", "AccountId", "BillId", "Price", "Date", "Billed"));) {

            for(Mission missionToSave : missionsToKeep) {
                writeMission(csvPrinter, missionToSave);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
