package key_wallet.crypto;

public class LFSREncryption implements Encryption {
    private byte[] cipher(byte[] data, byte[] key) {
        byte xoredKey = key[0];
        for (int i = 1; i < key.length; ++i) {
            xoredKey ^= key[i];
        }

        try {
            LFSR lfsr = new LFSR(8, 0x8E, xoredKey);

            for (int i = 0; i < data.length; ++i) {
                data[i] ^= lfsr.nextByte();
            }

            return data;
        } catch (LFSRException e) {
           return new byte[]{};
        }
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
