package key_wallet;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import key_wallet.core.*;
import key_wallet.crypto.AESEncryption;
import key_wallet.ui.MainWindow;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Program {
    public static void main(String[] args) throws IOException, MasterPasswordException {
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
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

        Config config;
        try {
            config = Config.loadConfig();
        } catch (ConfigException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return;
        }

        File dataFile = config.getDatabaseFile();

        if (config.getUiTheme().equals("Dark")) {
            try {
                UIManager.setLookAndFeel( new FlatDarkLaf() );
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }

        // Skip bc its annoying
        // Get password
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter the password:");
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Master Password",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        // Exit if no password was entered
        if (option != 0) {
            return;
        }

        // 1chtrinkenurBIER

        try {
            // test only
            String password = new String(pass.getPassword());
            final MasterPassword mp = new MasterPassword(password, new AESEncryption());
            final Database db = new Database(dataFile, mp);

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    MainWindow content = new MainWindow(db);
                    JFrame frame = new JFrame("key-wallet");
                    frame.setContentPane(content.$$$getRootComponent$$$());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800, 600);
                    frame.setVisible(true);
                    content.initUiState();
                }
            });
        } catch (MasterPasswordException | ParseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}