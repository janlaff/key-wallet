package key_wallet.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class CSVDataFormat implements DataFormat {
    private static final String CSV_SEPARATOR = ",";
    private static final String NEWLINE = "\n";

    @Override
    public Data decode(byte[] encodedData) throws DataFormatException {
        Data data = new Data();

        String strBytes = new String(encodedData, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(new StringReader(strBytes));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                data.credentials.add(decodeLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public byte[] encode(Data data) {
        StringBuilder output = new StringBuilder();
        for (Credential c : data.credentials) {
            output.append(encodeCredential(c));
        }
        return output.toString().getBytes(StandardCharsets.UTF_8);
    }

    private Credential decodeLine(String line) throws DataFormatException {
        String[] parts = line.split(CSV_SEPARATOR);

        // There are exactly 6 parts of information stored per line.
        // if its not 6, something wrong happened here
        if (parts.length != 6) {
            throw new DataFormatException("Unable to construct credential object from string: " + line);
        } else {
            return new Credential(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
    }

    private String encodeCredential(Credential credential) {
        return credential.name + CSV_SEPARATOR
                + credential.login + CSV_SEPARATOR
                + credential.email + CSV_SEPARATOR
                + credential.password + CSV_SEPARATOR
                + credential.website + CSV_SEPARATOR
                + credential.category + NEWLINE;
    }
}
