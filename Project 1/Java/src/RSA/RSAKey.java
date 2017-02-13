package RSA;

import java.math.BigInteger;

/**
 * Created by noah on 1/28/17.
 */
abstract public class RSAKey {
    public BigInteger n;
    public BigInteger ed;

    public RSAKey(BigInteger ed, BigInteger n) {
        this.n = n;
        this.ed = ed;
    }
}
