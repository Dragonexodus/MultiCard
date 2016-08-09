package view.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import view.model.MainModel;

import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    private static final Integer STATUS_LENGTH = 64;
    static private MainModel model = new MainModel();

    private static Timer timer = new Timer();
    private static TimerTask task;
    private static int TIMEOUT = 5000;

    public Label lblStatusConnection;
    public Label lblStatusStatus;
    public Tab tabStudent, tabDisco;

    public MainController() {
    }

    static public void setConnectionStatus(boolean connectionStatus, String s, Color c) {
        Platform.runLater(() -> {
            model.setConnectionStatusString(s);
            model.setConnectionStatusStringColor(c);
            ConnectionController.setConnectionStatus(connectionStatus);
        });
    }

    static public void setStatus(String s, Color c) {    // weil final für lambda...
        if (s.length() > STATUS_LENGTH)
            s = s.substring(0, STATUS_LENGTH);
        setStatus2(s, c);
    }

    static void setStatus2(String s, Color c) {
        Platform.runLater(() -> {
            model.setStatus(s);
            model.setStatusColor(c);
            startTimer();
        });
    }

    static public void setStatus(String s) {
        Platform.runLater(() -> {
            model.setStatus(s);
            model.setStatusColor(Color.BLACK);
            startTimer();
        });
    }

    static public void clearStatus() {
        Platform.runLater(() -> {
            model.setStatus("");
            model.setStatusColor(Color.BLACK);
        });
    }

    static private void startTimer() {
        if (task != null)
            task.cancel();
        task = new TimerTask() {
            @Override
            public void run() {
                MainController.clearStatus();
            }
        };
        timer.schedule(task, TIMEOUT);
    }

    static public void cancelTimer() {
        timer.cancel();
    }

    @FXML
    public void initialize() {
        lblStatusConnection.textProperty().bind(model.connectionStatusStringProperty());
        lblStatusConnection.textFillProperty().bind(model.connectionStatusStringColorProperty());
        lblStatusStatus.textProperty().bind(model.statusProperty());
        lblStatusStatus.textFillProperty().bind(model.statusColorProperty());

        tabStudent.selectedProperty().addListener((ov, oldTab, newTab) -> {
            if (newTab)
                StudentController.getInstance().getStudent();
        });
        tabDisco.selectedProperty().addListener((ov, oldTab, newTab) -> {
            if (newTab)
                DiscoController.getInstance().getState();
        });
    }
}
