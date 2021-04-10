package key_wallet.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption implements Encryption {
    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        try {
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES");

            c.init(Cipher.ENCRYPT_MODE, k);
            return c.doFinal(data);
        } catch (Exception e) {
            // Ignore
        }

        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        try {
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES");

            c.init(Cipher.DECRYPT_MODE, k);
            return c.doFinal(data);
        } catch (Exception e) {
            // Ignore
        }

        return new byte[0];
    }
}
