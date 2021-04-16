package key_wallet;

import com.formdev.flatlaf.FlatDarkLaf;
import key_wallet.core.Config;
import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.AESEncryption;
import key_wallet.crypto.XorEncryption;
import key_wallet.data.Credential;
import key_wallet.core.Database;
import key_wallet.ui.MainWindow;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Program {
    public static void main(String[] args) throws IOException, MasterPasswordException {
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        // Round input corners
        UIManager.put( "TextComponent.arc", 5 );
        // Better focus
        UIManager.put( "Component.focusWidth", 1 );
        // Better scroll bars
        UIManager.put( "ScrollBar.thumbArc", 999 );
        UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );

        File dataFile = Config.getDatabaseFile();

        // Get password
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter the password:");
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "The title",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        // Exit if no password was entered
        if (option != 0) {
            return;
        }

        try {
            final MasterPassword mp = new MasterPassword(new String(pass.getPassword()), new AESEncryption());
            final Database db = new Database(dataFile, mp);

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MainWindow(db.getCredentials());
                }
            });
        } catch (MasterPasswordException | ParseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}