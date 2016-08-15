package multiCard;

import javacard.framework.AID;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;

public class Disco extends Applet {
	// Java Card
	// Applet
	private static final byte DISCO_CLA = 0x30;

	// Befehle
	private static final byte INS_GET_BONUS = (byte) 0x30;
	private static final byte INS_ADD_BONUS = (byte) 0x31;
	private static final byte INS_SUB_BONUS = (byte) 0x32;
	private static final byte INS_RESET_BONUS = (byte) 0x33;
	private static final byte INS_GET_MONEY = (byte) 0x20;
	private static final byte INS_ADD_MONEY = (byte) 0x21;
	private static final byte INS_SUB_MONEY = (byte) 0x22;
	private static final byte INS_RESET_MONEY = (byte) 0x23;
	private static final byte INS_GET_DRINKS = (byte) 0x34;
	private static final byte INS_ADD_DRINK = (byte) 0x35;
	private static final byte INS_SET_PAID_DRINKS = (byte) 0x36;

	// Andere Applets
	private static final byte[] CRYPTO_AID = { 0x43, 0x72, 0x79, 0x70, 0x74,
			0x6f };
	private static final byte CRYPTO_SECRET = (byte) 0x88;
	private static final byte[] STUDENT_AID = { 0x53, 0x74, 0x75, 0x64, 0x65,
			0x6e, 0x74 };

	// Fehler
	private static final short ERROR_ADD_BONUS_OVERFLOW = (short) 0xE030;
	private static final short ERROR_SUB_BONUS_OVERFLOW = (short) 0xE031;
	private static final short ERROR_SUB_INSUFFICIENT_BONUS = (short) 0xE032;
	private static final short ERROR_ADD_DRINK_HAD_TO_MUCH = (short) 0xE033;

	// Konstanten und Offsets
	private static final byte MAX_BONUS_VALUE = (byte) 0x7F;
	private static final byte BYTE_LENGTH = (byte) 0x01;
	private static final byte SHORT_LENGTH = (byte) 0x02;
	private static final byte MAX_DRINKS = (byte) 0x32;			// 50 Getränke

	// Daten
	private byte bonus;
	private byte currentDrinkCount = 0;
	private byte[] drinkDictionary;

	public Disco() {
		register();
		this.bonus = 0;
		this.drinkDictionary = new byte[MAX_DRINKS];
	}

	/**
	 * Install Applet
	 * 
	 * @param bArray
	 * @param bOffset
	 * @param bLength
	 */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new Disco();
	}

	public void process(APDU apdu) throws ISOException {
		if (selectingApplet()) {
			ISOException.throwIt(ISO7816.SW_NO_ERROR);
			return;
		}

		byte[] buffer = apdu.getBuffer();

		if (buffer[ISO7816.OFFSET_CLA] != DISCO_CLA) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
			return;
		}

		switch (buffer[ISO7816.OFFSET_INS]) {
		case INS_GET_BONUS:
			getBonus(apdu);
			break;
		case INS_ADD_BONUS:
			addBonus(apdu);
			break;
		case INS_SUB_BONUS:
			subBonus(apdu);
			break;
		case INS_RESET_BONUS:
			resetBonus(apdu);
			break;
		case INS_RESET_MONEY:
			resetMoney(apdu);
			break;
		case INS_GET_MONEY:
			getMoney(apdu);
			break;
		case INS_ADD_MONEY:
			addMoney(apdu);
			break;
		case INS_SUB_MONEY:
			subMoney(apdu);
			break;
		case INS_ADD_DRINK:
			addDrink(apdu);
			break;
		case INS_GET_DRINKS:
			getDrinks(apdu);
			break;
		case INS_SET_PAID_DRINKS:
			setPaidDrinks(apdu);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			return;
		}
	}

	/**
	 * Reset der Bonuspunkte
	 * 
	 * @param apdu
	 */
	private void resetBonus(APDU apdu) {
		this.bonus = 0;
	}

	/**
	 * Addiert Punkte Überprüft ob ein Overflow-Auftritt Maximalwert für
	 * Bonuspunkte sind limitert
	 * 
	 * @param apdu
	 */
	private void addBonus(APDU apdu) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		if (messageLength != BYTE_LENGTH) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			return;
		}
		byte tmpBonus = buffer[0];

		if (tmpBonus < 0) {
			ISOException.throwIt(ERROR_ADD_BONUS_OVERFLOW);
			return;
		}
		if (this.bonus + tmpBonus > MAX_BONUS_VALUE
				|| this.bonus + tmpBonus < 0) {
			ISOException.throwIt(ERROR_ADD_BONUS_OVERFLOW);
			return;
		}
		this.bonus += tmpBonus;
	}

	/**
	 * Subtrahiert Punkte Überprüft ob ein Underflow-Auftritt
	 * 
	 * @param apdu
	 */
	private void subBonus(APDU apdu) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		if (messageLength != BYTE_LENGTH) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			return;
		}
		byte tmpBonus = buffer[0];

		if (tmpBonus < 0) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
			return;
		}
		if (tmpBonus > MAX_BONUS_VALUE) {
			ISOException.throwIt(ERROR_SUB_BONUS_OVERFLOW);
			return;
		}
		if (this.bonus - tmpBonus < 0) {
			ISOException.throwIt(ERROR_SUB_INSUFFICIENT_BONUS);
			return;
		}
		this.bonus -= tmpBonus;
	}

	/**
	 * Wandelt short zu byteArray und sendet es an die APDU
	 * 
	 * @param apdu
	 */
	private void getBonus(APDU apdu) {
		apdu.setIncomingAndReceive();
		send(apdu, new byte[] { this.bonus }, (byte) 0, BYTE_LENGTH);
	}

	/**
	 * Fügt ein Getränk der Liste hinzu
	 * 
	 * @param apdu
	 */
	private void addDrink(APDU apdu) {

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		if (messageLength != 1) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			return;
		}

		if (currentDrinkCount >= MAX_DRINKS) {
			ISOException.throwIt(ERROR_ADD_DRINK_HAD_TO_MUCH);
			return;
		}
		Util.arrayCopy(buffer, (short) 0, drinkDictionary, currentDrinkCount,
				BYTE_LENGTH);
		currentDrinkCount++;
	}

	/**
	 * Liefert eine Liste der Getränke
	 * 
	 * @param apdu
	 */
	private void getDrinks(APDU apdu) {
		apdu.setIncomingAndReceive();
		send(apdu, drinkDictionary, (byte) 0, currentDrinkCount);
	}

	/**
	 * Entfernt bereits bezahle Getränke (1) aus der Liste, dazu wird ein
	 * ByteArray mit 0/1 übergeben ByteArray muss gleiche Länge wie Anzahl der
	 * Getränke besitzen
	 * 
	 * @param apdu
	 */
	private void setPaidDrinks(APDU apdu) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();

		short messageLength = decryptMessage(buffer);

		if (messageLength != currentDrinkCount) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			return;
		}
		byte[] notPaidDrinksDict = new byte[MAX_DRINKS];
		byte notPaidCount = 0;
		for (byte idx = 0; idx < messageLength; idx++) {
			if (buffer[idx] != 1) {
				notPaidDrinksDict[notPaidCount] = drinkDictionary[idx];
				notPaidCount++;
			}
		}
		Util.arrayCopy(notPaidDrinksDict, (short) 0, drinkDictionary,
				(short) 0, notPaidCount);
		currentDrinkCount = notPaidCount;
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
	 * Verschlüsselt die Nachricht, in dem es das Crypto-Applet via
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
		AID cryptoAid = JCSystem.lookupAID(CRYPTO_AID, (short) 0,
				(byte) CRYPTO_AID.length);
		ICrypto cryptoApp = (ICrypto) JCSystem
				.getAppletShareableInterfaceObject(cryptoAid, CRYPTO_SECRET);

		return cryptoApp.encrypt(buffer, message, offset, length);
	}

	/**
	 * Entschlüsselt die Nachricht an der Stelle 0 via dem Crypto-Applet von der
	 * Applet Firewall.
	 * 
	 * @param buffer
	 *            Quell-und Zielspeicher, Nachricht startet bei
	 *            ISO7816.OFFSET_CDATA und das Ergebnis beginnt ab Offset 0.
	 * @return length der verschlüsselten Nachricht
	 */
	private short decryptMessage(byte[] buffer) {
		AID cryptoAid = JCSystem.lookupAID(CRYPTO_AID, (short) 0,
				(byte) CRYPTO_AID.length);
		ICrypto cryptoApp = (ICrypto) JCSystem
				.getAppletShareableInterfaceObject(cryptoAid, CRYPTO_SECRET);

		return cryptoApp.decrypt(buffer, ISO7816.OFFSET_CDATA);
	}

	/**
	 * Geld-Funktion aus Student verwenden
	 * 
	 * @param apdu
	 */
	private void resetMoney(APDU apdu) {
		AID studentAid = JCSystem.lookupAID(STUDENT_AID, (short) 0,
				(byte) STUDENT_AID.length);
		IMoney studentApp = (IMoney) JCSystem
				.getAppletShareableInterfaceObject(studentAid, (byte) 0);

		studentApp.resetMoney();
	}

	/**
	 * Geld-Funktion aus Student verwenden
	 * 
	 * @param apdu
	 */
	private void getMoney(APDU apdu) {
		AID studentAid = JCSystem.lookupAID(STUDENT_AID, (short) 0,
				(byte) STUDENT_AID.length);
		IMoney studentApp = (IMoney) JCSystem
				.getAppletShareableInterfaceObject(studentAid, (byte) 0);

		apdu.setIncomingAndReceive();
		send(apdu, studentApp.getMoney(), (byte) 0, SHORT_LENGTH);
	}

	/**
	 * Geld-Funktion aus Student verwenden
	 * 
	 * @param apdu
	 */
	private void addMoney(APDU apdu) {
		AID studentAid = JCSystem.lookupAID(STUDENT_AID, (short) 0,
				(byte) STUDENT_AID.length);
		IMoney studentApp = (IMoney) JCSystem
				.getAppletShareableInterfaceObject(studentAid, (byte) 0);

		studentApp.addMoneyS(apdu);
	}

	/**
	 * Geld-Funktion aus Student verwenden
	 * 
	 * @param apdu
	 */
	private void subMoney(APDU apdu) {
		AID studentAid = JCSystem.lookupAID(STUDENT_AID, (short) 0,
				(byte) STUDENT_AID.length);
		IMoney studentApp = (IMoney) JCSystem
				.getAppletShareableInterfaceObject(studentAid, (byte) 0);

		studentApp.subMoneyS(apdu);
	}
}
