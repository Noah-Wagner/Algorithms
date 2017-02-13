package RSA;

import javafx.util.converter.BigIntegerStringConverter;

import java.io.*;
import java.math.BigInteger;

/**
 * Created by noah on 2/13/17.
 */
public class KeyPair {
    private RSAKeyPublic publicKey;
    private RSAKeySecret secretKey;

    private KeyPair() {

    }

    KeyPair(RSAKeyPublic publicKey, RSAKeySecret secretKey) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
        writeKeyPair();
    }

    public RSAKeyPublic getPublicKey() {
        return publicKey;
    }

    public RSAKeySecret getSecretKey() {
        return secretKey;
    }

    public static KeyPair keyPairFromFile() {
        KeyPair keyPair = null;
        try (BufferedReader br = new BufferedReader(new FileReader("d_n.txt"))) {
            BigIntegerStringConverter converter = new BigIntegerStringConverter();
            String line = br.readLine();
            BigInteger d = converter.fromString(line);
            line = br.readLine();
            BigInteger n = converter.fromString(line);

            try (BufferedReader br2 = new BufferedReader(new FileReader("e_n.txt"))) {
                line = br2.readLine();
                BigInteger e = converter.fromString(line);
                keyPair = new KeyPair(new RSAKeyPublic(e, n), new RSAKeySecret(d, n));
//                keyPair = new KeyPair();
//                keyPair.publicKey = new RSAKeyPublic(d, n);
//                keyPair.secretKey = new RSAKeySecret(e, n);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    private void writeKeyPair() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("e_n.txt");
            BigIntegerStringConverter converter = new BigIntegerStringConverter();
            fileWriter.write(converter.toString(publicKey.ed) + "\n");
            fileWriter.write(converter.toString(publicKey.n) + "\n");
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
        fileWriter = null;
        try {
            fileWriter = new FileWriter("d_n.txt");
            BigIntegerStringConverter converter = new BigIntegerStringConverter();
            fileWriter.write(converter.toString(secretKey.ed) + "\n");
            fileWriter.write(converter.toString(secretKey.n) + "\n");
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


    public static class RSAKeyPublic extends RSAKey {
        RSAKeyPublic(BigInteger ed, BigInteger n) {
            super(ed, n);
        }
    }

    public static class RSAKeySecret extends RSAKey {
        RSAKeySecret(BigInteger ed, BigInteger n) {
            super(ed, n);
        }
    }

}
