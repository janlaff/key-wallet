package key_wallet.core;

import java.nio.charset.StandardCharsets;

public class MasterPassword {
    private byte[] password;

    public MasterPassword(String password) {
        this.password = password.getBytes(StandardCharsets.UTF_8);
    }

    public String decrypt(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public byte[] encrypt(String data) {
        byte[] strBytes = data.getBytes(StandardCharsets.UTF_8);

        // TODO: encrypt

        return strBytes;
    }
}
