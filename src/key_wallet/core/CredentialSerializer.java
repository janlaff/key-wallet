package key_wallet.core;

import key_wallet.data.Credential;

import java.text.ParseException;

public class CredentialSerializer {
    private static final String CSV_SEPARATOR = ",";
    private static final String NEWLINE = "\n";

    public static Credential deserialize(String credentialStr) throws ParseException {
        String[] parts = credentialStr.split(CSV_SEPARATOR);

        if (parts.length != 3) {
            throw new ParseException("Unable to construct credential object from string: " + credentialStr, 0);
        } else {
            return new Credential(parts[0], parts[1], parts[2]);
        }
    }

    public static String serialize(Credential credentialObj) {
        return credentialObj.description + CSV_SEPARATOR + credentialObj.username + CSV_SEPARATOR + credentialObj.password + NEWLINE;
    }
}
