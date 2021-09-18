package app.kenavo.billapplication.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.Map;

public class AlertNotifications {

    public static void alertOnErrorSave(String object, Map<TextField, String> map) {
        int numberErrors = map.size();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("You can't save this " + object);
        alert.setContentText("Ooops, You still have " + numberErrors + " !!");

        alert.showAndWait();
    }
}
