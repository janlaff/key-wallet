package key_wallet.crypto;

public class PlaintextEncryption implements Encryption {
    @Override
    public byte[] cipher(byte[] data, byte[] key) {
        return data;
    }
}
