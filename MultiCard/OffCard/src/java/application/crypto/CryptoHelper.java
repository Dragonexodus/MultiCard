package application.crypto;

import helper.LogHelper;
import helper.LogLevel;
import helper.ErrorResult;
import helper.Result;
import helper.SuccessResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

public class CryptoHelper {
    public static Result<ImportedKeys> readKeysFromFile(Path filePath) {
        if (!Files.exists(filePath)) {
            LogHelper.log(LogLevel.FAILURE, "CryptoHelper.readKeysFromFile(): Lesen von %s fehlgeschlagen. Dateiexistiert nicht.", filePath);
            return new ErrorResult<>("CryptoHelper.readKeysFromFile(): Lesen von %s fehlgeschlagen. Dateiexistiert nicht.", filePath);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath.toString()));
            BigInteger privateMod = CryptoHelper.readLineAsBigInteger(br);
            BigInteger privateExp = CryptoHelper.readLineAsBigInteger(br);
            BigInteger publicMod = CryptoHelper.readLineAsBigInteger(br);
            BigInteger publicExp = CryptoHelper.readLineAsBigInteger(br);
            return new SuccessResult<>(new ImportedKeys(privateMod, privateExp, publicMod, publicExp));
        } catch (IOException e) {
            LogHelper.log(LogLevel.FAILURE, "CryptoHelper.readKeysFromFile(): Auslesen der Zeile aus der Datei %s fehlgeschlagen", filePath);
            return new ErrorResult<>("CryptoHelper.readKeysFromFile(): Auslesen der Zeile aus der Datei %s fehlgeschlagen", filePath);
        }
    }

    /**
     * Liest zeilenweise den Inhalt in BufferReader aus
     * Konvertiert das Ausgelesene in BigInteger
     *
     * @param br br von keyFile
     * @return Zeile als BigInteger
     * @throws IOException           geht etwas schief mit readline()
     * @throws NumberFormatException ausgelesene Zeile kann nicht in BigInteger konvertiert werden
     */
    public static BigInteger readLineAsBigInteger(BufferedReader br) throws IOException, NumberFormatException {
        String str = br.readLine();
        if (str == null)
            throw new IOException();
        return new BigInteger(str);
    }

    public static byte[] stripLeadingZero(byte[] value) {
        byte[] result = value;
        if (value[0] == 0) {
            result = new byte[value.length - 1];
            System.arraycopy(value, 1, result, 0, result.length);
        }
        return result;
    }

    public static byte[] addLeadingZero(byte[] value) {
        byte[] result = new byte[value.length + 1];
        result[0] = 0;
        System.arraycopy(value, 0, result, 1, value.length);
        return result;
    }
}
