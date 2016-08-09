package multiCard;

import javacard.framework.Shareable;
import javacard.framework.APDU;

public interface IMoney extends Shareable {
	public byte[] getMoney();

	public void addMoneyS(APDU apdu);

	public void subMoneyS(APDU apdu);

	public void resetMoney();
}
