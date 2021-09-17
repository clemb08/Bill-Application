module app.kenavo.billapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.csv;
    requires pdfa;
    requires kernel;
    requires layout;
    requires io;


    opens app.kenavo.billapplication to javafx.fxml;
    opens app.kenavo.billapplication.controllers to javafx.fxml;
    opens app.kenavo.billapplication.model to javafx.base;
    exports app.kenavo.billapplication;
    exports app.kenavo.billapplication.controllers to javafx.fxml;
}