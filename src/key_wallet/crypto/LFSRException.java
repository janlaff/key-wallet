package key_wallet.crypto;

public class LFSRException extends Exception {
    public LFSRException(String reason) {
        super("LFSRException: " + reason);
    }
}
