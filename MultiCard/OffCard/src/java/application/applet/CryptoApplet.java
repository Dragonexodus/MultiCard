package application.applet;

import application.card.JavaCardHelper;
import application.crypto.CryptoHelper;
import application.crypto.ImportedKeys;
import application.crypto.RSACryptoHelper;
import application.log.LogHelper;
import application.log.LogLevel;
import helper.ErrorResult;
import helper.KeyPath;
import helper.Result;
import helper.SuccessResult;

import java.math.BigInteger;

public class CryptoApplet {
    private static final String AppletName = "Cryptography";

    private static final byte CLA = (byte) 0x43;
    private static final byte INS_ImportCardPrivateMod = (byte) 0xF0;
    private static final byte INS_ImportCardPrivateExp = (byte) 0xF1;
    private static final byte INS_ImportCardPublicMod = (byte) 0xF2;
    private static final byte INS_ImportCardPublicExp = (byte) 0xF3;
    private static final byte INS_ExportCardPublicMod = (byte) 0xF4;
    private static final byte INS_ExportCardPublicExp = (byte) 0xF5;

    private static final byte INS_ImportTerminalPublicMod = (byte) 0xE0;
    private static final byte INS_ImportTerminalPublicExp = (byte) 0xE1;

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
            LogHelper.log(LogLevel.FAILURE, "Import of modulus from card failed.");
            return new ErrorResult<>(exportModResult.getErrorMessage());
        }

        Result<byte[]> exportExponentResult = JavaCardHelper.sendCommandWithoutEncryption(CLA, INS_ExportCardPublicExp, (byte) 0x03);
        if (!exportExponentResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Import of exponent from failed.");
            return new ErrorResult<>(exportExponentResult.getErrorMessage());
        }

        byte[] modulus = CryptoHelper.addLeadingZero(exportModResult.get());
        byte[] exponent = CryptoHelper.addLeadingZero(exportExponentResult.get());

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
            return new ErrorResult<>(readResult.getErrorMessage());

        Result<Boolean> exportToCartResult = setKeyToCard(
                CLA,
                readResult.get().getPrivateMod().toByteArray(),
                INS_ImportCardPrivateMod,
                readResult.get().getPrivateExp().toByteArray(),
                INS_ImportCardPrivateExp);
        if (!exportToCartResult.isSuccess())
            return exportToCartResult;

        return setKeyToCard(
                CLA,
                readResult.get().getPublicMod().toByteArray(),
                INS_ImportCardPublicMod,
                readResult.get().getPublicExp().toByteArray(),
                INS_ImportCardPublicExp);
    }

    private static Result<Boolean> setKeyToCard(byte cla, byte[] modulus, byte insMod, byte[] exponent, byte insExp) {
        byte[] mod = CryptoHelper.stripLeadingZero(modulus);

        Result<byte[]> importModResult = JavaCardHelper.sendCommandWithoutEncryption(cla, insMod, mod);
        if (!importModResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Mod ist fehlgeschlagen.");
            return new ErrorResult<>(importModResult.getErrorMessage());
        }

        byte[] exp = CryptoHelper.stripLeadingZero(exponent);

        Result<byte[]> importExpResult = JavaCardHelper.sendCommandWithoutEncryption(cla, insExp, exp);
        if (!importExpResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Exp ist fehlgeschlagen.");
            return new ErrorResult<>(importExpResult.getErrorMessage());
        }
        return new SuccessResult<>(true);
    }
}
