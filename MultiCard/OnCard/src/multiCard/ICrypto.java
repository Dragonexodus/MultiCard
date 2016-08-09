package multiCard;
import javacard.framework.Shareable;

public interface ICrypto extends Shareable
{

    public short encrypt(byte[] buffer, byte[] message, byte offset, byte length);

    public short decrypt(byte[] buffer, byte offset);
   
}
