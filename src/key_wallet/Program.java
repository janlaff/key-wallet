package key_wallet;

import com.formdev.flatlaf.FlatLightLaf;
import key_wallet.core.*;
import key_wallet.ui.App;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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

        SwingUtilities.invokeLater(App::new);
    }
}