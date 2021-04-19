package key_wallet.core;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseTest {
    @Test
    public void createDatabaseTest() throws DatabaseException {
        Assert.assertTrue(Database.create("file:////secret.kwdb") instanceof LocalDatabase);
        Assert.assertTrue(Database.create("sqlite:////secret.db") instanceof SqliteDatabase);
    }
}
