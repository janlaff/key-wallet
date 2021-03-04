package key_wallet.core;

public class MasterPasswordException extends Exception {
    public MasterPasswordException() {
        super("Invalid Master Password");
    }
}
