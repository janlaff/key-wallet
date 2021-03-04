package key_wallet;

import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.data.Credential;
import key_wallet.core.Database;

import java.io.File;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        File dataFile = new File("./passwords.csv");
        MasterPassword mp = new MasterPassword("123");

        Database db = null;
        try {
            db = new Database(dataFile, mp);
        } catch (MasterPasswordException e) {
            System.err.println("Master password could not be validated");
        }


        db.addCredential(new Credential("web.de", "peter", "123"));
        db.saveToFile(dataFile, mp);
    }
}