package key_wallet.ui;

import key_wallet.core.*;
import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static java.lang.Math.max;
import static key_wallet.ui.UserInterface.UiState.EDIT_CREDENTIAL;

public class App {
    public enum Event {
        DELETE_DISCARD_CREDENTIAL,
        EDIT_CREATE_SAVE_CREDENTIAL,
        SELECT_CREDENTIAL,
        SWITCH_DATABASE,
        SWITCH_MASTERPASSWORD,
        LOAD_CONFIG,
        LOAD_DATABASE,
        LOAD_MASTERPASSWORD,
        LOAD_CREDENTIALS,
        GENERATE_PASSWORD,
        FILTER_CREDENTIALS,
        LOAD_DATABASE_URI,
        LOAD_UI_THEME,
    }

    private UserInterface userInterface;
    private Config config;
    private Database database;
    private MasterPassword masterPassword;

    public App() {
        userInterface = new SwingUserInterface(this);
        // Load configuration file
        handle(Event.LOAD_CONFIG);
    }

    // Main logic of the app
    // TODO: refactor into methods
    public boolean handle(Event event) {
        try {
            switch (event) {
                case SWITCH_DATABASE -> {
                    handle(Event.LOAD_DATABASE_URI);
                    handle(Event.LOAD_CONFIG);
                }
                case LOAD_UI_THEME -> {
                    config.setUiTheme(userInterface.loadUiTheme());
                    handle(Event.LOAD_DATABASE_URI);
                }
                case LOAD_DATABASE_URI -> {
                    config.setDatabaseUri(userInterface.loadDbConnectionUri());
                    config.save();
                }
                case LOAD_CONFIG -> {
                    userInterface.setUiState(UserInterface.UiState.DISABLED);

                    if (Config.available()) {
                        config = Config.load();
                    } else { // Create new config
                        config = new Config("", "");
                        handle(Event.LOAD_UI_THEME);
                    }

                    userInterface.setTheme(config.getUiTheme());

                    if (handle(Event.LOAD_MASTERPASSWORD)) {
                        handle(Event.LOAD_DATABASE);
                    }

                }
                case LOAD_MASTERPASSWORD -> {
                    String mpassword = userInterface.loadMasterPassword();
                    if (!mpassword.isEmpty()) {
                        try {
                            masterPassword = new MasterPassword(mpassword, new AESEncryption());
                            return true;
                        } catch (MasterPasswordException e) {
                            userInterface.showMessage(e.getMessage());
                            return handle(Event.LOAD_MASTERPASSWORD);
                        }
                    }
                }
                case LOAD_DATABASE -> {
                    try {
                        database = Database.create(config.getDatabaseUri());
                        database.createOrOpen(masterPassword);
                        userInterface.showDbConnectionString(database.getConnectionString());
                        handle(Event.LOAD_CREDENTIALS);
                        return true;
                    } catch (MasterPasswordException e) { // Password is invalid
                        userInterface.showMessage(e.getMessage());
                        // Retry password
                        return handle(Event.LOAD_MASTERPASSWORD) && handle(Event.LOAD_DATABASE);
                    }
                }
                case LOAD_CREDENTIALS -> {
                    List<Database.IdWith<String>> credentialNames = database.fetchCredentialNames();
                    List<String> categories = database.fetchCategories();

                    userInterface.showCredentials(credentialNames, categories);
                    userInterface.setUiState(credentialNames.isEmpty() ? UserInterface.UiState.NO_CREDENTIALS : UserInterface.UiState.DISPLAY_CREDENTIAL);
                }
                case EDIT_CREATE_SAVE_CREDENTIAL -> {
                    if (userInterface.getUiState() == UserInterface.UiState.CREATE_CREDENTIAL) {
                        Credential c = userInterface.getCredentialInput();
                        int id = database.insertCredential(c);
                        userInterface.addCredential(new Database.IdWith<>(id, c.name));
                        userInterface.setUiState(UserInterface.UiState.DISPLAY_CREDENTIAL);
                    } else if (userInterface.getUiState() == EDIT_CREDENTIAL) {
                        int id = userInterface.getSelectedCredentialId();
                        Credential c = userInterface.getCredentialInput();

                        database.updateCredential(id, c);
                        userInterface.setUiState(UserInterface.UiState.DISPLAY_CREDENTIAL);
                    } else if (userInterface.getUiState() == UserInterface.UiState.DISPLAY_CREDENTIAL) {
                        userInterface.setUiState(EDIT_CREDENTIAL);
                    }
                }
                case SELECT_CREDENTIAL -> {
                    Credential c = database.fetchCredential(userInterface.getSelectedCredentialId());
                    userInterface.showCredential(c);
                }
                case DELETE_DISCARD_CREDENTIAL -> {
                    if (userInterface.getUiState() == UserInterface.UiState.CREATE_CREDENTIAL || userInterface.getUiState() == UserInterface.UiState.EDIT_CREDENTIAL) {
                        if (userInterface.getSelectedCredentialId() == -1) {
                            userInterface.setUiState(UserInterface.UiState.NO_CREDENTIALS);
                        } else {
                            int id = userInterface.getSelectedCredentialId();
                            Credential c = database.fetchCredential(id);

                            userInterface.showCredential(c);
                            userInterface.setUiState(UserInterface.UiState.DISPLAY_CREDENTIAL);
                        }
                    } else if (userInterface.getUiState() == UserInterface.UiState.DISPLAY_CREDENTIAL) {
                        int id = userInterface.getSelectedCredentialId();
                        userInterface.removeSelectedCredential();
                        database.deleteCredential(id);
                    }
                }
                case GENERATE_PASSWORD -> {
                    userInterface.setGeneratedPassword(generatePassword());
                }
                default -> {
                    userInterface.showMessage("Unhandled event");
                }
            }
        } catch (ConfigException e) {
            userInterface.showMessage(e.getMessage());
        } catch (DatabaseException e) {
            userInterface.showMessage(e.getMessage());

            handle(Event.LOAD_DATABASE_URI);
            handle(Event.LOAD_CONFIG);
        }

        return false;
    }

    private String generatePassword() {
        // TODO: password generation algorithm
        return "some_strong_password";
    }
}
