package RSA;

import javafx.util.converter.BigIntegerStringConverter;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;



/**
 * Created by noah on 1/28/17.
 */

public class RSAHandler {

    RSAKey publicKey;
    RSAKey secretKey;

    BigInteger n;

    public RSAHandler() {


        BigInteger p = getRandomPrime(16);
        BigInteger q = getRandomPrime(16);

//        p = BigInteger.valueOf(47);
//        q = BigInteger.valueOf(71);

        p = BigInteger.valueOf(61);
        q = BigInteger.valueOf(53);

        n = p.multiply(q);


        BigInteger n_mod = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        n_mod = n_mod.divide(extendedEuclid(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE)).d);

        BigInteger e = getCoprime(n_mod);
//        e = BigInteger.valueOf(79);
        e = BigInteger.valueOf(17);
        assert (e.compareTo(n_mod) < 0);
        assert (extendedEuclid(e, n_mod).d.equals(BigInteger.ONE));

        EEWrapper wrapper = multiplicativeInverse(e, n_mod);
        assert (wrapper.x.equals(BigInteger.valueOf(413)));

        publicKey = new RSAKeyPublic(e, n);
        secretKey = new RSAKeySecret(wrapper.x, n);
    }

    private BigInteger getRandomPrime(int numBits) {
        BigInteger random = new BigInteger(numBits, new Random());
        random = random.setBit(0); // Required to have an odd number!
        while (!isPrime(random)) {
            random = random.add(BigInteger.valueOf(2));
        }
        return random;
    }

    private BigInteger getCoprime(BigInteger n) {
        return n.subtract(BigInteger.ONE);
    }

    private EEWrapper multiplicativeInverse(BigInteger x, BigInteger y) {
        EEWrapper wrapper = extendedEuclid(x, y);
        wrapper.x = wrapper.x.mod(y).add(y).mod(y);
        return wrapper;
    }

    private EEWrapper extendedEuclid(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return new EEWrapper(a, BigInteger.ONE, BigInteger.ZERO);
        EEWrapper wrapper = extendedEuclid(b, a.mod(b));
        return new EEWrapper(wrapper.d, wrapper.y, wrapper.x.subtract(a.divide(b).multiply(wrapper.y)));
    }

    private class EEWrapper {
        BigInteger d;
        BigInteger x;
        BigInteger y;

        EEWrapper(BigInteger d, BigInteger x, BigInteger y) {
            this.d = d;
            this.x = x;
            this.y = y;
        }
    }

    public String encrypt(String message) {
        return encryptionBase(publicKey, 3, message);
    }

    public String decrypt(String message) {
        return encryptionBase(secretKey, 4, message);
    }

    private String encryptionBase(RSAKey key, String message) {
        BigIntegerStringConverter converter = new BigIntegerStringConverter();
        BigInteger bigInteger = converter.fromString(message);
        bigInteger = bigInteger.modPow(key.ed, key.n);
        return converter.toString(bigInteger);
    }

    private static String encryptionBase(RSAKey key, int chunkSize, String message) {
        int i = 0;
        char[] buffer = new char[chunkSize];
        BigIntegerStringConverter converter = new BigIntegerStringConverter();
        StringBuilder newMessage = new StringBuilder();
        for (Character c : message.toCharArray()) {
            buffer[i++] = c;
            if (i == chunkSize) {
                int num = Integer.valueOf(String.valueOf(buffer));
                BigInteger bigNum = BigInteger.valueOf(num);
                bigNum = bigNum.modPow(key.ed, key.n);
                newMessage.append(converter.toString(bigNum));
                i = 0;
            }
        }
        if (i > 0) {
            int j = 0;
            char[] newBuffer = new char[i];
            while (j < i) {
                newBuffer[j] = buffer[j];
                j++;
            }
            int num = Integer.valueOf(String.valueOf(newBuffer));
            BigInteger bigNum = BigInteger.valueOf(num);
            bigNum = bigNum.modPow(key.ed, key.n);
            newMessage.append(converter.toString(bigNum));
        }
        return newMessage.toString();
    }

    private boolean isPrime(BigInteger n) {
        return fermatTest(n) && millerRabinTest(n);
    }

    private boolean fermatTest(BigInteger n) {
        BigInteger a = BigInteger.valueOf(2);
        return a.modPow(n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE);
    }

    private boolean millerRabinTest(BigInteger n) {
        return true;
    }



    public class RSAKeyPublic extends RSAKey {

        RSAKeyPublic(BigInteger ed, BigInteger n) {
            super(ed, n);
        }
    }

    public class RSAKeySecret extends RSAKey {

        RSAKeySecret(BigInteger ed, BigInteger n) {
            super(ed, n);
        }
    }
}
