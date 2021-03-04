package key_wallet.core;

import key_wallet.crypto.PlaintextEncryption;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class MasterPasswordTest {
    @Test(expected = MasterPasswordException.class)
    public void emptyPasswordTest() throws MasterPasswordException {
        new MasterPassword("", new PlaintextEncryption());
    }

    @Test
    public void encryptAndDecryptTest() throws MasterPasswordException {
        MasterPassword mp = new MasterPassword("ichtrinkenurbier", new PlaintextEncryption());
        String randomData = "Fischers Fritz";
        byte[] encryptedData = mp.encrypt(randomData);
        String result = mp.decrypt(encryptedData);

        Assert.assertEquals(randomData, result);
    }

    @Ignore
    @Test(expected = MasterPasswordException.class)
    public void weakPasswordTest() {
        // TODO
    }
}
