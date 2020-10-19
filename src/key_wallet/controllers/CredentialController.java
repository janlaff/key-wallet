package key_wallet.controllers;

import java.util.UUID;

public class CredentialController {
    private static CredentialController instance;
    private CredentialController() {}

    public static CredentialController getInstance() {
        if (instance == null) {
            instance = new CredentialController();
        }
        return instance;
    }

    public UUID createCredential(String login, String password) {
        String masterPassword = MasterPasswordController.getInstance().getMasterPassword();
        String encryptedPassword = EncryptionController.getInstance().encryptPassword(password, masterPassword);
        UUID credentialId = UUID.randomUUID();

        StorageController.getInstance().saveCredentialToFile(credentialId, login, encryptedPassword);

        return credentialId;
    }

    public void removeCredential(UUID credentialId) {
        StorageController.getInstance().removeCredentialFile(credentialId);
    }
}
