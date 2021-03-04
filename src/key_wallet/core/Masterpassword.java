package key_wallet.core;

import java.nio.charset.StandardCharsets;

public class Masterpassword {
    private String password;

    public Masterpassword(String password) {
        this.password = password;
    }

    public String decrypt(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public byte[] encrypt(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }
}
