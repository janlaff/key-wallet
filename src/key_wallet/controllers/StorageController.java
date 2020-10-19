package key_wallet.controllers;

import java.util.UUID;

/*
TODO: create files schema
 - Stored credentials
 - Stored Hash of the master password
 - Determine existing installation
 - Allow to create new installation
 */

public class StorageController {
    private static StorageController instance;
    private StorageController() {}

    public static StorageController getInstance() {
        if (instance == null) {
            instance = new StorageController();
        }
        return instance;
    }

    public void saveCredentialToFile(UUID credentialId, String login, String encryptedPassword) {
        // TODO: save to disk
    }

    public void removeCredentialFile(UUID credentialId) {
        StorageController.getInstance().removeCredentialFile(credentialId);
    }
}
