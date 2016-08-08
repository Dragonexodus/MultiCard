package application.applet;

import application.card.JavaCardHelper;
import helper.LogHelper;
import helper.LogLevel;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

public final class CommonApplet {
    public static final byte ANSWER_LENGTH = (byte) 0x80;

    /**
     * Setzt Applet zurück
     *
     * @param applet
     * @param cla
     * @param ins
     * @return
     */
    public static Result<Boolean> reset(String applet, byte cla, byte ins) {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(applet);
        if (!selectResult.isSuccess())
            return selectResult;

        Result<byte[]> result = JavaCardHelper.sendCommandWithoutEncryption(cla, ins);
        if (!result.isSuccess()) {
            LogHelper.log(LogLevel.INFO, "Zurücksetzen fehlgeschlagen");
            return new ErrorResult<>(result.getErrorMsg());
        }
        return new SuccessResult<>(true);
    }

    /**
     * Sendet APDU mit den Daten zur SC und erwartet eine Antwort zurück
     *
     * @param applet
     * @param cla
     * @param ins
     * @param data         Daten zur Verschlüsselung und zum Senden
     * @param answerLength
     * @return
     */
    public static Result<byte[]> sendValue(String applet, byte cla, byte ins, byte[] data, byte answerLength) {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(applet);
        if (!selectResult.isSuccess())
            return new ErrorResult<>(selectResult.getErrorMsg());

        Result<byte[]> result = JavaCardHelper.sendCommand(cla, ins, data, answerLength);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());
        return result;
    }

    /**
     * Sendet APDU mit den Daten zur SC
     *
     * @param applet
     * @param cla
     * @param ins
     * @param data   Daten zur Verschlüsselung und zum Senden
     * @return
     */
    public static Result<byte[]> sendValue(String applet, byte cla, byte ins, byte[] data) {
        return sendValue(applet, cla, ins, data, (byte) 0x00);
    }

    /**
     * Sendet APDU zur SC und erwartet eine Antwort zurück
     *
     * @param applet
     * @param cla
     * @param ins
     * @param answerLength
     * @return
     */
    public static Result<byte[]> sendValue(String applet, byte cla, byte ins, byte answerLength) {
        return sendValue(applet, cla, ins, new byte[0], answerLength);
    }

    public static Result<byte[]> sendValue(String applet, byte cla, byte ins) {
        return sendValue(applet, cla, ins, new byte[0]);
    }
}
