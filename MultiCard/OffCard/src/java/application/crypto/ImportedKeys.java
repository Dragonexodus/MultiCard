package application.crypto;

import java.math.BigInteger;

public class ImportedKeys {
    private final BigInteger privateMod;
    private final BigInteger privateExp;
    private final BigInteger publicMod;
    private final BigInteger publicExp;

    public ImportedKeys(BigInteger privateMod, BigInteger privateExp, BigInteger publicMod, BigInteger publicExp) {
        this.privateMod = privateMod;
        this.privateExp = privateExp;
        this.publicMod = publicMod;
        this.publicExp = publicExp;
    }

    public BigInteger getPrivateMod() {
        return privateMod;
    }

    public BigInteger getPrivateExp() {
        return privateExp;
    }

    public BigInteger getPublicMod() {
        return publicMod;
    }

    public BigInteger getPublicExp() {
        return publicExp;
    }
}
