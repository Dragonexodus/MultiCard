package multiCard;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class Student extends Applet{
	 // Java Card
    // Applet
    private static final byte STUDENT_CLA = 0x53;

    // Befehle
    private static final byte INS_DO_STH = (byte) 0xB0;

    // Andere Applets
    private static final byte[] CRYPTOGRAPHY_AID = {0x43, 0x72, 0x79, 0x70, 0x74, 0x6f, 0x67, 0x72, 0x61, 0x70, 0x68, 0x79};
    private static final byte CRYPTOGRAPHY_SECRET = 0x2A;

    // Konstanten und Offsets


    // Daten

    public Student()
    {
        register();
    }

    /**
     * Install Applet
     *
     * @param bArray
     * @param bOffset
     * @param bLength
     */
    public static void install(byte[] bArray, short bOffset, byte bLength)
    {
        new Student();
    }

    public void process(APDU apdu) throws ISOException
    {
        if (selectingApplet())
        {
            ISOException.throwIt(ISO7816.SW_NO_ERROR);
            return;
        }

        byte[] buffer = apdu.getBuffer();

        if (buffer[ISO7816.OFFSET_CLA] != STUDENT_CLA)
        {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            return;
        }

        switch (buffer[ISO7816.OFFSET_INS])
        {	
        	case INS_DO_STH:		
            	break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
}
