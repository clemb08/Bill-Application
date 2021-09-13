package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Bill;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BillsController implements Initializable {

    @FXML BillsListDetailController listDetail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setBill(List<Bill> bills, String billId) {
        listDetail.setBill(bills, billId);
    }
}
