package app.kenavo.billapplication;

import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.services.AccountService;
import app.kenavo.billapplication.services.AccountServiceImpl;
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
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        primaryStage.setTitle("Projects Management");
        primaryStage.setScene(new Scene(root, 900, 450));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}