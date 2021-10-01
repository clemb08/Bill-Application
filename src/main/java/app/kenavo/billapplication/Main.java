package app.kenavo.billapplication;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Setting;
import app.kenavo.billapplication.services.AccountService;
import app.kenavo.billapplication.services.AccountServiceImpl;
import app.kenavo.billapplication.services.SettingService;
import app.kenavo.billapplication.services.SettingServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Main extends Application {

    private static AccountService accountService = new AccountServiceImpl();
    public List<Account> accounts = accountService.getAllAccounts();

    @Override
    public void start(Stage primaryStage) throws IOException {

        SettingService settingService = new SettingServiceImpl();
        Setting setting = settingService.getSetting();
        Parent listProjects;

        //If there is no Settings register get the user to the setting page
        if(setting == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
            listProjects = loader.load();
            //If the settings exist get the User to the Welcome page
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            listProjects = loader.load();
        }

        primaryStage.setTitle("Projects Management");
        primaryStage.setScene(new Scene(listProjects));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}