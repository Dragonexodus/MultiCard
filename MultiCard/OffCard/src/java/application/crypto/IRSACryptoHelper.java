package application.crypto;

import helper.Result;

import java.math.BigInteger;

/**
 * Created by Patrick on 23.06.2015.
 */
public interface IRSACryptoHelper {
    Result<Boolean> importTerminalKeyFromFile();

    void setCardPublicKey(BigInteger modulus, BigInteger exponent);

    Result<byte[]> encrypt(byte[] message);

    Result<byte[]> decrypt(byte[] message);

    byte[] getPublicMod();

    byte[] getPublicExp();
}
