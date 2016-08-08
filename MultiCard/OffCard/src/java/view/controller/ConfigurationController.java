package view.controller;

import application.applet.AccessApplet;
import application.applet.BonusApplet;
import application.applet.IdentificationApplet;
import helper.LogHelper;
import helper.LogLevel;
import helper.Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;
import view.model.ConfigurationModel;
import view.widget.StringTextField;

public class ConfigurationController {
    public Button setIdentificationButton, resetIdentification, addPointsButton, resetPoints;
    public TextField nameTextField, pointsTextField;
    public StringTextField carIdTextField;
//    public NumericTextField safePinTextField;

    private ConfigurationModel model;

    public ConfigurationController() {
        model = new ConfigurationModel();
    }

    @FXML
    public void initialize() {
        initBindings();
    }

    private void setIdentificationData() {
        Result<Boolean> result = IdentificationApplet.setName(model.getName());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }

        result = IdentificationApplet.setCarId(model.getMatrikel());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }

        LogHelper.log(LogLevel.INFO, "Daten wurden erfolgreich übernommen");
        MainController.setStatus("Daten wurden erfolgreich übernommen", Color.GREEN);
    }

    private void addPoints() {
        Result<Boolean> result = BonusApplet.registerBonus((short) model.getPoints());
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
        LogHelper.log(LogLevel.INFO, "Daten wurden erfolgreich übernommen");
        MainController.setStatus("Daten wurden erfolgreich übernommen", Color.GREEN);
    }

    private void resetIdentification() {
        Result<Boolean> result = IdentificationApplet.reset();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
    }

    private void resetAccess() {
        Result<Boolean> result = AccessApplet.reset();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
    }

    private void resetPoints() {
        Result<Boolean> result = BonusApplet.reset();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
        }
    }

    private void initBindings() {
        setIdentificationButton.addEventHandler(ActionEvent.ACTION, e -> setIdentificationData());
        addPointsButton.addEventHandler(ActionEvent.ACTION, e -> addPoints());
        resetIdentification.addEventHandler(ActionEvent.ACTION, e -> resetIdentification());
        resetPoints.addEventHandler(ActionEvent.ACTION, e -> resetPoints());

        nameTextField.textProperty().bindBidirectional(this.model.nameProperty());
        carIdTextField.setMaxlength(IdentificationApplet.CARID_LENGTH);
        carIdTextField.textProperty().bindBidirectional(this.model.matrikelProperty());
        pointsTextField.textProperty().bindBidirectional(this.model.pointsProperty(), new NumberStringConverter());
    }
}
