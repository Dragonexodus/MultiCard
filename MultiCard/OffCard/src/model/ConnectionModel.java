package model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * Created by Patrick on 24.06.2015.
 */
public class ConnectionModel {
    private BooleanProperty isConnectionEstablished = new SimpleBooleanProperty(false);
    private StringProperty connectionStatus = new SimpleStringProperty("Disconnected");
    private ObjectProperty<Paint> connectionStatusColor = new SimpleObjectProperty<>(Color.RED);

    private BooleanProperty isTerminalKeyFileAvailable = new SimpleBooleanProperty(false);
    private StringProperty terminalKeyStatus = new SimpleStringProperty("Missing");
    private ObjectProperty<Paint> terminalKeyStatusColor = new SimpleObjectProperty<>(Color.RED);

    private BooleanProperty isCardKeyFileAvailable = new SimpleBooleanProperty(false);
    private StringProperty cardKeyStatus = new SimpleStringProperty("Missing");
    private ObjectProperty<Paint> cardKeyStatusColor = new SimpleObjectProperty<>(Color.RED);

    public boolean getIsConnectionEstablished() {
        return isConnectionEstablished.get();
    }

    public BooleanProperty isConnectionEstablishedProperty() {
        return isConnectionEstablished;
    }

    public void setIsConnectionEstablished(boolean isConnectionEstablished) {
        this.isConnectionEstablished.set(isConnectionEstablished);
    }

    public String getConnectionStatus() {
        return connectionStatus.get();
    }

    public StringProperty connectionStatusProperty() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus.set(connectionStatus);
    }

    public Paint getConnectionStatusColor() {
        return connectionStatusColor.get();
    }

    public ObjectProperty<Paint> connectionStatusColorProperty() {
        return connectionStatusColor;
    }

    public void setConnectionStatusColor(Paint connectionStatusColor) {
        this.connectionStatusColor.set(connectionStatusColor);
    }

    public String getTerminalKeyStatus() {
        return terminalKeyStatus.get();
    }

    public StringProperty terminalKeyStatusProperty() {
        return terminalKeyStatus;
    }

    private void setTerminalKeyStatus(String terminalKeyStatus) {
        this.terminalKeyStatus.set(terminalKeyStatus);
    }

    public Paint getTerminalKeyStatusColor() {
        return terminalKeyStatusColor.get();
    }

    public ObjectProperty<Paint> terminalKeyStatusColorProperty() {
        return terminalKeyStatusColor;
    }

    private void setTerminalKeyStatusColor(Paint terminalKeyStatusColor) {
        this.terminalKeyStatusColor.set(terminalKeyStatusColor);
    }

    public String getCardKeyStatus() {
        return cardKeyStatus.get();
    }

    public StringProperty cardKeyStatusProperty() {
        return cardKeyStatus;
    }

    private void setCardKeyStatus(String cardKeyStatus) {
        this.cardKeyStatus.set(cardKeyStatus);
    }

    public Paint getCardKeyStatusColor() {
        return cardKeyStatusColor.get();
    }

    public ObjectProperty<Paint> cardKeyStatusColorProperty() {
        return cardKeyStatusColor;
    }

    private void setCardKeyStatusColor(Paint cardKeyStatusColor) {
        this.cardKeyStatusColor.set(cardKeyStatusColor);
    }

    public boolean getIsTerminalKeyFileAvailable() {
        return isTerminalKeyFileAvailable.get();
    }

    public BooleanProperty isTerminalKeyFileAvailableProperty() {
        return isTerminalKeyFileAvailable;
    }

    public void setIsTerminalKeyFileAvailable(boolean isTerminalKeyFileAvailable) {
        if (isTerminalKeyFileAvailable) {
            setTerminalKeyStatus("available");
            setTerminalKeyStatusColor(Color.GREEN);
        } else {
            setTerminalKeyStatus("missing");
            setTerminalKeyStatusColor(Color.RED);
        }

        this.isTerminalKeyFileAvailable.set(isTerminalKeyFileAvailable);
    }

    public boolean getIsCardKeyFileAvailable() {
        return isCardKeyFileAvailable.get();
    }

    public BooleanProperty isCardKeyFileAvailableProperty() {
        return isCardKeyFileAvailable;
    }

    public void setIsCardKeyFileAvailable(boolean isCardKeyFileAvailable) {
        if (isCardKeyFileAvailable) {
            setCardKeyStatus("available");
            setCardKeyStatusColor(Color.GREEN);
        } else {
            setCardKeyStatus("missing");
            setCardKeyStatusColor(Color.RED);
        }

        this.isCardKeyFileAvailable.set(isCardKeyFileAvailable);
    }
}
