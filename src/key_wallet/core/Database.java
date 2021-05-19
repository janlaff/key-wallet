package key_wallet.core;

import key_wallet.data.CSVDataFormat;
import key_wallet.data.Credential;
import key_wallet.data.JSONDataFormat;

import java.io.File;
import java.util.List;

public interface Database {
    class IdWith<V> {
        public int id;
        public V value;

        public IdWith(int id, V value) {
            this.id = id;
            this.value = value;
        }
    }

    static Database create(String connectionString) throws DatabaseException {
        if (connectionString.startsWith(LocalDatabase.LOCATOR)) {
            return new LocalDatabase(new File(connectionString.substring(LocalDatabase.LOCATOR.length())), new JSONDataFormat());
        } else if (connectionString.startsWith(SqliteDatabase.LOCATOR)) {
            return new SqliteDatabase();
        } else {
            throw new DatabaseException("Unknown database uri");
        }
    }

    void createOrOpen(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException;

    void close() throws DatabaseException;

    List<IdWith<Credential>> fetchCredentials() throws DatabaseException;

    List<IdWith<String>> fetchCredentialNames() throws DatabaseException;

    List<String> fetchCategories() throws DatabaseException;

    Credential fetchCredential(int credentialId) throws DatabaseException;

    int insertCredential(Credential credential) throws DatabaseException;

    void deleteCredential(int credentialId) throws DatabaseException;

    void updateCredential(int credentialId, Credential credential) throws DatabaseException;

    String getConnectionString();
}
