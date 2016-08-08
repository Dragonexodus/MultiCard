package multiCard;

import javacard.framework.AID;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;

public class Student extends Applet {
	// Java Card
	// Applet
	private static final byte STUDENT_CLA = (byte) 0x20;

	// Befehle
	private static final byte INS_GET_MONEY = (byte) 0x20;
	private static final byte INS_ADD_MONEY = (byte) 0x21;
	private static final byte INS_SUB_MONEY = (byte) 0x22;
	private static final byte INS_RESET_MONEY = (byte) 0x23;
	private static final byte INS_SET_NAME = (byte) 0x24;
	private static final byte INS_GET_NAME = (byte) 0x25;
	private static final byte INS_SET_MATRIKEL = (byte) 0x26;
	private static final byte INS_GET_MATRIKEL = (byte) 0x27;
	private static final byte INS_SET_ROOMS = (byte) 0x28;
	private static final byte INS_GET_ROOMS = (byte) 0x29;

	// Fehler
	private static final short ERROR_ADD_EURO_OVERFLOW = (short) 0xE021;
	private static final short ERROR_ADD_CENT_OVERFLOW = (short) 0xE121;
	private static final short ERROR_ADD_MONEY_OVERFLOW = (short) 0xE221;
	private static final short ERROR_SUB_EURO_OVERFLOW = (short) 0xE022;
	private static final short ERROR_SUB_CENT_OVERFLOW = (short) 0xE122;
	private static final short ERROR_SUB_INSUFFICIENT_MONEY = (short) 0xE222;
	private static final short ERROR_SET_MATRIKEL_NEGATIVE = (short) 0xE025;
	private static final short ERROR_SET_MATRIKEL_OVERFLOW = (short) 0xE125;

	// TODO
	// Mensa und Räume

	// Andere Applets
	private static final byte[] CRYPTOGRAPHY_AID = { 0x43, 0x72, 0x79, 0x70,
			0x74, 0x6f };
	private static final byte CRYPTOGRAPHY_SECRET = 0x2A;

	// Konstanten und Offsets
	private static final byte MAX_EURO_VALUE = (byte) 0x7F;
	private static final byte MAX_CENT_VALUE = (byte) 0x63;
	private static final byte SHORT_LENGTH = (byte) 0x02;
	private static final byte MAX_NAME_LENGTH = (byte) 0x3F;
	private static final short MAX_MATRIKEL = (short) 0x7FFF;
	// Daten
	private byte euro;
	private byte cent;
	private byte[] name;
	private byte currentNameLength = 0;
	private short matrikel;

	public Student() {
		register();
		this.euro = 0;
		this.cent = 0;
		this.matrikel = 0;
		name = new byte[MAX_NAME_LENGTH];

	}

	/**
	 * Install Applet
	 * 
	 * @param bArray
	 * @param bOffset
	 * @param bLength
	 */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new Student();

	}

	public void process(APDU apdu) throws ISOException {
		if (selectingApplet()) {
			ISOException.throwIt(ISO7816.SW_NO_ERROR);
			return;
		}

		byte[] buffer = apdu.getBuffer();

		if (buffer[ISO7816.OFFSET_CLA] != STUDENT_CLA) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
			return;
		}

		switch (buffer[ISO7816.OFFSET_INS]) {
		case INS_RESET_MONEY:
			resetMoney(apdu);
			break;
		case INS_ADD_MONEY:
			addMoney(apdu);
			break;
		case INS_SUB_MONEY:
			subMoney(apdu);
			break;
		case INS_GET_MONEY:
			getMoney(apdu);
			break;
		case INS_SET_NAME:
			setName(apdu);
			break;
		case INS_GET_NAME:
			getName(apdu);
			break;
		case INS_SET_MATRIKEL:
			setMatrikel(apdu);
			break;
		case INS_GET_MATRIKEL:
			getMatrikel(apdu);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	/**
	 * Reset des Geldes 
	 * @param apdu
	 */
	private void resetMoney(APDU apdu) {
		this.euro = 0;
		this.cent = 0;
	}
	
	/**
	 * Fügt Geld hinzu
	 * 
	 * @param apdu
	 */
	private void addMoney(APDU apdu) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		// short ist 2 bytes lang
		if (messageLength != SHORT_LENGTH)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

		short money = Util.getShort(buffer, (short) 0);
		// TODO Testen, evtl. falsch
		byte tmpEuro = (byte) (money << 0);
		byte tmpCent = (byte) (money << 8);

		if (tmpEuro > MAX_EURO_VALUE || tmpEuro < 0)
			ISOException.throwIt(ERROR_ADD_EURO_OVERFLOW);
		if (tmpCent > MAX_CENT_VALUE || tmpCent < 0)
			ISOException.throwIt(ERROR_ADD_CENT_OVERFLOW);

		// Cent Overflow
		if ((tmpCent + this.cent) > MAX_CENT_VALUE || tmpCent + this.cent < 0) {
			tmpEuro++;
			tmpCent = (byte) (tmpCent + this.cent - MAX_CENT_VALUE - 1);
		} else
			tmpCent += tmpCent + this.cent;

		// Money Overflow!
		if (tmpEuro + this.euro > MAX_EURO_VALUE || tmpEuro + this.euro < 0) {
			ISOException.throwIt(ERROR_ADD_MONEY_OVERFLOW);
		}

		this.euro += tmpEuro;
		this.cent = tmpCent;
	}

	/**
	 * Erlaubt bezahlen mit Geld, bei falschen Angaben wird Exception geworfen
	 * 
	 * @param apdu
	 */
	private void subMoney(APDU apdu) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		// short ist 2 bytes lang
		if (messageLength != SHORT_LENGTH)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

		short money = Util.getShort(buffer, (short) 0);
		// TODO Testen, evtl. falsch
		byte tmpEuro = (byte) (money << 0);
		byte tmpCent = (byte) (money << 8);

		if (tmpEuro > MAX_EURO_VALUE || tmpEuro < 0)
			ISOException.throwIt(ERROR_SUB_EURO_OVERFLOW);
		if (tmpCent > MAX_CENT_VALUE || tmpCent < 0)
			ISOException.throwIt(ERROR_SUB_CENT_OVERFLOW);

		if ((this.cent - tmpCent) < 0) {
			if (tmpEuro > 0) {
				tmpEuro--;
			} else {
				ISOException.throwIt(ERROR_SUB_INSUFFICIENT_MONEY);
			}
			tmpCent = (byte) (this.cent - tmpCent + MAX_CENT_VALUE);
		} else
			tmpCent += this.cent - tmpCent;

		// Money Overflow!
		if (this.euro - tmpEuro > MAX_EURO_VALUE || this.euro - tmpEuro < 0) {
			ISOException.throwIt(ERROR_SUB_INSUFFICIENT_MONEY);
		}

		this.euro -= tmpEuro;
		this.cent = tmpCent;
	}

	/**
	 * Wandelt short zu byteArray und sendet es an die APDU
	 * 
	 * @param apdu
	 */
	private void getMoney(APDU apdu) {
		apdu.setIncomingAndReceive();
		send(apdu, new byte[] { this.euro, this.cent }, (byte) 0, SHORT_LENGTH);
	}

	/**
	 * Setzt den Namen
	 * 
	 * @param apdu
	 */
	private void setName(APDU apdu) {

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		if (messageLength > MAX_NAME_LENGTH || messageLength == 0) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			return;
		}

		Util.arrayCopy(buffer, (short) (0x00 & 0x00FF), name,
				(short) (0x00 & 0x00FF), messageLength);
		currentNameLength = (byte) messageLength;
	}

	/**
	 * Sendet den Namen
	 * 
	 * @param apdu
	 */
	private void getName(APDU apdu) {
		apdu.setIncomingAndReceive();
		send(apdu, name, (byte) 0, currentNameLength);
	}

	private void setMatrikel(APDU apdu) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		if (messageLength != SHORT_LENGTH)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

		if (Util.getShort(buffer, (short) 0) < 0)
			ISOException.throwIt(ERROR_SET_MATRIKEL_NEGATIVE);

		if (Util.getShort(buffer, (short) 0) > MAX_MATRIKEL)
			ISOException.throwIt(ERROR_SET_MATRIKEL_OVERFLOW);

		this.matrikel = Util.getShort(buffer, (short) 0);
	}

	/**
	 * Wandelt short zu byteArray und sendet es an die APDU
	 * 
	 * @param apdu
	 */
	private void getMatrikel(APDU apdu) {
		apdu.setIncomingAndReceive();
		send(apdu, new byte[] { (byte) ((this.matrikel >> 8) & 0xFF),
				(byte) (this.matrikel & 0x00FF) }, (byte) 0, SHORT_LENGTH);
	}

	/**
	 * Sendet den verschlüsselten Inhalt an die APDU
	 * 
	 * @param apdu
	 * @param content
	 *            Zu verschlüsselender Inhalt
	 * @param offset
	 *            Startpos des zu verschlüsselenden Inhalts
	 * @param length
	 *            Länge des Inhalts
	 */
	private void send(APDU apdu, byte[] content, byte offset, byte length) {
		byte[] buffer = apdu.getBuffer();
		short len = encryptMessage(buffer, content, offset, length);

		apdu.setOutgoingAndSend((short) 0, len);
	}

	/**
	 * Verschlüsselt die Nachricht, in dem es das Cryptography-Applet via
	 * applet-firewall nutzt.
	 * 
	 * @param buffer
	 *            Ziel Speicher. Resultat wird ab Offset 0 gespeichert.
	 * @param message
	 *            Nachricht.
	 * @param offset
	 *            Offset wo Nachricht beginnt.
	 * @param length
	 *            Länge der Nachricht.
	 * @return length Länge der verschlüsselten Nachricht.
	 */
	private short encryptMessage(byte[] buffer, byte[] message, byte offset,
			byte length) {
		AID cryptographyAid = JCSystem.lookupAID(CRYPTOGRAPHY_AID, (short) 0,
				(byte) CRYPTOGRAPHY_AID.length);
		ICrypto cryptoApp = (ICrypto) JCSystem
				.getAppletShareableInterfaceObject(cryptographyAid,
						CRYPTOGRAPHY_SECRET);

		return cryptoApp.encrypt(buffer, message, offset, length);
	}

	/**
	 * Entschlüsselt die Nachricht an der Stelle 0 via dem Cryptographie-Applet
	 * von der Applet Firewall.
	 * 
	 * @param buffer
	 *            Quell-und Zielspeicher, Nachricht startet bei
	 *            ISO7816.OFFSET_CDATA und das Ergebnis beginnt ab Offset 0.
	 * @return length der verschlüsselten Nachricht
	 */
	private short decryptMessage(byte[] buffer) {
		AID cryptographyAid = JCSystem.lookupAID(CRYPTOGRAPHY_AID, (short) 0,
				(byte) CRYPTOGRAPHY_AID.length);
		ICrypto cryptoApp = (ICrypto) JCSystem
				.getAppletShareableInterfaceObject(cryptographyAid,
						CRYPTOGRAPHY_SECRET);

		return cryptoApp.decrypt(buffer, ISO7816.OFFSET_CDATA);
	}

}
