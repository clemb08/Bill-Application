package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import app.kenavo.billapplication.services.BillService;
import app.kenavo.billapplication.services.BillServiceImpl;
import app.kenavo.billapplication.services.MissionService;
import app.kenavo.billapplication.services.MissionsServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MissionSelectionController implements Initializable {

    @FXML public TableView<Mission> tableMissions;
    @FXML public TableColumn number;
    @FXML public TableColumn date;
    @FXML public TableColumn price;

    @FXML public Button select;
    @FXML public Button cancel;

    private Bill bill;

    MissionService missionService = new MissionsServiceImpl();
    BillService billService = new BillServiceImpl();
    TableView tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tableMissions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cancel.setOnAction(event -> {
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        });

        select.setOnAction(event -> {
            List<Mission> allMissions = missionService.getAllMissions();
            List<Mission> missions = tableMissions.getSelectionModel().getSelectedItems();
            System.out.println(missions);
            missions.forEach(mission -> {
                System.out.println(mission.getNumber());
                mission.setBillId(this.bill.getId());
            });
            missionService.update(allMissions, missions);
            tableView.getItems().addAll(missions);

            List<Mission> allMissionsUpdated = missionService.getAllMissions();
            List<Bill> bills = billService.getAllBills();
            Bill bill = billService.getBillById(bills, this.bill.getId());
            Bill updatedBill = billService.getAmountBill(bill, allMissionsUpdated);
            billService.update(bills, updatedBill);

            Stage stage = (Stage) select.getScene().getWindow();
            stage.close();
        });
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public void setMissions(List<Mission> missions) {
        ObservableList<Mission> data = FXCollections.observableArrayList(missions);
        number.setCellValueFactory(new PropertyValueFactory<>("Number"));
        date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        price.setCellValueFactory(new PropertyValueFactory<>("Price"));

        tableMissions.setItems(data);
    }

    public void setTable(TableView table) {
        tableView = table;
    }
}
