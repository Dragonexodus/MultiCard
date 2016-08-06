package view.controller;

import application.applet.BonusApplet;
import helper.Result;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import view.model.BonusModel;

public class BonusController {
    public Label pointsLabel;
    public Button getButton;

    private BonusModel model;

    public BonusController() {
        model = new BonusModel();
    }

    @FXML
    public void initialize() {
        getButton.addEventHandler(ActionEvent.ACTION, e -> getIdentificationData());
        pointsLabel.textProperty().bind(Bindings.convert(model.pointsProperty()));
    }

    /**
     * Uses the BonusApplet to getData Points
     */
    private void getIdentificationData() {
        Result<Short> pointsResult = BonusApplet.getAllBonus();
        if (pointsResult.isSuccess()) {
            this.model.setPoints(pointsResult.getData());
        }
    }
}
