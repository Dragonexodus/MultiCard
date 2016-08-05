package presentation.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import presentation.model.MainModel;

public class MainController {

    private static final Integer STATUS_LENGTH = 64;
    static private MainModel model = new MainModel();
    public Label lblStatusConnection;
    public Label lblStatusStatus;

    public MainController() {
    }

    static void setStatusConnection(String s, Color c) {
        Platform.runLater(() -> {
            model.setConnectionStatus(s);
            model.setConnectionStatusColor(c);
        });
    }

    static void setStatusConnection(String s) {
        Platform.runLater(() -> {
            model.setConnectionStatus(s);
            model.setConnectionStatusColor(Color.BLACK);
        });
    }

    static void setStatusStatus(String s, Color c) {    // weil final für lambda...
        if (s.length() > STATUS_LENGTH)
            s = s.substring(0, 63);
        setStatusStatus2(s, c);
    }

    static void setStatusStatus2(String s, Color c) {
        Platform.runLater(() -> {
            model.setStatus(s);
            model.setStatusColor(c);
        });
    }

    static void setStatusStatus(String s) {
        Platform.runLater(() -> {
            model.setStatus(s);
            model.setStatusColor(Color.BLACK);
        });
    }

    @FXML
    public void initialize() {
        lblStatusConnection.textProperty().bind(model.connectionStatusProperty());
        lblStatusConnection.textFillProperty().bind(model.connectionStatusColorProperty());

        lblStatusStatus.textProperty().bind(model.statusProperty());
        lblStatusStatus.textFillProperty().bind(model.statusColorProperty());
    }
}
