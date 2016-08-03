package multiCard;
import javacard.framework.Shareable;

public interface ICryptography extends Shareable
{

    public short encrypt(byte[] buffer, byte[] message, byte offset, byte length);

    short decrypt(byte[] buffer, byte offset);
   
}
