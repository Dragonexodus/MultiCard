package application.card;

import application.crypto.RSACryptoHelper;
import helper.LogHelper;
import helper.LogLevel;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

public class JavaCardHelper {
    public static Result<Boolean> selectApplet(String appletId) {
        Cmd command = ApduHelper.getSelectCommand(appletId);
        Result<byte[]> selectResult = JavaCard.getInstance().sendCommand(command);
        if (!selectResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Applet: %s kann nicht ausgewählt werden", appletId);
            return new ErrorResult<>(selectResult.getErrorMsg());
        }
        return new SuccessResult<>(true);
    }

    public static Result<byte[]> sendCommand(byte cla, byte ins, byte[] content, byte answerLength) {
        Result<byte[]> encryptedMessage = RSACryptoHelper.getInstance().encrypt(content);
        if (!encryptedMessage.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Verschlüsseln fehlgeschlagen");
            return new ErrorResult<>(encryptedMessage.getErrorMsg());
        }

        Cmd command = ApduHelper.getCommand(cla, ins, encryptedMessage.getData(), answerLength);
        Result<byte[]> commandResult = JavaCard.getInstance().sendCommand(command);
        if (!commandResult.isSuccess() || commandResult.getData().length < 1) {
            return commandResult;
        }

        Result<byte[]> decryptedMessage = RSACryptoHelper.getInstance().decrypt(commandResult.getData());
        if (!decryptedMessage.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Entschlüsseln fehlgeschlagen");
            return new ErrorResult<>(decryptedMessage.getErrorMsg());
        }
        return decryptedMessage;
    }

    public static Result<byte[]> sendCmdWithoutEncryption(byte cla, byte ins, byte[] content, byte answerLength) {
        Cmd command = ApduHelper.getCommand(cla, ins, content, answerLength);
        return JavaCard.getInstance().sendCommand(command);
    }

    public static Result<byte[]> sendCmdWithoutEncryption(byte cla, byte ins, byte[] content) {
        return sendCmdWithoutEncryption(cla, ins, content, (byte) 0x00);
    }

    public static Result<byte[]> sendCmdWithoutEncryption(byte cla, byte ins, byte answerLength) {
        return sendCmdWithoutEncryption(cla, ins, new byte[0], answerLength);
    }

    public static Result<byte[]> sendCmdWithoutEncryption(byte cla, byte ins) {
        return sendCmdWithoutEncryption(cla, ins, new byte[0], (byte) 0x00);
    }
}
