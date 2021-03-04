package key_wallet.core;

public class MasterPasswordException extends Exception {
    public MasterPasswordException(String reason) {
        super("Invalid Master Password: " + reason);
    }
}
