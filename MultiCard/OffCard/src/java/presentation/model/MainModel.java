package presentation.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class MainModel {
    private StringProperty connectionStatus = new SimpleStringProperty();       // StatusBar: connection
    private ObjectProperty<Paint> connectionStatusColor = new SimpleObjectProperty<>(Color.RED);

    private StringProperty status = new SimpleStringProperty();           // StatusBar: last status
    private ObjectProperty<Paint> statusColor = new SimpleObjectProperty<>(Color.RED);

    public String getConnectionStatus() {
        return connectionStatus.get();
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus.set(connectionStatus);
    }

    public StringProperty connectionStatusProperty() {
        return connectionStatus;
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

    public Paint getConnectionStatusColor() {
        return connectionStatusColor.get();
    }

    public void setConnectionStatusColor(Paint connectionStatusColor) {
        this.connectionStatusColor.set(connectionStatusColor);
    }

    public ObjectProperty<Paint> connectionStatusColorProperty() {
        return connectionStatusColor;
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
