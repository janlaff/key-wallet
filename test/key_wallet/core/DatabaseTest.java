package key_wallet.core;

import key_wallet.crypto.PlaintextEncryption;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

public class DatabaseTest {
    @Test(expected = MasterPasswordException.class)
    public void invalidMasterPasswordTest() throws IOException, MasterPasswordException, ParseException {
        /*MasterPassword mp = new MasterPassword("123", new PlaintextEncryption());
        // Write some random shit
        BufferedWriter writer = new BufferedWriter(new FileWriter("test.txt"));
        writer.write("invalid-stuff");
        writer.close();

        // Try to read the file as an database, it should fail horribly and throw an exception
        Database db = new Database(new File("test.txt"), mp);*/
    }
}
