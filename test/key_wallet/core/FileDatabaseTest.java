package key_wallet.core;

import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FileDatabaseTest {
    @Test
    public void createNewDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dbFile = new File("./create-new-test.kw");
        dbFile.delete();

        FileDatabase db = new FileDatabase(dbFile);
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
            FileDatabase db = new FileDatabase(dbFile);
            db.open(mp);

            Assert.assertTrue(dbFile.isFile());
        }

        FileDatabase db = new FileDatabase(dbFile);
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
            FileDatabase db = new FileDatabase(dbFile);
            db.open(mp);
            db.insertCredential(sample);

            Assert.assertTrue(dbFile.isFile());
        }

        FileDatabase db = new FileDatabase(dbFile);
        db.open(mp);
        Assert.assertEquals(sample.password, db.fetchCredential(0).password);
        Assert.assertTrue(dbFile.delete());
    }

    @Test
    public void generateSampleDatabaseTest() throws MasterPasswordException, DatabaseException {
        File dataFile = new File("secret.kw");

        if (dataFile.exists()) {
            dataFile.delete();
        }

        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        FileDatabase db = new FileDatabase(dataFile);
        db.open(mp);

        db.insertCredential(new Credential("Google", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://google.de", "Website"));
        db.insertCredential(new Credential("GMail", "", "max.mustermann@gmail.com", "m@xThaGangs1a", "https://mail.google.de", "Email"));
        db.insertCredential(new Credential("Moodle", "", "max.mustermann@student.dhbw-karlsruhe.de", "m@xThaGangs1a", "https://moodle.dhbw.de", "Other"));
    }
}
