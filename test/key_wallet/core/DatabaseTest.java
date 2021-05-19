package key_wallet.core;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseTest {
    @Test
    public void instantiateDatabaseTypeByURI() throws DatabaseException {
        // URI's with preceding 'file:////' should instantiate a LocalDatabase
        Assert.assertTrue(Database.create("file:////secret.kwdb") instanceof LocalDatabase);
        // URI's with preceding 'sqlite:////' should instantiate a SqliteDatabase
        Assert.assertTrue(Database.create("sqlite:////secret.db") instanceof SqliteDatabase);
    }
}
