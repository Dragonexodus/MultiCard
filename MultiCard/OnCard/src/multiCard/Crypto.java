package multiCard;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.Cipher;

public class Crypto extends Applet implements ICrypto {
	// Applet
	// TODO byte statt short???
	private static final byte CRYPTO_CLA = (byte) 0x10;
	private static final byte CRYPTOGRAPHY_SECRET = 0x2A;
	private static final short KEY_SIZE = (byte) 0x40;

	// ID's anderer Applets
	private static final byte[] DISCO_AID = { 0x44, 0x69, 0x73, 0x63, 0x6f };
	private static final byte[] STUDENT_AID = { 0x53, 0x74, 0x75, 0x64, 0x65,
			0x6e, 0x74 };

	// Befehle
	private static final byte INS_IMPORT_CARD_PRIVATE_MOD = (byte) 0x10;
	private static final byte INS_IMPORT_CARD_PRIVATE_EXP = (byte) 0x11;
	private static final byte INS_IMPORT_CARD_PUBLIC_MOD = (byte) 0x12;
	private static final byte INS_IMPORT_CARD_PUBLIC_EXP = (byte) 0x13;
	private static final byte INS_EXPORT_CARD_PUBLIC_MOD = (byte) 0x14;
	private static final byte INS_EXPORT_CARD_PUBLIC_EXP = (byte) 0x15;
	private static final byte INS_IMPORT_TERMINAL_PUBLIC_MOD = (byte) 0x16;
	private static final byte INS_IMPORT_TERMINAL_PUBLIC_EXP = (byte) 0x17;

	// Fehler
	private static final short ERROR_IMPORT_CARD_PRIVATE_MOD = (short) 0xE010;
	private static final short ERROR_IMPORT_CARD_PRIVATE_EXP = (short) 0xE011;
	private static final short ERROR_IMPORT_CARD_PUBLIC_MOD = (short) 0xE012;
	private static final short ERROR_IMPORT_CARD_PUBLIC_EXP = (short) 0xE013;
	private static final short ERROR_EXPORT_CARD_PUBLIC_MOD = (short) 0xE014;
	private static final short ERROR_EXPORT_CARD_PUBLIC_EXP = (short) 0xE015;
	private static final short ERROR_IMPORT_TERMINAL_PUBLIC_MOD = (short) 0xE016;
	private static final short ERROR_IMPORT_TERMINAL_PUBLIC_EXP = (short) 0xE017;

	// Verschlüsselung
	private RSAPrivateKey cardPrivateKey;
	private RSAPublicKey cardPublicKey;
	private RSAPublicKey terminalPublicKey;
	private Signature signature;
	private Cipher rsaCipher = null;

	/**
	 * Schlüsse-Flags 0x00 nichts gesetzt 0x0F modulus gesetzt 0xF0 exponent
	 * gesetzt 0xFF beides gesetzt
	 */
	private byte cardPrivateKeyFlag;
	private byte cardPublicKeyFlag;
	private byte terminalPublicKeyFlag;

	protected Crypto() {
		register();

		cardPrivateKeyFlag = 0x00;
		cardPrivateKey = (RSAPrivateKey) KeyBuilder.buildKey(
				KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_512, false);

		cardPublicKeyFlag = 0x00;
		cardPublicKey = (RSAPublicKey) KeyBuilder.buildKey(
				KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_512, false);

		terminalPublicKeyFlag = 0x00;
		terminalPublicKey = (RSAPublicKey) KeyBuilder.buildKey(
				KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_512, false);

		rsaCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
		signature = Signature.getInstance(Signature.ALG_RSA_MD5_PKCS1, false);
	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new Crypto();
	}

	public void process(APDU apdu) {
		if (selectingApplet()) {
			ISOException.throwIt(ISO7816.SW_NO_ERROR);
			return;
		}

		byte[] buf = apdu.getBuffer();

		if (buf[ISO7816.OFFSET_CLA] != CRYPTO_CLA) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
			return;
		}

		short messageLength;
		switch (buf[ISO7816.OFFSET_INS]) {
		case INS_EXPORT_CARD_PUBLIC_MOD:
			exportCardPublicModulus(apdu);
			break;
		case INS_EXPORT_CARD_PUBLIC_EXP:
			exportCardPublicExponent(apdu);
			break;
		case INS_IMPORT_TERMINAL_PUBLIC_MOD:
			messageLength = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
			importTerminalPublicModulus(apdu, messageLength);
			break;
		case INS_IMPORT_TERMINAL_PUBLIC_EXP:
			messageLength = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
			importTerminalPublicExponent(apdu, messageLength);
			break;
		case INS_IMPORT_CARD_PRIVATE_MOD:
			messageLength = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
			importCardPrivateModulus(apdu, messageLength);
			break;
		case INS_IMPORT_CARD_PRIVATE_EXP:
			messageLength = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
			importCardPrivateExponent(apdu, messageLength);
			break;
		case INS_IMPORT_CARD_PUBLIC_MOD:
			messageLength = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
			importCardPublicModulus(apdu, messageLength);
			break;
		case INS_IMPORT_CARD_PUBLIC_EXP:
			messageLength = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
			importCardPublicExponent(apdu, messageLength);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	public boolean select() {
		return true;
	}

	public void deselect() {
	}

	/**
	 * Schreibt den öffentlichen Modulus in die APDU Falls öffentlicher Modulus
	 * 
	 * @param apdu
	 */
	private void exportCardPublicModulus(APDU apdu) {
		if ((cardPublicKeyFlag & 0x0F) != 0x0F) {
			ISOException.throwIt(ERROR_EXPORT_CARD_PUBLIC_MOD);
			return;
		}

		byte buffer[] = apdu.getBuffer();
		short modLen = cardPublicKey.getModulus(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, modLen);
	}

	/**
	 * Schreibt den öffentlichen Exponenten in die APDU
	 * 
	 * @param apdu
	 */
	private void exportCardPublicExponent(APDU apdu) {
		if ((cardPublicKeyFlag & 0xF0) != 0xF0) {
			ISOException.throwIt(ERROR_EXPORT_CARD_PUBLIC_EXP);
			return;
		}

		byte buffer[] = apdu.getBuffer();
		short expLen = cardPublicKey.getExponent(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, expLen);
	}

	/**
	 * Setzt den übergeben privaten Modulus zum privaten Schlüssel der Karte
	 * 
	 * @param apdu
	 */
	private void importCardPrivateModulus(APDU apdu, short lc) {
		if ((cardPrivateKeyFlag & 0x0F) == 0x0F) {
			ISOException.throwIt(ERROR_IMPORT_CARD_PRIVATE_MOD);
			return;
		}

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		cardPrivateKey.setModulus(buffer, ISO7816.OFFSET_CDATA, lc);

		cardPrivateKeyFlag = (byte) (cardPrivateKeyFlag | 0x0F);
	}

	/**
	 * Setzt den übergeben privaten Exponent zum privaten Schlüssel der Karte
	 * 
	 * @param apdu
	 */
	private void importCardPrivateExponent(APDU apdu, short lc) {
		if ((cardPrivateKeyFlag & 0xF0) == 0xF0) {
			ISOException.throwIt(ERROR_IMPORT_CARD_PRIVATE_EXP);
			return;
		}

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		cardPrivateKey.setExponent(buffer, ISO7816.OFFSET_CDATA, lc);

		cardPrivateKeyFlag = (byte) (cardPrivateKeyFlag | 0xF0);
	}

	/**
	 * Setzt den übergeben public Modulus zum öffentlichen Schlüssel der Karte
	 * 
	 * @param apdu
	 */
	private void importCardPublicModulus(APDU apdu, short lc) {
		if ((cardPublicKeyFlag & 0x0F) == 0x0F) {
			ISOException.throwIt(ERROR_IMPORT_CARD_PUBLIC_MOD);
			return;
		}

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		cardPublicKey.setModulus(buffer, ISO7816.OFFSET_CDATA, lc);

		cardPublicKeyFlag = (byte) (cardPublicKeyFlag | 0x0F);
	}

	/**
	 * Setzt den übergeben public Exponent zum öffentlichen Schlüssel der Karte
	 * 
	 * @param apdu
	 */
	private void importCardPublicExponent(APDU apdu, short lc) {
		if ((cardPublicKeyFlag & 0xF0) == 0xF0) {
			ISOException.throwIt(ERROR_IMPORT_CARD_PUBLIC_EXP);
			return;
		}

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		cardPublicKey.setExponent(buffer, ISO7816.OFFSET_CDATA, lc);

		cardPublicKeyFlag = (byte) (cardPublicKeyFlag | 0xF0);
	}

	/**
	 * Setzt den übergeben public Modulus des Terminals zum öffentlichen
	 * Schlüssels des Terminals
	 * 
	 * @param apdu
	 */
	private void importTerminalPublicModulus(APDU apdu, short lc) {
		if ((terminalPublicKeyFlag & 0x0F) == 0x0F) {
			ISOException.throwIt(ERROR_IMPORT_TERMINAL_PUBLIC_MOD);
			return;
		}

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		terminalPublicKey.setModulus(buffer, ISO7816.OFFSET_CDATA, lc);

		terminalPublicKeyFlag = (byte) (terminalPublicKeyFlag | 0x0F);
	}

	/**
	 * Setzt den übergeben public Exponent des Terminals zum öffentlichen
	 * Schlüssels des Terminals
	 * 
	 * @param apdu
	 */
	private void importTerminalPublicExponent(APDU apdu, short lc) {
		if ((terminalPublicKeyFlag & 0xF0) == 0xF0) {
			ISOException.throwIt(ERROR_IMPORT_TERMINAL_PUBLIC_EXP);
			return;
		}

		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		terminalPublicKey.setExponent(buffer, ISO7816.OFFSET_CDATA, lc);

		terminalPublicKeyFlag = (byte) (terminalPublicKeyFlag | 0xF0);
	}

	/**
	 * Verschlüsselt die übergebene Nachricht mit dem öffentlichen Schlüssel des
	 * Terminals und schreibt es in den Buffer an Offset 0.
	 * 
	 * @param buffer
	 *            apdu buffer
	 * @param message
	 *            Zu verschlüsselnde Nachricht
	 * @param offset
	 *            Start offset der Nachricht
	 * @param length
	 *            länge der Nachricht
	 * @return length länge der verschlüsselten Nachricht (normalerweise 64
	 *         Byte)
	 */
	public short encrypt(byte[] buffer, byte[] message, byte offset, byte length) {
		signature.init(cardPrivateKey, Signature.MODE_SIGN);
		short len = signature.sign(message, (short) offset, (short) length,
				buffer, KEY_SIZE);

		rsaCipher.init(terminalPublicKey, Cipher.MODE_ENCRYPT);
		short len2 = rsaCipher.doFinal(message, (short) offset, (short) length,
				buffer, (short) 0);

		return (short) (len + len2);
	}

	/**
	 * Entschlüsselt die übergebene Nachricht mit dem privaten Schlüssel der
	 * Karte und schreibt es in den Buffer an Offset 0.
	 * 
	 * @param buffer
	 *            apdu buffer
	 * @param offset
	 *            (64 Byte)
	 * @return trimmed Getrimmte entschlüsselte Nachricht
	 */
	public short decrypt(byte[] buffer, byte offset) {
		rsaCipher.init(cardPrivateKey, Cipher.MODE_DECRYPT);
		short len = rsaCipher.doFinal(buffer, (short) offset, KEY_SIZE, buffer,
				(short) 0);

		signature.init(terminalPublicKey, Signature.MODE_VERIFY);
		if (!signature.verify(buffer, (short) 0, len, buffer,
				(short) (offset + KEY_SIZE), KEY_SIZE)) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}

		return len;
	}

	/**
	 * Aufgerufen, wenn andere Applets dieses Applet verwenden möchten
	 * 
	 * @param client_aid
	 * @param parameter
	 *            Geheimnis
	 * @return this dieses Applet
	 */
	public Shareable getShareableInterfaceObject(AID client_aid, byte parameter) {
		if (!client_aid.equals(DISCO_AID, (short) 0, (byte) DISCO_AID.length)
				&& !client_aid.equals(STUDENT_AID, (short) 0,
						(byte) STUDENT_AID.length)) {
			return null;
		}

		if (parameter != CRYPTOGRAPHY_SECRET) {
			return null;
		}

		return this;
	}
}
