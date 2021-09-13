package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Account;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {

    @FXML AccountsListDetailController listDetail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setAccount(List<Account> accounts, String accountId) {
        listDetail.setAccount(accounts, accountId);
    }
}
