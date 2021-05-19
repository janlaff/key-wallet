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

public class KWDBFileFormatTest {
    @Test(expected = IOException.class)
    public void readNonExistentFile() throws MasterPasswordException, IOException, DataFormatException {
        MasterPassword masterPassword = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        KWDBFileFormat.read(new File("does-not-exist.kwdb"), masterPassword);
    }

    @Test
    public void readInvalidFile() throws IOException, MasterPasswordException {
        File invalidFile = new File("invalid.kwdb");

        // Fill file with random data
        OutputStream os = new FileOutputStream(invalidFile);
        os.write("lkmasdlkmaslkdmlkmsad".getBytes(StandardCharsets.UTF_8));
        os.close();

        MasterPassword masterPassword = new MasterPassword("1chtrinkenurBIER", new AESEncryption());

        // Make sure it was written to disk
        Assert.assertTrue(invalidFile.isFile());
        try {
            KWDBFileFormat.read(invalidFile, masterPassword);
            // If no exception was thrown, something went terribly wrong
            Assert.fail();
        } catch (DataFormatException ignored) { }
        // Remove file for next test iteration
        Assert.assertTrue(invalidFile.delete());
    }

    @Test
    public void readWithWrongMasterPassword() throws MasterPasswordException, IOException, DataFormatException {
        File someFile = new File("some-wallet.kwdb");
        KWDBFileFormat.write(
                "hello world".getBytes(StandardCharsets.UTF_8),
                someFile,
                new MasterPassword("cry1forAnother", new AESEncryption())
        );

        // Check if file was saved to disk
        Assert.assertTrue(someFile.isFile());
        try {
            // Try open with wrong master password
            KWDBFileFormat.read(someFile, new MasterPassword("1chtrinkenurBIER", new AESEncryption()));
            // If no exception was thrown, something went terribly wrong
            Assert.fail();
        } catch (MasterPasswordException ignored) { }
        // Remove file for next test iteration
        Assert.assertTrue(someFile.delete());
    }

    @Test
    public void writeReadFile() throws MasterPasswordException, IOException, DataFormatException {
        // Create valid kwdb file
        File dbFile = new File("someDb.kwdb");
        MasterPassword masterPassword = new MasterPassword("1chtrinkenurBIER", new AESEncryption());
        KWDBFileFormat.write("hello".getBytes(StandardCharsets.UTF_8), dbFile, masterPassword);

        // Make sure it was saved to disk
        Assert.assertTrue(dbFile.isFile());
        // Read back data and check it data that was written before
        String result = new String(KWDBFileFormat.read(dbFile, masterPassword), StandardCharsets.UTF_8);
        Assert.assertEquals("hello", result);
        // Remove file for next test iteration
        Assert.assertTrue(dbFile.delete());
    }
}
