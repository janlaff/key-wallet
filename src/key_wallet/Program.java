package key_wallet;

import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.XorEncryption;
import key_wallet.data.Credential;
import key_wallet.core.Database;


import java.io.File;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        File dataFile = new File("secret_password_for_my_phub");
        MasterPassword mp = new MasterPassword("kjblkasdfjnlkjnsdaflkjnasdlkjfnlksdajnflkjsdanflkjnsadflkjndsa", new XorEncryption());

        Database db = null;
        try {
            db = new Database(dataFile, mp);
        } catch (MasterPasswordException e) {
            System.err.println("Master password could not be validated");
        }

        for (Credential c : db.getCredentials()) {
            System.out.println("Desc:" + c.description);
            System.out.println("Username:" + c.username);
            System.out.println("Password:" + c.password);
            System.out.println();
        }

        db.addCredential(new Credential("web.de", "peter", "abc"));
        db.saveToFile(dataFile, mp);
    }
}