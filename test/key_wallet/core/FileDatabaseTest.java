package key_wallet.core;

import key_wallet.crypto.AESEncryption;
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
}
