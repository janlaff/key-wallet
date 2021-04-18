package key_wallet.core;

import key_wallet.data.Credential;

import java.util.List;

public interface IDatabase {
    class WithId<V> {
        public int id;
        public V value;

        public WithId(int id, V value) {
            this.id = id;
            this.value = value;
        }
    }

    void open(MasterPassword masterPassword) throws DatabaseException, MasterPasswordException;
    void close() throws DatabaseException;

    List<WithId<Credential>> fetchCredentials() throws DatabaseException;
    List<WithId<String>> fetchCredentialNames() throws DatabaseException;
    List<String> fetchCategories() throws DatabaseException;

    int insertCredential(Credential credential) throws DatabaseException;

    void deleteCredential(int credentialId) throws DatabaseException;
    void updateCredential(int credentialId, Credential credential) throws DatabaseException;

    String getConnectionString();
}
