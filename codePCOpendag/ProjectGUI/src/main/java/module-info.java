module com.example.projectgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires com.fazecast.jSerialComm;
    requires java.net.http;
    requires json.simple;

    opens com.example.projectgui to javafx.fxml;
    exports com.example.projectgui;
}