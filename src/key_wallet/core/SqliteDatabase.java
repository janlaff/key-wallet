package key_wallet.core;

import key_wallet.data.Credential;

import java.util.List;

public class SqliteDatabase implements IDatabase {
    public static final String LOCATOR = "sqlite:////";
    public static final String DEFAULT_URI = LOCATOR + "secret.db";

    @Override
    public void open(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException {

    }

    @Override
    public void close() throws DatabaseException {

    }

    @Override
    public List<WithId<Credential>> fetchCredentials() throws DatabaseException {
        return null;
    }

    @Override
    public List<WithId<String>> fetchCredentialNames() throws DatabaseException {
        return null;
    }

    @Override
    public List<String> fetchCategories() throws DatabaseException {
        return null;
    }

    @Override
    public Credential fetchCredential(int credentialId) throws DatabaseException {
        return null;
    }

    @Override
    public int insertCredential(Credential credential) throws DatabaseException {
        return 0;
    }

    @Override
    public void deleteCredential(int credentialId) throws DatabaseException {

    }

    @Override
    public void updateCredential(int credentialId, Credential credential) throws DatabaseException {

    }

    @Override
    public String getConnectionString() {
        return null;
    }
}
