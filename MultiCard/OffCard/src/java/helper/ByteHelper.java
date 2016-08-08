package helper;

import java.nio.ByteBuffer;

public class ByteHelper {

    /**
     * Konvertiert ein Integer in ein byte[] (MSB)
     * Beim Fehler wird null zurückgegeben.
     *
     * @param val   Integer-Wert
     * @param bytes Anzahl der gewünschten Bytes (max 4)
     * @return byte[]
     */
    public static byte[] intToByteArray(Integer val, Integer bytes) {
        if (val < 0 || bytes < 0 || bytes > 4)
            return null;
        if (val > ((1 << (bytes * 8)) - 1)) {
            return null;
        }
        byte[] aTemp = ByteBuffer.allocate(4).putInt(val).array();
        byte[] a = new byte[bytes];
        int indexA = 0;
        for (int i = 4 - 1; i >= 4 - bytes; i--)
            a[indexA++] = aTemp[i];
        return a;
    }

    /**
     * Konvertiert byte[] in Integer (nur ganze Zahlen)
     *
     * @param a byte[]
     * @return Integer
     */
    public static Integer byteArrayToInteger(byte[] a) {
        if (a == null)
            return -1;
        byte[] aNew = new byte[4];
        int indexA = 0;
        for (int i = 3; i >= 0; i--)
            if (indexA < a.length)
                aNew[i] = a[indexA++];
            else
                break;
        return ByteBuffer.wrap(aNew).getInt();
    }

//    public static String ToHexString(byte[] b) {
//        final StringBuilder builder = new StringBuilder();
//        for (byte by : b) {
//            builder.append(String.format("%02x ", by));
//        }
//        return builder.toString();
//    }
}
