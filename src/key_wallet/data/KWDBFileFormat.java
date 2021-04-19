package key_wallet.data;

import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class KWDBFileFormat {
    public static String MAGIC_HEADER = "KWDB";
    public static int HASH_SIZE = 32;

    public static byte[] read(File databaseFile, MasterPassword masterPassword) throws DataFormatException, MasterPasswordException, IOException {
        try {
            InputStream is = new FileInputStream(databaseFile);
            byte[] magic = is.readNBytes(MAGIC_HEADER.length());
            byte[] hash = is.readNBytes(HASH_SIZE);
            byte[] data = is.readAllBytes();
            is.close();

            if (!MAGIC_HEADER.equals(new String(magic, StandardCharsets.UTF_8))) {
                throw new DataFormatException("Not a valid KWDatabase");
            }

            byte[] decryptedData =  masterPassword.decrypt(data);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] decryptedDataHash = digest.digest(decryptedData);

            if (!Arrays.equals(hash, decryptedDataHash)) {
                throw new MasterPasswordException("Incorrect master password");
            }

            return decryptedData;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static void write(byte[] data, File databaseFile, MasterPassword masterPassword) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            byte[] encryptedData = masterPassword.encrypt(data);

            OutputStream os = new FileOutputStream(databaseFile);
            os.write(MAGIC_HEADER.getBytes(StandardCharsets.UTF_8));
            os.write(hash);
            os.write(encryptedData);
            os.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
