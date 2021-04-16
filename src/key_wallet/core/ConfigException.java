package key_wallet.core;

public class ConfigException extends Exception {
    public ConfigException(String reason) {
        super("ConfigException: " + reason);
    }
}
