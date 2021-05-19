package key_wallet.core;

import key_wallet.crypto.PlaintextEncryption;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class MasterPasswordTest {
    @Test
    public void encryptAndDecryptData() throws MasterPasswordException {
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new PlaintextEncryption());
        String randomData = "Fischers Fritz";
        byte[] encryptedData = mp.encrypt(randomData.getBytes(StandardCharsets.UTF_8));
        String result = new String(mp.decrypt(encryptedData), StandardCharsets.UTF_8);

        // Compare before and after encrypt & decrypt
        Assert.assertEquals(randomData, result);
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionWhenEmpty() throws MasterPasswordException {
        new MasterPassword("", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionWhenTooShort() throws MasterPasswordException {
        new MasterPassword("Ab124", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionOnOnlyNumbers() throws MasterPasswordException {
        new MasterPassword("12345678", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionOnOnlyLetters() throws MasterPasswordException {
        new MasterPassword("ABCDEFG", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionOnOnlySymbols() throws MasterPasswordException {
        new MasterPassword("!§$%&/()=", new PlaintextEncryption());
    }

    @Test
    public void validateStrongPasswords() throws MasterPasswordException {
        String[] goodPasswords = new String[] {
                "1Ki77zyu",
                ".Susan53",
                "jelly22fi$h",
                "$m3llycat",
                "a11Black$",
                "!ush3rss",
                "&ebay.44",
                "d3ltagamm@",
                "!Lov3MyPiano",
                "SterlingGmail20.15",
                "BankLogin!3"
        };

        for (String pw : goodPasswords) {
            new MasterPassword(pw, new PlaintextEncryption());
        }
    }
}
