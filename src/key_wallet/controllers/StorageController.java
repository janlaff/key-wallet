package key_wallet.controllers;

import java.util.UUID;

public class StorageController {
    private static StorageController instance;
    private StorageController() {}

    public static StorageController getInstance() {
        if (instance == null) {
            instance = new StorageController();
        }
        return instance;
    }

    public boolean saveCredentialToFile(UUID credentialId, String login, String encryptedPassword) {
        // TODO: save to disk
        return true;
    }

    public void removeCredentialFile(UUID credentialId) {
        StorageController.getInstance().removeCredentialFile(credentialId);
    }
}
