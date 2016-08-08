package view.model;

import javafx.beans.property.*;

public class ConfigurationModel {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty matrikel = new SimpleStringProperty("");
    private IntegerProperty points = new SimpleIntegerProperty(0);

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getMatrikel() {
        return matrikel.get();
    }

    public void setMatrikel(String matrikel) {
        this.matrikel.set(matrikel);
    }

    public StringProperty matrikelProperty() {
        return matrikel;
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
