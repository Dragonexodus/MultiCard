package application.applet;

import helper.ByteHelper;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

/**
 * Created by Patrick on 23.06.2015.
 */
public class StudentApplet {
    private static final String AppletName = "Student";

    private static final byte CLA = (byte) 0x20;
    private static final byte INS_GET_MONEY = (byte) 0x20;
    private static final byte INS_ADD_MONEY = (byte) 0x21;
    private static final byte INS_SUB_MONEY = (byte) 0x22;
    private static final byte INS_SET_NAME = (byte) 0x23;
    private static final byte INS_GET_NAME = (byte) 0x24;
    private static final byte INS_SET_MATRIKEL = (byte) 0x25;
    private static final byte INS_GET_MATRIKEL = (byte) 0x26;
    private static final byte INS_SET_ROOMS = (byte) 0x27;
    private static final byte INS_GET_ROOMS = (byte) 0x28;
    private static final byte INS_RESET_MONEY = (byte) 0x29;  //TODO

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

    public static Result<Boolean> setMatrikel(Integer matrikel) {
        byte[] a = ByteHelper.intToByteArray(matrikel, MATRIKEL_BYTE_LENGTH);
        if (a == null)
            return new ErrorResult<>("falsche Matrikel-Eingabe");

        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_MATRIKEL, a);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMsg()) : new SuccessResult<>(true);
    }

    public static Result<String> getMatrikel() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_MATRIKEL);
        if (!result.isSuccess())
            return new ErrorResult<>(result.getErrorMsg());

        return new SuccessResult<>(new String(ByteHelper.byteArrayToInteger(result.getData()).toString()));
    }

    private static byte[] ConvertSafePin(String safePin) {
        byte[] pin = new byte[4];
        for (int i = 0; i < 4; i++)
            pin[i] = (byte) Integer.parseInt(Character.toString(safePin.charAt(i)));
        return pin;
    }
}
