package app.kenavo.billapplication;

import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import app.kenavo.billapplication.utils.Navigation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Navigation navigation = new Navigation();
    SettingService settingService = new SettingServiceImpl();
    Setting setting = settingService.getSetting();

    @FXML
    public MenuBar myMenuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}