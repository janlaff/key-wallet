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
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        // Round input corners
        UIManager.put("TextComponent.arc", 5);
        // Better focus
        UIManager.put("Component.focusWidth", 1);
        // Better scroll bars
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

        SwingUtilities.invokeLater(() -> {
            MainWindow content = new MainWindow();
            JFrame frame = new JFrame("key-wallet");
            frame.setContentPane(content.$$$getRootComponent$$$());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
            content.runStateMachine();
        });
    }
}