package view.controller;

import helper.LogHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import view.model.LogModel;

public class LogController {
    public TextArea logTextArea;

    private LogModel model;

    public LogController() {
        model = new LogModel();
    }

    @FXML
    public void initialize() {
        logTextArea.textProperty().bind(model.logMessageProperty());
        LogHelper.setOnNewLogEntry(this::onNewLog);
    }

    /**
     * Fügt neue logMsg zur property in der LogModel
     *
     * @param line logMsg
     */
    private void onNewLog(String line) {
        String m = model.getLogMessage() + line + "\n";
        model.setLogMessage(m);
    }
}
