package key_wallet.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import key_wallet.core.*;
import key_wallet.crypto.AESEncryption;
import key_wallet.data.Credential;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.List;

import static java.lang.Math.max;

public class App {
    public enum Event {
        DELETE_DISCARD_CREDENTIAL,
        EDIT_CREATE_SAVE_CREDENTIAL,
        ADD_CREDENTIAL,
        SELECT_CREDENTIAL,
        SWITCH_DATABASE,
        SWITCH_MASTERPASSWORD,
        LOAD_CONFIG,
        LOAD_DATABASE,
        LOAD_MASTERPASSWORD,
        LOAD_CREDENTIALS,
        UPDATE_DATABASE,
        COPY_GENERATE_PASSWORD,
        COPY_LOGIN,
        COPY_EMAIL,
        SHOW_HIDE_PASSWORD,
        OPEN_WEBSITE,
        FILTER_CREDENTIALS,
    }

    public enum UiState {
        DISABLED,
        NO_CREDENTIALS,
        DISPLAY_CREDENTIAL,
        EDIT_CREDENTIAL,
        CREATE_CREDENTIAL,
    }

    private UiState uiState;
    private MainWindow window;
    private Config config;
    private Database database;
    private MasterPassword masterPassword;
    private DefaultListModel<String> credentialListModel;
    private DefaultComboBoxModel<String> categoryComboModel;

    public App() {
        uiState = UiState.DISABLED;

        window = new MainWindow(this);

        JFrame frame = new JFrame("key-wallet");
        frame.setContentPane(window.$$$getRootComponent$$$());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        handle(Event.LOAD_CONFIG);
    }

    public boolean handle(Event event) {
        switch (event) {
            case LOAD_CONFIG -> {
                uiState = UiState.DISABLED;
                updateUI();

                try {
                    if (Config.available()) {
                        config = Config.load();
                    } else {
                        // Theme setup
                        String[] themeOptions = new String[]{"Light", "Dark"};

                        int themeChoice = JOptionPane.showOptionDialog(
                                window.mainPanel,
                                "Choose ui theme",
                                "Ui Theme Selection",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                themeOptions,
                                themeOptions[0]
                        );

                        // Database setup
                        String[] databaseOptions = new String[]{"Create New", "Choose Existing"};

                        int databaseChoice = JOptionPane.showOptionDialog(
                                window.mainPanel,
                                "Choose application database",
                                "Database Setup",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                databaseOptions,
                                databaseOptions[0]
                        );

                        if (databaseChoice == 1) { // Choose existing
                            JFileChooser fileChooser = new JFileChooser("./");
                            int fileChooserResult = fileChooser.showOpenDialog(window.mainPanel);

                            if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
                                File databaseFile = fileChooser.getSelectedFile();
                                // TODO: validate selected file is key-wallet database
                                config = Config.create(databaseFile, themeOptions[themeChoice]);
                            }
                        } else { // Create new
                            config = Config.create(new File(Database.DEFAULT_FILENAME), themeOptions[themeChoice]);
                        }
                    }

                    if (config.getUiTheme().equals("Dark")) {
                        try {
                            UIManager.setLookAndFeel(new FlatDarkLaf());
                            SwingUtilities.updateComponentTreeUI(window.mainPanel.getRootPane());
                        } catch (UnsupportedLookAndFeelException e) {
                            JOptionPane.showMessageDialog(window.mainPanel, e.getMessage());
                        }
                    }

                    if (handle(Event.LOAD_MASTERPASSWORD)) {
                        handle(Event.LOAD_DATABASE);
                    }
                } catch (ConfigException e) {
                    JOptionPane.showMessageDialog(window.mainPanel, e.getMessage());
                }
            }
            case LOAD_MASTERPASSWORD -> {
                JPasswordField passwordField = new JPasswordField(10);
                int choice = JOptionPane.showConfirmDialog(
                        window.mainPanel,
                        passwordField,
                        "Enter Master Password",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (choice == JOptionPane.OK_OPTION) {
                    try {
                        masterPassword = new MasterPassword(new String(passwordField.getPassword()), new AESEncryption());
                        return true;
                    } catch (MasterPasswordException e) {
                        JOptionPane.showMessageDialog(window.mainPanel, e.getMessage());

                        return handle(Event.LOAD_MASTERPASSWORD);
                    }
                }
            }
            case LOAD_DATABASE -> {
                try {
                    database = new Database(config.getDatabaseFile(), masterPassword);
                    if (handle(Event.UPDATE_DATABASE)) {
                        window.databaseLabel.setText("Database File: " + config.getDatabaseFile().getName());
                        handle(Event.LOAD_CREDENTIALS);
                        return true;
                    }
                } catch (MasterPasswordException e) { // Password is invalid
                    JOptionPane.showMessageDialog(
                            window.mainPanel,
                            "Incorrect Password"
                    );
                    // Retry password
                    return handle(Event.LOAD_MASTERPASSWORD) && handle(Event.LOAD_DATABASE);
                } catch (ParseException e) { // Corrupt database
                    JOptionPane.showMessageDialog(
                            window.mainPanel,
                            "Database is corrupt"
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case LOAD_CREDENTIALS -> {
                List<String> credentialNames = database.getCredentialNames();
                credentialListModel = new DefaultListModel<>();
                categoryComboModel = new DefaultComboBoxModel<>();

                if (credentialNames.isEmpty()) {
                    uiState = UiState.NO_CREDENTIALS;
                } else {
                    credentialListModel.addAll(credentialNames);
                    categoryComboModel.addAll(database.getCategories());

                    uiState = UiState.DISPLAY_CREDENTIAL;
                }

                window.credentialInfoList.setModel(credentialListModel);
                window.categoryComboBox.setModel(categoryComboModel);
                window.credentialInfoList.setSelectedIndex(0);

                updateUI();
            }
            case ADD_CREDENTIAL -> {
                uiState = UiState.CREATE_CREDENTIAL;
                updateUI();
            }
            case EDIT_CREATE_SAVE_CREDENTIAL -> {
                if (uiState == UiState.CREATE_CREDENTIAL) {
                    Credential c = new Credential(
                            window.nameField.getText(),
                            window.loginField.getText(),
                            window.emailField.getText(),
                            new String(window.passwordField.getPassword()),
                            window.websiteField.getText(),
                            (String) window.categoryComboBox.getSelectedItem()
                    );

                    if (categoryComboModel.getIndexOf(c.category) == -1) {
                        categoryComboModel.addElement(c.category);
                    }

                    database.addCredential(c);

                    handle(Event.UPDATE_DATABASE);

                    credentialListModel.addElement(c.name);
                    window.credentialInfoList.setSelectedIndex(credentialListModel.getSize() - 1);

                    uiState = UiState.DISPLAY_CREDENTIAL;
                } else if (uiState == UiState.EDIT_CREDENTIAL) {
                    int index = window.credentialInfoList.getSelectedIndex();
                    Credential c = database.getCredential(index);
                    c.name = window.nameField.getText();
                    c.login = window.loginField.getText();
                    c.email = window.emailField.getText();
                    c.password = new String(window.passwordField.getPassword());
                    c.website = window.websiteField.getText();
                    c.category = (String) window.categoryComboBox.getSelectedItem();
                    credentialListModel.set(window.credentialInfoList.getSelectedIndex(), c.name);

                    if (categoryComboModel.getIndexOf(c.category) == -1) {
                        categoryComboModel.addElement(c.category);
                    }

                    database.updateCredential(window.credentialInfoList.getSelectedIndex(), c);

                    handle(Event.UPDATE_DATABASE);

                    uiState = UiState.DISPLAY_CREDENTIAL;
                } else if (uiState == UiState.DISPLAY_CREDENTIAL) {
                    uiState = UiState.EDIT_CREDENTIAL;
                }

                updateUI();
            }
            case SELECT_CREDENTIAL -> {
                int index = window.credentialInfoList.getSelectedIndex();

                if (index != -1) {
                    Credential c = database.getCredential(index);

                    window.nameField.setText(c.name);
                    window.loginField.setText(c.login);
                    window.emailField.setText(c.email);
                    window.passwordField.setText(c.password);
                    window.websiteField.setText(c.website);
                    window.categoryComboBox.setSelectedItem(c.category);
                }
            }
            case UPDATE_DATABASE -> {
                try {
                    database.update();
                    return true;
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(window.mainPanel, "Failed write to database");
                }
            }
            case DELETE_DISCARD_CREDENTIAL -> {
                if (uiState == UiState.CREATE_CREDENTIAL || uiState == UiState.EDIT_CREDENTIAL) {
                    if (credentialListModel.getSize() == 0) {
                        uiState = UiState.NO_CREDENTIALS;
                    } else {
                        int index = window.credentialInfoList.getSelectedIndex();
                        Credential c = database.getCredential(index);

                        window.nameField.setText(c.name);
                        window.loginField.setText(c.login);
                        window.emailField.setText(c.email);
                        window.passwordField.setText(c.password);
                        window.websiteField.setText(c.website);
                        window.categoryComboBox.setSelectedItem(c.category);

                        uiState = UiState.DISPLAY_CREDENTIAL;
                    }
                } else if (uiState == UiState.DISPLAY_CREDENTIAL) {
                    int idx = window.credentialInfoList.getSelectedIndex();
                    credentialListModel.remove(idx);

                    database.removeCredential(idx);

                    handle(Event.UPDATE_DATABASE);

                    if (credentialListModel.getSize() > 0) {
                        window.credentialInfoList.setSelectedIndex(max(idx - 1, 0));

                        uiState = UiState.DISPLAY_CREDENTIAL;
                    } else {
                        uiState = UiState.NO_CREDENTIALS;
                    }
                }

                updateUI();
            }
            case COPY_GENERATE_PASSWORD -> {
                if (uiState == UiState.CREATE_CREDENTIAL || uiState == UiState.EDIT_CREDENTIAL) {
                    window.passwordField.setText(generatePassword());
                } else if (uiState == UiState.DISPLAY_CREDENTIAL) {
                    copyToClipboard(new String(window.passwordField.getPassword()));
                }
            }
            case COPY_EMAIL -> {
                copyToClipboard(window.emailField.getText());
            }
            case COPY_LOGIN -> {
                copyToClipboard(window.loginField.getText());
            }
            case OPEN_WEBSITE -> {
                String website = window.websiteField.getText();

                if (!website.isEmpty()) {
                    try {
                        Desktop.getDesktop().browse(URI.create(website));
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(window.mainPanel, "Failed to open web browser");
                    }
                }
            }
            case SHOW_HIDE_PASSWORD -> {
                if (window.showPasswordButton.getText().equals("Show")) {
                    window.showPasswordButton.setText("Hide");
                    window.passwordField.setEchoChar((char)0);
                } else {
                    window.showPasswordButton.setText("Show");
                    window.passwordField.setEchoChar('•');
                }
            }
            default -> {
                JOptionPane.showMessageDialog(window.mainPanel, "Unhandled event");
            }
        }

        return false;
    }

    private void copyToClipboard(String value) {
        StringSelection selection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);

        JOptionPane.showMessageDialog(window.mainPanel, "Copied to clipboard!");
    }

    private String generatePassword() {
        // TODO: password generation algorithm
        return "some_strong_password";
    }

    private void updateUI() {
        switch (uiState) {
            case DISABLED -> {
                window.searchTextField.setEnabled(false);
                window.searchButton.setEnabled(false);
                window.credentialInfoList.setEnabled(false);
                window.addButton.setEnabled(false);
                window.editButton.setEnabled(false);
                window.deleteButton.setEnabled(false);
                window.nameField.setEnabled(false);
                window.loginField.setEnabled(false);
                window.emailField.setEnabled(false);
                window.passwordField.setEnabled(false);
                window.websiteField.setEnabled(false);
                window.categoryComboBox.setEnabled(false);
                window.copyLoginButton.setEnabled(false);
                window.copyEmailButton.setEnabled(false);
                window.copyPasswordButton.setEnabled(false);
                window.showPasswordButton.setEnabled(false);
                window.openButton.setEnabled(false);
                window.categoryComboBox.setEnabled(false);
                window.switchDatabaseButton.setEnabled(false);
            }
            case NO_CREDENTIALS -> {
                window.searchTextField.setEnabled(false);
                window.searchButton.setEnabled(false);
                window.credentialInfoList.setEnabled(false);
                window.addButton.setEnabled(true);
                window.editButton.setEnabled(false);
                window.deleteButton.setEnabled(false);
                window.nameField.setEnabled(false);
                window.loginField.setEnabled(false);
                window.emailField.setEnabled(false);
                window.passwordField.setEnabled(false);
                window.websiteField.setEnabled(false);
                window.categoryComboBox.setEnabled(false);
                window.copyLoginButton.setEnabled(false);
                window.copyEmailButton.setEnabled(false);
                window.copyPasswordButton.setEnabled(false);
                window.showPasswordButton.setEnabled(false);
                window.openButton.setEnabled(false);
                window.categoryComboBox.setEnabled(false);
                window.switchDatabaseButton.setEnabled(true);

                window.nameField.setText("");
                window.loginField.setText("");
                window.emailField.setText("");
                window.passwordField.setText("");
                window.websiteField.setText("");
                window.categoryComboBox.setSelectedItem("");

                window.addButton.requestFocus();
            }
            case CREATE_CREDENTIAL -> {
                window.searchTextField.setEnabled(false);
                window.searchButton.setEnabled(false);
                window.credentialInfoList.setEnabled(false);
                window.addButton.setEnabled(false);
                window.editButton.setEnabled(true);
                window.deleteButton.setEnabled(true);
                window.nameField.setEnabled(true);
                window.loginField.setEnabled(true);
                window.emailField.setEnabled(true);
                window.passwordField.setEnabled(true);
                window.websiteField.setEnabled(true);
                window.categoryComboBox.setEnabled(true);
                window.copyLoginButton.setEnabled(false);
                window.copyEmailButton.setEnabled(false);
                window.copyPasswordButton.setEnabled(true);
                window.showPasswordButton.setEnabled(false);
                window.openButton.setEnabled(false);
                window.categoryComboBox.setEnabled(true);
                window.switchDatabaseButton.setEnabled(false);

                window.nameField.setText("");
                window.loginField.setText("");
                window.emailField.setText("");
                window.passwordField.setText("");
                window.websiteField.setText("");
                window.categoryComboBox.setSelectedItem("");
                window.passwordField.setEchoChar((char)0);

                window.editButton.setText("Create");
                window.deleteButton.setText("Cancel");
                window.copyPasswordButton.setText("Generate");
                window.showPasswordButton.setText("Show");

                window.nameField.requestFocus();
            }
            case DISPLAY_CREDENTIAL -> {
                window.searchTextField.setEnabled(true);
                window.searchButton.setEnabled(true);
                window.credentialInfoList.setEnabled(true);
                window.addButton.setEnabled(true);
                window.editButton.setEnabled(true);
                window.deleteButton.setEnabled(true);
                window.nameField.setEnabled(false);
                window.loginField.setEnabled(false);
                window.emailField.setEnabled(false);
                window.passwordField.setEnabled(false);
                window.websiteField.setEnabled(false);
                window.categoryComboBox.setEnabled(false);
                window.copyLoginButton.setEnabled(true);
                window.copyEmailButton.setEnabled(true);
                window.copyPasswordButton.setEnabled(true);
                window.showPasswordButton.setEnabled(true);
                window.openButton.setEnabled(true);
                window.categoryComboBox.setEnabled(false);
                window.switchDatabaseButton.setEnabled(true);

                window.passwordField.setEchoChar('•');

                window.editButton.setText("Edit");
                window.deleteButton.setText("Delete");
                window.copyPasswordButton.setText("Copy");
                window.showPasswordButton.setText("Show");

                window.credentialInfoList.requestFocus();
            }
            case EDIT_CREDENTIAL -> {
                window.searchTextField.setEnabled(false);
                window.searchButton.setEnabled(false);
                window.credentialInfoList.setEnabled(false);
                window.addButton.setEnabled(false);
                window.editButton.setEnabled(true);
                window.deleteButton.setEnabled(true);
                window.nameField.setEnabled(true);
                window.loginField.setEnabled(true);
                window.emailField.setEnabled(true);
                window.passwordField.setEnabled(true);
                window.websiteField.setEnabled(true);
                window.categoryComboBox.setEnabled(true);
                window.copyLoginButton.setEnabled(false);
                window.copyEmailButton.setEnabled(false);
                window.copyPasswordButton.setEnabled(true);
                window.showPasswordButton.setEnabled(false);
                window.openButton.setEnabled(false);
                window.categoryComboBox.setEnabled(true);
                window.switchDatabaseButton.setEnabled(false);

                window.passwordField.setEchoChar((char)0);

                window.editButton.setText("Save");
                window.deleteButton.setText("Discard");
                window.copyPasswordButton.setText("Generate");
                window.showPasswordButton.setText("Show");

                window.nameField.requestFocus();
            }
        }
    }
}
