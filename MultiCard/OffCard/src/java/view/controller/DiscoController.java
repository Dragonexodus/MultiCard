package view.controller;

import application.applet.DiscoApplet;
import application.applet.StudentApplet;
import helper.ByteHelper;
import helper.LogHelper;
import helper.LogLevel;
import helper.Result;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import view.model.DiscoModel;
import view.model.Drinks;

public class DiscoController {

    private static DiscoController instance;
    public Label lblBonus, lblMoney;
    public TextField tfAddMoney;
    public Button butIn, butOut, butAddDrink, butAddMoney;
    public ChoiceBox cbDrink;
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
        if (m.isSuccess()) {
            model.setMoneyGet(m.getData());
        }
        Result<String> b = DiscoApplet.getBonus();
        if (b.isSuccess()) {
            model.setBonusGet(b.getData());
        }
    }

    private void addMoney() {
        Result<Boolean> result = StudentApplet.addMoney(model.getMoneyAdd());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        Result<String> moneyResult = StudentApplet.getMoney();
        if (moneyResult.isSuccess()) {
            model.setMoneyGet(moneyResult.getData());
        }
        LogHelper.log(LogLevel.INFO, "das Geld wurde eingezahlt");
        MainController.setStatus("das Geld wurde eingezahlt", Color.GREEN);
    }

    private void addDrink() {
        Result<byte[]> r1 = ByteHelper.intToByteArrayLsb(model.getDrink(), 1);
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
        Result<String> r3 = drinks.getDrinkString(model.getDrink());
        if (!r3.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, r3.getErrorMsg());
            MainController.setStatus(r3.getErrorMsg(), Color.RED);
            return;
        }
        LogHelper.log(LogLevel.INFO, "%s konsumiert :)", r3.getData());
        MainController.setStatus(r3.getData() + " konsumiert :)", Color.GREEN);
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
    }
}
