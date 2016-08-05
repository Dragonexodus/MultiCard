package view.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Created by Patrick on 22.06.2015.
 */
public class ConfigurationModel {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty carId = new SimpleStringProperty("");
    private ObjectProperty<LocalDate> birthDate = new SimpleObjectProperty<>();
    private StringProperty safePin = new SimpleStringProperty("");
    private IntegerProperty points = new SimpleIntegerProperty(0);

    private BooleanProperty classicBarAccess = new SimpleBooleanProperty(false);
    private BooleanProperty casinoAccess = new SimpleBooleanProperty(false);
    private BooleanProperty poolAccess = new SimpleBooleanProperty(false);
    private BooleanProperty skyBarAccess = new SimpleBooleanProperty(false);
    private BooleanProperty wellnessAccess = new SimpleBooleanProperty(false);

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCarId() {
        return carId.get();
    }

    public void setCarId(String carId) {
        this.carId.set(carId);
    }

    public StringProperty carIdProperty() {
        return carId;
    }

    public LocalDate getBirthDate() {
        return birthDate.get();
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate.set(birthDate);
    }

    public ObjectProperty<LocalDate> birthDateProperty() {
        return birthDate;
    }

    public String getSafePin() {
        return safePin.get();
    }

    public void setSafePin(String safePin) {
        this.safePin.set(safePin);
    }

    public StringProperty safePinProperty() {
        return safePin;
    }

    public boolean getClassicBarAccess() {
        return classicBarAccess.get();
    }

    public BooleanProperty classicBarAccessProperty() {
        return classicBarAccess;
    }

    public void setClassicBarAccess(boolean classicBarAccess) {
        this.classicBarAccess.set(classicBarAccess);
    }

    public boolean getCasinoAccess() {
        return casinoAccess.get();
    }

    public BooleanProperty casinoAccessProperty() {
        return casinoAccess;
    }

    public void setCasinoAccess(boolean casinoAccess) {
        this.casinoAccess.set(casinoAccess);
    }

    public boolean getPoolAccess() {
        return poolAccess.get();
    }

    public BooleanProperty poolAccessProperty() {
        return poolAccess;
    }

    public void setPoolAccess(boolean poolAccess) {
        this.poolAccess.set(poolAccess);
    }

    public boolean getSkyBarAccess() {
        return skyBarAccess.get();
    }

    public BooleanProperty skyBarAccessProperty() {
        return skyBarAccess;
    }

    public void setSkyBarAccess(boolean skyBarAccess) {
        this.skyBarAccess.set(skyBarAccess);
    }

    public boolean getWellnessAccess() {
        return wellnessAccess.get();
    }

    public BooleanProperty wellnessAccessProperty() {
        return wellnessAccess;
    }

    public void setWellnessAccess(boolean wellnessAccess) {
        this.wellnessAccess.set(wellnessAccess);
    }

    public int getPoints() {
        return points.get();
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setPoints(int points) {
        this.points.set(points);
    }
}
