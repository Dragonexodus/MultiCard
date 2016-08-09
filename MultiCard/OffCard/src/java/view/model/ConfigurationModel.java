package view.model;

import javafx.beans.property.*;

public class ConfigurationModel {
    private StringProperty name = new SimpleStringProperty("");
    private IntegerProperty matrikel = new SimpleIntegerProperty(0);
    private StringProperty money = new SimpleStringProperty("0");
    private IntegerProperty bonus = new SimpleIntegerProperty(0);
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

    public int getBonus() {
        return bonus.get();
    }

    public void setBonus(int bonus) {
        this.bonus.set(bonus);
    }

    public IntegerProperty bonusProperty() {
        return bonus;
    }

    public int getMatrikel() {
        return matrikel.get();
    }

    public void setMatrikel(int matrikel) {
        this.matrikel.set(matrikel);
    }

    public IntegerProperty matrikelProperty() {
        return matrikel;
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
}
