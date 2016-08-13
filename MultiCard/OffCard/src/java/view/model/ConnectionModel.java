package view.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ConnectionModel {
    private BooleanProperty isConnectionEstablished = new SimpleBooleanProperty(false);
    //    private StringProperty connectionStatus = new SimpleStringProperty("Disconnected");
    private ObjectProperty<Paint> connectionStatusColor = new SimpleObjectProperty<>(Color.RED);

    private BooleanProperty isTerminalKeyFileAvailable = new SimpleBooleanProperty(false);
    private StringProperty terminalKeyStatus = new SimpleStringProperty("nicht vorhanden");
    private ObjectProperty<Paint> terminalKeyStatusColor = new SimpleObjectProperty<>(Color.RED);

    private BooleanProperty isCardKeyFileAvailable = new SimpleBooleanProperty(false);
    private StringProperty cardKeyStatus = new SimpleStringProperty("nicht vorhanden");
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

//    public String getConnectionStatusString() {
//        return connectionStatus.getData();
//    }
//
//    public StringProperty connectionStatusStringProperty() {
//        return connectionStatus;
//    }
//
//    public void setConnectionStatusString(String connectionStatus) {
//        this.connectionStatus.set(connectionStatus);
//    }

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

    public boolean isTerminalKeyFileAvailable() {
        return isTerminalKeyFileAvailable.get();
    }

    public BooleanProperty isTerminalKeyFileAvailableProperty() {
        return isTerminalKeyFileAvailable;
    }

    public void setIsTerminalKeyFileAvailable(boolean isTerminalKeyFileAvailable) {
        if (isTerminalKeyFileAvailable) {
            setTerminalKeyStatus("vorhanden");
            setTerminalKeyStatusColor(Color.GREEN);
        } else {
            setTerminalKeyStatus("nicht vorhanden");
            setTerminalKeyStatusColor(Color.RED);
        }

        this.isTerminalKeyFileAvailable.set(isTerminalKeyFileAvailable);
    }

    public boolean isCardKeyFileAvailable() {
        return isCardKeyFileAvailable.get();
    }

    public BooleanProperty isCardKeyFileAvailableProperty() {
        return isCardKeyFileAvailable;
    }

    public void setIsCardKeyFileAvailable(boolean isCardKeyFileAvailable) {
        if (isCardKeyFileAvailable) {
            setCardKeyStatus("vorhanden");
            setCardKeyStatusColor(Color.GREEN);
        } else {
            setCardKeyStatus("nicht vorhanden");
            setCardKeyStatusColor(Color.RED);
        }

        this.isCardKeyFileAvailable.set(isCardKeyFileAvailable);
    }
}
