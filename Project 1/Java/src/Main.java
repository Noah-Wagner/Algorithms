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

import RSA.KeyPair;
import RSA.RSAHandler;
import javafx.util.converter.BigIntegerStringConverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) {

        KeyPair keyPair = null;

        if (args.length == 0) {
            keyPair = RSAHandler.generateKeyPair();
        } else {
            keyPair = KeyPair.keyPairFromFile();

            if (getFileExtension(args[0]).equals(".signed")) {

                try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    StringBuilder builder = new StringBuilder();
                    String hash = br.readLine();
                    String line = br.readLine();

                    while (line != null) {
                        builder.append(line);
                        builder.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    line = builder.toString();

                    String signed = RSAHandler.applyKey(keyPair.getPublicKey(), hash);

                    digest.update(line.getBytes(Charset.defaultCharset()));
                    byte[] bytes = digest.digest();

                    BigInteger bigInteger = new BigInteger(1, bytes);

                    BigIntegerStringConverter converter = new BigIntegerStringConverter();
                    System.out.println(converter.toString(bigInteger).equals(signed) ? "File integrity verified" : "File modified!");


                } catch (NoSuchAlgorithmException | IOException e) {
                    e.printStackTrace();
                }


            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    StringBuilder builder = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        builder.append(line);
                        builder.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    line = builder.toString();
                    digest.update(line.getBytes(Charset.defaultCharset()));
                    byte[] bytes = digest.digest();

                    BigInteger bigInteger = new BigInteger(1, bytes);

                    BigIntegerStringConverter converter = new BigIntegerStringConverter();
                    String signed = RSAHandler.applyKey(keyPair.getSecretKey(), converter.toString(bigInteger));

                    try (FileWriter fileWriter = new FileWriter(args[0] + ".signed")) {
                        fileWriter.write(signed);
                        fileWriter.write(System.lineSeparator());
                        fileWriter.write(line);
                    }
                } catch (NoSuchAlgorithmException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    private static void runTest() {
        KeyPair keyPair = RSAHandler.generateKeyPair();
        String m = "6882326865654132132135135684654326546513213179666683";
        String s1 = RSAHandler.applyKey(keyPair.getPublicKey(), m);
        String s2 = RSAHandler.applyKey(keyPair.getSecretKey(), s1);
        System.out.println("Encrypted: " + s1);
        System.out.println("Decrypted: " + s2);
        System.out.println("Original:  " + m);
        System.out.println(m.equals(s2));
        assert (m.equals(s2));
    }
}
