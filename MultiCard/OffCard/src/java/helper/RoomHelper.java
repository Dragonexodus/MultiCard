package helper;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class RoomHelper {

    private static int MAX_SHORT_VALUE = 65535;
    private static int ARRAY_LENGTH = 3;

    public static Result<String> getRoomStringFromByteArray(byte[] a) {
        if (a.length % ARRAY_LENGTH != 0)
            return new ErrorResult<>("Im byte[] fehlt die Information!");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < a.length / ARRAY_LENGTH; i++) {
            sb.append(getRoomStringOnEnum(a[i * ARRAY_LENGTH]));
            ByteBuffer bb = new ByteBuffer(2);
            bb.append(a[i * ARRAY_LENGTH + 1]);
            bb.append(a[i * ARRAY_LENGTH + 2]);
            sb.append(ByteHelper.byteArrayToIntegerLsb(bb.toArray()).getData());
            sb.append("\n");
        }
        return new SuccessResult<>(sb.toString());
    }

    public static Result<byte[]> getRoomByteArrayFromString(String s) {
        if (s.equals(""))
            return new ErrorResult<byte[]>("Keine Einträge enthalten!");

        String[] sa = s.split("\\n");

        ByteBuffer bb = new ByteBuffer(ARRAY_LENGTH);

        for (int i = 0; i < sa.length; i++) {
            int raumBezeichnungCount = 0;
            StringBuffer sb1 = new StringBuffer();
            for (int j = 0; j < sa[i].length(); j++) {                          // Raum-Buchstabe
                if (Character.isLetter(sa[i].charAt(j))) {
                    sb1.append(sa[i].charAt(j));
                    raumBezeichnungCount++;
                } else
                    break;
            }
            if (sb1.toString().equals(""))
                return new ErrorResult<byte[]>("Keine Raumbuchstabe (Zeile %d)", i + 1);
            if (getRoomByteOnString(sb1.toString()) == ROOM.ERROR.ordinal())
                return new ErrorResult<byte[]>("Falsche Raumbuchstabe (Zeile %d)", i + 1);
            bb.append(getRoomByteOnString(sb1.toString()));                     // Raum-Buchstabe hinzugefügt

            StringBuffer sb2 = new StringBuffer();
            for (int j = raumBezeichnungCount; j < sa[i].length(); j++) {       // Raum-Zahl
                if (Character.isDigit(sa[i].charAt(j)))
                    sb2.append(sa[i].charAt(j));
                else
                    return new ErrorResult<byte[]>("Ungültiges Zeichen in Raumbezeichnung (Zeile %d)", i + 1);
            }
            if (Integer.parseInt(sb2.toString()) > MAX_SHORT_VALUE)
                return new ErrorResult<byte[]>("Zu große Raumzahl (Zeile %d)", i + 1);
            Result<byte[]> a = ByteHelper.intStringToByteArrayLsb(sb2.toString(), 2);
            if (!a.isSuccess())
                return new ErrorResult<byte[]>(a.getErrorMsg());
            for (int j = 0; j < 2; j++)
                bb.append(a.getData()[j]);
        }
        bb.trimToSize();
        return new SuccessResult<>(bb.toArray());
    }

    public static String getRoomStringOnEnum(int b) {
        switch (ROOM.values()[b]) {
            case F:
                return ROOM.F.toString();
            case G:
                return ROOM.G.toString();
            case GU:
                return ROOM.GU.toString();
            case LI:
                return ROOM.LI.toString();
            case Z:
                return ROOM.Z.toString();
            default:
                return ROOM.ERROR.toString();
        }
    }

    public static byte getRoomByteOnString(String s) {
        if (s.toUpperCase().equals(ROOM.F.toString()))
            return (byte) ROOM.F.ordinal();
        else if (s.toUpperCase().equals(ROOM.G.toString()))
            return (byte) ROOM.G.ordinal();
        else if (s.toUpperCase().equals(ROOM.GU.toString()))
            return (byte) ROOM.GU.ordinal();
        else if (s.toUpperCase().equals(ROOM.LI.toString()))
            return (byte) ROOM.LI.ordinal();
        else if (s.toUpperCase().equals(ROOM.Z.toString()))
            return (byte) ROOM.Z.ordinal();
        return (byte) ROOM.ERROR.ordinal();
    }

    public enum ROOM {
        F,
        G,
        GU,
        LI,
        Z,
        ERROR
    }
}
