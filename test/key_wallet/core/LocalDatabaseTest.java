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

        db.createOrOpen(mp);

        // Check if new database file was created
        Assert.assertTrue(dbFile.isFile());
    }

    @Test
    public void openExistingDatabase() throws MasterPasswordException, DatabaseException {
        File dbFile = new File(temp.getRoot() + "/database.kw");
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        {
            LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
            // Saves a copy silently to 'dbFile'
            db.createOrOpen(mp);

            // File should exist now
            Assert.assertTrue(dbFile.isFile());
        }

        LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
        db.createOrOpen(mp); // Can throw if not successful
    }

    @Test
    public void readWriteItemsToDatabase() throws MasterPasswordException, DatabaseException {
        File dbFile = new File(temp.getRoot() + "/database.kw");

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        Credential sample = new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website");

        // Create database containing a credential
        {
            LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
            db.createOrOpen(mp);
            db.insertCredential(sample);

            Assert.assertTrue(dbFile.isFile());
        }

        LocalDatabase db = new LocalDatabase(dbFile, new JSONDataFormat());
        db.createOrOpen(mp);

        // Check that credential was properly saved to file
        Assert.assertEquals(sample.password, db.fetchCredential(0).password);
    }
}
