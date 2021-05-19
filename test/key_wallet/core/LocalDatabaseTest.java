package key_wallet.core;

import key_wallet.crypto.AESEncryption;
import key_wallet.data.JSONDataFormat;
import key_wallet.data.Credential;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class LocalDatabaseTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void createNewLocalDatabaseFile() throws MasterPasswordException, DatabaseException {
        File dbFile = new File(temp.getRoot() + "/database.kw");

        LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        db.open(mp);

        // Check if new database file was created
        Assert.assertTrue(dbFile.isFile());
    }

    @Test
    public void openExistingDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dbFile = new File(temp.getRoot() + "/database.kw");
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        {
            LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
            // Saves a copy silently to 'dbFile'
            db.open(mp);

            // File should exist now
            Assert.assertTrue(dbFile.isFile());
        }

        LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
        db.open(mp); // Can throw if not successful
    }

    @Test
    public void loadDataFromDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dbFile = new File(temp.getRoot() + "/database.kw");

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        Credential sample = new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website");

        // Create database containing a credential
        {
            LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
            db.open(mp);
            db.insertCredential(sample);

            Assert.assertTrue(dbFile.isFile());
        }

        LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
        db.open(mp);

        // Check that credential was properly saved to file
        Assert.assertEquals(sample.password, db.fetchCredential(0).password);
    }

    // Not a real unit test!!
    // Just a simple code snippet to generate a demo wallet
    @Ignore("Only useful for development")
    @Test
    public void generateSampleDatabaseTest() throws MasterPasswordException, DatabaseException {
        // Note that this is not using the temporary folder
        File dataFile = new File("secret.kwdb");

        // Override existing wallet
        if (dataFile.exists()) {
            dataFile.delete();
        }

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        LocalDatabase db = new LocalDatabase(dataFile, new JSONDataFormat());
        db.open(mp);

        db.insertCredential(new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website"));
        db.insertCredential(new Credential("GMail", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://mail.google.de", "Email"));
        db.insertCredential(new Credential("Moodle", "", "max.mustermann@student.dhbw-karlsruhe.de", "m@xThaGangs1a", "https://moodle.dhbw.de", "Other"));
    }
}
