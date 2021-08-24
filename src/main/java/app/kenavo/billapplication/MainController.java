package app.kenavo.billapplication;

import app.kenavo.billapplication.services.AccountService;
import app.kenavo.billapplication.services.AccountServiceImpl;
import app.kenavo.billapplication.utils.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Navigation navigation = new Navigation();

    @FXML
    public MenuBar myMenuBar;

    @FXML
    private Label welcomeText;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}