package view.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by Patrick on 24.06.2015.
 */
public class IdentificationModel {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty birthDate = new SimpleStringProperty("");
    private StringProperty matrikel = new SimpleStringProperty("");

    private StringProperty safePin = new SimpleStringProperty("");
    private StringProperty checkStatus = new SimpleStringProperty("Unchecked");
    private ObjectProperty<Paint> checkStatusColor = new SimpleObjectProperty<>(Color.ORANGE);

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getBirthDate() {
        return birthDate.get();
    }

    public StringProperty birthDateProperty() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate.set(birthDate);
    }

    public String getMatrikel() {
        return matrikel.get();
    }

    public StringProperty matrikelProperty() {
        return matrikel;
    }

    public void setMatrikel(String matrikel) {
        this.matrikel.set(matrikel);
    }


    public String getSafePin() {
        return safePin.get();
    }

    public StringProperty safePinProperty() {
        return safePin;
    }

    public void setSafePin(String safePin) {
        this.safePin.set(safePin);
    }

    public String getCheckStatus() {
        return checkStatus.get();
    }

    public StringProperty checkStatusProperty() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus.set(checkStatus);
    }

    public Paint getCheckStatusColor() {
        return checkStatusColor.get();
    }

    public ObjectProperty<Paint> checkStatusColorProperty() {
        return checkStatusColor;
    }

    public void setCheckStatusColor(Paint checkStatusColor) {
        this.checkStatusColor.set(checkStatusColor);
    }
}
