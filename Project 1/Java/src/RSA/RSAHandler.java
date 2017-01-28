package RSA;

import javafx.util.converter.BigIntegerStringConverter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by noah on 1/28/17.
 */

public class RSAHandler {

    RSAKey publicKey;
    RSAKey secretKey;

    BigInteger n;

    public RSAHandler() {

        BigInteger p = getRandomPrime();
        BigInteger q = getRandomPrime();

        p = BigInteger.valueOf(47);
        q = BigInteger.valueOf(71);

        n = p.multiply(q);

        BigInteger n_mod = (p.subtract(BigInteger.valueOf(1))).multiply(q.subtract(BigInteger.valueOf(1)));

        BigInteger e = getRelativePrime(n_mod);
        e = BigInteger.valueOf(79);
        BigInteger d = multiplicativeInverse(e, n_mod);
        publicKey = new RSAKeyPublic(e, n);
        secretKey = new RSAKeySecret(d, n);
    }

    private BigInteger getRandomPrime() {
        return BigInteger.probablePrime(320, new Random());
    }

    private BigInteger getRelativePrime(BigInteger n) {
        //TODO: Implement actual random here
        return n.add(BigInteger.valueOf(1));
    }

    private BigInteger multiplicativeInverse(BigInteger e, BigInteger n) {
        int i = 1;
        BigInteger[] bigIntegers;
        while (true) {
            bigIntegers = (n.multiply(BigInteger.valueOf(i))).add(BigInteger.valueOf(1)).divideAndRemainder(e);
            if (bigIntegers[1].compareTo(BigInteger.ZERO) == 0) {
                break;
            }
            i++;
        }
        return bigIntegers[0];
    }


    public String encrypt(String message) {
        return encryptionBase(publicKey, 3, message);
    }

    public String decrypt(String message) {
        return encryptionBase(secretKey, 4, message);
    }

    private String encryptionBase(RSAKey key, int chunkSize, String message) {
        int i = 0;
        char[] buffer = new char[chunkSize];
        BigIntegerStringConverter converter = new BigIntegerStringConverter();
        StringBuilder newMessage = new StringBuilder();
        for (Character c : message.toCharArray()) {
            buffer[i++] = c;
            if (i == chunkSize) {
                String bufferString = String.valueOf(buffer) + key.ed;
                newMessage.append(converter.fromString(bufferString).mod(n));
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
            String bufferString = String.valueOf(newBuffer) + key.ed;
            newMessage.append(converter.fromString(bufferString).mod(n));
        }
        return newMessage.toString();
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
