package key_wallet.core;

import key_wallet.data.Credential;
import key_wallet.data.DataFormat;
import key_wallet.data.KWDBFormat;
import key_wallet.data.DataFormatException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LocalDatabase implements Database {
    public static final String DEFAULT_FILENAME = "secret.kwdb";
    public static final String LOCATOR = "file:////";
    public static final String DEFAULT_URI = LOCATOR + DEFAULT_FILENAME;

    private File file;
    private MasterPassword masterPassword;
    private final Map<Integer, Credential> credentials = new HashMap<>();
    private final DataFormat dataFormat;

    public LocalDatabase(File file, DataFormat dataFormat) {
        this.file = file;
        this.dataFormat = dataFormat;
    }

    @Override
    public void open(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException {
        this.masterPassword = masterPassword;

        if (file.isFile()) {
            try {
                byte[] dataBytes = KWDBFormat.read(file, masterPassword);
                DataFormat.Data data = dataFormat.decode(dataBytes);

                int i = 0;
                for (Credential c : data.credentials) {
                    credentials.put(i++, c);
                }
            } catch (IOException | DataFormatException e) {
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
    public List<IdWith<Credential>> fetchCredentials() throws DatabaseException {
        List<IdWith<Credential>> tmp = new ArrayList<>();

        for (Map.Entry<Integer, Credential> c : credentials.entrySet()) {
            tmp.add(new IdWith<>(c.getKey(), c.getValue()));
        }

        return tmp;
    }

    @Override
    public List<IdWith<String>> fetchCredentialNames() throws DatabaseException {
        List<IdWith<String>> tmp = new ArrayList<>();

        for (Map.Entry<Integer, Credential> c : credentials.entrySet()) {
            tmp.add(new IdWith<>(c.getKey(), c.getValue().name));
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

    private void saveChanges() throws DatabaseException {
        try {
            DataFormat.Data data = new DataFormat.Data();
            data.credentials = new ArrayList<>(credentials.values());
            KWDBFormat.write(dataFormat.encode(data), file, masterPassword);
        } catch (IOException e) {
            throw new DatabaseException("Failed to write to file");
        }
    }
}
