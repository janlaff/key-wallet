package key_wallet.data;

public class DataFormatException extends Exception {
    public DataFormatException(String reason) {
        super("DataFormatException: " + reason);
    }
}
