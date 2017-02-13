package RSA;

import javafx.util.converter.BigIntegerStringConverter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by noah on 2/13/17.
 */
public class KeyPair {
    final private RSAKeyPublic publicKey;
    final private RSAKeySecret secretKey;

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


    static class RSAKeyPublic extends RSAKey {
        RSAKeyPublic(BigInteger ed, BigInteger n) {
            super(ed, n);
        }
    }

    static class RSAKeySecret extends RSAKey {
        RSAKeySecret(BigInteger ed, BigInteger n) {
            super(ed, n);
        }
    }

}
