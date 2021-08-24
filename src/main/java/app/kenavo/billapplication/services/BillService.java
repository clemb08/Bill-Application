package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;

import java.util.List;

public interface BillService {

    Bill getAmountBill(Bill bill, List<Mission> missions);

    public List<Bill> getAllBills();

    public List<Bill> getAllBillsByAccount(List<Bill> bills, String accountId);

    public Bill getBillById(List<Bill> bills, String id);

    public Bill create(Bill bill);

    public void update(List<Bill> bills, Bill bill);

    public void delete(List<Bill> bills, Bill bill);
}
