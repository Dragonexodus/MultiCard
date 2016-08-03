package multiCard;

import javacard.framework.*;

public class Bonus extends Applet
{
    // Java Card
    // Applet
    private static final byte BONUS_CLA = 0x42;

    // Befehle
    private static final byte INS_GET = (byte) 0xB0;
    private static final byte INS_ADD_OR_SUB = (byte) 0xB1;
    private static final byte INS_RESET = (byte) 0xF0;

    // Andere Applets
    private static final byte[] CRYPTOGRAPHY_AID = {0x43, 0x72, 0x79, 0x70, 0x74, 0x6f, 0x67, 0x72, 0x61, 0x70, 0x68, 0x79};
    private static final byte CRYPTOGRAPHY_SECRET = 0x2A;

    // Konstanten und Offsets
    private static final short MAX_MIN_BONUS_VALUE = 1000;
    private static final byte BONUS_LENGTH = 2;

    // Daten
    private short bonusPoints;

    public Bonus()
    {
        register();
        this.bonusPoints = 0;
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
        new Bonus();
    }

    public void process(APDU apdu) throws ISOException
    {
        if (selectingApplet())
        {
            ISOException.throwIt(ISO7816.SW_NO_ERROR);
            return;
        }

        byte[] buffer = apdu.getBuffer();

        if (buffer[ISO7816.OFFSET_CLA] != BONUS_CLA)
        {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            return;
        }

        switch (buffer[ISO7816.OFFSET_INS])
        {	
        	case INS_GET:
        		getBonus(apdu);
            	break;
            case INS_ADD_OR_SUB:
                addOrSubBonus(apdu);
                break;
            case INS_RESET:
                resetBonus(apdu);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    /**
     * Reset der Bonuspunkte
     * @param apdu
     */
    private void resetBonus(APDU apdu)
    {
        this.bonusPoints = 0;
    }

    /**
     * Addiert oder Subtrahiert Punkte zu den Bonuspunkten
     * Überprüft ob ein Overflow-Auftritt
     * Maximalwert für Bonuspunkte sind limitert {@code MAX_MIN_BONUS_VALUE}.
     * @param apdu
     */
    private void addOrSubBonus(APDU apdu)
    {
        apdu.setIncomingAndReceive();
        byte[] buffer = apdu.getBuffer();

        short messageLength = decryptMessage(buffer);

        // short ist 2 bytes lang
        if (messageLength != BONUS_LENGTH)
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        //Overflow-Test
        if (Util.getShort(buffer, (short) 0) < -MAX_MIN_BONUS_VALUE || Util.getShort(buffer, (short) 0) > MAX_MIN_BONUS_VALUE )
            ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        
        if((this.bonusPoints - Util.getShort(buffer, (short) 0)) < 0)
        	this.bonusPoints = 0;
        else if(this.bonusPoints + Util.getShort(buffer, (short) 0) > MAX_MIN_BONUS_VALUE)
        	this.bonusPoints = MAX_MIN_BONUS_VALUE;
        else
        	this.bonusPoints += Util.getShort(buffer, (short) 0);
    }


    
    /**
     * Wandelt short zu byteArray und sendet es an die APDU
     * @param apdu
     */
    private void getBonus(APDU apdu)
    {
        apdu.setIncomingAndReceive();
        send(apdu, new byte[] { (byte) ((this.bonusPoints >> 8) & 0xFF), (byte) (this.bonusPoints & 0x00FF) }, (byte) 0, BONUS_LENGTH);
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
