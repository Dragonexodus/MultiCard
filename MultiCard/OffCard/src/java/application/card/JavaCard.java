package application.card;

import helper.LogHelper;
import helper.LogLevel;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;
import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardRequest;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardTerminalException;
import opencard.core.terminal.ResponseAPDU;
import opencard.core.util.HexString;
import opencard.opt.util.PassThruCardService;

public class JavaCard implements IJavaCard, CTListener {
    private static IJavaCard instance = null;

    SmartCard card;

    private JavaCard() {
    }

    public static IJavaCard getInstance() {
        return instance == null ? (instance = new JavaCard()) : instance;
    }

    @Override
    public Result<Boolean> connect() {
        if (card != null)
            return new SuccessResult<>(true);
        try {
            SmartCard.start();
            EventGenerator.getGenerator().addCTListener(this);
        } catch (ClassNotFoundException e) {
            LogHelper.log(e);
            return new ErrorResult<>("Terminal ist nicht verfügbar. Prüfen Sie die opencard.properties-Datei!");
        } catch (Exception e) {
            LogHelper.log(e);
            return new ErrorResult<>("SC kann nicht gestartet werden!");
        }

        LogHelper.log(LogLevel.INFO, "Verbindungsaufbau zu SC");

        CardRequest cardRequest = new CardRequest(CardRequest.ANYCARD, null, null);
        cardRequest.setTimeout(1);

        try {
            card = SmartCard.waitForCard(cardRequest);
        } catch (Exception e) {
            LogHelper.log(e);
            return new ErrorResult<>("Keine SC gefunden!");
        }
        if (card == null) {
            LogHelper.log(LogLevel.WARNING, "Keine SC gefunden!");
            return new ErrorResult<>("Keine SC gefunden!");
        }
        LogHelper.log(LogLevel.INFO, "Verbindung zu SC aufgebaut");
        return new SuccessResult<>(true);
    }

    @Override
    public Result<byte[]> sendCommand(Cmd command) {                            //TODO: eigene Fehlern definieren & reset oder nicht
        if (card == null) {
            LogHelper.log(LogLevel.WARNING, "JavaCard.sendCmd(): Keine SC vorhanden");
            return new ErrorResult<>("JavaCard.sendCmd(): Keine SC vorhanden");
        }
        try {
            PassThruCardService passThru = (PassThruCardService) card.getCardService(PassThruCardService.class, true);

            LogHelper.log(LogLevel.INFO, "Senden: %s", command.toString());
            ResponseAPDU responseApdu = passThru.sendCommandAPDU(command);

            // allgemein -------------------------------------------------------
            String status = HexString.hexifyShort(responseApdu.sw1(), responseApdu.sw2());
            if (status.equals("6E00")) {
                LogHelper.log(LogLevel.FAILURE, "ClassByte %02x ist unbekannt", command.getCLA());
                return new ErrorResult<>("ClassByte %02x ist unbekannt", command.getCLA());
            } else if (status.equals("6D00")) {
                LogHelper.log(LogLevel.FAILURE, "Unbekanntes InstructionByte %02x", command.getINS());
                return new ErrorResult<>("Unbekanntes InstructionByte %02x", command.getINS());
            } else if (status.equals("6984")) {
                LogHelper.log(LogLevel.FAILURE, "Ungültige Daten, siehe Log!");
                return new ErrorResult<>("Ungültige Daten, siehe Log!");
                // Crypto ------------------------------------------------------
            } else if (status.equals("E010")) {
                LogHelper.log(LogLevel.FAILURE, "Error: import_card_private_mod");
                return new ErrorResult<>("Error: import_card_private_mod");
            } else if (status.equals("E011")) {
                LogHelper.log(LogLevel.FAILURE, "Error: import_card_private_exp");
                return new ErrorResult<>("Error: import_card_private_exp");
            } else if (status.equals("E012")) {
                LogHelper.log(LogLevel.FAILURE, "Error: import_card_public_mod");
                return new ErrorResult<>("Error: import_card_public_mod");
            } else if (status.equals("E013")) {
                LogHelper.log(LogLevel.FAILURE, "Error: import_card_public_exp");
                return new ErrorResult<>("Error: import_card_public_exp");
            } else if (status.equals("E014")) {
                LogHelper.log(LogLevel.FAILURE, "Error: export_card_public_mod");
                return new ErrorResult<>("Error: export_card_public_mod");
            } else if (status.equals("E015")) {
                LogHelper.log(LogLevel.FAILURE, "Error: export_card_public_exp");
                return new ErrorResult<>("Error: export_card_public_exp");
            } else if (status.equals("E016")) {
                LogHelper.log(LogLevel.FAILURE, "Error: import_terminal_public_mod");
                return new ErrorResult<>("Error: import_terminal_public_mod");
            } else if (status.equals("E017")) {
                LogHelper.log(LogLevel.FAILURE, "Error: import_terminal_public_exp");
                return new ErrorResult<>("Error: import_terminal_public_exp");
                // Student -----------------------------------------------------
            } else if (status.equals("E021")) {
                LogHelper.log(LogLevel.FAILURE, "Error: add_euro_overflow");
                return new ErrorResult<>("Error: add_euro_overflow");
            } else if (status.equals("E121")) {
                LogHelper.log(LogLevel.FAILURE, "Error: add_cent_overflow");
                return new ErrorResult<>("Error: add_cent_overflow");
            } else if (status.equals("E221")) {
                LogHelper.log(LogLevel.FAILURE, "Error: add_money_overflow");
                return new ErrorResult<>("Error: add_money_overflow");
            } else if (status.equals("E022")) {
                LogHelper.log(LogLevel.FAILURE, "Error: sub_euro_overflow");
                return new ErrorResult<>("Error: sub_euro_overflow");
            } else if (status.equals("E122")) {
                LogHelper.log(LogLevel.FAILURE, "Error: sub_cent_overflow");
                return new ErrorResult<>("Error: sub_cent_overflow");
            } else if (status.equals("E222")) {
                LogHelper.log(LogLevel.FAILURE, "Error: sub_insufficient_money");
                return new ErrorResult<>("Error: sub_insufficient_money");
            } else if (status.equals("E025")) {
                LogHelper.log(LogLevel.FAILURE, "Error: set_matrikel_negative");
                return new ErrorResult<>("Error: set_matrikel_negative");
            } else if (status.equals("E125")) {
                LogHelper.log(LogLevel.FAILURE, "Error: set_matrikel_overflow");
                return new ErrorResult<>("Error: set_matrikel_overflow");
                // Disco -------------------------------------------------------
            } else if (status.equals("E030")) {
                LogHelper.log(LogLevel.FAILURE, "Error: add_bonus_overflow");
                return new ErrorResult<>("Error: add_bonus_overflow");
            } else if (status.equals("E031")) {
                LogHelper.log(LogLevel.FAILURE, "Error: sub_bonus_overflow");
                return new ErrorResult<>("Error: sub_bonus_overflow");
            } else if (status.equals("E032")) {
                LogHelper.log(LogLevel.FAILURE, "Error: sub_insufficient_bonus");
                return new ErrorResult<>("Error: sub_insufficient_bonus");
            } else if (status.equals("E033")) {
                LogHelper.log(LogLevel.FAILURE, "Error: add_drink_had_to_much");
                return new ErrorResult<>("Error: add_drink_had_to_much");
                // other -------------------------------------------------------
            } else if (status.equals("6700")) {
                LogHelper.log(LogLevel.FAILURE, "Error: wrong_byte[]_length");
                return new ErrorResult<>("Error: wrong_byte[]_length");
            } else if (status.startsWith("61")) {
                System.out.println("################################################################");
                Cmd c = new Cmd((byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x80);
                LogHelper.log(LogLevel.INFO, "Sending %s", c.toString());
                ResponseAPDU response = passThru.sendCommandAPDU(c);
                LogHelper.log(LogLevel.INFO, "Cmd erfolgreich");
                byte[] data = response.data();
                return data == null ? new SuccessResult<>(new byte[0]) : new SuccessResult<>(data);
            } else if (!status.equals("9000")) {
                LogHelper.log(LogLevel.FAILURE, "Ungültiges Antwort: %s", status);
                return new ErrorResult<>("Ungültiges Antwort: %s", status);
            }
            LogHelper.log(LogLevel.INFO, "Cmd erfolgreich");
            byte[] data = responseApdu.data();
            return data == null ? new SuccessResult<>(new byte[0]) : new SuccessResult<>(data);
        } catch (Exception e) {
            LogHelper.log(e);
            return new ErrorResult<>(e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        LogHelper.log(LogLevel.INFO, "Beenden initialisiert");
        try {
            if (card != null) {
                card.close();
                card = null;
            }
            SmartCard.shutdown();
        } catch (CardTerminalException ex) {
            LogHelper.log(ex);
        }
    }

    @Override
    public void cardInserted(CardTerminalEvent cardTerminalEvent) throws CardTerminalException {
        LogHelper.log(LogLevel.INFO, "SC eingelegt");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        CardRequest cardRequest = new CardRequest(CardRequest.ANYCARD, null, null);
        cardRequest.setTimeout(5);
        card = SmartCard.getSmartCard(cardTerminalEvent, cardRequest);
    }

    @Override
    public void cardRemoved(CardTerminalEvent cardTerminalEvent) throws CardTerminalException {
        LogHelper.log(LogLevel.INFO, "SC entfernt");
    }

    //implemented
    public void setOnCardInserted(Action action) {
        System.out.println("CONNECTED");
    }

    //implemented
    public void setOnCardRemoved(Action action) {
        System.out.println("DISCONNECTED");
    }
}
