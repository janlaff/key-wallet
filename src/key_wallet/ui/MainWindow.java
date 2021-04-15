package key_wallet.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.sun.tools.javac.Main;
import key_wallet.core.Database;
import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainWindow extends JFrame {
    public MainWindow(List<Credential> credentials) {
        super("key-wallet");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        DefaultListModel<Credential> listModel = new DefaultListModel<>();
        listModel.addAll(credentials);

        JList list = new JList(listModel);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((Credential)value).username, index, isSelected, cellHasFocus);
            }
        });
        JScrollPane scrollPane = new JScrollPane(list);

        JPanel infoPanel = new JPanel();

        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(infoPanel);
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);
        getContentPane().add(splitPane);

        setVisible(true);
    }

    public static void main(String[] args) throws MasterPasswordException {
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
        //
        UIManager.put( "Component.arrowType", "triangle" );

        // Get password file
        JFileChooser chooser = new JFileChooser("./");
        int fileChooserResult = chooser.showOpenDialog(null);

        // Exit if no file was selected
        if (fileChooserResult != 0) {
            return;
        }

        File dataFile = chooser.getSelectedFile();

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
            MasterPassword mp = new MasterPassword(new String(pass.getPassword()), new AESEncryption());

            Database db = null;
            try {
                db = new Database(dataFile, mp);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            new MainWindow(db.getCredentials());
        } catch (MasterPasswordException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
