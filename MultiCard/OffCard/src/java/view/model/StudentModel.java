package view.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentModel {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty matrikel = new SimpleStringProperty("");

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

}
