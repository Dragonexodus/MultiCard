package application.crypto;

import helper.LogHelper;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class KeyFileGenerator {

    private static int RSA_LENGTH = 512;

    public static Result<Boolean> generateKeysToFile(Path filePath) {
        try {
            RSAPrivateKey privateKey;
            RSAPublicKey publicKey;

            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(RSA_LENGTH);
            final KeyPair key = keyGen.generateKeyPair();

            privateKey = (RSAPrivateKey) key.getPrivate();
            publicKey = (RSAPublicKey) key.getPublic();

            PrintWriter writer = new PrintWriter(filePath.toString());
            writer.println(privateKey.getModulus());
            writer.println(privateKey.getPrivateExponent());
            writer.println(publicKey.getModulus());
            writer.println(publicKey.getPublicExponent());
            writer.close();
        } catch (Exception e) {
            LogHelper.log(e);
            return new ErrorResult<>("keyFile %s wurde nicht generiert", filePath.toString());
        }

        return new SuccessResult<>(true);
    }
}
