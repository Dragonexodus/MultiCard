package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Patrick on 24.06.2015.
 */
public class LogModel {
    private StringProperty logMessage = new SimpleStringProperty("");

    public String getLogMessage() {
        return logMessage.get();
    }

    public StringProperty logMessageProperty() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage.set(logMessage);
    }
}
