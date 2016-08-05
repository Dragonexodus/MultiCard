package application.applet;

import application.log.LogHelper;
import application.log.LogLevel;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

import java.time.LocalDate;

/**
 * Created by Patrick on 23.06.2015.
 */
public class IdentificationApplet {
    private static final String AppletName = "Identification";

    private static final byte CLA = (byte) 0x49;
    private static final byte INS_SET_NAME = (byte) 0xA0;
    private static final byte INS_GET_NAME = (byte) 0xA1;
    private static final byte INS_SET_BIRTHDAY = (byte) 0xB0;
    private static final byte INS_GET_BIRTHDAY = (byte) 0xB1;
    private static final byte INS_CHECK_AGE = (byte) 0xB2;
    private static final byte INS_SET_CARID = (byte) 0xC0;
    private static final byte INS_GET_CARID = (byte) 0xC1;
    private static final byte INS_SET_SAFEPIN = (byte) 0xD0;
    private static final byte INS_CHECK_SAFEPIN = (byte) 0xD1;

    private static final byte INS_RESET = (byte) 0xFF;

    public static final byte CARID_LENGTH = 0x08;   // 8
    public static final byte SAFEPIN_LENGTH = 0x04; // 8

    /**
     * Sends the given name to the card
     *
     * @param name name
     * @return result of the operation
     */
    public static Result<Boolean> setName(String name) {
        if (name.equals("")) {
            return new SuccessResult<>(true);
        }

        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_NAME, name.getBytes());
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMessage()) : new SuccessResult<>(true);
    }

    /**
     * receives the name from the card
     *
     * @return result of the operation
     */
    public static Result<String> getName() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_NAME);
        if (!result.isSuccess()) {
            return new ErrorResult<>(result.getErrorMessage());
        }

        return new SuccessResult<>(new String(result.get()));
    }

    /**
     * Sends the given carid to the card
     *
     * @param carId carId
     * @return result of the operation
     */
    public static Result<Boolean> setCarId(String carId) {
        if (carId.equals("")) {
            return new SuccessResult<>(true);
        }

        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_CARID, carId.getBytes());
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMessage()) : new SuccessResult<>(true);
    }

    /**
     * receives the carId from the card
     *
     * @return result of the operation
     */
    public static Result<String> getCarId() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_CARID);
        if (!result.isSuccess()) {
            return new ErrorResult<>(result.getErrorMessage());
        }

        return new SuccessResult<>(new String(result.get()));
    }

    /**
     * Sends the given carid to the card
     *
     * @param safePin safePin
     * @return result of the operation
     */
    public static Result<Boolean> setSafePin(String safePin) {
        if (safePin.equals("")) {
            return new SuccessResult<>(true);
        }

        if (safePin.length() < SAFEPIN_LENGTH || safePin.length() > SAFEPIN_LENGTH) {
            LogHelper.log(LogLevel.FAILURE, "Invalid SafePIN format");
            return new ErrorResult<>(String.format("Safe SafePIN needs to be %s digits long", SAFEPIN_LENGTH));
        }

        byte[] pin = ConvertSafePin(safePin);
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_SAFEPIN, pin);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMessage()) : new SuccessResult<>(true);
    }

    /**
     * sends the given safe pin to the card to check it
     *
     * @return result of the operation
     */
    public static Result<Boolean> checkSafePin(String safePin) {
        if (safePin.length() < SAFEPIN_LENGTH || safePin.length() > SAFEPIN_LENGTH) {
            LogHelper.log(LogLevel.FAILURE, "Invalid SafePIN format");
            return new ErrorResult<>(String.format("Safe SafePIN needs to be %s digits long", SAFEPIN_LENGTH));
        }

        byte[] pin = ConvertSafePin(safePin);
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_CHECK_SAFEPIN, pin);
        if (!result.isSuccess()) {
            return new ErrorResult<>(result.getErrorMessage());
        }

        if (result.get()[0] != 0x01) {
            LogHelper.log(LogLevel.FAILURE, "Wrong SafePIN");
            return new ErrorResult<>("Wrong SafePIN");
        }

        return new SuccessResult<>(true);
    }

    private static byte[] ConvertSafePin(String safePin) {
        byte[] pin = new byte[4];
        for (int i = 0; i < 4; i++) {
            pin[i] = (byte) Integer.parseInt(Character.toString(safePin.charAt(i)));
        }
        return pin;
    }

    /**
     * Sends the given date as birthday to the card
     *
     * @param date birthday
     * @return result of the operation
     */
    public static Result<Boolean> setBirthDay(LocalDate date) {
        if (date == null) {
            return new SuccessResult<>(true);
        }

        byte[] data = new byte[4];
        data[0] = (byte) date.getDayOfMonth();
        data[1] = (byte) date.getMonth().getValue();
        data[2] = (byte) (date.getYear() / 100);
        data[3] = (byte) (date.getYear() % 100);

        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_SET_BIRTHDAY, data);
        return !result.isSuccess() ? new ErrorResult<>(result.getErrorMessage()) : new SuccessResult<>(true);
    }

    /**
     * Recevies the birthday from the card
     *
     * @return result of the operation, date-string formatted as DD.MM.YYYY
     */
    public static Result<String> getBirthDay() {
        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_GET_BIRTHDAY);
        if (!result.isSuccess()) {
            return new ErrorResult<>(result.getErrorMessage());
        }

        int day = result.get()[0];
        int month = result.get()[1];
        int year = (result.get()[2] * 100) + result.get()[3];

        return new SuccessResult<>(String.format("%02d.%02d.%d", day, month, year));
    }

    /**
     * Sends the given date and age to the card for an age check
     *
     * @param date today
     * @param age  age
     * @return result of the operation
     */
    public static Result<Boolean> checkAge(LocalDate date, int age) {
        byte[] data = new byte[5];
        data[0] = (byte) date.getDayOfMonth();
        data[1] = (byte) date.getMonth().getValue();
        data[2] = (byte) (date.getYear() / 100);
        data[3] = (byte) (date.getYear() % 100);
        data[4] = (byte) age;

        Result<byte[]> result = CommonApplet.sendValue(AppletName, CLA, INS_CHECK_AGE, data);
        if (!result.isSuccess()) {
            return new ErrorResult<>(result.getErrorMessage());
        }

        if (result.get()[0] != 0x01) {
            LogHelper.log(LogLevel.FAILURE, "Age restriction not statisfied");
            return new ErrorResult<>("Age restriction not statisfied");
        }

        return new SuccessResult<>(true);
    }

    /**
     * Resets the identification applet
     *
     * @return result of the operation
     */
    public static Result<Boolean> reset() {
        return CommonApplet.reset(AppletName, CLA, INS_RESET);
    }
}
