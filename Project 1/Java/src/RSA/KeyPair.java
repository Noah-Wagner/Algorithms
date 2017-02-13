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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class KeyPair {
    private RSAKeyPublic publicKey;
    private RSAKeySecret secretKey;

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
