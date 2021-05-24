package key_wallet;

import key_wallet.core.DatabaseException;
import key_wallet.core.LocalDatabase;
import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;
import key_wallet.data.JSONDataFormat;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class HandySnippets {
    // Not a real unit test!!
    // Just a simple code snippet to generate a demo wallet
    @Ignore("Only useful for development")
    @Test
    public void generateSampleWallet() throws MasterPasswordException, DatabaseException {
        // Note that this is not using the temporary folder
        File dataFile = new File("secret.kwdb");

        // Override existing wallet
        if (dataFile.exists()) {
            dataFile.delete();
        }

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        LocalDatabase db = new LocalDatabase(dataFile, new JSONDataFormat());
        db.createOrOpen(mp);

        db.insertCredential(new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website"));
        db.insertCredential(new Credential("GMail", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://mail.google.de", "Email"));
        db.insertCredential(new Credential("Moodle", "", "max.mustermann@student.dhbw-karlsruhe.de", "m@xThaGangs1a", "https://moodle.dhbw.de", "Other"));
    }
}
