package application.applet;

import application.card.JavaCardHelper;
import helper.LogHelper;
import helper.LogLevel;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

/**
 * Created by Patrick on 07.07.2015.
 */
public final class CommonApplet {
    public static final byte ANSWER_LENGTH = (byte) 0x80;

    /**
     * Resets the applet
     *
     * @param appletName appletName
     * @param cla        ClassByte
     * @param insReset   Reset InstructionByte
     * @return result of the operation
     */
    public static Result<Boolean> reset(String appletName, byte cla, byte insReset) {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(appletName);
        if (!selectResult.isSuccess()) {
            return selectResult;
        }

        Result<byte[]> result = JavaCardHelper.sendCommandWithoutEncryption(cla, insReset);
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.INFO, "Zurücksetzen fehlgeschlagen");
            return new ErrorResult<>(result.getErrorMsg());
        }
        return new SuccessResult<>(true);
    }

    /**
     * Sends the given data to the card with the given instruction
     *
     * @param appletName applet to select
     * @param cla        cla
     * @param ins        instruction
     * @param data       data to encrypt and send
     * @return result of the operation
     */
    public static Result<byte[]> sendValue(String appletName, byte cla, byte ins, byte[] data, byte answerLength) {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(appletName);
        if (!selectResult.isSuccess()) {
            return new ErrorResult<>(selectResult.getErrorMsg());
        }

        Result<byte[]> result = JavaCardHelper.sendCommand(cla, ins, data, answerLength);

        if (!result.isSuccess()) {
            return new ErrorResult<>(result.getErrorMsg());
        }

        return result;
    }

    /**
     * Sends the given data to the card with the given instruction
     *
     * @param appletName applet to select
     * @param cla        cla
     * @param ins        instruction
     * @param data       data to encrypt and send
     * @return result of the operation
     */
    public static Result<byte[]> sendValue(String appletName, byte cla, byte ins, byte[] data) {
        return sendValue(appletName, cla, ins, data, (byte) 0x00);
    }

    /**
     * Sends the given instruction to the card
     *
     * @param appletName applet to select
     * @param cla        cla
     * @param ins        instruction
     * @return result of the operation
     */
    public static Result<byte[]> sendValue(String appletName, byte cla, byte ins, byte answerLength) {
        return sendValue(appletName, cla, ins, new byte[0], answerLength);
    }

    public static Result<byte[]> sendValue(String appletName, byte cla, byte ins) {
        return sendValue(appletName, cla, ins, new byte[0]);
    }
}