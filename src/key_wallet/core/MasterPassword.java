package key_wallet.core;

import key_wallet.crypto.Encryption;

import java.nio.charset.StandardCharsets;

public class MasterPassword {
    private final byte[] password;
    private final Encryption encryption;

    public MasterPassword(String password, Encryption encryption) {
        this.encryption = encryption;
        this.password = password.getBytes(StandardCharsets.UTF_8);
    }

    public String decrypt(byte[] data) {
        return new String(encryption.cipher(data, password), StandardCharsets.UTF_8);
    }

    public byte[] encrypt(String data) {
        byte[] strBytes = data.getBytes(StandardCharsets.UTF_8);
        return encryption.cipher(strBytes, password);
    }
}
