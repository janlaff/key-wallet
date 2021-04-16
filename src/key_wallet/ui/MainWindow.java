package key_wallet.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import key_wallet.core.Config;
import key_wallet.core.Database;
import key_wallet.core.MasterPassword;
import key_wallet.core.MasterPasswordException;
import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainWindow extends JFrame {
    private CredentialSelectPanel selectPanel;
    private CredentialInfoPanel infoPanel;

    public MainWindow(Database db) {
        super("key-wallet");

        infoPanel = new CredentialInfoPanel();
        selectPanel = new CredentialSelectPanel(infoPanel, db);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(selectPanel);
        splitPane.setRightComponent(infoPanel);
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);

        JPanel listSpacer = new JPanel();
        listSpacer.setLayout(new BorderLayout());
        listSpacer.setBorder(new EmptyBorder(10, 10, 10, 10));
        listSpacer.add(splitPane);

        setContentPane(listSpacer);

        setVisible(true);
    }
}
