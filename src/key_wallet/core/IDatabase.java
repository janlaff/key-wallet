package key_wallet.core;

import key_wallet.data.Credential;

import java.io.File;
import java.util.List;

public interface IDatabase {
    class IdWith<V> {
        public int id;
        public V value;

        public IdWith(int id, V value) {
            this.id = id;
            this.value = value;
        }
    }

    static IDatabase create(String connectionString) throws DatabaseException {
        if (connectionString.startsWith(CsvDatabase.LOCATOR)) {
            return new CsvDatabase(new File(connectionString.substring(CsvDatabase.LOCATOR.length())));
        } else if (connectionString.startsWith(SqliteDatabase.LOCATOR)) {
            return new SqliteDatabase();
        } else {
            throw new DatabaseException("Unknown database uri");
        }
    }

    void open(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException;

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
