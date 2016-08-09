package view.controller;

import application.applet.DiscoApplet;
import application.applet.StudentApplet;
import helper.LogHelper;
import helper.LogLevel;
import helper.Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import view.model.DiscoModel;

public class DiscoController {

    private static DiscoController instance;
    public Label lblGetMoney, lblBonus;
    public TextField tfAddMoney;
    public Button butIn, butOut, butTrink, butAddMoney;
    private DiscoModel model;

    public DiscoController() {
        instance = this;
        model = new DiscoModel();
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

    public void getState(){
        Result<String> moneyResult = StudentApplet.getMoney();
        if (moneyResult.isSuccess()) {
            model.setMoneyGet(moneyResult.getData());
        }
        Result<String> bonusResult = DiscoApplet.getBonus();
        if (bonusResult.isSuccess()) {
            model.setMoneyGet(bonusResult.getData());
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

    private void initBindings() {
        lblGetMoney.textProperty().bind(model.moneyGetProperty());
        tfAddMoney.textProperty().bindBidirectional(model.moneyAddProperty());
        butAddMoney.addEventHandler(ActionEvent.ACTION, e -> addMoney());
    }
}
