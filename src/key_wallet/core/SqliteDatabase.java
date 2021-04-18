package key_wallet.core;

import key_wallet.data.Credential;

import java.util.List;

public class SqliteDatabase implements IDatabase {
    public static final String LOCATOR = "sqlite:////";
    public static final String DEFAULT_URI = LOCATOR + "secret.db";

    @Override
    public void open(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public void close() throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public List<WithId<Credential>> fetchCredentials() throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public List<WithId<String>> fetchCredentialNames() throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public List<String> fetchCategories() throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public Credential fetchCredential(int credentialId) throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public int insertCredential(Credential credential) throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public void deleteCredential(int credentialId) throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public void updateCredential(int credentialId, Credential credential) throws DatabaseException {
        throw new DatabaseException("SqliteDatabase is currently not implemented");
    }

    @Override
    public String getConnectionString() {
        // TODO: return connection string
        return "Sqlite Database";
    }
}
