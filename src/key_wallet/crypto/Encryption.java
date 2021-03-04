package key_wallet.crypto;

public interface Encryption {
    byte[] cipher(byte[] data, byte[] key);
}
