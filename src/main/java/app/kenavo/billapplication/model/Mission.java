package app.kenavo.billapplication.model;

import java.time.LocalDate;

public class Mission {

    public String id;

    public String number;

    public String type;

    public String accountId;

    public String billId;

    public float price;

    public LocalDate date;

    public boolean isBilled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isBilled() {
        return isBilled;
    }

    public void setBilled(boolean billed) {
        isBilled = billed;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String date) {
        LocalDate dateParsed = LocalDate.parse(date);
        this.date = dateParsed;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNewNumber(int sizeList) {
        if (sizeList < 10) {
            this.number = "Mission-000" + sizeList;
        } else if (sizeList < 100) {
            this.number = "Mission-00" + sizeList;
        } else if (sizeList < 1000) {
            this.number = "Mission-0" + sizeList;
        } else {
            this.number = "Mission-" + sizeList;
        }
    }

}
