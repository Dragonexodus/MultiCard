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
    private static final int BONUS_BYTE_LENGTH = 1;

    public static Result<String> getBonus() {
        Result<byte[]> r1 = CommonApplet.sendValue(AppletName, CLA, INS_GET_BONUS);
        if (!r1.isSuccess())
            return new ErrorResult<>(r1.getErrorMsg());
        Result<Integer> r2 = ByteHelper.byteArrayToIntegerLsb(r1.getData());
        if (!r2.isSuccess())
            return new ErrorResult<>(r2.getErrorMsg());
        return new SuccessResult<>(r2.getData().toString());
    }

    public static Result<Boolean> addBonus(String s) {
        Result<byte[]> r = ByteHelper.intStringToByteArrayLsb(s, BONUS_BYTE_LENGTH);
        if (!r.isSuccess())
            return new ErrorResult<>(r.getErrorMsg());
        Result<byte[]> r2 = CommonApplet.sendValue(AppletName, CLA, INS_ADD_BONUS, r.getData());
        return !r2.isSuccess() ? new ErrorResult<>(r2.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<Boolean> subBonus(String s) {
        Result<byte[]> r = ByteHelper.intStringToByteArrayLsb(s, BONUS_BYTE_LENGTH);
        if (!r.isSuccess())
            return new ErrorResult<>(r.getErrorMsg());
        Result<byte[]> r2 = CommonApplet.sendValue(AppletName, CLA, INS_SUB_BONUS, r.getData());
        return !r2.isSuccess() ? new ErrorResult<>(r2.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<Boolean> resetBonus() {
        return CommonApplet.reset(AppletName, CLA, INS_RESET_BONUS);
    }

    public static Result<Boolean> addMoney(String s) {
        Result<byte[]> r = ByteHelper.doubleStringToByteArray(s);
        if (!r.isSuccess())
            return new ErrorResult<>(r.getErrorMsg());
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_ADD_MONEY, r.getData());
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());
        return new SuccessResult<>(true);
    }

    public static Result<Boolean> subMoney(String s) {
        Result<byte[]> r = ByteHelper.doubleStringToByteArray(s);
        if (!r.isSuccess())
            return new ErrorResult<>(r.getErrorMsg());
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SUB_MONEY, r.getData());
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
