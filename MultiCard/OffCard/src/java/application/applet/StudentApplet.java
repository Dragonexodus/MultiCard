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
        byte[] a = ByteHelper.intToByteArrayLsb(Integer.parseInt(matrikel), MATRIKEL_BYTE_LENGTH);
        if (a == null)
            return new ErrorResult<>("ungültige Matrikel-Eingabe");

        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_MATRIKEL, a);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<String> getMatrikel() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_MATRIKEL);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());

        String s = ByteHelper.byteArrayToIntegerLsb(result.getData()).toString();
        return new SuccessResult<>(new String(s));
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

    public static Result<Boolean> setRoom(byte[] a) {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_ROOMS, a);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<byte[]> getRoom() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_ROOMS);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(result.getData());
    }
}
