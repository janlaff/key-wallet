package key_wallet.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import key_wallet.core.*;
import key_wallet.data.Credential;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static java.lang.Integer.max;

public class SwingUserInterface implements UserInterface {
    private JPanel mainPanel;
    private JTextField searchTextField;
    private JList credentialInfoList;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JButton copyLoginButton;
    private JButton copyPasswordButton;
    private JButton showPasswordButton;
    private JButton searchButton;
    private JLabel databaseLabel;
    private JComboBox<String> categoryComboBox;
    private JButton switchDatabaseButton;
    private JTextField emailField;
    private JButton copyEmailButton;
    private JTextField websiteField;
    private JButton openButton;
    private DefaultListModel<Database.IdWith<String>> credentialListModel;
    private DefaultComboBoxModel<String> categoryComboModel;
    private UiState uiState;

    public SwingUserInterface(App app) {
        credentialListModel = new DefaultListModel<>();
        categoryComboModel = new DefaultComboBoxModel<>();

        addButton.addActionListener((e) -> {
            setUiState(UiState.CREATE_CREDENTIAL);
        });

        editButton.addActionListener((e) -> {
            switch (uiState) {
                case EDIT_CREDENTIAL -> app.updateCredential(getSelectedCredentialId(), getCredentialInput());
                case CREATE_CREDENTIAL -> app.createCredential(getCredentialInput());
                case DISPLAY_CREDENTIAL -> setUiState(UiState.EDIT_CREDENTIAL);
            }
        });

        credentialInfoList.addListSelectionListener((e) -> {
            if (!e.getValueIsAdjusting() && credentialInfoList.getSelectedIndex() != -1) {
                app.selectCredential();
            }
        });

        deleteButton.addActionListener((e) -> {
            switch (uiState) {
                case EDIT_CREDENTIAL -> app.discardCredential();
                case CREATE_CREDENTIAL -> app.discardCredential();
                case DISPLAY_CREDENTIAL -> app.deleteCredential();
            }
        });

        copyPasswordButton.addActionListener((e) -> {
            if (uiState == UiState.CREATE_CREDENTIAL || uiState == UiState.EDIT_CREDENTIAL) {
                copyToClipboard(new String(passwordField.getPassword()));
            } else {
                app.generatePassword();
            }
        });

        copyLoginButton.addActionListener((e) -> {
            copyToClipboard(loginField.getText());
        });

        copyEmailButton.addActionListener((e) -> {
            copyToClipboard(emailField.getText());
        });

        showPasswordButton.addActionListener((e) -> {
            if (showPasswordButton.getText().equals("Show")) {
                showPasswordButton.setText("Hide");
                passwordField.setEchoChar((char) 0);
            } else {
                showPasswordButton.setText("Show");
                passwordField.setEchoChar('•');
            }
        });

        openButton.addActionListener((e) -> {
            String website = websiteField.getText();

            if (!website.isEmpty()) {
                try {
                    Desktop.getDesktop().browse(URI.create(website));
                } catch (IOException ioException) {
                    showMessage("Failed to open web browser");
                }
            }
        });

        searchButton.addActionListener((e) -> {
        });

        switchDatabaseButton.addActionListener((e) -> {
            app.switchDatabase();
        });

        credentialInfoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((Database.IdWith<String>) value).value, index, isSelected, cellHasFocus);
            }
        });

        JFrame frame = new JFrame("key-wallet");
        frame.setContentPane($$$getRootComponent$$$());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    @Override
    public void setTheme(String theme) {
        if (theme.equals("Dark")) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                SwingUtilities.updateComponentTreeUI(mainPanel.getRootPane());
            } catch (UnsupportedLookAndFeelException e) {
                JOptionPane.showMessageDialog(mainPanel, e.getMessage());
            }
        }
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(mainPanel, message);
    }

    @Override
    public void showDbConnectionString(String uri) {
        databaseLabel.setText("Database: " + uri);
    }

    @Override
    public void showCredentials(List<Database.IdWith<String>> credentialNames, List<String> categories) {
        credentialListModel = new DefaultListModel<>();
        categoryComboModel = new DefaultComboBoxModel<>();
        credentialListModel.addAll(credentialNames);
        categoryComboModel.addAll(categories);
        credentialInfoList.setModel(credentialListModel);
        categoryComboBox.setModel(categoryComboModel);
        credentialInfoList.setSelectedIndex(0);
    }

    @Override
    public void showCredential(Credential credential) {
        nameField.setText(credential.name);
        loginField.setText(credential.login);
        emailField.setText(credential.email);
        passwordField.setText(credential.password);
        websiteField.setText(credential.website);
        categoryComboBox.setSelectedItem(credential.category);
    }

    @Override
    public void addCredential(Database.IdWith<String> credentialName) {
        credentialListModel.addElement(credentialName);
        credentialInfoList.setSelectedIndex(credentialListModel.getSize() - 1);
    }

    @Override
    public void updateCredential(Database.IdWith<String> credentialName) {
        credentialListModel.set(credentialInfoList.getSelectedIndex(), credentialName);
    }

    @Override
    public void removeSelectedCredential() {
        int idx = credentialInfoList.getSelectedIndex();
        credentialListModel.remove(idx);

        if (credentialListModel.getSize() > 0) {
            credentialInfoList.setSelectedIndex(max(idx - 1, 0));

            setUiState(UiState.DISPLAY_CREDENTIAL);
        } else {
            setUiState(UiState.NO_CREDENTIALS);
        }
    }

    @Override
    public void setGeneratedPassword(String password) {
        passwordField.setText(password);
    }

    @Override
    public Credential getCredentialInput() {
        Credential c = new Credential(
                nameField.getText(),
                loginField.getText(),
                emailField.getText(),
                new String(passwordField.getPassword()),
                websiteField.getText(),
                (String) categoryComboBox.getSelectedItem()
        );

        if (categoryComboModel.getIndexOf(c.category) == -1) {
            categoryComboModel.addElement(c.category);
        }

        return c;
    }

    @Override
    public int getSelectedCredentialId() {
        int index = credentialInfoList.getSelectedIndex();
        if (index == -1) return -1;
        return credentialListModel.get(index).id;
    }

    @Override
    public String loadMasterPassword() {
        JPasswordField passwordField = new JPasswordField(10);
        int choice = JOptionPane.showConfirmDialog(
                mainPanel,
                passwordField,
                "Enter Master Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        } else {
            return "";
        }
    }

    @Override
    public String loadUiTheme() {
        String[] themeOptions = new String[]{"Light", "Dark"};

        int themeChoice = JOptionPane.showOptionDialog(
                mainPanel,
                "Choose ui theme",
                "Ui Theme Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                themeOptions,
                themeOptions[0]
        );

        return themeOptions[themeChoice];
    }

    @Override
    public String loadDbConnectionUri() {
        String[] databaseOptions = new String[]{"Create new", "Choose existing", "Connect to remote"};

        int databaseChoice = JOptionPane.showOptionDialog(
                mainPanel,
                "Choose application database",
                "Database Setup",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                databaseOptions,
                databaseOptions[0]
        );

        if (databaseChoice == 0) {
            return LocalDatabase.DEFAULT_URI;
        } else if (databaseChoice == 1) {
            JFileChooser fileChooser = new JFileChooser("./");
            int fileChooserResult = fileChooser.showOpenDialog(mainPanel);

            if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
                File databaseFile = fileChooser.getSelectedFile();
                return LocalDatabase.LOCATOR + databaseFile.getAbsolutePath();
            } else {
                return loadDbConnectionUri();
            }
        } else {
            // TODO: add new dialog
            return SqliteDatabase.DEFAULT_URI;
        }
    }

    @Override
    public void setUiState(UiState state) {
        uiState = state;
        update();
    }

    @Override
    public UiState getUiState() {
        return uiState;
    }

    private void copyToClipboard(String value) {
        StringSelection selection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);

        showMessage("Copied to clipboard");
    }

    private void update() {
        switch (uiState) {
            case DISABLED -> {
                searchTextField.setEnabled(false);
                searchButton.setEnabled(false);
                credentialInfoList.setEnabled(false);
                addButton.setEnabled(false);
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                nameField.setEnabled(false);
                loginField.setEnabled(false);
                emailField.setEnabled(false);
                passwordField.setEnabled(false);
                websiteField.setEnabled(false);
                categoryComboBox.setEnabled(false);
                copyLoginButton.setEnabled(false);
                copyEmailButton.setEnabled(false);
                copyPasswordButton.setEnabled(false);
                showPasswordButton.setEnabled(false);
                openButton.setEnabled(false);
                categoryComboBox.setEnabled(false);
                switchDatabaseButton.setEnabled(false);
            }
            case NO_CREDENTIALS -> {
                searchTextField.setEnabled(false);
                searchButton.setEnabled(false);
                credentialInfoList.setEnabled(false);
                addButton.setEnabled(true);
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                nameField.setEnabled(false);
                loginField.setEnabled(false);
                emailField.setEnabled(false);
                passwordField.setEnabled(false);
                websiteField.setEnabled(false);
                categoryComboBox.setEnabled(false);
                copyLoginButton.setEnabled(false);
                copyEmailButton.setEnabled(false);
                copyPasswordButton.setEnabled(false);
                showPasswordButton.setEnabled(false);
                openButton.setEnabled(false);
                categoryComboBox.setEnabled(false);
                switchDatabaseButton.setEnabled(true);

                nameField.setText("");
                loginField.setText("");
                emailField.setText("");
                passwordField.setText("");
                websiteField.setText("");
                categoryComboBox.setSelectedItem("");

                addButton.requestFocus();
            }
            case CREATE_CREDENTIAL -> {
                searchTextField.setEnabled(false);
                searchButton.setEnabled(false);
                credentialInfoList.setEnabled(false);
                addButton.setEnabled(false);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                nameField.setEnabled(true);
                loginField.setEnabled(true);
                emailField.setEnabled(true);
                passwordField.setEnabled(true);
                websiteField.setEnabled(true);
                categoryComboBox.setEnabled(true);
                copyLoginButton.setEnabled(false);
                copyEmailButton.setEnabled(false);
                copyPasswordButton.setEnabled(true);
                showPasswordButton.setEnabled(false);
                openButton.setEnabled(false);
                categoryComboBox.setEnabled(true);
                switchDatabaseButton.setEnabled(false);

                nameField.setText("");
                loginField.setText("");
                emailField.setText("");
                passwordField.setText("");
                websiteField.setText("");
                categoryComboBox.setSelectedItem("");
                passwordField.setEchoChar((char) 0);

                editButton.setText("Create");
                deleteButton.setText("Cancel");
                copyPasswordButton.setText("Generate");
                showPasswordButton.setText("Show");

                nameField.requestFocus();
            }
            case DISPLAY_CREDENTIAL -> {
                searchTextField.setEnabled(true);
                searchButton.setEnabled(true);
                credentialInfoList.setEnabled(true);
                addButton.setEnabled(true);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                nameField.setEnabled(false);
                loginField.setEnabled(false);
                emailField.setEnabled(false);
                passwordField.setEnabled(false);
                websiteField.setEnabled(false);
                categoryComboBox.setEnabled(false);
                copyLoginButton.setEnabled(true);
                copyEmailButton.setEnabled(true);
                copyPasswordButton.setEnabled(true);
                showPasswordButton.setEnabled(true);
                openButton.setEnabled(true);
                categoryComboBox.setEnabled(false);
                switchDatabaseButton.setEnabled(true);

                passwordField.setEchoChar('•');

                editButton.setText("Edit");
                deleteButton.setText("Delete");
                copyPasswordButton.setText("Copy");
                showPasswordButton.setText("Show");

                credentialInfoList.requestFocus();
            }
            case EDIT_CREDENTIAL -> {
                searchTextField.setEnabled(false);
                searchButton.setEnabled(false);
                credentialInfoList.setEnabled(false);
                addButton.setEnabled(false);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                nameField.setEnabled(true);
                loginField.setEnabled(true);
                emailField.setEnabled(true);
                passwordField.setEnabled(true);
                websiteField.setEnabled(true);
                categoryComboBox.setEnabled(true);
                copyLoginButton.setEnabled(false);
                copyEmailButton.setEnabled(false);
                copyPasswordButton.setEnabled(true);
                showPasswordButton.setEnabled(false);
                openButton.setEnabled(false);
                categoryComboBox.setEnabled(true);
                switchDatabaseButton.setEnabled(false);

                passwordField.setEchoChar((char) 0);

                editButton.setText("Save");
                deleteButton.setText("Discard");
                copyPasswordButton.setText("Generate");
                showPasswordButton.setText("Show");

                nameField.requestFocus();
            }
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        credentialInfoList = new JList();
        scrollPane1.setViewportView(credentialInfoList);
        addButton = new JButton();
        addButton.setText("Add");
        mainPanel.add(addButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(12, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Login");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loginField = new JTextField();
        loginField.setEditable(true);
        panel1.add(loginField, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Password");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        panel1.add(passwordField, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Name");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameField = new JTextField();
        nameField.setEditable(true);
        panel1.add(nameField, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        copyLoginButton = new JButton();
        copyLoginButton.setText("Copy");
        panel1.add(copyLoginButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyPasswordButton = new JButton();
        copyPasswordButton.setText("Copy");
        panel2.add(copyPasswordButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showPasswordButton = new JButton();
        showPasswordButton.setText("Show");
        panel2.add(showPasswordButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Category");
        panel1.add(label4, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        categoryComboBox = new JComboBox();
        categoryComboBox.setEditable(true);
        panel1.add(categoryComboBox, new com.intellij.uiDesigner.core.GridConstraints(11, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Email");
        panel1.add(label5, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        emailField = new JTextField();
        panel1.add(emailField, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        copyEmailButton = new JButton();
        copyEmailButton.setText("Copy");
        panel1.add(copyEmailButton, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Website");
        panel1.add(label6, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        websiteField = new JTextField();
        panel1.add(websiteField, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        panel1.add(openButton, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editButton = new JButton();
        editButton.setText("Edit");
        panel3.add(editButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Delete");
        panel3.add(deleteButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        databaseLabel = new JLabel();
        databaseLabel.setText("Database: None");
        mainPanel.add(databaseLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchTextField = new JTextField();
        searchTextField.setText("");
        panel4.add(searchTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchButton = new JButton();
        searchButton.setText("Search");
        panel4.add(searchButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        switchDatabaseButton = new JButton();
        switchDatabaseButton.setText("Switch Database");
        mainPanel.add(switchDatabaseButton, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
