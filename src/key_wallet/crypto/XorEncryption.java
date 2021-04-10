package key_wallet.crypto;

public class XorEncryption implements Encryption {
    private byte[] cipher(byte[] data, byte[] key) {
        int keyIdx = 0;

        for (int dataIdx = 0; dataIdx < data.length; ++dataIdx) {
            data[dataIdx] ^= key[keyIdx++];
            keyIdx %= key.length;
        }
        return data;
    }

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        return cipher(data, key);
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        return cipher(data, key);
    }
}
