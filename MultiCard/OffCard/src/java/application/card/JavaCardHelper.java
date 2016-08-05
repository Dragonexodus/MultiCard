package application.card;

import application.crypto.RSACryptoHelper;
import application.log.LogHelper;
import application.log.LogLevel;
import common.ErrorResult;
import common.Result;
import common.SuccessResult;

public class JavaCardHelper {
    public static Result<Boolean> selectApplet(String appletId) {
        McCmd command = ApduHelper.getSelectCommand(appletId);
        Result<byte[]> selectResult = JavaCard.current().sendCommand(command);

        if (!selectResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Could not select applet: %s", appletId);
            return new ErrorResult<>(selectResult.getErrorMessage());
        }

        return new SuccessResult<>(true);
    }

    public static Result<byte[]> sendCommand(byte cla, byte ins, byte[] content, byte answerLength) {
        Result<byte[]> encryptedMessage = RSACryptoHelper.current().encrypt(content);
        if (!encryptedMessage.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Encryption failed");
            return new ErrorResult<>(encryptedMessage.getErrorMessage());
        }

        McCmd command = ApduHelper.getCommand(cla, ins, encryptedMessage.get(), answerLength);
        Result<byte[]> commandResult = JavaCard.current().sendCommand(command);
        if (!commandResult.isSuccess() || commandResult.get().length < 1) {
            return commandResult;
        }

        Result<byte[]> decryptedMessage = RSACryptoHelper.current().decrypt(commandResult.get());
        if (!decryptedMessage.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Decryption failed");
            return new ErrorResult<>(decryptedMessage.getErrorMessage());
        }

        return decryptedMessage;
    }

    public static Result<byte[]> sendCommandWithoutEncryption(byte cla, byte ins, byte[] content, byte answerLength) {
        McCmd command = ApduHelper.getCommand(cla, ins, content, answerLength);
        return JavaCard.current().sendCommand(command);
    }

    public static Result<byte[]> sendCommandWithoutEncryption(byte cla, byte ins, byte[] content) {
        return sendCommandWithoutEncryption(cla, ins, content, (byte) 0x00);
    }

    public static Result<byte[]> sendCommandWithoutEncryption(byte cla, byte ins, byte answerLength) {
        return sendCommandWithoutEncryption(cla, ins, new byte[0], answerLength);
    }

    public static Result<byte[]> sendCommandWithoutEncryption(byte cla, byte ins) {
        return sendCommandWithoutEncryption(cla, ins, new byte[0], (byte) 0x00);
    }
}
