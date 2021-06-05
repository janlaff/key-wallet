package key_wallet.core;

import key_wallet.crypto.Encryption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MasterPasswordTest {
    private Encryption encryptionMock;

    @Before
    public void setUp() {
        encryptionMock = Mockito.mock(Encryption.class);
        // Mock encrypt method
        when(encryptionMock.encrypt(any(byte[].class), any(byte[].class)))
                .thenAnswer((Answer<byte[]>) invocationOnMock -> invocationOnMock.getArgument(0));
        // Mock decrypt method
        when(encryptionMock.decrypt(any(byte[].class), any(byte[].class)))
                .thenAnswer((Answer<byte[]>) invocationOnMock -> invocationOnMock.getArgument(0));
    }

    @Test
    public void encryptAndDecryptData() throws MasterPasswordException {
        MasterPassword mp = new MasterPassword("1chtrinkenurBIER", encryptionMock);
        String randomData = "Fischers Fritz";
        byte[] encryptedData = mp.encrypt(randomData.getBytes(StandardCharsets.UTF_8));
        String result = new String(mp.decrypt(encryptedData), StandardCharsets.UTF_8);

        // Compare before and after encrypt & decrypt
        Assert.assertEquals(randomData, result);
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionWhenEmpty() throws MasterPasswordException {
        new MasterPassword("", encryptionMock);
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionWhenTooShort() throws MasterPasswordException {
        new MasterPassword("Ab124", encryptionMock);
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionOnOnlyNumbers() throws MasterPasswordException {
        new MasterPassword("12345678", encryptionMock);
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionOnOnlyLetters() throws MasterPasswordException {
        new MasterPassword("ABCDEFG", encryptionMock);
    }

    @Test(expected = MasterPasswordException.class)
    public void exceptionOnOnlySymbols() throws MasterPasswordException {
        new MasterPassword("!§$%&/()=", encryptionMock);
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
            new MasterPassword(pw, encryptionMock);
        }
    }
}
