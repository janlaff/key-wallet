package key_wallet;

import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.XorEncryption;
import key_wallet.data.Credential;
import key_wallet.core.Database;


import java.io.File;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException, MasterPasswordException {
        File dataFile = new File("secret_password_for_my_phub");
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new XorEncryption());

        Database db = null;
        try {
            db = new Database(dataFile, mp);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Current contents:");

        for (Credential c : db.getCredentials()) {
            System.out.println("Desc:" + c.description);
            System.out.println("Username:" + c.username);
            System.out.println("Password:" + c.password);
            System.out.println();
        }

        System.out.println("Adding credential...");
        db.addCredential(new Credential("web.de", "peter", "abc"));
        System.out.println("Saving...");
        db.saveToFile(dataFile, mp);
    }
}