package key_wallet.core;

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
    public static final String DB_HEADER_LINE = "V1.0-key-wallet\n";
    private final List<Credential> credentials = new ArrayList<>();
    private final MasterPassword mp;
    private final File dataFile;

    public Database(File dataFile, MasterPassword mp) throws IOException, MasterPasswordException, ParseException {
        this.dataFile = dataFile;
        this.mp = mp;

        if (Files.exists(dataFile.toPath())) {
            byte[] contents = Files.readAllBytes(dataFile.toPath());
            String decrypted = mp.decrypt(contents);

            if (!validateFileHeader(decrypted)) {
                throw new MasterPasswordException("Password is incorrect!");
            } else {
                decrypted = decrypted.substring(DB_HEADER_LINE.length());
            }

            deserializeCredentials(decrypted);
        } else {
            System.err.println("[WARNING]: Creating new database file");
        }
    }

    public List<Credential> getCredentials() {
        return credentials;
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

    public File getFile() {
        return dataFile;
    }

    public void update() throws IOException {
        byte[] contents = mp.encrypt(serializeCredentials());
        Files.write(dataFile.toPath(), contents);
    }

    @Deprecated(since = "This method is intended for testing only")
    public void clearCredentials() {
        credentials.clear();
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Credential c : credentials) {
            if (!categories.contains(c.category)) {
                categories.add(c.category);
            }
        }
        return categories;
    }

    private void deserializeCredentials(String csv) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new StringReader(csv));
        String line;

        while ((line = reader.readLine()) != null)
        {
            credentials.add(CredentialSerializer.deserialize(line));
        }
    }

    private String serializeCredentials() {
        StringBuilder output = new StringBuilder(DB_HEADER_LINE);
        for (Credential c : credentials) {
            output.append(CredentialSerializer.serialize(c));
        }
        return output.toString();
    }

    private boolean validateFileHeader(String decryptedFile) {
        BufferedReader reader = new BufferedReader(new StringReader(decryptedFile));

        try {
            String line = reader.readLine();
            return line != null && (line + "\n").equals(DB_HEADER_LINE);
        } catch (IOException e) {
            return false;
        }
    }
}
