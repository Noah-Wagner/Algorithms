import RSA.KeyPair;
import RSA.RSAHandler;
import sun.plugin2.message.Message;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) {

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
            System.out.println(new String(bytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
