package key_wallet.data;

import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.AESEncryption;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class KWDBFormatTest {
    @Test(expected = IOException.class)
    public void readNonExistentFileTest() throws MasterPasswordException, IOException, DataFormatException {
        MasterPassword masterPassword = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        KWDBFormat.read(new File("does-not-exist.kwdb"), masterPassword);
    }

    @Test
    public void readInvalidFileTest() throws IOException, MasterPasswordException {
        File invalidFile = new File("invalid.kwdb");
        OutputStream os = new FileOutputStream(invalidFile);
        os.write("lkmasdlkmaslkdmlkmsad".getBytes(StandardCharsets.UTF_8));
        os.close();

        MasterPassword masterPassword = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        Assert.assertTrue(invalidFile.isFile());
        try {
            KWDBFormat.read(invalidFile, masterPassword);
            Assert.fail();
        } catch (DataFormatException ignored) { }
        Assert.assertTrue(invalidFile.delete());
    }

    @Test
    public void readWithWrongMasterPasswordTest() throws MasterPasswordException, IOException, DataFormatException {
        File someFile = new File("some-wallet.kwdb");
        KWDBFormat.write(
                "hello world".getBytes(StandardCharsets.UTF_8),
                someFile,
                new MasterPassword("cry1forAnother", new AESEncryption())
        );

        Assert.assertTrue(someFile.isFile());
        try {
            KWDBFormat.read(someFile, new MasterPassword("1chtrinkenurBIER", new AESEncryption()));
            Assert.fail();
        } catch (MasterPasswordException ignored) { }
        Assert.assertTrue(someFile.delete());
    }

    @Test
    public void writeReadFileTest() throws MasterPasswordException, IOException, DataFormatException {
        File dbFile = new File("someDb.kwdb");
        MasterPassword masterPassword = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        KWDBFormat.write("hello".getBytes(StandardCharsets.UTF_8), dbFile, masterPassword);

        Assert.assertTrue(dbFile.isFile());
        String result = new String(KWDBFormat.read(dbFile, masterPassword), StandardCharsets.UTF_8);
        Assert.assertEquals("hello", result);
        Assert.assertTrue(dbFile.delete());
    }
}
