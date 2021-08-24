package app.kenavo.billapplication.model;

import java.time.LocalDate;

public class Bill {

    @Override public String toString() { return getNumber(); }

    public String id;

    public String number;

    public String accountId;

    //monthly, hebdo...
    public String type;

    public LocalDate date;

    public boolean isCredited;

    public float amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String date) {
        LocalDate dateParsed = LocalDate.parse(date);
        this.date = dateParsed;
    }

    public boolean isCredited() {
        return isCredited;
    }

    public void setCredited(boolean credited) {
        isCredited = credited;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNewNumber(int sizeList) {
        if(sizeList < 10) {
            this.number = "Bill-000" + sizeList;
        } else if(sizeList < 100) {
            this.number = "Bill-00" + sizeList;
        } else if(sizeList < 1000) {
            this.number = "Bill-0" + sizeList;
        } else {
            this.number = "Bill-" + sizeList;
        }

    }
}
