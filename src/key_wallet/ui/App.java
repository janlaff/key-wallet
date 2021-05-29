package key_wallet.ui;

import key_wallet.core.*;
import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;

import java.util.List;

public class App {
    private UserInterface userInterface;
    private Config config;
    private Database database;
    private MasterPassword masterPassword;

    public App() {
        userInterface = new SwingUserInterface(this);
        loadConfig();
    }

    public void createCredential(Credential c) {
        try {
            int id = database.insertCredential(c);
            userInterface.addCredential(new Database.IdWith<>(id, c.name));
            userInterface.setUiState(UserInterface.UiState.DISPLAY_CREDENTIAL);
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    public void updateCredential(int id, Credential c) {
        try {
            database.updateCredential(id, c);
            userInterface.updateCredential(new Database.IdWith<>(id, c.name));
            userInterface.setUiState(UserInterface.UiState.DISPLAY_CREDENTIAL);
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    public void selectCredential() {
        try {
            Credential c = database.fetchCredential(userInterface.getSelectedCredentialId());
            userInterface.showCredential(c);
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    public void generatePassword() {
        // TODO: password generation algorithm
        userInterface.setGeneratedPassword("some_strong_password");
    }

    public void discardCredential() {
        try {
            if (userInterface.getSelectedCredentialId() == -1) {
                userInterface.setUiState(UserInterface.UiState.NO_CREDENTIALS);
            } else {
                int id = userInterface.getSelectedCredentialId();
                Credential c = database.fetchCredential(id);

                userInterface.showCredential(c);
                userInterface.setUiState(UserInterface.UiState.DISPLAY_CREDENTIAL);
            }
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    public void deleteCredential() {
        try {
            int id = userInterface.getSelectedCredentialId();
            userInterface.removeSelectedCredential();
            database.deleteCredential(id);
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    public void switchDatabase() {
        try {
            loadDatabaseUri();
            loadConfig();
        } catch (ConfigException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    private void loadConfig() {
        try {
            userInterface.setUiState(UserInterface.UiState.DISABLED);

            if (Config.available()) {
                config = Config.load();
            } else { // Create new config
                config = new Config("", "");
                config.setUiTheme(userInterface.loadUiTheme());
                config.setDatabaseUri(userInterface.loadDbConnectionUri());
                config.save();
            }

            userInterface.setTheme(config.getUiTheme());

            if (loadMasterPassword()) {
                loadDatabase();
            }
        } catch (ConfigException e) {
            userInterface.showMessage(e.getMessage());
        }
    }

    private void loadDatabaseUri() throws ConfigException {
        config.setDatabaseUri(userInterface.loadDbConnectionUri());
        config.save();
    }

    private boolean loadMasterPassword() {
        String mpassword = userInterface.loadMasterPassword();
        if (!mpassword.isEmpty()) {
            try {
                masterPassword = new MasterPassword(mpassword, new AESEncryption());
                return true;
            } catch (MasterPasswordException e) {
                userInterface.showMessage(e.getMessage());
                return loadMasterPassword();
            }
        }
        return false;
    }

    private boolean loadDatabase() throws ConfigException {
        try {
            database = Database.create(config.getDatabaseUri());
            database.createOrOpen(masterPassword);
            userInterface.showDbConnectionString(database.getConnectionString());
            List<Database.IdWith<String>> credentialNames = database.fetchCredentialNames();
            List<String> categories = database.fetchCategories();

            userInterface.showCredentials(credentialNames, categories);
            userInterface.setUiState(credentialNames.isEmpty() ? UserInterface.UiState.NO_CREDENTIALS : UserInterface.UiState.DISPLAY_CREDENTIAL);
            return true;
        } catch (MasterPasswordException e) { // Password is invalid
            userInterface.showMessage(e.getMessage());
            // Retry password
            return loadMasterPassword() && loadDatabase();
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());
            loadDatabaseUri();
            loadConfig();
        }

        return false;
    }
}
