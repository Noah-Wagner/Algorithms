import RSA.KeyPair;
import RSA.RSAHandler;
import javafx.util.converter.BigIntegerStringConverter;
import sun.plugin2.message.Message;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) {

        KeyPair keyPair = null;

        args = new String[1];
        args[0] = "file.txt.signed";

        if (args.length == 0) {
            keyPair = RSAHandler.generateKeyPair();
        } else {
            BigIntegerStringConverter converter2 = new BigIntegerStringConverter();
            keyPair = KeyPair.keyPairFromFile();
            assert (keyPair.getPublicKey().ed.equals(converter2.fromString("65537")));

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
                    System.out.println("File: " + signed);
                    System.out.println("SHA:  " + converter.toString(bigInteger));
                    System.out.println(converter.toString(bigInteger).equals(signed));


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
//        runTest();
    }

    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    private static void runTest() {
        KeyPair keyPair = RSAHandler.generateKeyPair();
        String m = "6882326865654132132135135684654326546513213179666683";
//        String m = "68823548426923726234852923546546548";
        String s1 = RSAHandler.applyKey(keyPair.getPublicKey(), m);
        String s2 = RSAHandler.applyKey(keyPair.getSecretKey(), s1);
        System.out.println("Encrypted: " + s1);
        System.out.println("Decrypted: " + s2);
        System.out.println("Original:  " + m);
        System.out.println(m.equals(s2));
    }
}
