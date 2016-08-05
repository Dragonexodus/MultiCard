package view.controller;

import application.card.JavaCard;
import application.crypto.KeyFileGenerator;
import application.crypto.RSACryptoHelper;
import application.applet.CryptoApplet;
import application.log.LogHelper;
import application.log.LogLevel;
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
    public Button connectButton, generateKeyButton, initializeCardButton;
    public Label terminalKeyStatus, cardKeyStatus;

    public static ConnectionModel model;

    public ConnectionController() {
        model = new ConnectionModel();
    }

    public static void setConnectionStatus(boolean b){
        model.setIsConnectionEstablished(b);
    }

    @FXML
    public void initialize() {
        connectButton.addEventHandler(ActionEvent.ACTION, e -> connectToCardAsync(true));
        connectButton.disableProperty().bind(model.isConnectionEstablishedProperty());

        generateKeyButton.addEventHandler(ActionEvent.ACTION, e -> generateRsaKeys());
        generateKeyButton.disableProperty().bind(model.isTerminalKeyFileAvailableProperty().and(model.isTerminalKeyFileAvailableProperty()));

        initializeCardButton.addEventHandler(ActionEvent.ACTION, e -> setupCardKeys());
        initializeCardButton.disableProperty().bind(model.isConnectionEstablishedProperty());

        terminalKeyStatus.textProperty().bind(model.terminalKeyStatusProperty());
        terminalKeyStatus.textFillProperty().bind(model.terminalKeyStatusColorProperty());
        cardKeyStatus.textProperty().bind(model.cardKeyStatusProperty());
        cardKeyStatus.textFillProperty().bind(model.cardKeyStatusColorProperty());

        // prüft, ob die keys da sind
        Result<Boolean> checkRsaKeyFilesResult = checkRsaKeyFiles();
        if (!checkRsaKeyFilesResult.isSuccess() || !checkRsaKeyFilesResult.get()) {
            LogHelper.log(LogLevel.INFO, "Schlüsseldateien nicht vorhanden");
            MainController.setStatus("Schlüsseldateien nicht vorhanden", Color.ORANGE);
            return;
        }

        // cardKeyFile wird eingelesen und der Objekt initialisiert
        Result<Boolean> initializeTerminalCryptography = initTerminalCrypto();
        if (!initializeTerminalCryptography.isSuccess()) {
            return;
        }

        connectToSmartCard();

//        JavaCard.current().setOnCardInserted(() -> onCardInserted());
//        JavaCard.current().setOnCardRemoved(() -> onCardRemoved());
    }

    /**
     * Connects asynchronously to the smartcard.
     *
     * @param showStatus determines if error messages are shown in an alert window
     */
    private void connectToCardAsync(boolean showStatus) {
        new Thread(() -> connectToSmartCard(/*showStatus*/)).start();
    }

    /**
     * Verbindungsaufbau mir der SC
     * PublicKeys werden mit der SC ausgetauscht
     */
    private void connectToSmartCard() {                      //TODO: showMassage
        MainController.setConnectionStatus(false, "verbinden", Color.ORANGE);

        Result<Boolean> connectResult = JavaCard.current().connect();
        if (!connectResult.isSuccess()) {
            MainController.setConnectionStatus(false, "nicht verbunden", Color.RED);
            LogHelper.log(LogLevel.WARNING, connectResult.getErrorMessage());
            MainController.setStatus(connectResult.getErrorMessage(), Color.ORANGE);
            return;
        }

        Result<Boolean> importCardPublicKeyResult = CryptoApplet.getPublicKeyFromCard();
        if (!importCardPublicKeyResult.isSuccess()) {
            LogHelper.log(LogLevel.WARNING, "publicCardKey-Fehler -> SC initialisieren" + importCardPublicKeyResult.getErrorMessage());
            MainController.setStatus("publicCardKey-Fehler -> SC initialisieren", Color.ORANGE);
            return;
        }
        MainController.setConnectionStatus(true, "verbunden", Color.GREEN);
    }

    /**
     * generiert keyFiles und initialisiert Terminal-crypto
     *
     * @return result
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
        return new SuccessResult<>(checkRsaKeyFiles().get());   // noch ein mal prüfen, ob die keys da sind
    }

    /**
     * Terminal-crypto wird initialisiert
     *
     * @return result
     */
    private Result<Boolean> initTerminalCrypto() {
        Result<Boolean> setupTerminalKey = RSACryptoHelper.current().importTerminalKeyFromFile();
        if (!setupTerminalKey.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, setupTerminalKey.getErrorMessage());
            MainController.setStatus(setupTerminalKey.getErrorMessage(), Color.RED);
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
            LogHelper.log(LogLevel.ERROR, result.getErrorMessage());
            MainController.setStatus(result.getErrorMessage(), Color.RED);
            return;
        }

        result = CryptoApplet.setTerminalPublicKeyToCard();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMessage());
            MainController.setStatus(result.getErrorMessage(), Color.RED);
            return;
        }

        result = CryptoApplet.getPublicKeyFromCard();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMessage());
            MainController.setStatus(result.getErrorMessage(), Color.RED);
            return;
        }
        MainController.setConnectionStatus(true, "verbunden", Color.GREEN);
    }

//    private void setConnectionStatusString(boolean isConnectionEstablished, String statusText, Color color) {
//        Platform.runLater(() -> {
//            this.model.setIsConnectionEstablished(isConnectionEstablished);
//            this.model.setConnectionStatusString(statusText);
//            this.model.setConnectionStatusStringColor(color);
//        });
//    }

    private void onCardInserted() {
        Result<Boolean> result = CryptoApplet.getPublicKeyFromCard();
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.ERROR, result.getErrorMessage());
            MainController.setStatus(result.getErrorMessage(), Color.RED);
            return;
        }
        MainController.setConnectionStatus(true, "verbunden", Color.GREEN);
    }

    private void onCardRemoved() {
        MainController.setConnectionStatus(false, "nicht verbunden", Color.RED);
    }
}
