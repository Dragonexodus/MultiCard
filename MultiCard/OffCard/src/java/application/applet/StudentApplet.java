package application.applet;

import helper.ByteHelper;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

public class StudentApplet {
    private static final String AppletName = "Student";

    private static final byte CLA = (byte) 0x20;
    private static final byte INS_GET_MONEY = (byte) 0x20;
    private static final byte INS_ADD_MONEY = (byte) 0x21;
    private static final byte INS_SUB_MONEY = (byte) 0x22;
    private static final byte INS_RESET_MONEY = (byte) 0x23;
    private static final byte INS_SET_NAME = (byte) 0x24;
    private static final byte INS_GET_NAME = (byte) 0x25;
    private static final byte INS_SET_MATRIKEL = (byte) 0x26;
    private static final byte INS_GET_MATRIKEL = (byte) 0x27;
    private static final byte INS_SET_ROOMS = (byte) 0x28;
    private static final byte INS_GET_ROOMS = (byte) 0x29;

    private static final int MATRIKEL_BYTE_LENGTH = 2;

    public static Result<Boolean> setName(String name) {
        if (name.equals(""))
            name = " ";
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_NAME, name.getBytes());
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<String> getName() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_NAME);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());
        return new SuccessResult<>(new String(result.getData()));
    }

    public static Result<Boolean> setMatrikel(String matrikel) {
        if (matrikel.equals(""))
            matrikel = "0";
        Result<byte[]> r = ByteHelper.intStringToByteArrayLsb(matrikel, MATRIKEL_BYTE_LENGTH);
        if (!r.isSuccess())
            return new ErrorResult<>(r.getErrorMsg());
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_MATRIKEL, r.getData());
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<String> getMatrikel() {
        Result<byte[]> r1 = CommonApplet.sendValue(AppletName, CLA, INS_GET_MATRIKEL);
        if (!r1.isSuccess())
            return new ErrorResult<>(r1.getErrorMsg());
        Result<Integer> r2 = ByteHelper.byteArrayToIntegerLsb(r1.getData());
        if (!r2.isSuccess())
            return new ErrorResult<>(r2.getErrorMsg());
        return new SuccessResult<>(r2.getData().toString());
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

    public static Result<Boolean> setRoom(byte[] a) {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_ROOMS, a);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<byte[]> getRoom() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_ROOMS);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(result.getData());
    }
}
