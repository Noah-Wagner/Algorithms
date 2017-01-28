import RSA.RSAHandler;

import java.io.Console;

public class Main {

    public static void main(String[] args) {
        RSAHandler handler = new RSAHandler();
        String s1 = handler.encrypt("6882326879666683");
        System.out.println(s1);
        System.out.println(handler.decrypt(s1));
    }
}
