package controller;

import helpers.LogHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import model.LogModel;

/**
 * Created by Patrick on 08.07.2015.
 */
public class LogController {
    public TextArea logTextArea;

    private LogModel model;

    public LogController() {
        this.model = new LogModel();
    }

    @FXML
    public void initialize() {
        initializeBindings();

        LogHelper.setOnNewLogEntry(this::onNewLog);
    }

    /**
     * Adds a new log entry to the property in the model
     *
     * @param line log entry
     */
    private void onNewLog(String line) {
        String m = this.model.getLogMessage() + line + "\n";
        this.model.setLogMessage(m);
    }

    private void initializeBindings() {
        logTextArea.textProperty().bind(this.model.logMessageProperty());
    }
}
