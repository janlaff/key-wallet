package key_wallet.core;

import key_wallet.core.Masterpassword;
import key_wallet.data.Credential;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    // Start -> MPasswort -> Account listen google, web, usernames
    // Account auswählen -> MPasswort -> Passwort kopieren, editieren, anzeigen

    private List<Credential> credentials;

    public Database(File dataFile, Masterpassword mp) throws IOException {
        credentials = new ArrayList<>();

        if (Files.exists(dataFile.toPath())) {
            byte[] contents = Files.readAllBytes(dataFile.toPath());
            String decrypted = mp.decrypt(contents);
            deserializeCredentials(decrypted);
        } else {
            System.err.println("[WARNING]: Specified file does not exist yet");
        }
    }

    public void saveToFile(File dataFile, Masterpassword mp) throws IOException {
        byte[] contents = mp.encrypt(serializeCredentials());
        Files.write(dataFile.toPath(), contents);
    }

    public void addCredential(Credential credential) {
        credentials.add(credential);
    }

    public void removeCredential(int index) {
        credentials.remove(index);
    }

    public void updateCredential(int index, Credential credential) {
        credentials.set(index, credential);
    }

    private void deserializeCredentials(String csv) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(csv));
        String line = null;

        while((line = reader.readLine()) != null )
        {
            try {
                credentials.add(CredentialSerializer.deserialize(line));
            } catch (ParseException e) {
                System.err.println("[WARNING]: Ignoring invalid credential");
            }
        }
    }

    private String serializeCredentials() {
        StringBuilder output = new StringBuilder();
        for (Credential c : credentials) {
            output.append(CredentialSerializer.serialize(c));
        }
        return output.toString();
    }
}
