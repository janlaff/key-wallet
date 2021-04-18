package key_wallet.core;

public class DatabaseException extends Exception {
    public DatabaseException(String reason) {
        super("DatabaseException: " + reason);
    }
}
