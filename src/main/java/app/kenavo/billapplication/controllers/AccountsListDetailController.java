package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.*;
import app.kenavo.billapplication.utils.Navigation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static app.kenavo.billapplication.utils.AlertNotifications.alertOnErrorSave;
import static app.kenavo.billapplication.utils.ValidationFields.*;
import static java.lang.String.valueOf;

public class AccountsListDetailController extends AnchorPane implements Initializable {

    @FXML public ListView<Account> listViewAccounts;
    @FXML public ListView<Bill> listViewBills;
    @FXML public Text accountTitle;
    @FXML public TextField accountTitleField;
    @FXML public Text accountTitleError;
    @FXML public TextField accountAddress;
    @FXML public Text accountAddressError;
    @FXML public TextField accountContact;
    @FXML public Text accountContactError;
    @FXML public TextField accountEmail;
    @FXML public Text accountEmailError;
    @FXML public TextField accountPhone;
    @FXML public Text accountPhoneError;
    @FXML public TextField accountCA;
    @FXML public Label billsLabel;

    @FXML public Button createAccount;
    @FXML public Button deleteAccount;
    @FXML public Button editAccount;
    @FXML public Button accountSave;
    @FXML public Button accountCancel;



    Map<TextField, String> errors = new HashMap<TextField, String>();

    Navigation navigation = new Navigation();
    BillService billService = new BillServiceImpl();
    AccountService accountService = new AccountServiceImpl();
    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();

    Account cachedAccount = null;
    Account inProcessAccount = null;
    String context = "";

    public AccountsListDetailController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/accounts_List_Detail.fxml"));

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

        //Validation Form Edit or Create
        accountTitleField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequired(errors, accountTitleError, accountTitleField);
            }
        });

        accountContact.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequired(errors, accountContactError, accountContact);
            }
        });

        accountAddress.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            Boolean notBlank;
            if (!newValue) { // when focus lost
                notBlank = checkRequired(errors, accountAddressError, accountAddress);
                System.out.println(notBlank);
                if (notBlank) {
                    checkAddress(errors, accountAddressError, accountAddress);
                }
            }
        });

        accountEmail.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            System.out.println("lost focus Email");
            if (!newValue) { // when focus lost
                checkEmail(errors, accountEmailError, accountEmail);
            }
        });

        accountPhone.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkPhone(errors, accountPhoneError, accountPhone);
            }
        });

        List<Account> accounts = accountService.getAllAccounts();
        List<Bill> bills = billService.getAllBills();

        ObservableList<Account> observableAccounts = FXCollections.observableArrayList();
        observableAccounts.addAll(accounts);

        listViewAccounts.getItems().addAll(observableAccounts);

        createAccount.setOnAction((event -> {
            this.context = "create";
            this.cachedAccount = listViewAccounts.getSelectionModel().getSelectedItem();
            displayCreateNewAccount();
        }));
        editAccount.setOnAction(event -> {
            this.context = "edit";
            this.cachedAccount = listViewAccounts.getSelectionModel().getSelectedItem();
            displayEditableScreen("Edit Account");
        });

        accountSave.setOnAction(event -> {
            try {
                if(this.context.equals("create")) {
                    onSave(accounts, this.inProcessAccount);
                } else if(this.context.equals("edit")) {
                    onSave(accounts, this.cachedAccount);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        accountCancel.setOnAction(event -> onCancel(bills));
        deleteAccount.setOnAction(event -> onDelete(accounts));

        listViewAccounts.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
            @Override
            public ListCell<Account> call(ListView<Account> param) {
                final Label label = new Label();
                final Tooltip tooltip = new Tooltip();
                final ListCell<Account> cell = new ListCell<Account>() {
                    @Override
                    public void updateItem(Account account, boolean empty) {
                        super.updateItem(account, empty);
                        if(account != null) {
                            label.setText(account.getName());
                            setText(account.getName());
                            tooltip.setText(account.getName());
                            setTooltip(tooltip);
                        }
                    }
                };
                return cell;
            }
        });

        //Add Event Listener to ListView Account
        listViewAccounts.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Account>() {
                    @Override
                    public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                        System.out.println(newValue);
                        if(newValue.getName() != null) {
                            displayAccountSelected(newValue);
                            displayBillsAccountSelected(bills, newValue);
                        } else {
                            resetAccountSelected();
                        }
                    }
                });

        listViewBills.setCellFactory(new Callback<ListView<Bill>, ListCell<Bill>>() {
            @Override
            public ListCell<Bill> call(ListView<Bill> param) {
                final Label label = new Label();
                final Tooltip tooltip = new Tooltip();
                final ListCell<Bill> cell = new ListCell<Bill>() {
                    @Override
                    public void updateItem(Bill bill, boolean empty) {
                        super.updateItem(bill, empty);
                        if(bill != null) {
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
    }

    public Account displayAccountSelected(Account account) {
        accountTitle.setText(account.getName());
        accountTitleField.setText(account.getName());
        accountAddress.setText(account.getAddress());
        accountContact.setText(account.getContact());
        accountEmail.setText(account.getContact());
        accountPhone.setText(account.getPhone());
        accountCA.setText(valueOf(account.getCa()));
        return account;
    }

    public void resetAccountSelected() {
        accountTitle.setText(null);
        accountTitleField.setText(null);
        accountAddress.setText(null);
        accountContact.setText(null);
        accountEmail.setText(null);
        accountPhone.setText(null);
        accountCA.setText(null);
    }

    public void displayBillsAccountSelected(List<Bill> bills, Account account) {
        List<Bill> billsToDisplay = billService.getAllBillsByAccount(bills, account.getId());
        ObservableList<Bill> observableBills = FXCollections.observableArrayList();
        observableBills.addAll(billsToDisplay);

        ObservableList<Bill> items = listViewBills.getItems();
        items.removeAll(listViewBills.getItems());
        System.out.println(listViewBills.getItems());
        items.addAll(observableBills);

        listViewBills.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    Bill bill = listViewBills.getSelectionModel().getSelectedItem();

                    try {
                        navigation.navigateToBill(click, bill.getId(), bills);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void displayCreateNewAccount() {
        Account newAccount = new Account();
        this.inProcessAccount = newAccount;
        listViewAccounts.getItems().add(newAccount);
        listViewAccounts.getSelectionModel().select(newAccount);
        displayEditableScreen("Create New Account");
    }

    public void onCancel(List<Bill> bills) {
        if(this.context.equals("create")) {
            listViewAccounts.getItems().removeAll(this.inProcessAccount);
        } else if(this.context.equals("edit")) {
            displayBillsAccountSelected(bills, this.cachedAccount);
        }
        displayReadOnlyScreen(this.cachedAccount);
        this.context = "";
        errors = new HashMap<TextField, String>();
    }

    public void onSave(List<Account> accounts, Account account) throws IOException {
        Map<TextField, Text> fields = new HashMap<TextField, Text>();
        fields.put(accountTitleField, accountTitleError);
        fields.put(accountAddress, accountAddressError);
        checkRequiredFields(errors, fields);

        if(errors.size() == 0) {
            account.setName(accountTitleField.getText());
            account.setAddress(accountAddress.getText());
            account.setContact(accountContact.getText());
            account.setEmail(accountEmail.getText());
            account.setPhone(accountPhone.getText());
            account.setCa(accountCA.getText());

            if(this.context.equals("create")) {
                accountService.create(account);
            } else if(this.context.equals("edit")) {
                accountService.update(accounts, account);
            }
            displayReadOnlyScreen(account);
        } else {
            alertOnErrorSave("Account", errors);
        }
    }

    public void onDelete(List<Account> accounts) {
        Account accountToDelete = listViewAccounts.getSelectionModel().getSelectedItem();
        listViewAccounts.getSelectionModel().select(listViewAccounts.getSelectionModel().getSelectedIndex() + 1);
        accountService.delete(accounts, accountToDelete);
    }

    private void displayEditableScreen(String title) {
        accountTitleField.setEditable(true);
        accountAddress.setEditable(true);
        accountContact.setEditable(true);
        accountPhone.setEditable(true);
        accountEmail.setEditable(true);

        listViewAccounts.setVisible(false);
        listViewBills.setVisible(false);
        listViewBills.getItems().removeAll(listViewBills.getItems());
        billsLabel.setVisible(false);
        editAccount.setVisible(false);

        accountSave.setVisible(true);
        accountCancel.setVisible(true);

        accountTitle.setText(title);
    }

    private void displayReadOnlyScreen(Account account) {
        accountTitleField.setEditable(false);
        accountAddress.setEditable(false);
        accountContact.setEditable(false);
        accountPhone.setEditable(false);
        accountEmail.setEditable(false);

        listViewAccounts.setVisible(true);
        listViewBills.setVisible(true);
        billsLabel.setVisible(true);
        editAccount.setVisible(true);

        accountSave.setVisible(false);
        accountCancel.setVisible(false);

        accountTitle.setText(account.getName());
        listViewAccounts.getSelectionModel().select(account);
    }

    public void setAccount(List<Account> accounts, String accountId) {
        Account account = accountService.getAccountById(accounts, accountId);
        listViewAccounts.getSelectionModel().select(account);
    }

}
