package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Bill;
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

import static java.lang.Float.parseFloat;

public class BillServiceImpl implements BillService {
    private static final String BILL_FILE = "./BillApplication_data/bills.csv";

    public Bill createBill(CSVRecord record) throws ParseException {
        Bill bill = new Bill();
        bill.setId(record.get("Id"));
        bill.setNumber(record.get("Number"));
        bill.setAccountId(record.get("AccountId"));
        bill.setType(record.get("Type"));
        bill.setDate(record.get("Date"));
        bill.setAmount(parseFloat(record.get("Amount")));
        bill.setCredited(Boolean.parseBoolean(record.get("Credited")));
        bill.setVersionPDF(Integer.parseInt(record.get("VersionPDF")));

        return bill;
    }

    public void writeBill(CSVPrinter csvPrinter, Bill bill) throws IOException {
        csvPrinter.printRecord(bill.getId(), bill.getNumber(), bill.getAccountId(), bill.getType(), bill.getDate().toString(), bill.getAmount(),bill.isCredited(), bill.getVersionPDF());
    }

    @Override
    public Bill getAmountBill(Bill bill, List<Mission> missions) {

        String billId = bill.getId();
        //Has to be in an array to be used in a forEach stream
        final float[] billAmount = {0};

        List<Mission> missionsToAccount = missions.stream()
                .filter(mission -> mission.getBillId().equals(billId))
                .collect(Collectors.toList());

        missionsToAccount.forEach(mission -> billAmount[0] += mission.getPrice());

        bill.setAmount(billAmount[0]);

        return bill;
    }

    @Override
    public List<Bill> getAllBills() {

        List<Bill> bills = new ArrayList<Bill>();

        try(Reader reader = Files.newBufferedReader(Paths.get(BILL_FILE));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {

            for(CSVRecord record : csvParser) {
                Bill bill = createBill(record);
                bills.add(bill);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return bills;
    }

    @Override
    public List<Bill> getAllBillsByAccount(List<Bill> bills, String accountId) {

        List<Bill> billsToReturn = new ArrayList<Bill>();

        billsToReturn = bills.stream()
                .filter(bill -> bill.getAccountId().equals(accountId))
                .collect(Collectors.toList());

        return billsToReturn;
    }

    @Override
    public Bill getBillById(List<Bill> bills, String id) {

        Bill billToReturn = bills.stream()
                .filter(bill -> bill.getId().equals(id))
                .collect(Collectors.toList()).get(0);

        return billToReturn;
    }

    @Override
    public Bill create(Bill bill) {

        bill.setId(UUID.randomUUID().toString());
        bill.setVersionPDF(0);

        try(
                BufferedWriter writer = Files.newBufferedWriter(
                        Paths.get(BILL_FILE),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        ) {
            writeBill(csvPrinter, bill);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bill;
    }

    @Override
    public void update(List<Bill> bills, Bill bill) {

        String updatedId = bill.getId();

        List<Bill> billsToKeep = bills.stream()
                .filter(bi -> !bi.getId().equals(updatedId))
                .collect(Collectors.toList());

        billsToKeep.add(bill);

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(BILL_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "Number", "AccountID", "Type", "Date", "Amount", "Credited", "VersionPDF"));) {

            for(Bill billToSave : billsToKeep) {
                writeBill(csvPrinter, billToSave);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(List<Bill> bills, Bill bill) {

        String updatedId = bill.getId();

        List<Bill> billsToKeep = bills.stream()
                .filter(bi -> bi.getId().equals(updatedId))
                .collect(Collectors.toList());

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(BILL_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "Number", "AccountID", "Type", "Date", "Amount", "Credited", "VersionPDF"));) {

            for(Bill billToSave : billsToKeep) {
                writeBill(csvPrinter, billToSave);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
