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
     * Exportiert publicKey vom Terminal zu der SC
     * Schlüssel kommt von RSACryptoHelper
     *
     * @return result
     */
    public static Result<Boolean> setTerminalPublicKeyToCard() {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(AppletName);
        if (!selectResult.isSuccess())
            return selectResult;
        return setKeyToCard(CLA, RSACryptoHelper.getInstance().getPublicMod(), INS_ImportTerminalPublicMod, RSACryptoHelper.getInstance().getPublicExp(), INS_ImportTerminalPublicExp);
    }

    /**
     * Importiert publicKey von der SC in RSACryptoHelper
     *
     * @return result
     */
    public static Result<Boolean> getPublicKeyFromCard() {
        Result<Boolean> selectResult = JavaCardHelper.selectApplet(AppletName);
        if (!selectResult.isSuccess())
            return selectResult;

        Result<byte[]> exportModResult = JavaCardHelper.sendCmdWithoutEncryption(CLA, INS_ExportCardPublicMod, (byte) 0x40);
        if (!exportModResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Mod von der SC ist fehlgeschlagen");
            return new ErrorResult<>(exportModResult.getErrorMsg());
        }

        Result<byte[]> exportExponentResult = JavaCardHelper.sendCmdWithoutEncryption(CLA, INS_ExportCardPublicExp, (byte) 0x03);
        if (!exportExponentResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Exp von der SC ist fehlgeschlagen");
            return new ErrorResult<>(exportExponentResult.getErrorMsg());
        }

        byte[] modulus = CryptoHelper.addLeadingZero(exportModResult.getData());
        byte[] exponent = CryptoHelper.addLeadingZero(exportExponentResult.getData());

        RSACryptoHelper.getInstance().setCardPublicKey(new BigInteger(modulus), new BigInteger(exponent));

        return new SuccessResult<>(true);
    }

    /**
     * Lädt cardKeys vom CardKeyFilePath
     * und exportiert die Keys zu der SC
     *
     * @return result
     */
    public static Result<Boolean> loadAndSetCardKeys() {
        Result<ImportedKeys> r = CryptoHelper.readKeysFromFile(KeyPath.CARD_KEY_PATH);
        if (!r.isSuccess())
            return new ErrorResult<>(r.getErrorMsg());

        Result<Boolean> exportToCartResult = setKeyToCard(CLA, r.getData().getPrivateMod().toByteArray(), INS_ImportCardPrivateMod, r.getData().getPrivateExp().toByteArray(), INS_ImportCardPrivateExp);
        if (!exportToCartResult.isSuccess())
            return exportToCartResult;
        return setKeyToCard(CLA, r.getData().getPublicMod().toByteArray(), INS_ImportCardPublicMod, r.getData().getPublicExp().toByteArray(), INS_ImportCardPublicExp);
    }

    private static Result<Boolean> setKeyToCard(byte cla, byte[] modulus, byte insMod, byte[] exponent, byte insExp) {
        byte[] mod = CryptoHelper.stripLeadingZero(modulus);

        Result<byte[]> importModResult = JavaCardHelper.sendCmdWithoutEncryption(cla, insMod, mod);
        if (!importModResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Mod ist fehlgeschlagen.");
            return new ErrorResult<>(importModResult.getErrorMsg());
        }

        byte[] exp = CryptoHelper.stripLeadingZero(exponent);

        Result<byte[]> importExpResult = JavaCardHelper.sendCmdWithoutEncryption(cla, insExp, exp);
        if (!importExpResult.isSuccess()) {
            LogHelper.log(LogLevel.FAILURE, "Importieren von Exp ist fehlgeschlagen.");
            return new ErrorResult<>(importExpResult.getErrorMsg());
        }
        return new SuccessResult<>(true);
    }
}
