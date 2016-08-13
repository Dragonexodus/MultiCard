package view.controller;

import application.applet.DiscoApplet;
import application.applet.StudentApplet;
import helper.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import view.model.DiscoModel;
import view.model.Drinks;

public class DiscoController {

    private static DiscoController instance;
    private final Integer EINTRITT = 10;
    private final Integer EINTRITT_BONUS = 20;
    private final Integer EINTRITT_ADD_BONUS = 10;
    private final Integer MAX_MONEY_VALUE = 127;
    public Label lblInMoney, lblInBonus, lblBonus, lblMoney, lblConsumed, lblBonusPlus, lblRest;
    public TextField tfAddMoney;
    public Button butInMoney, butInBonus, butOut, butAddDrink, butAddMoney;
    public ChoiceBox cbDrink;
    public TitledPane tpBar, tpExit;
    public TextArea taConsumed;
    private DiscoModel model;
    private Drinks drinks = new Drinks();

    public DiscoController() {
        instance = this;
        model = new DiscoModel();

        drinks.addDrink("Fanta", 2.0);
        drinks.addDrink("Cola", 2.2);
        drinks.addDrink("Bier hell", 3.1);
        drinks.addDrink("Bier dunkel", 3.3);
        drinks.addDrink("Vodka", 4.4);

    }

    public static DiscoController getInstance() {
        if (instance == null)
            instance = new DiscoController();
        return instance;
    }

    @FXML
    public void initialize() {
        initBindings();
    }

    public void getState() {
        Result<String> m = DiscoApplet.getMoney();
        if (!m.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, m.getErrorMsg());
            MainController.setStatus(m.getErrorMsg(), Color.RED);
            return;
        }
        model.setMoneyGet(m.getData() + "€");
        Result<String> b = DiscoApplet.getBonus();
        if (!b.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, b.getErrorMsg());
            MainController.setStatus(b.getErrorMsg(), Color.RED);
            return;
        }
        model.setBonusGet(b.getData() + "€");
    }

    private Result<Integer> getBonus() {
        Result<String> bonus = DiscoApplet.getBonus();
        if (!bonus.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, bonus.getErrorMsg());
            MainController.setStatus(bonus.getErrorMsg(), Color.RED);
            return new ErrorResult<Integer>("");
        }
        return new SuccessResult<>(Integer.parseInt(bonus.getData()));
    }

    private Result<Double> getMoney() {
        Result<String> money = DiscoApplet.getMoney();
        if (!money.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, money.getErrorMsg());
            MainController.setStatus(money.getErrorMsg(), Color.RED);
            return new ErrorResult<Double>("");
        }
        return new SuccessResult<>(Double.parseDouble(money.getData()));
    }

    private void addMoney() {
        Result<Boolean> result = StudentApplet.addMoney(model.getMoneyAdd());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        getState();
        LogHelper.log(LogLevel.INFO, "das Geld wurde eingezahlt");
        MainController.setStatus("das Geld wurde eingezahlt", Color.GREEN);
    }

    private void addDrink() {
        Result<byte[]> r1 = drinks.getDrinkByteArray(model.getDrink());         //ByteHelper.intToByteArrayLsb(model.getConsumed(), 1);
        if (!r1.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r1.getErrorMsg());
            MainController.setStatus(r1.getErrorMsg(), Color.RED);
            return;
        }
        Result<Boolean> r2 = DiscoApplet.addDrink(r1.getData());
        if (!r2.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r2.getErrorMsg());
            MainController.setStatus(r2.getErrorMsg(), Color.RED);
            return;
        }
        Result<String> r3 = drinks.getDrinkName(model.getDrink());
        if (!r3.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r3.getErrorMsg());
            MainController.setStatus(r3.getErrorMsg(), Color.RED);
            return;
        }
        getConsumed();
        LogHelper.log(LogLevel.INFO, "%s konsumiert :)", r3.getData());
        MainController.setStatus(r3.getData() + " konsumiert :)", Color.GREEN);
    }

    private Result<byte[]> getConsumedDrinks() {
        Result<byte[]> r = DiscoApplet.getDrink();
        if (!r.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r.getErrorMsg());
            MainController.setStatus(r.getErrorMsg(), Color.RED);
            return new ErrorResult<byte[]>("");
        }
        return new SuccessResult<>(r.getData());
    }

    private void getConsumed() {
        Result<byte[]> r1 = getConsumedDrinks();
        if (!r1.isSuccess())
            return;

        Result<String> r2 = drinks.getConsumedDrinksString(r1.getData());
        if (!r2.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r2.getErrorMsg());
            MainController.setStatus(r2.getErrorMsg(), Color.RED);
            return;
        }
        model.setConsumedDrinks(r2.getData());

        Result<Double> consumed = getConsumedMoney(r1.getData());
        if (consumed.isSuccess())
            model.setConsumedMoney(consumed.getData().toString() + "€");

        Result<Integer> bonusPlus = getBonusPlus(r1.getData());
        if (bonusPlus.isSuccess())
            model.setBonusPlus(bonusPlus.getData().toString() + "€");

        Result<Integer> bonus = getBonus();
        if (bonus.isSuccess())
            model.setRest(consumed.getData() - (bonusPlus.getData() + bonus.getData()) + "€");
    }

    private Result<Double> getConsumedMoney(byte[] a) {
        Result<Double> r = drinks.getConsumedMoney(a);
        if (!r.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r.getErrorMsg());
            MainController.setStatus(r.getErrorMsg(), Color.RED);
            return new ErrorResult<Double>("");
        }
        return new SuccessResult<>(r.getData());
    }

    private Result<Integer> getBonusPlus(byte[] a) {
        Result<Integer> r = drinks.getBonusPlus(a);
        if (!r.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r.getErrorMsg());
            MainController.setStatus(r.getErrorMsg(), Color.RED);
            return new ErrorResult<Integer>("");
        }
        return new SuccessResult<>(r.getData());
    }

    private void initBindings() {
        lblBonus.textProperty().bind(model.bonusGetProperty());
        lblMoney.textProperty().bind(model.moneyGetProperty());
        tfAddMoney.textProperty().bindBidirectional(model.moneyAddProperty());
        butAddMoney.addEventHandler(ActionEvent.ACTION, e -> addMoney());

        butAddDrink.addEventHandler(ActionEvent.ACTION, e -> addDrink());

        cbDrink.setStyle("-fx-font: 13px \"Monospace\";");
        cbDrink.setItems(FXCollections.observableArrayList(drinks.getDrinkListString()));
        cbDrink.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                model.setDrink(newValue.intValue());
            }
        });

        lblInMoney.visibleProperty().bind(model.discoIOProperty().not());
        lblInBonus.visibleProperty().bind(model.discoIOProperty().not());
        butInMoney.visibleProperty().bind(model.discoIOProperty().not());
        butInBonus.visibleProperty().bind(model.discoIOProperty().not());
        tpBar.visibleProperty().bind(model.discoIOProperty());
        tpExit.visibleProperty().bind(model.discoIOProperty());

        lblConsumed.textProperty().bind(model.consumedMoneyProperty());
        lblBonusPlus.textProperty().bind(model.bonusPlusProperty());
        taConsumed.textProperty().bind(model.consumedDrinksProperty());
        lblRest.textProperty().bind(model.restProperty());

//        getConsumed();

        butInMoney.addEventHandler(ActionEvent.ACTION, e -> {
            Result<Double> r1 = getMoney();
            if (!r1.isSuccess())
                return;
            if (EINTRITT > r1.getData()) {
                LogHelper.log(LogLevel.INFO, "Zu wenig Geld auf der SC!");
                MainController.setStatus("Zu wenig Geld auf der SC!", Color.PURPLE);
                return;
            }
            Result<Boolean> r2 = DiscoApplet.subMoney(EINTRITT.toString());
            if (!r2.isSuccess()) {
                LogHelper.log(LogLevel.ERROR, r2.getErrorMsg());
                MainController.setStatus(r2.getErrorMsg(), Color.RED);
                return;
            }
            Result<Boolean> r3 = DiscoApplet.addBonus(EINTRITT_ADD_BONUS.toString());
            if (!r3.isSuccess()) {
                LogHelper.log(LogLevel.ERROR, r3.getErrorMsg());
                MainController.setStatus(r3.getErrorMsg(), Color.RED);
                return;
            }
            model.setDiscoIO(true);
            getState();
            getConsumed();
        });

        butInBonus.addEventHandler(ActionEvent.ACTION, e -> {
            Result<Integer> r1 = getBonus();
            if (!r1.isSuccess())
                return;
            if (EINTRITT > r1.getData()) {
                LogHelper.log(LogLevel.INFO, "Zu wenig Bonuspunkte auf der SC!");
                MainController.setStatus("Zu wenig Bonuspunkte auf der SC!", Color.PURPLE);
                return;
            }
            Result<Boolean> r2 = DiscoApplet.subBonus(EINTRITT_BONUS.toString());
            if (!r2.isSuccess()) {
                LogHelper.log(LogLevel.ERROR, r2.getErrorMsg());
                MainController.setStatus(r2.getErrorMsg(), Color.RED);
                return;
            }
//            Result<Boolean> r3 = DiscoApplet.addBonus(EINTRITT_ADD_BONUS.toString());
//            if (!r3.isSuccess()) {
//                LogHelper.log(LogLevel.ERROR, r3.getErrorMsg());
//                MainController.setStatus(r3.getErrorMsg(), Color.RED);
//                return;
//            }
            model.setDiscoIO(true);
            getState();
            getConsumed();
        });

        butOut.addEventHandler(ActionEvent.ACTION, e -> {
            //TODO
            Result<byte[]> drinks = getConsumedDrinks();
            if (!drinks.isSuccess())
                return;
            Result<Double> consum = getConsumedMoney(drinks.getData());
            if (!consum.isSuccess())
                return;
            Result<Double> money = getMoney();
            if (!money.isSuccess())
                return;
            Result<Integer> bonus = getBonus();
            if (!bonus.isSuccess())
                return;
            Result<Integer> bonusPlus = getBonusPlus(drinks.getData());
            if (!bonusPlus.isSuccess())
                return;

            Double moneyAndBonus = money.getData() + bonus.getData() + bonusPlus.getData();
            // Teilbezahlung ---------------------------------------------------
            if (consum.getData() > moneyAndBonus) {
                int paidDrinks = 0;
                Double summe = 0.0;
                Double newMoneyAndBonus = 0.0;
                for (int i = 0; i < drinks.getData().length; i++) {
                    byte[] newBonusPlus = new byte[i + 1];                      // i+1, da sonst geht ein Getränk verloren
                    for (int j = 0; j < i + 1; j++)                             // i+1, da sonst geht ein Getränk verloren
                        newBonusPlus[j] = drinks.getData()[j];

                    newMoneyAndBonus = money.getData() + bonus.getData() + this.drinks.getBonusPlus(newBonusPlus).getData();
                    summe += this.drinks.getDrinkPrice(drinks.getData()[i]);
                    paidDrinks++;
                    if (drinks.getData().length <= i + 1) {
                        LogHelper.log(LogLevel.INFO, "Ungenügend Geld auf SC, bitte nachladen!");
                        MainController.setStatus("Ungenügend Geld auf SC, bitte nachladen!", Color.PURPLE);
                        return;
                    }
                    if (newMoneyAndBonus < summe + this.drinks.getDrinkPrice(drinks.getData()[i + 1])) {
//                        System.err.println("plus: " + this.drinks.getBonusPlus(newBonusPlus).getData());
//                        System.err.println("newMoneyAndBonus: " + newMoneyAndBonus);
//                        System.err.println("summe: " + summe);
                        break;
                    }
                }
                Double restMoney = newMoneyAndBonus - summe;

                byte[] aPaid = new byte[drinks.getData().length];               // bezahlte Drinks auf Bezahlt setzen
                for (int i = 0; i < paidDrinks; i++)
                    aPaid[i] = 1;
                Result<Boolean> rPaid = DiscoApplet.setPaidDrinks(aPaid);
                if (!rPaid.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, rPaid.getErrorMsg());
                    MainController.setStatus(rPaid.getErrorMsg(), Color.RED);
                    return;
                }
                Result<Boolean> rResetM = DiscoApplet.resetMoney();
                if (!rResetM.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, rResetM.getErrorMsg());
                    MainController.setStatus(rResetM.getErrorMsg(), Color.RED);
                    return;
                }
                Result<Boolean> rAddM = DiscoApplet.addMoney(String.format("%.2f", restMoney));
                if (!rAddM.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, rAddM.getErrorMsg());
                    MainController.setStatus(rAddM.getErrorMsg(), Color.RED);
                    return;
                }
                Result<Boolean> rResetB = DiscoApplet.resetBonus();
                if (!rResetB.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, rResetB.getErrorMsg());
                    MainController.setStatus(rResetB.getErrorMsg(), Color.RED);
                    return;
                }
                getState();
                getConsumed();
                LogHelper.log(LogLevel.INFO, "Teilbezahlung erfolgreich, bitte nachzahlen!");
                MainController.setStatus("Teilbezahlung erfolgreich, bitte nachzahlen!", Color.PURPLE);
                return;
            }
            // Bezahlung -------------------------------------------------------
            if (consum.getData() < bonus.getData() + bonusPlus.getData()) {     // nur Bonuspinkte ausreichen
                consum.setData(consum.getData() - bonusPlus.getData());
                Result<Boolean> subBonus = DiscoApplet.subBonus(getNextInt(consum.getData()).toString());
                if (!subBonus.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, subBonus.getErrorMsg());
                    MainController.setStatus(subBonus.getErrorMsg(), Color.RED);
                    return;
                }
            } else {
                consum.setData(consum.getData() - (bonus.getData() + bonusPlus.getData()));

                Result<Boolean> subMoney = DiscoApplet.subMoney(String.format("%.2f", consum.getData()));
                if (!subMoney.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, subMoney.getErrorMsg());
                    MainController.setStatus(subMoney.getErrorMsg(), Color.RED);
                    return;
                }

                Result<Boolean> resetBonus = DiscoApplet.resetBonus();
                if (!resetBonus.isSuccess()) {
                    LogHelper.log(LogLevel.ERROR, resetBonus.getErrorMsg());
                    MainController.setStatus(resetBonus.getErrorMsg(), Color.RED);
                    return;
                }
            }

            byte[] aPaid = new byte[drinks.getData().length];                   // alle Drinks auf Bezahlt setzen
            for (int i = 0; i < drinks.getData().length; i++)
                aPaid[i] = 1;
            Result<Boolean> rPaid = DiscoApplet.setPaidDrinks(aPaid);
            if (!rPaid.isSuccess()) {
                LogHelper.log(LogLevel.ERROR, rPaid.getErrorMsg());
                MainController.setStatus(rPaid.getErrorMsg(), Color.RED);
                return;
            }

            getState();
            getConsumed();
            model.setDiscoIO(false);
        });

//        tpExit.setExpanded(false);
//        tpExit.setOnMouseEntered(e -> {
//            tpExit.setExpanded(true);
//            getConsumed();
//        });
//        tpExit.setOnMouseExited(e -> {
//            tpExit.setExpanded(false);
//        });
    }

    private Integer getNextInt(Double d) {
        int i = 0;
        d = Math.round(d * 100.0) / 100.0;                                      // aufrunden
        if (d / d.intValue() != 0)
            i = d.intValue() + 1;
        else
            i = d.intValue();
        return i;
    }
}
