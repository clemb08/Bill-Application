package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import app.kenavo.billapplication.services.*;
import app.kenavo.billapplication.utils.Navigation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.String.valueOf;

public class MissionsController implements Initializable {

    @FXML public ListView<Mission> listViewMissions;
    @FXML public Text missionId;
    @FXML public TextField missionType;
    @FXML public TextField missionAccount;
    @FXML public TextField missionBill;
    @FXML public TextField missionPrice;
    @FXML public CheckBox missionBilled;
    @FXML public TextField missionDate;

    @FXML public ChoiceBox<Bill> picklistBills;
    @FXML public ChoiceBox<Account> picklistAccounts;
    @FXML public DatePicker datePicker;

    @FXML public Button createMission;
    @FXML public Button deleteMission;
    @FXML public Button editMission;
    @FXML public Button missionSave;
    @FXML public Button missionCancel;

    Navigation navigation = new Navigation();
    AccountService accountService = new AccountServiceImpl();
    BillService billService = new BillServiceImpl();
    MissionService missionService = new MissionsServiceImpl();

    List<Account> accounts = accountService.getAllAccounts();
    List<Bill> bills = billService.getAllBills();
    List<Mission> missions = missionService.getAllMissions();

    Mission cachedMission = null;
    Mission inProcessMission = null;
    String context = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Mission> observableMissions = FXCollections.observableArrayList();
        observableMissions.addAll(missions);

        listViewMissions.getItems().addAll(observableMissions);

        createMission.setOnAction((event -> {
            this.context = "create";
            this.cachedMission = listViewMissions.getSelectionModel().getSelectedItem();
            System.out.println(this.cachedMission);
            displayCreateNewMission(accounts, bills);
        }));
        editMission.setOnAction(event -> {
            this.context = "edit";
            this.cachedMission = listViewMissions.getSelectionModel().getSelectedItem();
            displayEditableScreen("Edit Bill", accounts, bills);
        });

        missionSave.setOnAction(event -> {
            try {
                if(this.context.equals("create")) {
                    onSave(missions, this.inProcessMission);
                } else if(this.context.equals("edit")) {
                    onSave(missions, this.cachedMission);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        missionCancel.setOnAction(event -> onCancel());
        deleteMission.setOnAction(event -> onDelete(missions));

        missionAccount.setOnMouseClicked(event -> {
            try {
                if(listViewMissions.getSelectionModel().getSelectedItem().getAccountId() != null) {
                    navigation.navigateToAccounts(event, accounts, listViewMissions.getSelectionModel().getSelectedItem().getAccountId(), myMenuBar);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        missionBill.setOnMouseClicked(event -> {
            try {
                if(listViewMissions.getSelectionModel().getSelectedItem().getBillId() != "None") {
                    navigation.navigateToBills(event, bills, listViewMissions.getSelectionModel().getSelectedItem().getBillId(), myMenuBar);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        listViewMissions.setCellFactory(new Callback<ListView<Mission>, ListCell<Mission>>() {
            @Override
            public ListCell<Mission> call(ListView<Mission> param) {
                final Label label = new Label();
                final Tooltip tooltip = new Tooltip();
                final ListCell<Mission> cell = new ListCell<Mission>() {
                    @Override
                    public void updateItem(Mission mission, boolean empty) {
                        super.updateItem(mission, empty);
                        if(mission != null) {
                            label.setText(mission.getNumber());
                            setText(mission.getNumber());
                            tooltip.setText(mission.getNumber());
                            setTooltip(tooltip);
                        }
                    }
                };
                return cell;
            }
        });

        //Add Event Listener to ListView Account
        listViewMissions.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Mission>() {
                    @Override
                    public void changed(ObservableValue<? extends Mission> observable, Mission oldValue, Mission newValue) {
                        System.out.println(newValue);
                        System.out.println(newValue.getId());
                        if(newValue.getId() != null) {
                            displayMissionSelected(newValue, accounts, bills);
                        } else {
                            resetMissionSelected();
                        }
                    }
                });
    }

    public Mission displayMissionSelected(Mission mission, List<Account> accounts, List<Bill> bills) {
        missionId.setText(mission.getNumber());
        Account account = accountService.getAccountById(accounts, mission.getAccountId());
        missionAccount.setText(account.getName());
        if(mission.getBillId().equals("None")) {
            missionBill.setText(null);
        } else {
            Bill bill = billService.getBillById(bills, mission.getBillId());
            missionBill.setText(bill.getNumber());
        }
        missionPrice.setText(valueOf(mission.getPrice()));
        if(mission.isBilled()) {
            missionBilled.isSelected();
        }
        missionType.setText(mission.getType());
        missionDate.setText(String.valueOf(mission.getDate()));
        return mission;
    }

    public void resetMissionSelected() {
        missionId.setText(null);
        missionAccount.setText(null);
        missionBill.setText(null);
        missionPrice.setText(null);
        missionType.setText(null);
        missionDate.setText(null);
        datePicker.setValue(null);
    }

    public void onCancel() {
        if(this.context.equals("create")) {
            listViewMissions.getItems().removeAll(this.inProcessMission);
        }

        displayReadOnlyScreen(this.cachedMission);
        this.context = "";
    }

    public void onSave(List<Mission> missions, Mission mission) throws IOException, ParseException {
        if(this.context == "create") {
            mission.setNewNumber(missions.size() + 1);
        } else {
            mission.setNumber(mission.getNumber());
        }

        mission.setAccountId(picklistAccounts.getSelectionModel().getSelectedItem().getId());
        if(picklistBills.getSelectionModel().getSelectedItem() != null) {
            mission.setBillId(picklistBills.getSelectionModel().getSelectedItem().getId());
            Bill bill = billService.getBillById(bills, mission.getBillId());
            Bill updatedBill = billService.getAmountBill(bill, missions);
            billService.update(bills, updatedBill);
        } else {
            mission.setBillId("None");
        }
        mission.setType(missionType.getText());
        mission.setPrice(Float.parseFloat(missionPrice.getText()));
        mission.setDate(datePicker.getValue().toString());
        mission.setBilled(missionBilled.isSelected());

        Account account = accountService.getAccountById(accounts, mission.getAccountId());

        if(this.context.equals("create")) {
            missionService.create(mission);
        } else if(this.context.equals("edit")) {
            missionService.update(missions, mission);
        }

        Account updatedAccount = accountService.getCAAccount(account, missions);
        accountService.update(accounts, updatedAccount);

        displayReadOnlyScreen(mission);
        this.context = "";
    }

    public void onDelete(List<Mission> missions) {
        Mission missionToDelete = listViewMissions.getSelectionModel().getSelectedItem();
        listViewMissions.getSelectionModel().select(listViewMissions.getSelectionModel().getSelectedIndex() + 1);
        missionService.delete(missions, missionToDelete);
    }

    public void displayCreateNewMission(List<Account> accounts, List<Bill> bills) {
        Mission newMission = new Mission();
        this.inProcessMission = newMission;
        listViewMissions.getItems().add(newMission);
        listViewMissions.getSelectionModel().select(newMission);
        displayEditableScreen("Create New Bill", accounts, bills);
    }

    private void displayEditableScreen(String title, List<Account> accounts, List<Bill> bills) {
        missionType.setEditable(true);
        missionAccount.setVisible(false);
        picklistAccounts.setVisible(true);
        ObservableList<Account> observableAccounts = FXCollections.observableArrayList();
        observableAccounts.addAll(accounts);
        picklistAccounts.setItems(observableAccounts);

        missionBill.setVisible(false);
        picklistBills.setVisible(true);
        ObservableList<Bill> observableBills = FXCollections.observableArrayList();
        observableBills.addAll(bills);
        picklistBills.setItems(observableBills);

        if(this.context == "edit") {
            System.out.println(missionBill.getText());
            if(missionBill.getText() == null) {
                picklistBills.setValue(null);
            } else {
                Bill bill = billService.getBillById(bills, this.cachedMission.getBillId());
                picklistBills.setValue(bill);
            }
            Account account = accountService.getAccountById(accounts, this.cachedMission.getAccountId());
            picklistAccounts.setValue(account);
            datePicker.setValue(LocalDate.parse(missionDate.getText()));
        }

        missionPrice.setEditable(true);

        missionDate.setVisible(false);
        datePicker.setVisible(true);

        editMission.setVisible(false);
        missionSave.setVisible(true);
        missionCancel.setVisible(true);

        missionId.setText(title);
    }

    private void displayReadOnlyScreen(Mission mission) {
        missionType.setEditable(false);
        missionAccount.setVisible(true);
        picklistAccounts.setVisible(false);
        picklistAccounts.getItems().removeAll(picklistAccounts.getItems());
        missionBill.setVisible(true);
        picklistBills.setVisible(false);
        picklistBills.getItems().removeAll(picklistBills.getItems());
        missionPrice.setEditable(false);
        missionDate.setVisible(true);
        datePicker.setVisible(false);

        listViewMissions.setVisible(true);
        editMission.setVisible(true);
        missionSave.setVisible(false);
        missionCancel.setVisible(false);

        missionId.setText(mission.getNumber());

        listViewMissions.getSelectionModel().select(mission);
    }

    public void setMission(List<Mission> missions, String missionId) {
        Mission mission = missionService.getMissionById(missions, missionId);
        listViewMissions.getSelectionModel().select(mission);
    }

    //--------------------------------NAVIGATE FUNCTIONS----------------------
    @FXML
    public MenuBar myMenuBar;

    @FXML
    public void navigateToAccounts(ActionEvent event) throws IOException {
        navigation.navigateToAccounts(event, null, null, myMenuBar);
    }

    @FXML
    public void navigateToBills(ActionEvent event) throws IOException {
        navigation.navigateToBills(event, null, null, myMenuBar);
    }

    @FXML
    public void navigateToMissions(ActionEvent event) throws IOException {
        navigation.navigateToMissions(event, myMenuBar);
    }
}
