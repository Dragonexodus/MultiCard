package view.controller;

import application.applet.StudentApplet;
import helper.Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import view.model.StudentModel;

public class StudentController {
    public Label lblName, lblMatrikel, lblMoney;
    public Button butGetStudent, butAddMoney, butPayMoney, butGetRoom;
    //TODO: Room

    private StudentModel model = new StudentModel();

    public StudentController() {
        model = new StudentModel();
    }

    @FXML
    public void initialize() {
        initBindings();
    }

    private void getStudent() {
        Result<String> nameResult = StudentApplet.getName();
        if (nameResult.isSuccess()) {
            model.setName(nameResult.getData());
        }

        Result<String> matrikelResult = StudentApplet.getMatrikel();
        if (matrikelResult.isSuccess()) {
            model.setMatrikel(matrikelResult.getData());
        }
    }

    private void initBindings() {
        butGetStudent.addEventHandler(ActionEvent.ACTION, e -> getStudent());
        lblName.textProperty().bind(model.nameProperty());
        lblMatrikel.textProperty().bind(model.matrikelProperty());
    }
}
