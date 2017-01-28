import RSA.RSAHandler;

import java.io.Console;

public class Main {

    public static void main(String[] args) {
        RSAHandler handler = new RSAHandler();
        System.out.println(handler.encrypt("6882326879666683"));
    }
}
