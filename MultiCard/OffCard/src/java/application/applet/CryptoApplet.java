package application.applet;

import application.card.JavaCardHelper;
import application.crypto.CryptoHelper;
import application.crypto.ImportedKeys;
import application.crypto.RSACryptoHelper;
import helper.LogHelper;
import helper.LogLevel;
import helper.ErrorResult;
import helper.KeyPath;
import helper.Result;
import helper.SuccessResult;

import java.math.BigInteger;

public class CryptoApplet {
    private static final String AppletName = "Crypto";

    private static final byte CLA = (byte) 0x10;
    private static final byte INS_ImportCardPrivateMod = (byte) 0x10;
    private static final byte INS_ImportCardPrivateExp = (byte) 0x11;
    private static final byte INS_ImportCardPublicMod = (byte) 0x12;
    private static final byte INS_ImportCardPublicExp = (byte) 0x13;
    private static final byte INS_ExportCardPublicMod = (byte) 0x14;
    private static final byte INS_ExportCardPublicExp = (byte) 0x15;
    private static final byte INS_ImportTerminalPublicMod = (byte) 0x16;
    private static final byte INS_ImportTerminalPublicExp = (byte) 0x17;

    /**
     * Exports the public key of the terminal to the card
     * The key is received from RSACryptoHelper
     *
     * @return result of the operation
     */
    public static Result<Boolean> setTerminalPublicKeyToCard() {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(AppletName);
        if (!selectResult.isSuccess())
            return selectResult;

        return setKeyToCard(
                CLA,
                RSACryptoHelper.current().getPublicMod(),
                INS_ImportTerminalPublicMod,
                RSACryptoHelper.current().getPublicExp(),
                INS_ImportTerminalPublicExp);
    }

    /**
     * Imports the public key from the card into the RSACryptoHelper
     *
     * @return result of the operation
     */
    public static Result<Boolean> getPublicKeyFromCard() {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(AppletName);
        if (!selectResult.isSuccess())
            return selectResult;

        Result<byte[]> exportModResult = JavaCardHelper.sendCommandWithoutEncryption(CLA, INS_ExportCardPublicMod, (byte) 0x40);
        if (!exportModResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Mod von der SC ist fehlgeschlagen");
            return new ErrorResult<>(exportModResult.getErrorMsg());
        }

        Result<byte[]> exportExponentResult = JavaCardHelper.sendCommandWithoutEncryption(CLA, INS_ExportCardPublicExp, (byte) 0x03);
        if (!exportExponentResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Exp von der SC ist fehlgeschlagen");
            return new ErrorResult<>(exportExponentResult.getErrorMsg());
        }

        byte[] modulus = CryptoHelper.addLeadingZero(exportModResult.getData());
        byte[] exponent = CryptoHelper.addLeadingZero(exportExponentResult.getData());

        RSACryptoHelper.current().setCardPublicKey(new BigInteger(modulus), new BigInteger(exponent));

        return new SuccessResult<>(true);
    }

    /**
     * loads the card keys from CardKeyFilePath
     * exports the keys to the card
     *
     * @return result of the operation
     */
    public static Result<Boolean> loadAndSetCardKeys() {
        Result<ImportedKeys> readResult = CryptoHelper.readKeysFromFile(KeyPath.CARD_KEY_PATH);
        if (!readResult.isSuccess())
            return new ErrorResult<>(readResult.getErrorMsg());

        Result<Boolean> exportToCartResult = setKeyToCard(
                CLA,
                readResult.getData().getPrivateMod().toByteArray(),
                INS_ImportCardPrivateMod,
                readResult.getData().getPrivateExp().toByteArray(),
                INS_ImportCardPrivateExp);
        if (!exportToCartResult.isSuccess())
            return exportToCartResult;

        return setKeyToCard(
                CLA,
                readResult.getData().getPublicMod().toByteArray(),
                INS_ImportCardPublicMod,
                readResult.getData().getPublicExp().toByteArray(),
                INS_ImportCardPublicExp);
    }

    private static Result<Boolean> setKeyToCard(byte cla, byte[] modulus, byte insMod, byte[] exponent, byte insExp) {
        byte[] mod = CryptoHelper.stripLeadingZero(modulus);

        Result<byte[]> importModResult = JavaCardHelper.sendCommandWithoutEncryption(cla, insMod, mod);
        if (!importModResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Mod ist fehlgeschlagen.");
            return new ErrorResult<>(importModResult.getErrorMsg());
        }

        byte[] exp = CryptoHelper.stripLeadingZero(exponent);

        Result<byte[]> importExpResult = JavaCardHelper.sendCommandWithoutEncryption(cla, insExp, exp);
        if (!importExpResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Exp ist fehlgeschlagen.");
            return new ErrorResult<>(importExpResult.getErrorMsg());
        }
        return new SuccessResult<>(true);
    }
}
