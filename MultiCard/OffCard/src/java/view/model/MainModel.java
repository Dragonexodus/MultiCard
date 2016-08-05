package view.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class MainModel {
    private StringProperty connectionStatusString = new SimpleStringProperty();       // StatusBar: connection
    private ObjectProperty<Paint> connectionStatusStringColor = new SimpleObjectProperty<>(Color.RED);

    private StringProperty status = new SimpleStringProperty();           // StatusBar: last status
    private ObjectProperty<Paint> statusColor = new SimpleObjectProperty<>(Color.RED);

    public String getConnectionStatusString() {
        return connectionStatusString.get();
    }

    public void setConnectionStatusString(String connectionStatusString) {
        this.connectionStatusString.set(connectionStatusString);
    }

    public StringProperty connectionStatusStringProperty() {
        return connectionStatusString;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public Paint getConnectionStatusStringColor() {
        return connectionStatusStringColor.get();
    }

    public void setConnectionStatusStringColor(Paint connectionStatusStringColor) {
        this.connectionStatusStringColor.set(connectionStatusStringColor);
    }

    public ObjectProperty<Paint> connectionStatusStringColorProperty() {
        return connectionStatusStringColor;
    }

    public Paint getStatusColor() {
        return statusColor.get();
    }

    public void setStatusColor(Paint statusColor) {
        this.statusColor.set(statusColor);
    }

    public ObjectProperty<Paint> statusColorProperty() {
        return statusColor;
    }
}
