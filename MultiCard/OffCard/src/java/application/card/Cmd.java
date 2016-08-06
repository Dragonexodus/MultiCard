package application.card;


import opencard.core.terminal.CommandAPDU;

/**
 * Created by Patrick on 16.08.2015.
 */
public class Cmd extends CommandAPDU {
    public Cmd(byte classByte, byte instruction, byte p1, byte p2, byte[] content, byte answerLength) {
        super(ToCommandBytes(classByte, instruction, p1, p2, content, answerLength));
    }

    public Cmd(byte classByte, byte instruction, byte p1, byte p2, byte answerLength) {
        super(ToCommandBytes(classByte, instruction, p1, p2, answerLength));
    }

    private static byte[] ToCommandBytes(byte cla, byte ins, byte p1, byte p2, byte[] content, byte answerLength) {
        if (content.length < 1) {
            return ToCommandBytes(cla, ins, p1, p2, answerLength);
        }

        final Integer contentLength = content.length;
        final byte[] command = new byte[6 + contentLength];

        command[0] = cla;
        command[1] = ins;
        command[2] = p1;
        command[3] = p2;
        command[4] = contentLength.byteValue();
        System.arraycopy(content, 0, command, 5, contentLength);

        command[5 + contentLength] = answerLength;

        return command;
    }

    private static byte[] ToCommandBytes(byte cla, byte ins, byte p1, byte p2, byte answerLength) {
        final byte[] command = new byte[5];

        command[0] = cla;
        command[1] = ins;
        command[2] = p1;
        command[3] = p2;
        command[4] = answerLength;

        return command;
    }

    public byte getCLA() {
        return this.apdu_buffer[0];
    }

    public byte getINS() {
        return this.apdu_buffer[1];
    }

    public byte getP1() {
        return this.apdu_buffer[2];
    }

    public byte getP2() {
        return this.apdu_buffer[3];
    }

    public byte getLC() {
        return this.apdu_buffer[4];
    }

    public byte getLE() {
        return this.apdu_buffer[this.apdu_buffer.length - 1];
    }

    public String toString() {
        StringBuffer ret = new StringBuffer("APDU_Buffer = ");
        ret.append(makeHex(this.getBytes()));
        ret.append(" | lc = ");
        ret.append(Byte.toUnsignedInt(getLC()));
        ret.append(" | le = ");
        ret.append(Byte.toUnsignedInt(getLE()));
        return ret.toString();
    }

    private String makeHex(byte[] buffer) {
        Integer length = buffer.length;
        String blank = "";
        StringBuffer ret = new StringBuffer(2 * length);

        for (int i = 0; i < 2 * length; ++i) {
            byte current = i % 2 == 1 ? (byte) (buffer[i / 2] & 15) : (byte) (buffer[i / 2] >> 4 & 15);
            ret.append((char) (current < 10 ? current + 48 : current + 55) + (i % 2 == 1 ? blank : ""));
        }

        return ret.toString();
    }
}
