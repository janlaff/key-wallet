package key_wallet.core;

import key_wallet.crypto.PlaintextEncryption;
import org.junit.Ignore;
import org.junit.Test;

public class MasterPasswordTest {
    @Test(expected = MasterPasswordException.class)
    public void emptyPasswordTest() throws MasterPasswordException {
        new MasterPassword("", new PlaintextEncryption());
    }

    @Ignore
    @Test(expected = MasterPasswordException.class)
    public void weakPasswordTest() {
        // TODO
    }
}
