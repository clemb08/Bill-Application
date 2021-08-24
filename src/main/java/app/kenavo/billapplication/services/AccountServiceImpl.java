package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Account;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {
    private static final String ACCOUNT_FILE = "./accounts.csv";

    private Account createAccount(CSVRecord record) {
        Account account = new Account();
        account.setId(record.get("Id"));
        account.setName(record.get("Name"));
        account.setAddress(record.get("Address"));
        account.setContact(record.get("Contact"));
        account.setEmail(record.get("Email"));
        account.setPhone(record.get("Phone"));
        account.setCa(record.get("CA"));

        return account;
    }

    private void writeAccount(CSVPrinter csvPrinter, Account account) throws IOException {
        csvPrinter.printRecord(account.getId(), account.getName(), account.getAddress(), account.getContact(), account.getEmail(), account.getPhone(), account.getCa());
    }

    public Account getCAAccount(Account account, List<Mission> missions) {

        String accountId = account.getId();
        //Has to be in an array to be used in a forEach stream
        final float[] accountCA = {0};

        List<Mission> missionsToAccount = missions.stream()
                .filter(mission -> mission.getAccountId().equals(accountId))
                .collect(Collectors.toList());

        missionsToAccount.forEach(mission -> accountCA[0] += mission.getPrice());

        account.setCa(String.valueOf(accountCA[0]));

        return account;
    }

    @Override
    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<Account>();

        try(Reader reader = Files.newBufferedReader(Paths.get(ACCOUNT_FILE));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {

            for(CSVRecord record : csvParser) {
                Account account = createAccount(record);
                accounts.add(account);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public Account getAccountById(List<Account> accounts, String id) {

        Account account = accounts.stream()
                            .filter(acc -> acc.id.equals(id))
                            .collect(Collectors.toList()).get(0);

        return account;
    }

    @Override
    public Account create(Account account) throws IOException {

        account.setId(UUID.randomUUID().toString());

        try(
                BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(ACCOUNT_FILE),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            ) {
                writeAccount(csvPrinter, account);
            }

        return account;
    }

    @Override
    public void update(List<Account> accounts, Account account) {

        String updatedId = account.getId();

        List<Account> accountsToKeep = accounts.stream()
                .filter(acc -> acc.getId() != updatedId)
                .collect(Collectors.toList());

        accountsToKeep.add(account);

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(ACCOUNT_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("ID", "Name", "Address", "Contact", "Email", "Phone", "CA"));) {

            for(Account accountToSave : accountsToKeep) {
                writeAccount(csvPrinter, accountToSave);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(List<Account> accounts, Account account) {

        String updatedId = account.getId();

        List<Account> accountsToKeep = accounts.stream()
                .filter(acc -> acc.getId() != updatedId)
                .collect(Collectors.toList());

        try(
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(ACCOUNT_FILE));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "Name", "Address", "Contact", "Email", "Phone", "CA"));) {

            for(Account accountToSave : accountsToKeep) {
                writeAccount(csvPrinter, accountToSave);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
