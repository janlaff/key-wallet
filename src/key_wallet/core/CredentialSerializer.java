package key_wallet.core;

import key_wallet.data.Credential;

import java.text.ParseException;

/**
 * Used to convert credentials between object and string representation state
 */
public class CredentialSerializer {
    private static final String CSV_SEPARATOR = ",";
    private static final String NEWLINE = "\n";

    // Tries to convert a seperated string into a credential object
    public static Credential deserialize(String credentialStr) throws ParseException {
        String[] parts = credentialStr.split(CSV_SEPARATOR);

        // There are exactly 6 parts of information stored per line.
        // if its not 6, something wrong happened here
        if (parts.length != 6) {
            throw new ParseException("Unable to construct credential object from string: " + credentialStr, 0);
        } else {
            return new Credential(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
    }

    // Converts a credential object to an immediate string representation
    public static String serialize(Credential credentialObj) {
        return credentialObj.name + CSV_SEPARATOR
                + credentialObj.login + CSV_SEPARATOR
                + credentialObj.email + CSV_SEPARATOR
                + credentialObj.password + CSV_SEPARATOR
                + credentialObj.website + CSV_SEPARATOR
                + credentialObj.category + NEWLINE;
    }
}
