package key_wallet.core;

import key_wallet.crypto.PlaintextEncryption;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class MasterPasswordTest {
    @Test(expected = MasterPasswordException.class)
    public void emptyPasswordTest() throws MasterPasswordException {
        new MasterPassword("", new PlaintextEncryption());
    }

    @Test
    public void encryptAndDecryptTest() throws MasterPasswordException {
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", new PlaintextEncryption());
        String randomData = "Fischers Fritz";
        byte[] encryptedData = mp.encrypt(randomData.getBytes(StandardCharsets.UTF_8));
        String result = new String(mp.decrypt(encryptedData), StandardCharsets.UTF_8);

        Assert.assertEquals(randomData, result);
    }

    @Test(expected = MasterPasswordException.class)
    public void passwordEmptyTest() throws MasterPasswordException {
        new MasterPassword("", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void passwordTooShortTest() throws MasterPasswordException {
        new MasterPassword("Ab124", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void passwordOnlyNumbersTest() throws MasterPasswordException {
        new MasterPassword("12345678", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void passwordOnlyLettersTest() throws MasterPasswordException {
        new MasterPassword("ABCDEFG", new PlaintextEncryption());
    }

    @Test(expected = MasterPasswordException.class)
    public void passwordOnlySymbolsTest() throws MasterPasswordException {
        new MasterPassword("!§$%&/()=", new PlaintextEncryption());
    }

    @Test
    public void passwordCorrectTest() throws MasterPasswordException {
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
