import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

/**
 * Created by noah on 1/28/17.
 */
public class RSAHandler {

    RSAKey publicKey;
    RSAKey secretKey;

    public RSAHandler() {

        BigInteger p = getRandomPrime();
        BigInteger q = getRandomPrime();
        BigInteger n = p.multiply(q);

        int e = getRelativePrime(p, q);
        int d = multiplicativeInverse(e, n);
        publicKey = createPublicKey(e, n);
        secretKey = createSecretKey(d, n);
    }

    private BigInteger getRandomPrime() {
        throw new NotImplementedException();
    }

    private int getRelativePrime(BigInteger p, BigInteger q) {
        throw new NotImplementedException();
    }

    private int multiplicativeInverse(int e, BigInteger n) {
        throw new NotImplementedException();
    }

    private RSAKey createPublicKey(int e, BigInteger n) {
        throw new NotImplementedException();
    }

    private RSAKey createSecretKey(int d, BigInteger n) {
        throw new NotImplementedException();
    }




    public class RSAKey {

    }
}
