package application.crypto;

import application.log.LogHelper;
import application.log.LogLevel;
import helper.ErrorResult;
import helper.KeyPath;
import helper.Result;
import helper.SuccessResult;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSACryptoHelper implements IRSACryptoHelper {

    private static int BLOCK_LENGTH = 64;

    private static IRSACryptoHelper instance;
    private Cipher rsaCipher;

    private RSAPrivateKey terminalPrivateKey;
    private RSAPublicKey terminalPublicKey;

    private Signature signature;

    private PublicKey cardPublicKey;

    private RSACryptoHelper() {
        try {
            rsaCipher = Cipher.getInstance("RSA");
            signature = Signature.getInstance("MD5withRSA");
        } catch (Exception ex) {
            LogHelper.log(ex);
            return;
        }

        LogHelper.log(LogLevel.INFO, "RSACryptoHelper initialisiert");
    }

    public static IRSACryptoHelper current() {
        return instance == null ? (instance = new RSACryptoHelper()) : instance;
    }

    public void setCardPublicKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            cardPublicKey = rsaKeyFactory.generatePublic(spec);
        } catch (Exception ex) {
            LogHelper.log(ex);
        }
    }

    /**
     * Lädt SC-keys von TERMINAL_KEY_PATH und setzt diese in RSACryptoHelper
     *
     * @return result
     */
    @Override
    public Result<Boolean> importTerminalKeyFromFile() {
        Result<ImportedKeys> readResult = CryptoHelper.readKeysFromFile(KeyPath.TERMINAL_KEY_PATH);
        if (!readResult.isSuccess())
            return new ErrorResult<>(readResult.getErrorMessage());

        try {
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");

            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(
                    readResult.get().getPrivateMod(),
                    readResult.get().getPrivateExp());
            terminalPrivateKey = (RSAPrivateKey) rsaKeyFactory.generatePrivate(keySpec);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(
                    readResult.get().getPublicMod(),
                    readResult.get().getPublicExp());
            terminalPublicKey = (RSAPublicKey) rsaKeyFactory.generatePublic(spec);
        } catch (Exception ex) {
            LogHelper.log(ex);
            return new ErrorResult<>("terminalKeys können nicht gesetzt werden");
        }
        return new SuccessResult<>(true);
    }

    @Override
    public Result<byte[]> encrypt(byte[] message) {
        try {
            signature.initSign(this.terminalPrivateKey);
            signature.update(message);
            byte[] signatureBytes = signature.sign();

            rsaCipher.init(Cipher.ENCRYPT_MODE, this.cardPublicKey);
            byte[] messageBytes = rsaCipher.doFinal(message);

            byte[] result = new byte[signatureBytes.length + messageBytes.length];
            System.arraycopy(messageBytes, 0, result, 0, messageBytes.length);
            System.arraycopy(signatureBytes, 0, result, messageBytes.length, signatureBytes.length);

            return new SuccessResult<>(result);
        } catch (Exception ex) {
            LogHelper.log(ex);
            return new ErrorResult<>("Verschlüssenl fehlgeschlagen");
        }
    }

    @Override
    public Result<byte[]> decrypt(byte[] message) {
        try {
            rsaCipher.init(Cipher.DECRYPT_MODE, this.terminalPrivateKey);
            byte[] messageBytes = rsaCipher.doFinal(message, 0, BLOCK_LENGTH);

            signature.initVerify(this.cardPublicKey);
            signature.update(messageBytes);
            if (!signature.verify(message, BLOCK_LENGTH, BLOCK_LENGTH))
                return new ErrorResult<>("ungültige Signatur");

            return new SuccessResult<>(messageBytes);
        } catch (Exception ex) {
            LogHelper.log(ex);
            return new ErrorResult<>("Entschlüsseln fehlgeschlagen");
        }
    }

    @Override
    public byte[] getPublicMod() {
        return terminalPublicKey.getModulus().toByteArray();
    }

    @Override
    public byte[] getPublicExp() {
        return terminalPublicKey.getPublicExponent().toByteArray();
    }
}
