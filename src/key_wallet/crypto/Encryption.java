package key_wallet.crypto;

/**
 * Simple interface to encapsulate symmetric encryption
 */
public interface Encryption {
    byte[] encrypt(byte[] data, byte[] key);
    byte[] decrypt(byte[] data, byte[] key);
}
