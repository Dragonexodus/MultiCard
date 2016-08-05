package helper;

/**
 * Created by Patrick on 23.06.2015.
 */
public class ByteHelper {
    public static String ToHexString(byte[] b) {
        final StringBuilder builder = new StringBuilder();
        for (byte by : b) {
            builder.append(String.format("%02x ", by));
        }
        return builder.toString();
    }
}
