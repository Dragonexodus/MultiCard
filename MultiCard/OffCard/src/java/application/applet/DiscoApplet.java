package application.applet;

import helper.ByteHelper;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

public class DiscoApplet {
    private static final String AppletName = "Disco";

    private static final byte CLA = (byte) 0x30;
    private static final byte INS_GET_BONUS = (byte) 0x30;
    private static final byte INS_ADD_BONUS = (byte) 0x31;
    private static final byte INS_SUB_BONUS = (byte) 0x32;
    private static final byte INS_RESET_BONUS = (byte) 0x33;
    private static final byte INS_GET_MONEY = (byte) 0x20;
    private static final byte INS_ADD_MONEY = (byte) 0x21;
    private static final byte INS_SUB_MONEY = (byte) 0x22;
    private static final byte INS_RESET_MONEY = (byte) 0x23;
    private static final byte INS_GET_DRINKS = (byte) 0x34;
    private static final byte INS_ADD_DRINKS = (byte) 0x35;
    private static final byte INS_SET_PAID_DRINKS = (byte) 0x36;

    private static final int MATRIKEL_BYTE_LENGTH = 2;

    public static Result<String> getBonus() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_MONEY);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());

        String s = ByteHelper.byteArrayToIntegerLsb(result.getData()).toString();
        return new SuccessResult<>(s);
    }

    public static Result<Boolean> addMoney(String s) {
        byte[] a = ByteHelper.doubleStringToByteArray(s);
        if (s == null)
            return new ErrorResult<>("Fehler in der Eingabe des Betrags!");
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_ADD_MONEY, a);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());

        return new SuccessResult<>(true);
    }

    public static Result<Boolean> subMoney(String s) {
        byte[] a = ByteHelper.doubleStringToByteArray(s);
        if (s == null)
            return new ErrorResult<>("Fehler in der Eingabe des Betrags!");
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SUB_MONEY, a);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());

        return new SuccessResult<>(true);
    }

    public static Result<String> getMoney() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_MONEY);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());

        return new SuccessResult<>(new String(ByteHelper.byteArrayMoneyToString(result.getData())));
    }

    public static Result<Boolean> resetMoney() {
        return CommonApplet.reset(AppletName, CLA, INS_RESET_MONEY);
    }
}
