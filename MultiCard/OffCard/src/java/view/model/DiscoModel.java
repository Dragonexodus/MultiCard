package view.model;

import javafx.beans.property.*;

public class DiscoModel {
    private StringProperty moneyGet = new SimpleStringProperty("0");
    private StringProperty bonusGet = new SimpleStringProperty("0");
    private StringProperty moneyAdd = new SimpleStringProperty("0");
    private StringProperty consumedMoney = new SimpleStringProperty("0");
    private StringProperty bonusPlus = new SimpleStringProperty("0");
    private StringProperty consumedDrinks = new SimpleStringProperty("");
    private StringProperty rest = new SimpleStringProperty("0");
    private IntegerProperty drink = new SimpleIntegerProperty(0);
    private BooleanProperty discoIO = new SimpleBooleanProperty(false);

    public String getMoneyGet() {
        return moneyGet.get();
    }

    public void setMoneyGet(String moneyGet) {
        this.moneyGet.set(moneyGet);
    }

    public StringProperty moneyGetProperty() {
        return moneyGet;
    }

    public String getBonusGet() {
        return bonusGet.get();
    }

    public void setBonusGet(String bonusGet) {
        this.bonusGet.set(bonusGet);
    }

    public StringProperty bonusGetProperty() {
        return bonusGet;
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

    public int getDrink() {
        return drink.get();
    }

    public void setDrink(int drink) {
        this.drink.set(drink);
    }

    public IntegerProperty drinkProperty() {
        return drink;
    }

    public String getConsumedMoney() {
        return consumedMoney.get();
    }

    public void setConsumedMoney(String consumedMoney) {
        this.consumedMoney.set(consumedMoney);
    }

    public StringProperty consumedMoneyProperty() {
        return consumedMoney;
    }

    public String getBonusPlus() {
        return bonusPlus.get();
    }

    public void setBonusPlus(String bonusPlus) {
        this.bonusPlus.set(bonusPlus);
    }

    public StringProperty bonusPlusProperty() {
        return bonusPlus;
    }

    public String getConsumedDrinks() {
        return consumedDrinks.get();
    }

    public void setConsumedDrinks(String consumedDrinks) {
        this.consumedDrinks.set(consumedDrinks);
    }

    public StringProperty consumedDrinksProperty() {
        return consumedDrinks;
    }

    public boolean isDiscoIO() {
        return discoIO.get();
    }

    public void setDiscoIO(boolean discoIO) {
        this.discoIO.set(discoIO);
    }

    public BooleanProperty discoIOProperty() {
        return discoIO;
    }

    public String getRest() {
        return rest.get();
    }

    public void setRest(String rest) {
        this.rest.set(rest);
    }

    public StringProperty restProperty() {
        return rest;
    }
}
