package key_wallet.core;

import key_wallet.crypto.AESEncryption;
import key_wallet.data.CSVDataFormat;
import key_wallet.data.Credential;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class LocalDatabaseTest {
    @Test
    public void createNewDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dbFile = new File("./create-new-test.kw");
        dbFile.delete();

        LocalDatabase db = new LocalDatabase(dbFile, new CSVDataFormat());
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        db.open(mp);

        Assert.assertTrue(dbFile.isFile());
        Assert.assertTrue(dbFile.delete());
    }

    @Test
    public void openExistingDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dbFile = new File("./open-existing-test.kw");
        dbFile.delete();

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        {
            LocalDatabase db = new LocalDatabase(dbFile, new CSVDataFormat());
            db.open(mp);

            Assert.assertTrue(dbFile.isFile());
        }

        LocalDatabase db = new LocalDatabase(dbFile, new CSVDataFormat());
        db.open(mp);

        Assert.assertTrue(dbFile.delete());
    }

    @Test
    public void loadDataFromDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dbFile = new File("./load-data-test.kw");
        dbFile.delete();

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        Credential sample = new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website");

        {
            LocalDatabase db = new LocalDatabase(dbFile, new CSVDataFormat());
            db.open(mp);
            db.insertCredential(sample);

            Assert.assertTrue(dbFile.isFile());
        }

        LocalDatabase db = new LocalDatabase(dbFile, new CSVDataFormat());
        db.open(mp);
        Assert.assertEquals(sample.password, db.fetchCredential(0).password);
        Assert.assertTrue(dbFile.delete());
    }

    @Test
    public void generateSampleDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dataFile = new File("secret.kwdb");

        if (dataFile.exists()) {
            dataFile.delete();
        }

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        LocalDatabase db = new LocalDatabase(dataFile, new CSVDataFormat());
        db.open(mp);

        db.insertCredential(new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website"));
        db.insertCredential(new Credential("GMail", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://mail.google.de", "Email"));
        db.insertCredential(new Credential("Moodle", "", "max.mustermann@student.dhbw-karlsruhe.de", "m@xThaGangs1a", "https://moodle.dhbw.de", "Other"));
    }
}
