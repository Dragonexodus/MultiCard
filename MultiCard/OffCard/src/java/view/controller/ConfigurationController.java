package view.controller;

import application.applet.DiscoApplet;
import application.applet.StudentApplet;
import helper.LogHelper;
import helper.LogLevel;
import helper.Result;
import helper.RoomHelper;
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
        LogHelper.log(LogLevel.INFO, "Student wurden erfolgreich konfiguriert");
        MainController.setStatus("Student wurden erfolgreich konfiguriert", Color.GREEN);
    }

    private void addMoney() {
        Result<Boolean> result = StudentApplet.addMoney(model.getMoney());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        LogHelper.log(LogLevel.INFO, "Geld wurde eingezahlt");
        MainController.setStatus("Geld wurde eingezahlt", Color.GREEN);
    }

    private void resetMoney() {
        Result<Boolean> result = StudentApplet.resetMoney();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
        LogHelper.log(LogLevel.INFO, "Guthaben wurde zurückgesetzt");
        MainController.setStatus("Guthaben wurde zurückgesetzt", Color.GREEN);
    }

    private void addBonus(){
        Result<Boolean> result = DiscoApplet.addBonus(model.getBonus());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        LogHelper.log(LogLevel.INFO, "Bunuspunkte wurden gutgeschrieben");
        MainController.setStatus("Bunuspunkte wurden gutgeschrieben", Color.GREEN);
    }

    private void resetBonus() {
        Result<Boolean> result = DiscoApplet.resetBonus();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
        LogHelper.log(LogLevel.INFO, "Bonuspunkte wurden zurückgesetzt");
        MainController.setStatus("Bonuspunkte wurden zurückgesetzt", Color.GREEN);
    }

    private void getRoom() {
        Result<byte[]> r1 = StudentApplet.getRoom();
        if (!r1.isSuccess()) {
            LogHelper.log(LogLevel.INFO, r1.getErrorMsg());
            MainController.setStatus(r1.getErrorMsg(), Color.RED);
            return;
        }
        Result<String> r2 = RoomHelper.getRoomStringFromByteArray(r1.getData());
        if (!r2.isSuccess()) {
            LogHelper.log(LogLevel.INFO, r2.getErrorMsg());
            MainController.setStatus(r2.getErrorMsg(), Color.RED);
            return;
        }
        model.setRoom(r2.getData());
    }

    private void setRoom() {
        Result<byte[]> r1 = RoomHelper.getRoomByteArrayFromString(model.getRoom());
        if (!r1.isSuccess()) {
            LogHelper.log(LogLevel.INFO, r1.getErrorMsg());
            MainController.setStatus(r1.getErrorMsg(), Color.PURPLE);
            return;
        }
        Result<Boolean> r2 = StudentApplet.setRoom(r1.getData());
        if (!r2.isSuccess()) {
            LogHelper.log(LogLevel.INFO, r2.getErrorMsg());
            MainController.setStatus(r2.getErrorMsg(), Color.RED);
        }
    }

    private void initBindings() {
        butSetIdentification.addEventHandler(ActionEvent.ACTION, e -> setStudent());
        butAddMoney.addEventHandler(ActionEvent.ACTION, e -> addMoney());
        butResetMoney.addEventHandler(ActionEvent.ACTION, e -> resetMoney());

        butAddBonus.addEventHandler(ActionEvent.ACTION, e -> addBonus());
        butResetBonus.addEventHandler(ActionEvent.ACTION, e -> resetBonus());

        butGetRoom.addEventHandler(ActionEvent.ACTION, e -> getRoom());
        butSetRoom.addEventHandler(ActionEvent.ACTION, e -> setRoom());
        taRoom.textProperty().bindBidirectional(model.roomProperty());

        tfName.textProperty().bindBidirectional(model.nameProperty());
        tfMatrikel.textProperty().bindBidirectional(model.matrikelProperty());
        tfMoney.textProperty().bindBidirectional(model.moneyProperty());
        tfBonus.textProperty().bindBidirectional(model.bonusProperty());
    }
}
