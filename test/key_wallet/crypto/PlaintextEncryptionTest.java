package key_wallet.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlaintextEncryptionTest {
    @Test
    public static void testInputEqualsOutput() {
        PlaintextEncryption pe = new PlaintextEncryption();

        byte[] randomData = new byte[] { 0x00, 0x52, 0x23, 0x55 };
        byte[] randomKey = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] result = pe.cipher(randomData, randomKey);

        Assertions.assertArrayEquals(randomData, result);
    }
}
