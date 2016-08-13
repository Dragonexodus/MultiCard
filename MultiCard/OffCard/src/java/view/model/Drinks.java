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

    public Double getDrinkPrice(int i) {
        return this.get(i).getDrinkPrice();
    }

    public Result<Integer> getBonusPlus(byte[] a) {
        Result<Double> r1 = getConsumedMoney(a);
        if (!r1.isSuccess())
            return new ErrorResult<Integer>(r1.getErrorMsg());
        Integer bonus = new Integer(new Double(r1.getData() / 3).intValue());
        return new SuccessResult<>(bonus);
    }

    public Result<Double> getConsumedMoney(byte[] a) {
        Double d = new Double(0);
        for (int i = 0; i < a.length; i++) {
            if (a[i] > this.size() - 1 || a[i] < 0)
                return new ErrorResult<>("Drinks.getConsumedMoney: ungültiges Index!");
            d += this.get(a[i]).getDrinkPrice();
        }
        d = Math.round(d * 100.0) / 100.0;                                      // aufrunden
        return new SuccessResult<>(d);
    }

    public Result<String> getConsumedDrinksString(byte[] a) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < a.length; i++) {
            Result<String> r1 = getDrinkName(a[i]);
            if (!r1.isSuccess())
                return new ErrorResult<String>(r1.getErrorMsg());
            sb.append(r1.getData() + "\n");
        }
        return new SuccessResult<>(sb.toString());
    }

    public Result<byte[]> getDrinkByteArray(int index) {
        if (index > this.size() - 1 || index < 0)
            return new ErrorResult<>("Drinks.getDrinkByteArray: ungültiges Index!");
        return new SuccessResult<>(this.get(index).getB());
    }

    public Result<String> getDrinkName(int drink) {
        if (drink > this.size() - 1 || drink < 0)
            return new ErrorResult<>("Drinks.getDrinkName: ungültiges Index!");
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
    private byte[] b;

    public Drink(String drinkName, Double drinkPrice, byte b) {
        this.drinkName = drinkName;
        this.drinkPrice = drinkPrice;
        this.b = new byte[1];
        this.b[0] = b;
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

    public byte[] getB() {
        return b;
    }

    public void setB(byte[] b) {
        this.b = b;
    }
}