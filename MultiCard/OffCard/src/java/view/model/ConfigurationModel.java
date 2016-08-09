package view.model;

import javafx.beans.property.*;

public class ConfigurationModel {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty matrikel = new SimpleStringProperty("");
    private StringProperty money = new SimpleStringProperty("0");
    private StringProperty bonus = new SimpleStringProperty("0");
    //TODO: Room

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getMoney() {
        return money.get();
    }

    public void setMoney(String money) {
        this.money.set(money);
    }

    public StringProperty moneyProperty() {
        return money;
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

    public String getBonus() {
        return bonus.get();
    }

    public StringProperty bonusProperty() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus.set(bonus);
    }
}
