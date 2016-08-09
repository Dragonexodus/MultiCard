package view.controller;

import application.applet.StudentApplet;
import helper.LogHelper;
import helper.LogLevel;
import helper.Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import view.model.ConfigurationModel;

public class ConfigurationController {
    public Button butSetIdentification, butAddMoney, butResetMoney, butAddBonus, butResetBonus, butGetRoom, butSetRoom;
    public TextField tfName, tfMatrikel, tfMoney, tfBonus;
    public TextArea taRoom;

    private ConfigurationModel model;

    public ConfigurationController() {
        model = new ConfigurationModel();
    }

    @FXML
    public void initialize() {
        initBindings();
    }

    private void setStudent() {
        Result<Boolean> result;
        if (!model.getName().equals("")) {
            result = StudentApplet.setName(model.getName());
            if (!result.isSuccess()) {
                LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
                MainController.setStatus(result.getErrorMsg(), Color.RED);
                return;
            }
        }
        if (!model.getMatrikel().equals("")) {
            result = StudentApplet.setMatrikel(model.getMatrikel());
            if (!result.isSuccess()) {
                LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
                MainController.setStatus(result.getErrorMsg(), Color.RED);
                return;
            }
        }
        LogHelper.log(LogLevel.INFO, "Daten wurden erfolgreich übernommen");
        MainController.setStatus("Daten wurden erfolgreich übernommen", Color.GREEN);
    }

    private void addMoney() {
        Result<Boolean> result = StudentApplet.addMoney(model.getMoney());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
        LogHelper.log(LogLevel.INFO, "Daten wurden erfolgreich übernommen");
        MainController.setStatus("Daten wurden erfolgreich übernommen", Color.GREEN);
    }

    private void resetMoney() {
        Result<Boolean> result = StudentApplet.resetMoney();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
    }

    private void initBindings() {
        butSetIdentification.addEventHandler(ActionEvent.ACTION, e -> setStudent());
        butAddMoney.addEventHandler(ActionEvent.ACTION, e -> addMoney());
        butResetMoney.addEventHandler(ActionEvent.ACTION, e -> resetMoney());

        tfName.textProperty().bindBidirectional(model.nameProperty());
        tfMatrikel.textProperty().bindBidirectional(model.matrikelProperty());
        tfMoney.textProperty().bindBidirectional(model.moneyProperty());
        tfBonus.textProperty().bindBidirectional(model.bonusProperty());
    }
}
