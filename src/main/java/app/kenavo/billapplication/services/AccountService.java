package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;

import java.io.IOException;
import java.util.List;

public interface AccountService {

    public List<Account> getAllAccounts();

    public Account getAccountById(List<Account> accounts, String id);

    public Account create(Account account) throws IOException;

    public void update(List<Account> accounts, Account account);

    public void delete(List<Account> accounts, Account account);

    public Account getCAAccount(Account account, List<Mission> missions);
}
