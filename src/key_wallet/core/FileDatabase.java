package key_wallet.core;

import key_wallet.data.Credential;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;

public class FileDatabase implements IDatabase {
    public static final String DEFAULT_FILENAME = "secret.kw";
    public static final String LOCATOR = "file:////";
    public static final String DEFAULT_URI = LOCATOR + DEFAULT_FILENAME;
    public static final String FILE_HEADER = "KWDB";
    public static final String PASSWORD_SALT = "FleurDeSalt\n";

    private File file;
    private MasterPassword masterPassword;
    private final Map<Integer, Credential> credentials = new HashMap<>();

    public FileDatabase(File file) {
        this.file = file;
    }

    @Override
    public void open(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException {
        this.masterPassword = masterPassword;

        if (file.isFile()) {
            try {
                String content = decrypt(Files.readAllBytes(file.toPath()));
                deserialize(content);
            } catch (IOException e) {
                throw new DatabaseException("Unable to open file: " + file.toPath());
            }
        } else {
            // Create files
            saveChanges();
        }
    }

    @Override
    public void close() throws DatabaseException {

    }

    @Override
    public List<WithId<Credential>> fetchCredentials() throws DatabaseException {
        List<WithId<Credential>> tmp = new ArrayList<>();

        for (Map.Entry<Integer, Credential> c : credentials.entrySet()) {
            tmp.add(new WithId<>(c.getKey(), c.getValue()));
        }

        return tmp;
    }

    @Override
    public List<WithId<String>> fetchCredentialNames() throws DatabaseException {
        List<WithId<String>> tmp = new ArrayList<>();

        for (Map.Entry<Integer, Credential> c : credentials.entrySet()) {
            tmp.add(new WithId<>(c.getKey(), c.getValue().name));
        }

        return tmp;
    }

    @Override
    public List<String> fetchCategories() throws DatabaseException {
        List<String> tmp = new ArrayList<>();

        for (Map.Entry<Integer, Credential> c : credentials.entrySet()) {
            String category = c.getValue().category;
            if (!tmp.contains(category)) {
                tmp.add(category);
            }
        }

        return tmp;
    }

    @Override
    public Credential fetchCredential(int credentialId) throws DatabaseException {
        if (!credentials.containsKey(credentialId)) {
            throw new DatabaseException("Credential with id " + credentialId + " does not exist");
        } else {
            return credentials.get(credentialId);
        }
    }

    @Override
    public int insertCredential(Credential credential) throws DatabaseException {
        // TODO: reuse low ids
        // Generate new id
        int id = credentials.size();
        while (credentials.containsKey(id)) id++;

        credentials.put(id, credential);
        saveChanges();
        return id;
    }

    @Override
    public void deleteCredential(int credentialId) throws DatabaseException {
        if (!credentials.containsKey(credentialId)) {
            throw new DatabaseException("Credential with id " + credentialId + " does not exist");
        } else {
            credentials.remove(credentialId);
        }
        saveChanges();
    }

    @Override
    public void updateCredential(int credentialId, Credential credential) throws DatabaseException {
        if (!credentials.containsKey(credentialId)) {
            throw new DatabaseException("Credential with id " + credentialId + " does not exist");
        } else {
            credentials.replace(credentialId, credential);
        }
        saveChanges();
    }

    @Override
    public String getConnectionString() {
        return "file:///" + file.getName();
    }

    private String decrypt(byte[] data) throws DatabaseException, MasterPasswordException {
        byte[] headerBytes = Arrays.copyOfRange(data, 0, FILE_HEADER.length());
        if (!FILE_HEADER.equals(new String(headerBytes, StandardCharsets.UTF_8))) {
            throw new DatabaseException(file.toPath() + " is not a valid file database");
        }

        byte[] contentBytes = Arrays.copyOfRange(data, FILE_HEADER.length(), data.length);
        String decrypted = masterPassword.decrypt(contentBytes);

        BufferedReader reader = new BufferedReader(new StringReader(decrypted));
        try {
            String line = reader.readLine();
            if (line == null || !(line + '\n').equals(PASSWORD_SALT)) {
                throw new MasterPasswordException("Incorrect password");
            }
        } catch (IOException e) {
            throw new DatabaseException("Exception while validating master password");
        }

        return decrypted.substring(PASSWORD_SALT.length());
    }

    private void deserialize(String csv) throws DatabaseException, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(csv));
        String line;

        try {
            int id = 0;
            while ((line = reader.readLine()) != null) {
                credentials.put(id++, CredentialSerializer.deserialize(line));
            }
        } catch (ParseException e) {
            throw new DatabaseException("Corrupt database: " + file.toPath());
        }
    }

    private String serialize() {
        StringBuilder output = new StringBuilder();
        for (Map.Entry<Integer, Credential> c : credentials.entrySet()) {
            output.append(CredentialSerializer.serialize(c.getValue()));
        }
        return output.toString();
    }

    private void saveChanges() throws DatabaseException {
        byte[] header = FILE_HEADER.getBytes(StandardCharsets.UTF_8);
        byte[] contents = masterPassword.encrypt(PASSWORD_SALT + serialize());
        byte[] complete = new byte[header.length + contents.length];

        System.arraycopy(header, 0, complete, 0, header.length);
        System.arraycopy(contents, 0, complete, header.length, contents.length);

        try {
            Files.write(file.toPath(), complete);
        } catch (IOException e) {
            throw new DatabaseException("Failed to write file: " + file.toPath());
        }
    }
}
