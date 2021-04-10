package key_wallet.crypto;

public class PlaintextEncryption implements Encryption {
    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        return data;
    }
}
