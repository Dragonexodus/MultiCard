package view.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentModel {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty matrikel = new SimpleStringProperty("");
    private StringProperty moneyGet = new SimpleStringProperty("0");
    private StringProperty moneyAdd = new SimpleStringProperty("0");
    private StringProperty moneySub = new SimpleStringProperty("0");

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

    public String getMoneyGet() {
        return moneyGet.get();
    }

    public void setMoneyGet(String moneyGet) {
        this.moneyGet.set(moneyGet);
    }

    public StringProperty moneyGetProperty() {
        return moneyGet;
    }

    public String getMoneyAdd() {
        return moneyAdd.get();
    }

    public void setMoneyAdd(String moneyAdd) {
        this.moneyAdd.set(moneyAdd);
    }

    public StringProperty moneyAddProperty() {
        return moneyAdd;
    }

    public String getMoneySub() {
        return moneySub.get();
    }

    public void setMoneySub(String moneySub) {
        this.moneySub.set(moneySub);
    }

    public StringProperty moneySubProperty() {
        return moneySub;
    }
}
