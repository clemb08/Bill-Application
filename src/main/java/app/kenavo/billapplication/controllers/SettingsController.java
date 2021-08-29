package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import app.kenavo.billapplication.utils.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML public TextField settingCompanyName;
    @FXML public TextField settingAddress;
    @FXML public TextField settingEmail;
    @FXML public TextField settingPhone;
    @FXML public TextField settingLogo;
    @FXML public TextField settingSiret;
    @FXML public TextField settingDownloadPath;

    @FXML public Text messageCreate;

    @FXML public Button settingCancel;
    @FXML public Button settingSave;
    @FXML public Button settingEdit;

    Navigation navigation = new Navigation();

    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();

    String context = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(this.context);

        settingSave.setOnAction(event -> {
            onSave();

            if(this.context.equals("create")) {
                try {
                    navigation.navigateToHome(null, myMenuBar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        settingEdit.setOnAction(event -> { onEdit();});
        settingCancel.setOnAction(event -> onCancel());
    }

    public void onEdit() {
        displayEditableScreen();
    }


    public void onCancel() {
        displayReadOnlyScreen();
    }

    public void onSave() {
        Setting setting = new Setting();
        setting.setCompanyName(settingCompanyName.getText());
        setting.setAddress(settingAddress.getText());
        setting.setEmail(settingEmail.getText());
        setting.setPhone(settingPhone.getText());
        setting.setLogo(settingLogo.getText());
        setting.setSiret(settingSiret.getText());
        setting.setDownloadPath(settingDownloadPath.getText());
        settingService.setSetting(setting);
        displayReadOnlyScreen();
    }

    private void displayEditableScreen() {
        settingCompanyName.setEditable(true);
        settingAddress.setEditable(true);
        settingEmail.setEditable(true);
        settingPhone.setEditable(true);
        settingLogo.setEditable(true);
        settingSiret.setEditable(true);
        settingDownloadPath.setEditable(true);
        settingEdit.setVisible(false);
        settingSave.setVisible(true);
        settingCancel.setVisible(true);
    }

    private void displayReadOnlyScreen() {
        settingCompanyName.setEditable(false);
        settingAddress.setEditable(false);
        settingEmail.setEditable(false);
        settingPhone.setEditable(false);
        settingLogo.setEditable(false);
        settingSiret.setEditable(false);
        settingDownloadPath.setEditable(false);
        settingEdit.setVisible(true);
        settingSave.setVisible(false);
        settingCancel.setVisible(false);
    }

    public void setContextSetting(String context) {
        this.context = context;
        if(this.context.equals("create")) {
            displayEditableScreen();
            settingCancel.setVisible(false);
            settingEdit.setVisible(false);
            settingSave.setVisible(true);
            messageCreate.setVisible(true);
        } else if(this.context.equals("view")) {
            Setting setting = settingService.getSetting();
            messageCreate.setVisible(false);
            settingCancel.setOnAction(event -> onCancel());
            settingSave.setOnAction(event -> onSave());
            settingEdit.setOnAction(event -> onEdit());

            settingCompanyName.setText(setting.getCompanyName());
            settingAddress.setText(setting.getAddress());
            settingEmail.setText(setting.getEmail());
            settingPhone.setText(setting.getPhone());
            settingLogo.setText(setting.getLogo());
            settingSiret.setText(setting.getSiret());
            settingDownloadPath.setText(setting.getDownloadPath());
        }
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
