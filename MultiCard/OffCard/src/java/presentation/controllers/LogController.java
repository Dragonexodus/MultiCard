package presentation.controllers;

import application.log.LogHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import presentation.model.LogModel;

/**
 * Created by Patrick on 08.07.2015.
 */
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
