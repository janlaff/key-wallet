package key_wallet.controllers;

public class EncryptionController {
    private static EncryptionController instance;
    private EncryptionController() {}

    public static EncryptionController getInstance() {
        if (instance == null) {
            instance = new EncryptionController();
        }
        return instance;
    }

    public String encryptPassword(String passwordToEncrypt, String masterPassword) {
        // TODO: encrypt the password with the master password
        return passwordToEncrypt;
    }

    public String decryptPassword(String passwordToDecrypt, String masterPassword) {
        // TODO: decrypt the password with the master password
        return passwordToDecrypt;
    }
}
