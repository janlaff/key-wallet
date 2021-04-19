package key_wallet.core;

import key_wallet.crypto.Encryption;

import java.nio.charset.StandardCharsets;

public class MasterPassword {
    private final byte[] password;
    private final Encryption encryption;

    public MasterPassword(String password, Encryption encryption) throws MasterPasswordException {
        validatePassword(password);
        this.encryption = encryption;
        this.password = password.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] decrypt(byte[] data) {
        return encryption.decrypt(data, password);
    }

    public byte[] encrypt(byte[] data) {
        return encryption.encrypt(data, password);
    }

    private void validatePassword(String password) throws MasterPasswordException {
        if (password.length() < 8) {
            throw new MasterPasswordException("Password needs to be at least 8 characters long!");
        }

        // Check if password contains digit
        if (!password.matches("(?=.*[0-9]).*")) {
            throw new MasterPasswordException("Password needs to contain at least one digit!");
        }

        // Check if password contains letter
        if (!password.matches("(?=.*[a-zA-Z]).*")) {
            throw new MasterPasswordException("Password needs to contain at least one letter!");
        }

        // Maybe also add the need for special characters
        // if (!password.matches("(?=.*[~!@#$%^&*()_-]).*")) {
        //     throw new MasterPasswordException("Password needs to contain at least one special character!");
        // }
    }
}
