package key_wallet.ui;

import key_wallet.core.Database;
import key_wallet.data.Credential;

import java.util.List;

public interface UserInterface {
    enum UiState {
        DISABLED,
        NO_CREDENTIALS,
        DISPLAY_CREDENTIAL,
        EDIT_CREDENTIAL,
        CREATE_CREDENTIAL,
    }

    void setTheme(String theme);
    void showMessage(String message);
    void showDbConnectionString(String uri);
    void showCredentials(List<Database.IdWith<String>> credentialNames, List<String> categories);
    void showCredential(Credential credential);
    void addCredential(Database.IdWith<String> credentialName);
    void updateCredential(Database.IdWith<String> credentialName);
    void removeSelectedCredential();
    void setGeneratedPassword(String password);
    Credential getCredentialInput();
    int getSelectedCredentialId();
    String loadMasterPassword();
    String loadUiTheme();
    String loadDbConnectionUri();
    void setUiState(UiState state);
}
