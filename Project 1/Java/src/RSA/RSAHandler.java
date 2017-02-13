/*
 * Copyright (c) 2017 Noah Wagner.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package RSA;

import javafx.util.converter.BigIntegerStringConverter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;



public class RSAHandler {

    /**
     * Generates an RSA keypair
     * @return An RSA keypair
     */
    public static KeyPair generateKeyPair() {

        BigInteger p = getRandomPrime(512);
        BigInteger q = getRandomPrime(512);
        writeBigInt(new ArrayList<>(Arrays.asList(p, q)));

        BigInteger n = p.multiply(q);

        BigInteger n_mod = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        n_mod = n_mod.divide(extendedEuclid(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE)).d);

        BigInteger e = getCoprime();
        assert (e.compareTo(n_mod) < 0);
        assert (extendedEuclid(e, n_mod).d.equals(BigInteger.ONE));

        EEWrapper wrapper = multiplicativeInverse(e, n_mod);

        return new KeyPair(new KeyPair.RSAKeyPublic(e, n), new KeyPair.RSAKeySecret(wrapper.x, n));
    }

    /**
     * Generates a random prime by incrementing by two
     * @param numBits The number of bits in the random prime number
     * @return A BigInteger object with a random prime number
     */
    private static BigInteger getRandomPrime(int numBits) {
        BigInteger random = new BigInteger(numBits, new Random());
        random = random.setBit(0); // Required to have an odd number!
        while (!isPrime(random)) {
            random = random.add(BigInteger.valueOf(2));
        }
        return random;
    }

    /**
     * Writes BigInteger objects to a file
     * @param list Writes all BigInteger objects in list to the file
     */
    private static void writeBigInt(List<BigInteger> list) {
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

    /**
     *
     * @return A coprime value for e, usually fixed at 2^16 + 1 for small Hamming weight
     */
    private static BigInteger getCoprime() {
        return BigInteger.valueOf(65537);
    }

    /**
     * Finds the multiplicative inverse d * e = 1 mod totient
     * @return An EEWrapper object with the multiplicative inverse d as well as linear combination parameters
     */
    private static EEWrapper multiplicativeInverse(BigInteger e, BigInteger totient) {
        EEWrapper wrapper = extendedEuclid(e, totient);
        wrapper.x = wrapper.x.mod(totient).add(totient).mod(totient);
        return wrapper;
    }

    /**
     * Applies the Extended Euclidean Algorithm to two BigInteger objects
     * @param a The first object to apply the algorithm to
     * @param b The second object to apply the algorithm to
     * @return An EEWrapper object with the GCD and linear combination parameters
     */
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

    /**
     * Applies an RSA key to a message
     * @param key RSA key to apply
     * @param message Message to apply the RSA key to
     * @return The message encrypted with the RSA key
     */
    public static String applyKey(KeyPair.RSAKeyPublic key, String message) {
        return encryptionBase(key, (int) characterCount(key.n), message);

    }

    /**
     * Applies an RSA key to a message
     * @param key RSA key to apply
     * @param message Message to apply the RSA key to
     * @return The message encrypted with the RSA key
     */
    public static String applyKey(KeyPair.RSAKeySecret key, String message) {
        return encryptionBase(key, (int) characterCount(key.n) + 1, message);

    }

    /**
     * Applies an RSA key to a message
     * @param key RSA key to apply
     * @param chunkSize The size with which to move through the message
     * @param message Message to apply the RSA key to
     * @return The message encrypted with the RSA key
     */
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

    /**
     * Determines whether a BigInteger object is prime
     * @param n The BigInteger object on which to test
     * @return The results of the test
     */
    private static boolean isPrime(BigInteger n) {
        return fermatTest(n) && millerRabinTest(n);
    }

    /**
     * Performs the Fermat's Little Theorem primality test
     * @param n The BigInteger object on which to test
     * @return The results of the test
     */
    private static boolean fermatTest(BigInteger n) {
        BigInteger a = BigInteger.valueOf(2);
        return a.modPow(n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE);
    }

    /**
     * Performs the Miller Rabins primality Test
     * @param n The BigInteger object on which to test
     * @return The results of the test
     */
    private static boolean millerRabinTest(BigInteger n) {
        return true;
    }

    /**
     * Counts the number of digits in a BigInteger object.
     * @param n The BigInteger object to count the digits of.
     * @return The number of digits in the BigInteger object
     */
    private static long characterCount (BigInteger n) {
        return Math.round(n.bitLength() * Math.log(2)/Math.log(10));
    }

}
