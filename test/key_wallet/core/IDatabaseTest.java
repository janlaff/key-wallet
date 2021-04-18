package key_wallet.core;

import org.junit.Assert;
import org.junit.Test;

public class IDatabaseTest {
    @Test
    public void createDatabaseTest() throws DatabaseException {
        Assert.assertTrue(IDatabase.create("file:////secret.kw") instanceof FileDatabase);
        Assert.assertTrue(IDatabase.create("sqlite:////secret.db") instanceof SqliteDatabase);
    }
}
