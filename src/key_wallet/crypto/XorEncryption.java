package key_wallet.crypto;

public class XorEncryption implements Encryption {
    @Override
    public byte[] cipher(byte[] data, byte[] key) {
        int keyIdx = 0;

        for (int dataIdx = 0; dataIdx < data.length; ++dataIdx) {
            data[dataIdx] ^= key[keyIdx++];
            keyIdx %= key.length;
        }
        return data;
    }
}
