import RSA.RSAHandler;

import java.io.Console;

public class Main {

    public static void main(String[] args) {
        RSAHandler handler = new RSAHandler();
//        String m = "6882326879666683";
        String m = "6882";
        String s1 = handler.encrypt(m);
        String s2 = handler.decrypt(s1);
        System.out.println("Encrypted: " + s1);
        System.out.println("Decrypted: " + s2);
        System.out.println("Original:  " + m);
        System.out.println(m.equals(s2));

    }
}
