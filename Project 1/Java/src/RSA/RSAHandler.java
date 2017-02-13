package RSA;

import javafx.util.converter.BigIntegerStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;



public class RSAHandler {

    public static KeyPair generateKeyPair() {

        BigInteger p = getRandomPrime(512);
        BigInteger q = getRandomPrime(512);
        writeBigInt(new ArrayList<BigInteger>(Arrays.asList(p, q)));

        BigInteger n = p.multiply(q);

        BigInteger n_mod = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        n_mod = n_mod.divide(extendedEuclid(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE)).d);

        BigInteger e = getCoprime();
        assert (e.compareTo(n_mod) < 0);
        assert (extendedEuclid(e, n_mod).d.equals(BigInteger.ONE));

        EEWrapper wrapper = multiplicativeInverse(e, n_mod);

        return new KeyPair(new KeyPair.RSAKeyPublic(e, n), new KeyPair.RSAKeySecret(wrapper.x, n));
    }

    private static BigInteger getRandomPrime(int numBits) {
        BigInteger random = new BigInteger(numBits, new Random());
        random = random.setBit(0); // Required to have an odd number!
        while (!isPrime(random)) {
            random = random.add(BigInteger.valueOf(2));
        }
        return random;
    }

    private static void writeBigInt(ArrayList<BigInteger> list) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("p_q.txt");
            BigIntegerStringConverter converter = new BigIntegerStringConverter();
            for (BigInteger bigInt : list) {
                fileWriter.write(converter.toString(bigInt) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static BigInteger getCoprime() {
        return BigInteger.valueOf(65537);
    }

    private static EEWrapper multiplicativeInverse(BigInteger x, BigInteger y) {
        EEWrapper wrapper = extendedEuclid(x, y);
        wrapper.x = wrapper.x.mod(y).add(y).mod(y);
        return wrapper;
    }

    private static EEWrapper extendedEuclid(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return new EEWrapper(a, BigInteger.ONE, BigInteger.ZERO);
        EEWrapper wrapper = extendedEuclid(b, a.mod(b));
        return new EEWrapper(wrapper.d, wrapper.y, wrapper.x.subtract(a.divide(b).multiply(wrapper.y)));
    }

    private static class EEWrapper {
        BigInteger d;
        BigInteger x;
        BigInteger y;

        EEWrapper(BigInteger d, BigInteger x, BigInteger y) {
            this.d = d;
            this.x = x;
            this.y = y;
        }
    }

    public static String applyKey(KeyPair.RSAKeyPublic key, String message) {
        return encryptionBase(key, (int) characterCount(key.n), message);

    }

    public static String applyKey(KeyPair.RSAKeySecret key, String message) {
        return encryptionBase(key, (int) characterCount(key.n) + 1, message);

    }

    private static String encryptionBase(RSAKey key, int chunkSize, String message) {
        int i = 0;
        char[] buffer = new char[chunkSize];
        BigIntegerStringConverter converter = new BigIntegerStringConverter();
        StringBuilder newMessage = new StringBuilder();
        for (Character c : message.toCharArray()) {
            buffer[i++] = c;
            if (i == chunkSize) {
                BigInteger bigNum = converter.fromString(String.valueOf(buffer));
                bigNum = bigNum.modPow(key.ed, key.n);
                char[] s1 = new char[chunkSize + 1];
                String s2 = converter.toString(bigNum);
                if (chunkSize == 3) {
                    int j = chunkSize;
                    for (int k = s2.length() - 1; k >= 0; j--, k--) {
                        s1[j] = s2.charAt(k);
                    }
                    while (j >= 0) {
                        s1[j] = '0';
                        j--;
                    }
                    s2 = new String(s1);
                }
                newMessage.append(s2);
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
            BigInteger bigNum = converter.fromString(String.valueOf(newBuffer));
            bigNum = bigNum.modPow(key.ed, key.n);
            newMessage.append(converter.toString(bigNum));
        }
        return newMessage.toString();
    }

    private static boolean isPrime(BigInteger n) {
        return fermatTest(n) && millerRabinTest(n);
    }

    private static boolean fermatTest(BigInteger n) {
        BigInteger a = BigInteger.valueOf(2);
        return a.modPow(n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE);
    }

    private static boolean millerRabinTest(BigInteger n) {
        return true;
    }

    private static long characterCount (BigInteger n) {
        return Math.round(n.bitLength() * Math.log(2)/Math.log(10));
    }



}
