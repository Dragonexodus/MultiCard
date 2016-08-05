package presentation.controllers;

import application.hotelbuddy.BonusApplet;
import common.Result;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import presentation.model.BonusModel;

/**
 * Created by Patrick on 08.07.2015.
 */
public class BonusController {
    public Label pointsLabel;
    public Button getButton;

    private BonusModel model = new BonusModel();

    public BonusController() {
        this.model = new BonusModel();
    }

    @FXML
    public void initialize() {
        initializeBindings();
    }

    /**
     * Uses the BonusApplet to get Points
     */
    private void getIdentificationData() {
        Result<Short> pointsResult = BonusApplet.getAllBonus();
        if (pointsResult.isSuccess()) {
            this.model.setPoints(pointsResult.get());
        }
    }

    private void initializeBindings() {
        getButton.addEventHandler(ActionEvent.ACTION, e -> getIdentificationData());

        pointsLabel.textProperty().bind(Bindings.convert(this.model.pointsProperty()));
    }
}
