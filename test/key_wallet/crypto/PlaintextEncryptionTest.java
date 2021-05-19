package key_wallet.crypto;

import org.junit.Assert;
import org.junit.Test;


public class PlaintextEncryptionTest {
    @Test
    public void passthroughData() {
        PlaintextEncryption pe = new PlaintextEncryption();

        byte[] randomData = new byte[] { 0x00, 0x52, 0x23, 0x55 };
        byte[] randomKey = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] result = pe.encrypt(randomData, randomKey);

        Assert.assertArrayEquals(randomData, result);
    }
}
