package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.*;
import app.kenavo.billapplication.utils.AddColumn;
import app.kenavo.billapplication.utils.Navigation;
import app.kenavo.billapplication.utils.PDFCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

public class BillsController implements Initializable {

    @FXML public ListView<Bill> listViewBills;
    @FXML public TableView<Mission> tableMissions;
    @FXML public TableColumn columnNumber;
    @FXML public TableColumn columnDate;
    @FXML public TableColumn columnPrice;
    @FXML public TableColumn columnDelete;
    @FXML public Text billId;
    @FXML public GridPane gridPane;
    @FXML public TextField billAccount;
    @FXML public TextField billType;
    @FXML public TextField billAmount;
    @FXML public CheckBox billCredited;
    @FXML public TextField billDate;

    @FXML public Label missionsLabel;
    @FXML public ChoiceBox<Account> picklistAccounts;
    @FXML public DatePicker datePicker;

    @FXML public Button createBill;
    @FXML public Button deleteBill;
    @FXML public Button editBill;
    @FXML public Button billSave;
    @FXML public Button billCancel;
    @FXML public Button addMission;
    @FXML public Button generatePDF;

    Navigation navigation = new Navigation();
    AccountService accountService = new AccountServiceImpl();
    BillService billService = new BillServiceImpl();
    MissionService missionService = new MissionsServiceImpl();

    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();


    PDFCreator pdfCreator = new PDFCreator();

    Bill cachedBill = null;
    Bill inProcessBill = null;
    String context = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Account> accounts = accountService.getAllAccounts();
        List<Bill> bills = billService.getAllBills();
        List<Mission> missions = missionService.getAllMissions();

        ObservableList<Bill> observableBills = FXCollections.observableArrayList();
        observableBills.addAll(bills);

        listViewBills.getItems().addAll(observableBills);

        createBill.setOnAction((event -> {
            this.context = "create";
            this.cachedBill = listViewBills.getSelectionModel().getSelectedItem();
            System.out.println(this.cachedBill);
            displayCreateNewBill(accounts);
        }));
        editBill.setOnAction(event -> {
            this.context = "edit";
            this.cachedBill = listViewBills.getSelectionModel().getSelectedItem();
            displayEditableScreen("Edit Bill", accounts);
        });

        billSave.setOnAction(event -> {
            try {
                if(this.context.equals("create")) {
                    onSave(bills, this.inProcessBill);
                } else if(this.context.equals("edit")) {
                    onSave(bills, this.cachedBill);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        billCancel.setOnAction(event -> onCancel(missions));
        deleteBill.setOnAction(event -> onDelete(bills));
        addMission.setOnAction(event -> {
            try {
                onAddMission(listViewBills.getSelectionModel().getSelectedItem(), missions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        generatePDF.setOnAction(event -> {
            Bill bill = listViewBills.getSelectionModel().getSelectedItem();
            List<Mission> missionsToBill = missionService.getAllMissionsByBill(missions, bill.getId());
            try {
                pdfCreator.generatePDF(bill, missionsToBill);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        listViewBills.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Bill> call(ListView<Bill> param) {
                final Label label = new Label();
                final Tooltip tooltip = new Tooltip();
                final ListCell<Bill> cell = new ListCell<Bill>() {
                    @Override
                    public void updateItem(Bill bill, boolean empty) {
                        super.updateItem(bill, empty);
                        if (bill != null) {
                            label.setText(bill.getNumber());
                            setText(bill.getNumber());
                            tooltip.setText(bill.getNumber());
                            setTooltip(tooltip);
                        }
                    }
                };
                return cell;
            }
        });

        //Add Event Listener to ListView Account
        listViewBills.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Bill>() {
                    @Override
                    public void changed(ObservableValue<? extends Bill> observable, Bill oldValue, Bill newValue) {
                        System.out.println(newValue);
                        if(newValue.getId() != null) {
                            displayBillSelected(newValue, accounts);
                            //Create and Format TableView of related Missions
                            List<Mission> missionsToDisplay = missionService.getAllMissionsByBill(missions, newValue.getId());
                            ObservableList<Mission> data = FXCollections.observableArrayList(missionsToDisplay);
                            columnNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
                            columnDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
                            columnPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));

                            columnDelete.setCellValueFactory(new PropertyValueFactory<>(""));
                            Callback<TableColumn<Mission, Void>, TableCell<Mission, Void>> cellDelete
                                    = AddColumn.addDeleteMissionBill(tableMissions, missions);
                            columnDelete.setCellFactory(cellDelete);

                            tableMissions.setItems(data);

                            addMission.setVisible(true);
                            generatePDF.setVisible(true);
                        } else {
                            resetBillSelected();
                            addMission.setVisible(false);
                        }
                    }
                });


    }

    public Bill displayBillSelected(Bill bill, List<Account> accounts) {
        billId.setText(bill.getNumber());
        Account account = accountService.getAccountById(accounts, bill.getAccountId());
        billAccount.setText(account.getName());
        billAmount.setText(valueOf(bill.getAmount()));
        if(bill.isCredited()) {
            billCredited.isSelected();
        }
        billType.setText(bill.getType());
        billDate.setText(String.valueOf(bill.getDate()));
        return bill;
    }

    public void resetBillSelected() {
        billId.setText(null);
        billAccount.setText(null);
        billAmount.setText(null);
        billType.setText(null);
        billDate.setText(null);
        datePicker.setValue(null);
    }

    public void displayCreateNewBill(List<Account> accounts) {
        Bill newBill = new Bill();
        this.inProcessBill = newBill;
        listViewBills.getItems().add(newBill);
        listViewBills.getSelectionModel().select(newBill);
        displayEditableScreen("Create New Bill", accounts);
    }

    public void onCancel(List<Mission> missions) {
        if(this.context.equals("create")) {
            listViewBills.getItems().removeAll(this.inProcessBill);
        }

        System.out.println(this.cachedBill);
        displayReadOnlyScreen(this.cachedBill);
        this.context = "";
    }

    public void onSave(List<Bill> bills, Bill bill) throws IOException, ParseException {

        if(this.context == "create") {
            final int[] highestNumber = {0};
            bills.forEach(currentBill -> {
                String[] numberString = currentBill.getNumber().split("-");
                int number = Integer.parseInt(numberString[1]);
                if(number > highestNumber[0]) {
                    highestNumber[0] = number;
                }
            });
            bill.setNewNumber(highestNumber[0] + 1);
        } else {
            bill.setNumber(bill.getNumber());
        }

        bill.setAccountId(picklistAccounts.getSelectionModel().getSelectedItem().getId());
        bill.setType(billType.getText());
        if(this.context == "create") {
            bill.setAmount(0);
        } else {
            bill.setAmount(bill.getAmount());
        }
        System.out.println(datePicker.getValue());
        bill.setDate(datePicker.getValue().toString());
        bill.setCredited(billCredited.isSelected());

        if(this.context.equals("create")) {
            billService.create(bill);
        } else if(this.context.equals("edit")) {
            billService.update(bills, bill);
        }
        displayReadOnlyScreen(bill);
        this.context = "";
    }

    public void onDelete(List<Bill> bills) {
        Bill billToDelete = listViewBills.getSelectionModel().getSelectedItem();
        listViewBills.getSelectionModel().select(listViewBills.getSelectionModel().getSelectedIndex() + 1);
        billService.delete(bills, billToDelete);
    }

    private void onAddMission(Bill bill, List<Mission> missions) throws IOException {
        List<Mission> listMissions = missions.stream()
                .filter(mission -> mission.getBillId().equals("None"))
                .filter(mission -> mission.getAccountId().equals(bill.getAccountId()))
                .collect(Collectors.toList());

        Stage newWindow = new Stage();
        newWindow.setTitle("Select Mission");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/missionsSelection.fxml"));
        Parent listProjects = loader.load();
        System.out.println(listProjects);
        MissionSelectionController controller = loader.getController();
        controller.setMissions(listMissions);
        controller.setBill(listViewBills.getSelectionModel().getSelectedItem());
        newWindow.setScene(new Scene(listProjects));
        newWindow.show();
    }

    private void displayEditableScreen(String title, List<Account> accounts) {
        billType.setEditable(true);
        billAccount.setVisible(false);
        picklistAccounts.setVisible(true);
        ObservableList<Account> observableAccounts = FXCollections.observableArrayList();
        observableAccounts.addAll(accounts);
        picklistAccounts.setItems(observableAccounts);
        if(this.context == "edit") {
            Account account = accountService.getAccountById(accounts, this.cachedBill.getAccountId());
            picklistAccounts.setValue(account);
            datePicker.setValue(LocalDate.parse(billDate.getText()));
        }
        billDate.setVisible(false);
        billCredited.setDisable(false);
        datePicker.setVisible(true);

        listViewBills.setVisible(false);
        missionsLabel.setVisible(false);
        editBill.setVisible(false);

        billSave.setVisible(true);
        billCancel.setVisible(true);

        billId.setText(title);
    }

    private void displayReadOnlyScreen(Bill bill) {
        billType.setEditable(false);
        billAccount.setVisible(true);
        picklistAccounts.setVisible(false);
        picklistAccounts.getItems().removeAll(picklistAccounts.getItems());
        billAmount.setEditable(false);
        billDate.setVisible(true);
        datePicker.setVisible(false);

        listViewBills.setVisible(true);
        missionsLabel.setVisible(true);
        editBill.setVisible(true);

        billSave.setVisible(false);
        billCancel.setVisible(false);

        billId.setText(bill.getNumber());
        System.out.println(bill);
        listViewBills.getSelectionModel().select(bill);
    }

    public void setBill(List<Bill> bills, String billId) {
        Bill bill = billService.getBillById(bills, billId);
        listViewBills.getSelectionModel().select(bill);
    }

    //--------------------------------NAVIGATE FUNCTIONS----------------------
    @FXML
    public MenuBar myMenuBar;

    @FXML
    public void navigateToAccounts(ActionEvent event) throws IOException {
        navigation.navigateToAccounts(event, null,null, myMenuBar);
    }

    @FXML
    public void navigateToBills(ActionEvent event) throws IOException {
        navigation.navigateToBills(event, null, null, myMenuBar);
    }

    @FXML
    public void navigateToMissions(ActionEvent event) throws IOException {
        navigation.navigateToMissions(event, myMenuBar);
    }

    @FXML
    public void navigateToSettings(ActionEvent event) throws IOException {
        navigation.navigateToSettings(event, setting, myMenuBar);
    }
}
