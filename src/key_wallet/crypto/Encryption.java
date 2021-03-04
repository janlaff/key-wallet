package key_wallet.crypto;

/**
 * Simple interface to encapsulate symmetric encryption
 */
public interface Encryption {
    // The cipher method is both used to encrypt and decrypt byte blocks
    byte[] cipher(byte[] data, byte[] key);
}
