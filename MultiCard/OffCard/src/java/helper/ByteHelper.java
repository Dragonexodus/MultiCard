package helper;

import java.nio.ByteBuffer;

public class ByteHelper {

    public static Result<byte[]> doubleStringToByteArray(String s) {
        if (s == "")
            s = "0";

        String[] sl;
        if (s.contains("."))
            sl = s.split("\\.");
        else if (s.contains(","))
            sl = s.split(",");
        else {                      // nur eine Zahl
            sl = new String[1];
            sl[0] = s;
        }

        for (int i = 0; i < sl.length; i++)
            for (int j = 0; j < sl[i].length(); j++)
                if (!Character.isDigit(sl[i].charAt(j)))
                    return new ErrorResult<byte[]>("Ungültiges Zeichen in der Eingabe!");

        if (sl.length == 2) {        // _,12 - Eingabe
            if (sl[0].equals(""))
                sl[0] = "0";
            if (sl[1].length() < 2 || sl[1].length() > 2)
                return new ErrorResult<byte[]>("Cent-Eingabe soll zweistellig sein!");
        }

        if (sl.length < 1 || sl.length > 2)
            return new ErrorResult<byte[]>("Zu viele Kommas eingegeben!");
        else if (sl.length >= 1 && Integer.parseInt(sl[0]) > 255)
            return new ErrorResult<byte[]>("Maximel zulässiger Betrag beträgt 255€!");
        else if (sl.length == 2 && Integer.parseInt(sl[1]) > 99)
            return new ErrorResult<byte[]>("Ungültige Cent-Eingabe!");

//        if (sl.length == 2)         // ,2 => ,20
//            if (Integer.parseInt(sl[1]) < 10) {
//                StringBuffer sb = new StringBuffer(sl[1]);
//                sb.append("0");
//                sl[1] = sb.toString();
//            }

        byte[] a = new byte[2];
        for (int i = 0; i < sl.length; i++) {
            a[i] = intToByte(Integer.parseInt(sl[i]));
        }
        return new SuccessResult<>(a);
    }

    public static byte intToByte(Integer i) {
        if (i > 255)
            return -1;
        if (i < 0)
            return 0;
        byte[] a = ByteBuffer.allocate(4).putInt(i).array();
        return a[3];
    }

    public static byte[] doubleToByteArray(Double d) {
        if (d > 255)
            return null;
        String s = d.toString();
        String[] sl = s.split("\\.");
        byte[] a = new byte[2];
        a[0] = intToByte(Integer.parseInt(sl[0]));
        a[1] = intToByte(Integer.parseInt(sl[1]));
        return a;
    }

    /**
     * Konvertiert ein Integer in ein byte[] (MSB)
     * Beim Fehler wird null zurückgegeben.
     *
     * @param val   Integer-Wert
     * @param bytes Anzahl der gewünschten Bytes (max 4)
     * @return byte[]
     */
    public static Result<byte[]> intToByteArrayMsb(Integer val, Integer bytes) {
        if (val < 0)
            return new ErrorResult<byte[]>("Negativer wert eingegeben!");
        else if (bytes < 0 || bytes > 4)
            return new ErrorResult<byte[]>("Gewünschte byte[]-Länge unzuläßig!");
        else if (val > ((1 << (bytes * 8)) - 1)) {
            return new ErrorResult<byte[]>("Integer-Wert zu groß!");
        }
        byte[] aTemp = ByteBuffer.allocate(4).putInt(val).array();
        byte[] a = new byte[bytes];
        int indexA = 0;
        for (int i = 4 - 1; i >= 4 - bytes; i--)
            a[indexA++] = aTemp[i];
        return new SuccessResult<>(a);
    }

    public static Result<byte[]> intToByteArrayLsb(Integer val, Integer bytes) {
        if (val < 0)
            return new ErrorResult<byte[]>("Negativer wert eingegeben!");
        else if (bytes < 0 || bytes > 4)
            return new ErrorResult<byte[]>("Gewünschte byte[]-Länge unzuläßig!");
        else if (val > ((1 << (bytes * 8)) - 1)) {
            return new ErrorResult<byte[]>("Integer-Wert zu groß!");
        }
        byte[] aTemp = ByteBuffer.allocate(4).putInt(val).array();
        byte[] a = new byte[bytes];
        int indexA = 3;
        for (int i = bytes - 1; i >= 0; i--)
            a[i] = aTemp[indexA--];
        return new SuccessResult<>(a);
    }

    public static Result<byte[]> intStringToByteArrayLsb(String s, Integer bytes) {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i)))
                return new ErrorResult<byte[]>("Ungültige Eingabe in der Position %d", i + 1);

        Integer val = Integer.parseInt(s);
        if (val < 0)
            return new ErrorResult<byte[]>("Negativer wert eingegeben!");
        else if (bytes < 0 || bytes > 4)
            return new ErrorResult<byte[]>("Gewünschte byte[]-Länge unzuläßig!");
        else if (val > ((1 << (bytes * 8)) - 1)) {
            return new ErrorResult<byte[]>("Integer-Wert zu groß!");
        }
        byte[] aTemp = ByteBuffer.allocate(4).putInt(val).array();
        byte[] a = new byte[bytes];
        int indexA = 3;
        for (int i = bytes - 1; i >= 0; i--)
            a[i] = aTemp[indexA--];
        return new SuccessResult<>(a);
    }

    /**
     * Konvertiert byte[] in Integer (nur ganze Zahlen)
     *
     * @param a byte[]
     * @return Integer
     */
    public static Integer byteArrayToIntegerMsb(byte[] a) {
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

    public static Result<Integer> byteArrayToIntegerLsb(byte[] a) {
        if (a == null)
            return new ErrorResult<Integer>("byte[] ist null!");
        byte[] aNew = new byte[4];
        int indexA = 3;
        for (int i = a.length - 1; i >= 0; i--)
            aNew[indexA--] = a[i];
        return new SuccessResult<>(ByteBuffer.wrap(aNew).getInt());
    }

    /**
     * Konvertiert byteArray (Geldbetrag, wo 1.Byte = Euro und der 2.Byte = Cent) der Länge 2 in eine entsprechende String-Representation
     *
     * @param a
     * @return
     */
    public static String byteArrayMoneyToString(byte[] a) {
        StringBuilder sb = new StringBuilder();
        int sOffset = 0;
        if (a[0] < 10)
            sOffset = 1;
        else if (a[0] < 100)
            sOffset = 2;
        else
            sOffset = 3;
        for (int i = 0; i < a.length; i++) {
            byte b = a[i];
            sb.append(byteToInteger(b).toString());
        }
        sb.insert(sOffset, ",");
        return sb.toString();
    }

    /**
     * Konvertiert byte zu Integer
     *
     * @param b
     * @return
     */
    public static Integer byteToInteger(byte b) {
        byte[] aNew = new byte[4];
        aNew[3] = b;
        return ByteBuffer.wrap(aNew).getInt();
    }

    private static byte[] stringToByteArray(String safePin) {
        byte[] pin = new byte[4];
        for (int i = 0; i < 4; i++)
            pin[i] = (byte) Integer.parseInt(Character.toString(safePin.charAt(i)));
        return pin;
    }

//    public static String ToHexString(byte[] b) {
//        final StringBuilder builder = new StringBuilder();
//        for (byte by : b) {
//            builder.append(String.format("%02x ", by));
//        }
//        return builder.toString();
//    }
}
