package app.kenavo.billapplication;

import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import app.kenavo.billapplication.utils.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Navigation navigation = new Navigation();
    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}