package app.kenavo.billapplication.controllers;

import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import app.kenavo.billapplication.utils.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class NavBarController extends AnchorPane {

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

    public NavBarController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/navBar.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
