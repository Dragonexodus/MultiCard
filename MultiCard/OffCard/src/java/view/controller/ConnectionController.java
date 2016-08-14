package view.controller;

import application.card.JavaCard;
import application.crypto.KeyFileGenerator;
import application.crypto.RSACryptoHelper;
import application.applet.CryptoApplet;
import helper.LogHelper;
import helper.LogLevel;
import helper.KeyPath;
import helper.Result;
import helper.SuccessResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import view.model.ConnectionModel;

import java.nio.file.Files;

public class ConnectionController {
    public Button butConnect, butGenKeys, butInitSC;
    public Label lblTerminalKeyStatus, lblCardKeyStatus;

    public static ConnectionModel model;

    public ConnectionController() {
        model = new ConnectionModel();
    }

    public static void setConnectionStatus(boolean b) {
        model.setIsConnectionEstablished(b);
    }

    @FXML
    public void initialize() {
        butConnect.addEventHandler(ActionEvent.ACTION, e -> connectToCardAsync());
        butConnect.disableProperty().bind(model.isConnectionEstablishedProperty());

        butGenKeys.addEventHandler(ActionEvent.ACTION, e -> generateRsaKeys());
        butGenKeys.disableProperty().bind(model.isTerminalKeyFileAvailableProperty().and(model.isTerminalKeyFileAvailableProperty()));

        butInitSC.addEventHandler(ActionEvent.ACTION, e -> setupCardKeys());
        butInitSC.disableProperty().bind(model.isConnectionEstablishedProperty());

        lblTerminalKeyStatus.textProperty().bind(model.terminalKeyStatusProperty());
        lblTerminalKeyStatus.textFillProperty().bind(model.terminalKeyStatusColorProperty());
        lblCardKeyStatus.textProperty().bind(model.cardKeyStatusProperty());
        lblCardKeyStatus.textFillProperty().bind(model.cardKeyStatusColorProperty());

        // prüft, ob die keys da sind
        Result<Boolean> checkRsaKeyFilesResult = checkRsaKeyFiles();
        if (!checkRsaKeyFilesResult.isSuccess() || !checkRsaKeyFilesResult.getData()) {
            LogHelper.log(LogLevel.INFO, "Schlüsseldateien nicht vorhanden");
            MainController.setStatus("Schlüsseldateien nicht vorhanden", Color.ORANGE);
            return;
        }

        // cardKeyFile wird eingelesen und der Objekt initialisiert
        Result<Boolean> initTerminalCrypto = initTerminalCrypto();
        if (!initTerminalCrypto.isSuccess()) {
            return;
        }

        connectToSmartCard();
    }

    /**
     * Verbinden zu SC (asynchron)
     */
    private void connectToCardAsync() {
        new Thread(() -> connectToSmartCard()).start();
    }

    /**
     * Verbindungsaufbau mir der SC
     * PublicKeys werden mit der SC ausgetauscht
     */
    private void connectToSmartCard() {
        MainController.setConnectionStatus(false, "verbinden", Color.ORANGE);

        Result<Boolean> connectResult = JavaCard.getInstance().connect();
        if (!connectResult.isSuccess()) {
            MainController.setConnectionStatus(false, "nicht verbunden", Color.RED);
            LogHelper.log(LogLevel.WARNING, connectResult.getErrorMsg());
            MainController.setStatus(connectResult.getErrorMsg(), Color.ORANGE);
            return;
        }

        Result<Boolean> importCardPublicKeyResult = CryptoApplet.getPublicKeyFromCard();
        if (!importCardPublicKeyResult.isSuccess()) {
            LogHelper.log(LogLevel.WARNING, "publicCardKey-Fehler -> SC initialisieren" + importCardPublicKeyResult.getErrorMsg());
            MainController.setStatus("publicCardKey-Fehler -> SC initialisieren", Color.ORANGE);
            return;
        }
        MainController.setConnectionStatus(true, "verbunden", Color.GREEN);
    }

    /**
     * generiert keyFiles und initialisiert Terminal-Crypto
     */
    private Result<Boolean> generateRsaKeys() {
        if (!model.isTerminalKeyFileAvailable()) {
            Result<Boolean> generateResult = KeyFileGenerator.generateKeysToFile(KeyPath.TERMINAL_KEY_PATH);
            if (!generateResult.isSuccess())
                return generateResult;
            initTerminalCrypto();
        }

        if (!model.isCardKeyFileAvailable()) {
            Result<Boolean> generateResult = KeyFileGenerator.generateKeysToFile(KeyPath.CARD_KEY_PATH);
            if (!generateResult.isSuccess())
                return generateResult;
        }
        return new SuccessResult<>(checkRsaKeyFiles().getData());   // noch ein mal prüfen, ob die keys da sind
    }

    /**
     * Terminal-crypto wird initialisiert
     */
    private Result<Boolean> initTerminalCrypto() {
        Result<Boolean> setupTerminalKey = RSACryptoHelper.getInstance().importTerminalKeyFromFile();
        if (!setupTerminalKey.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, setupTerminalKey.getErrorMsg());
            MainController.setStatus(setupTerminalKey.getErrorMsg(), Color.RED);
        }
        return setupTerminalKey;
    }

    /**
     * Prüft ob die Schlüsseln existieren
     *
     * @return true wenn alles gefunden wurde
     */
    private Result<Boolean> checkRsaKeyFiles() {
        model.setIsTerminalKeyFileAvailable(Files.exists(KeyPath.TERMINAL_KEY_PATH));
        model.setIsCardKeyFileAvailable(Files.exists(KeyPath.CARD_KEY_PATH));

        return new SuccessResult<>(model.isCardKeyFileAvailable() && model.isTerminalKeyFileAvailable());
    }

    /**
     * Konfiguration von private- und public-keys beider SC
     * Austausch von public-key mit der SC
     */
    private void setupCardKeys() {
        Result<Boolean> result;
        result = CryptoApplet.loadAndSetCardKeys();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }

        result = CryptoApplet.setTerminalPublicKeyToCard();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }

        result = CryptoApplet.getPublicKeyFromCard();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        MainController.setConnectionStatus(true, "verbunden", Color.GREEN);
    }

    private void onCardInserted() {
        Result<Boolean> result = CryptoApplet.getPublicKeyFromCard();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMsg());
            MainController.setStatus(result.getErrorMsg(), Color.RED);
            return;
        }
        MainController.setConnectionStatus(true, "verbunden", Color.GREEN);
    }

    private void onCardRemoved() {
        MainController.setConnectionStatus(false, "nicht verbunden", Color.RED);
    }
}
