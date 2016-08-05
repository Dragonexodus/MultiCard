package application.hotelbuddy;

import common.ErrorResult;
import common.Result;
import common.SuccessResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Patrick on 07.07.2015.
 */
public class AccessApplet {
    private static final String APPLET_NAME = "Access";

    private static final byte CLA = (byte) 0x41;
    private static final byte INS_SET_RIGHT = (byte) 0xC1;
    private static final byte INS_GET_RIGHT = (byte) 0xC2;

    private static final byte ACCESS_GRANTED = (byte) 0x10;
    private static final byte ACCESS_DENIED = (byte) 0x00;

    private static final byte INS_RESET = (byte) 0xF0;

    private static final int ENTRY_LENGTH = 3;

    /**
     * Resets the access applet
     *
     * @return result of the operation
     */
    public static Result<Boolean> reset() {
        return CommonApplet.reset(APPLET_NAME, CLA, INS_RESET);
    }

    public static Result<Boolean> setAccess(HashMap<AccessRestrictedRoom, Boolean> accessRestriction) {
        byte[] accessBytes = new byte[accessRestriction.size() * ENTRY_LENGTH];

        int count = 0;
        for (Map.Entry<AccessRestrictedRoom, Boolean> entry : accessRestriction.entrySet()) {
            AccessRestrictedRoom key = entry.getKey();
            Boolean value = entry.getValue();

            String roomNumber = String.format("%04d", key.getValue());
            accessBytes[count] = (byte) Integer.parseInt(roomNumber.substring(0, 2));
            accessBytes[count + 1] = (byte) Integer.parseInt(roomNumber.substring(2, 4));
            accessBytes[count + 2] = value ? ACCESS_GRANTED : ACCESS_DENIED;

            count += ENTRY_LENGTH;
        }

        Result<byte[]> result = CommonApplet.sendValue(APPLET_NAME, CLA, INS_SET_RIGHT, accessBytes);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMessage()) : new SuccessResult<>(true);
    }

    public static Result<Boolean> checkRoom(AccessRestrictedRoom room) {
        byte[] roomByte = new byte[2];

        String roomNumber = String.format("%04d", room.getValue());
        roomByte[0] = (byte) Integer.parseInt(roomNumber.substring(0, 2));
        roomByte[1] = (byte) Integer.parseInt(roomNumber.substring(2, 4));

        Result<byte[]> result = CommonApplet.sendValue(APPLET_NAME, CLA, INS_GET_RIGHT, roomByte, CommonApplet.ANSWER_LENGTH);
        return result.isSuccess() && result.get()[0] == ACCESS_GRANTED ? new SuccessResult<>(true) : new ErrorResult<>("Access Denied");
    }
}
