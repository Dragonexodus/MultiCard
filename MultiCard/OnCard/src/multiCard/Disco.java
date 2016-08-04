package multiCard;

import javacard.framework.AID;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;

public class Disco extends Applet{
	 // Java Card
    // Applet
    private static final byte DISCO_CLA = 0x44;

    // Befehle
    private static final byte INS_DO_STH = (byte) 0xB0;

    // Andere Applets
    private static final byte[] CRYPTOGRAPHY_AID = {0x43, 0x72, 0x79, 0x70, 0x74, 0x6f, 0x67, 0x72, 0x61, 0x70, 0x68, 0x79};
    private static final byte CRYPTOGRAPHY_SECRET = 0x2A;

    // Konstanten und Offsets


    // Daten

    public Disco()
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
        new Disco();
    }

    public void process(APDU apdu) throws ISOException
    {
        if (selectingApplet())
        {
            ISOException.throwIt(ISO7816.SW_NO_ERROR);
            return;
        }

        byte[] buffer = apdu.getBuffer();

        if (buffer[ISO7816.OFFSET_CLA] != DISCO_CLA)
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
    
    /**
     * Sendet den verschlüsselten Inhalt an die APDU
     * @param apdu   
     * @param content Zu verschlüsselender Inhalt
     * @param offset Startpos des zu verschlüsselenden Inhalts
     * @param length Länge des Inhalts 
     */
    private void send(APDU apdu, byte[] content, byte offset, byte length)
    {
        byte[] buffer = apdu.getBuffer();
        short len = encryptMessage(buffer, content, offset, length);

        apdu.setOutgoingAndSend((short) 0, len);
    }

    /**
     * Verschlüsselt die Nachricht, in dem es das Cryptography-Applet via applet-firewall nutzt.
     * @param buffer  Ziel Speicher. Resultat wird ab Offset 0 gespeichert.
     * @param message Nachricht.
     * @param offset  Offset wo Nachricht beginnt.
     * @param length  Länge der Nachricht.
     * @return length Länge der verschlüsselten Nachricht.
     */
    private short encryptMessage(byte[] buffer, byte[] message, byte offset, byte length)
    {
        AID cryptographyAid = JCSystem.lookupAID(CRYPTOGRAPHY_AID, (short) 0, (byte) CRYPTOGRAPHY_AID.length);
        ICryptography cryptoApp = (ICryptography) JCSystem.getAppletShareableInterfaceObject(cryptographyAid, CRYPTOGRAPHY_SECRET);

        return cryptoApp.encrypt(buffer, message, offset, length);
    }

    /**
     * Entschlüsselt die Nachricht an der Stelle 0 via dem Cryptographie-Applet von der Applet Firewall.
     * @param buffer Quell-und Zielspeicher, Nachricht startet bei ISO7816.OFFSET_CDATA und das Ergebnis beginnt ab Offset 0.
     * @return length der verschlüsselten Nachricht
     */
    private short decryptMessage(byte[] buffer)
    {
        AID cryptographyAid = JCSystem.lookupAID(CRYPTOGRAPHY_AID, (short) 0, (byte) CRYPTOGRAPHY_AID.length);
        ICryptography cryptoApp = (ICryptography) JCSystem.getAppletShareableInterfaceObject(cryptographyAid, CRYPTOGRAPHY_SECRET);

        return cryptoApp.decrypt(buffer, ISO7816.OFFSET_CDATA);
    }
    
}
