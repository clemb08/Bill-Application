package app.kenavo.billapplication.utils;

import app.kenavo.billapplication.controllers.AccountsController;
import app.kenavo.billapplication.controllers.BillsController;
import app.kenavo.billapplication.controllers.MissionsController;
import app.kenavo.billapplication.model.Account;
import app.kenavo.billapplication.model.Bill;
import app.kenavo.billapplication.model.Mission;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Navigation {

    public void navigateToAccounts(Event event, List<Account> accounts, String accountId, MenuBar root) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/accounts.fxml"));
        Stage primaryStage = (Stage) root.getScene().getWindow();
        Parent listProjects = loader.load();
        System.out.println(listProjects);
        if(accountId != null) {
            AccountsController controller = loader.getController();
            controller.setAccount(accounts, accountId);
        }
        primaryStage.setScene(new Scene(listProjects));
        primaryStage.show();
    }

    public void navigateToBills(Event event, List<Bill> bills, String billId, MenuBar root) throws IOException {
        Stage primaryStage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/bills.fxml"));
        Parent listProjects = loader.load();
        System.out.println(listProjects);
        if(billId != null) {
            BillsController controller = loader.getController();
            controller.setBill(bills, billId);
        }
        primaryStage.setScene(new Scene(listProjects));
        primaryStage.show();
    }

    public void navigateToMissions(Event event, MenuBar root) throws IOException {
        Stage primaryStage = (Stage) root.getScene().getWindow();
        Parent listProjects = FXMLLoader.load(getClass().getResource("/app/kenavo/billapplication/missions.fxml"));
        System.out.println(listProjects);
        primaryStage.setScene(new Scene(listProjects));
        primaryStage.show();
    }

    public void navigateToBill(Event event, String billId, List<Bill> bills) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/bills.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent listProjects = loader.load();
        BillsController controller = loader.getController();
        controller.setBill(bills, billId);
        primaryStage.setScene(new Scene(listProjects));
        primaryStage.show();
    }

    public void navigateToMission(Event event, String missionId, List<Mission> missions) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/kenavo/billapplication/missions.fxml"));
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent listProjects = loader.load();
        MissionsController controller = loader.getController();
        controller.setMission(missions, missionId);
        primaryStage.setScene(new Scene(listProjects));
        primaryStage.show();
    }

}
