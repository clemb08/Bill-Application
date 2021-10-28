package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.*;
import app.kenavo.billapplication.utils.Navigation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

import static app.kenavo.billapplication.utils.AlertNotifications.alertOnErrorSave;
import static app.kenavo.billapplication.utils.ValidationFields.*;
import static java.lang.String.valueOf;

public class MissionsListDetailController extends AnchorPane implements Initializable {


    @FXML
    public AnchorPane missionRoot;

    @FXML public ListView<Mission> listViewMissions;
    @FXML public Text missionId;
    @FXML public TextField missionType;
    @FXML public Text missionTypeError;
    @FXML public TextField missionAccount;
    @FXML public Text missionAccountError;
    @FXML public TextField missionBill;
    @FXML public TextField missionPrice;
    @FXML public Text missionPriceError;
    @FXML public CheckBox missionBilled;
    @FXML public TextField missionDate;
    @FXML public TextField missionDescription;
    @FXML public Text missionDescriptionError;
    @FXML public TextField missionQuantity;
    @FXML public Text missionQuantityError;

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

    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();
    Map<Object, String> errors = new HashMap<Object, String>();
    List<Text> errorFields = new ArrayList<Text>();

    Mission cachedMission = null;
    Mission inProcessMission = null;
    String context = "";

    public MissionsListDetailController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/missions_List_Detail.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Complete a list of error fields to be able to hide them on cancel
        errorFields.add(missionAccountError);
        errorFields.add(missionDescriptionError);
        errorFields.add(missionPriceError);
        errorFields.add(missionTypeError);
        errorFields.add(missionQuantityError);

        //Validation Form Edit or Create
        missionType.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequiredText(errors, missionTypeError, missionType);
            }
        });

        missionDescription.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequiredText(errors, missionDescriptionError, missionDescription);
            }
        });

        missionPrice.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                Boolean notBlank = checkRequiredText(errors, missionPriceError, missionPrice);
                if(notBlank) {
                    checkNumber(errors, missionPriceError, missionPrice);
                    if(!errors.containsKey(missionPrice)) {
                        checkRangeNumber(errors, missionPriceError, missionPrice, 1, null);
                    }
                }
            }
        });

        missionQuantity.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequiredText(errors, missionQuantityError, missionQuantity);
            }
        });


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
                    navigation.navigateToAccount(event, listViewMissions.getSelectionModel().getSelectedItem().getAccountId(), accounts);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        missionBill.setOnMouseClicked(event -> {
            try {
                if(listViewMissions.getSelectionModel().getSelectedItem().getBillId() != "None") {
                    navigation.navigateToBill(event, listViewMissions.getSelectionModel().getSelectedItem().getBillId(), bills);
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
        missionDescription.setText(mission.getDescription());
        missionQuantity.setText(String.valueOf(mission.getQuantity()));
        missionPrice.setText(valueOf(mission.getPrice()));
        if(mission.isBilled()) {
            missionBilled.isSelected();
        }
        missionType.setText(mission.getType());
        missionDate.setText(String.valueOf(mission.getDate()));
        editMission.setVisible(true);
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
        errors = new HashMap<Object, String>();
        errorFields.forEach(field -> field.setVisible(false));
    }

    public void onSave(List<Mission> missions, Mission mission) throws IOException, ParseException {
        Map<TextField, Text> fields = new HashMap<TextField, Text>();
        //fields.put(missionAccount, missionAccountError);
        checkRequiredPicklist(errors, missionAccountError, picklistAccounts);
        fields.put(missionPrice, missionPriceError);
        fields.put(missionQuantity, missionQuantityError);
        fields.put(missionType, missionTypeError);
        fields.put(missionDescription, missionDescriptionError);
        checkRequiredFields(errors, fields);

        if(errors.size() == 0) {
            missions = missionService.getAllMissions();
            if(this.context == "create") {
                final int[] highestNumber = {0};
                missions.forEach(currentMission -> {
                    String[] numberString = currentMission.getNumber().split("-");
                    int number = Integer.parseInt(numberString[1]);
                    if(number > highestNumber[0]) {
                        highestNumber[0] = number;
                    }
                });
                mission.setNewNumber(highestNumber[0] + 1);
            } else {
                mission.setNumber(mission.getNumber());
            }

            mission.setAccountId(picklistAccounts.getSelectionModel().getSelectedItem().getId());
            if(picklistBills.getSelectionModel().getSelectedItem() != null) {
                mission.setBillId(picklistBills.getSelectionModel().getSelectedItem().getId());
            } else {
                mission.setBillId("None");
            }
            mission.setType(missionType.getText());
            mission.setDescription(missionDescription.getText());
            mission.setQuantity(Integer.parseInt(missionQuantity.getText()));
            mission.setPrice(Float.parseFloat(missionPrice.getText()));
            mission.setDate(datePicker.getValue().toString());
            mission.setBilled(missionBilled.isSelected());

            Account account = accountService.getAccountById(accounts, mission.getAccountId());
            List<Mission> missionsToUpdate = new ArrayList<>();
            missionsToUpdate.add(mission);
            if(this.context.equals("create")) {
                missionService.create(mission);
            } else if(this.context.equals("edit")) {
                missionService.update(missions, missionsToUpdate);
            }

            Account updatedAccount = accountService.getCAAccount(account, missions);
            accountService.update(accounts, updatedAccount);

            displayReadOnlyScreen(mission);
            this.context = "";
        } else {
            alertOnErrorSave("Mission", errors);
        }

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
        missionDescription.setEditable(true);
        missionQuantity.setEditable(true);

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
        missionDescription.setEditable(false);
        missionQuantity.setEditable(false);
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

}
