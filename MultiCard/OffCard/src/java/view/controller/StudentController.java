package view.controller;

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
import view.model.StudentModel;

public class StudentController {
    private static StudentController instance;
    public Label lblName, lblMatrikel, lblMoney;
    public TextField tfAddMoney, tfSubMoney;
    //TODO: Room
    public Button butGetStudent, butAddMoney, butSubMoney, butGetRoom;
    private StudentModel model;

    public StudentController() {
        instance = this;
        model = new StudentModel();
    }

    public static synchronized StudentController getInstance() {
        if (instance == null)
            instance = new StudentController();
        return instance;
    }

    @FXML
    public void initialize() {
        initBindings();
    }

    public void getStudent() {
        Result<String> nameResult = StudentApplet.getName();
        if (nameResult.isSuccess()) {
            model.setName(nameResult.getData());
        }

        Result<String> matrikelResult = StudentApplet.getMatrikel();
        if (matrikelResult.isSuccess()) {
            model.setMatrikel(matrikelResult.getData());
        }

        Result<String> moneyResult = StudentApplet.getMoney();
        if (moneyResult.isSuccess()) {
            model.setMoneyGet(moneyResult.getData());
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

    private void subMoney() {
        Result<Boolean> result = StudentApplet.subMoney(model.getMoneySub());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        Result<String> moneyResult = StudentApplet.getMoney();
        if (moneyResult.isSuccess()) {
            model.setMoneyGet(moneyResult.getData());
        }
        LogHelper.log(LogLevel.INFO, "das Geld wurde ausgezahlt");
        MainController.setStatus("das Geld wurde ausgezahlt", Color.GREEN);
    }

    private void initBindings() {
        butGetStudent.addEventHandler(ActionEvent.ACTION, e -> getStudent());
        butAddMoney.addEventHandler(ActionEvent.ACTION, e -> addMoney());
        butSubMoney.addEventHandler(ActionEvent.ACTION, e -> subMoney());
        lblName.textProperty().bind(model.nameProperty());
        lblMatrikel.textProperty().bind(model.matrikelProperty());
        lblMoney.textProperty().bind(model.moneyGetProperty());
        tfAddMoney.textProperty().bindBidirectional(model.moneyAddProperty());
        tfSubMoney.textProperty().bindBidirectional(model.moneySubProperty());
    }
}
