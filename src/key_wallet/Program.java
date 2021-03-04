package key_wallet;

import key_wallet.core.Masterpassword;
import key_wallet.db.Credential;
import key_wallet.db.Database;

import java.io.File;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        File dataFile = new File("./passwords.csv");
        Masterpassword mp = new Masterpassword("123");
        Database db = new Database(dataFile, mp);
        db.addCredential(new Credential("web.de", "peter", "123"));
        db.saveToFile(dataFile, mp);
    }
}