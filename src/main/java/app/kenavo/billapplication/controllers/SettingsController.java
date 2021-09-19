package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import app.kenavo.billapplication.utils.Navigation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static app.kenavo.billapplication.utils.AlertNotifications.alertOnErrorSave;
import static app.kenavo.billapplication.utils.ValidationFields.*;

public class SettingsController extends AnchorPane implements Initializable {

    @FXML
    public MenuBar myMenuBar;

    @FXML public TextField settingCompanyName;
    @FXML public Text settingNameError;
    @FXML public TextField settingAddress;
    @FXML public Text settingAddressError;
    @FXML public TextField settingEmail;
    @FXML public Text settingEmailError;
    @FXML public TextField settingPhone;
    @FXML public Text settingPhoneError;
    @FXML public TextField settingLogo;
    @FXML public Text settingLogoError;
    @FXML public TextField settingSiret;
    @FXML public Text settingSiretError;
    @FXML public TextField settingDownloadPath;
    @FXML public Text settingPathError;
    @FXML public Button chooseDownloadPath;
    @FXML public Text textDownloadPath;
    @FXML public Button chooseLogo;
    @FXML public Text textLogo;

    @FXML public Text messageCreate;

    @FXML public Button settingCancel;
    @FXML public Button settingSave;
    @FXML public Button settingEdit;

    Navigation navigation = new Navigation();
    final FileChooser fileChooser = new FileChooser();
    final DirectoryChooser dirChooser = new DirectoryChooser();

    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();

    Map<TextField, String> errors = new HashMap<TextField, String>();
    List<Text> errorFields = new ArrayList<Text>();

    String context = "";

    public SettingsController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/settings_Detail.fxml"));

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
        errorFields.add(settingEmailError);
        errorFields.add(settingAddressError);
        errorFields.add(settingLogoError);
        errorFields.add(settingNameError);
        errorFields.add(settingPathError);
        errorFields.add(settingPhoneError);
        errorFields.add(settingSiretError);

        //Validation Form Edit or Create
        settingCompanyName.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequired(errors, settingNameError, settingCompanyName);
            }
        });

        settingAddress.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkRequired(errors, settingAddressError, settingAddress);
            }
        });

        settingEmail.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkEmail(errors, settingEmailError, settingEmail);
            }
        });

        settingPhone.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                checkPhone(errors, settingPhoneError, settingPhone);
            }
        });

        settingSiret.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { // when focus lost
                Boolean notBlank = checkRequired(errors, settingPhoneError, settingPhone);
                if(notBlank) {
                    checkStringSize(errors, settingPhoneError, settingPhone, "Siret", 14);
                }
            }
        });

        settingCompanyName.setText(setting.getCompanyName());
        settingAddress.setText(setting.getAddress());
        settingEmail.setText(setting.getEmail());
        settingPhone.setText(setting.getPhone());
        settingLogo.setText(setting.getLogo());
        settingSiret.setText(setting.getSiret());
        settingDownloadPath.setText(setting.getDownloadPath());

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

        settingEdit.setOnAction(event -> onEdit());
        settingCancel.setOnAction(event -> onCancel());

        chooseDownloadPath.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setTitle("Choose a download path");
            File file = dirChooser.showDialog(stage);
            System.out.println(file);
            textDownloadPath.setText(file.getAbsolutePath());
        });

        chooseLogo.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setTitle("Choose a logo file");
            File file = fileChooser.showOpenDialog(stage);
            System.out.println(file);
            textLogo.setText(file.getAbsolutePath());
        });
    }

    public void onEdit() {
        displayEditableScreen();
    }


    public void onCancel() {
        displayReadOnlyScreen();
        errors = new HashMap<TextField, String>();
        errorFields.forEach(field -> field.setVisible(false));
    }

    public void onSave() {
        Map<TextField, Text> fields = new HashMap<TextField, Text>();
        fields.put(settingCompanyName, settingNameError);
        fields.put(settingAddress, settingAddressError);
        fields.put(settingEmail, settingEmailError);
        fields.put(settingPhone, settingPhoneError);
        fields.put(settingDownloadPath, settingPathError);
        fields.put(settingLogo, settingLogoError);
        fields.put(settingSiret, settingSiretError);
        checkRequiredFields(errors, fields);

        if(errors.size() == 0) {
            Setting setting = new Setting();
            setting.setCompanyName(settingCompanyName.getText());
            setting.setAddress(settingAddress.getText());
            setting.setEmail(settingEmail.getText());
            setting.setPhone(settingPhone.getText());
            setting.setLogo(textLogo.getText());
            setting.setSiret(settingSiret.getText());
            setting.setDownloadPath(textDownloadPath.getText());
            settingService.setSetting(setting);
            displayReadOnlyScreen();
        } else {
            alertOnErrorSave("Setting", errors);
        }

    }

    private void displayEditableScreen() {
        settingCompanyName.setEditable(true);
        settingAddress.setEditable(true);
        settingEmail.setEditable(true);
        settingPhone.setEditable(true);
        settingLogo.setVisible(false);
        chooseLogo.setVisible(true);
        textLogo.setVisible(true);
        settingSiret.setEditable(true);
        settingDownloadPath.setVisible(false);
        chooseDownloadPath.setVisible(true);
        textDownloadPath.setVisible(true);
        settingEdit.setVisible(false);
        settingSave.setVisible(true);
        settingCancel.setVisible(true);
    }

    private void displayReadOnlyScreen() {
        settingCompanyName.setEditable(false);
        settingAddress.setEditable(false);
        settingEmail.setEditable(false);
        settingPhone.setEditable(false);
        settingLogo.setVisible(true);
        chooseLogo.setVisible(false);
        textLogo.setVisible(false);
        settingSiret.setEditable(false);
        settingDownloadPath.setVisible(true);
        chooseDownloadPath.setVisible(false);
        textDownloadPath.setVisible(false);
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
}
