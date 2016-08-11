package view.model;

import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

import java.util.ArrayList;

public class Drinks extends ArrayList<Drink> {
    private byte counter = 0;
    private int FIELD_LENGTH = 20;

    public Drinks() {
        super();
    }

    public void addDrink(String drinkName, Double drinkPrice) {
        this.add(new Drink(drinkName, drinkPrice, counter));
        counter++;
    }

    public Result<String> getDrinkString(int drink) {
        if (drink > this.size() - 1 || drink < 0)
            return new ErrorResult<String>("Drinks: ungültiges Index!");
        return new SuccessResult<>(this.get(drink).getDrinkName());
    }

    public String[] getDrinkListString() {
        String[] sl = new String[this.size()];
        for (int i = 0; i < this.size(); i++) {
            StringBuffer sb = new StringBuffer(this.get(i).getDrinkName());
            int l = sb.length();
            for (int j = 0; j < FIELD_LENGTH - l; j++)
                sb.append(" ");

            sb.append(this.get(i).getDrinkPrice() + "€");
            sl[i] = sb.toString();
        }
        return sl;
    }
}

class Drink {
    private String drinkName;
    private double drinkPrice;
    private byte b;

    public Drink(String drinkName, Double drinkPrice, byte b) {
        this.drinkName = drinkName;
        this.drinkPrice = drinkPrice;
        this.b = b;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public double getDrinkPrice() {
        return drinkPrice;
    }

    public void setDrinkPrice(double drinkPrice) {
        this.drinkPrice = drinkPrice;
    }
}