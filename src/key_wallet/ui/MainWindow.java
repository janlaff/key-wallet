package key_wallet.ui;

import key_wallet.core.Database;
import key_wallet.data.Credential;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static java.lang.Math.max;

public class MainWindow {
    private JPanel mainPanel;
    private JTextField searchTextField;
    private JList<String> credentialInfoList;
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
    private UiState state;

    enum UiState {
        DISPLAY_CREDENTIAL,
        CREATE_CREDENTIAL,
        EDIT_CREDENTIAL,
    }

    public MainWindow(Database db) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addAll(db.getCredentialNames());
        credentialInfoList.setModel(listModel);

        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        List<String> categories = db.getCategories();
        comboModel.addAll(categories);
        categoryComboBox.setModel(comboModel);

        addButton.addActionListener(e -> {
            nameField.setText("");
            loginField.setText("");
            emailField.setText("");
            passwordField.setText("");
            websiteField.setText("");
            categoryComboBox.setSelectedItem("Other");

            enterUiState(UiState.CREATE_CREDENTIAL);
        });
        editButton.addActionListener(e -> {
            if (state == UiState.DISPLAY_CREDENTIAL) {
                enterUiState(UiState.EDIT_CREDENTIAL);
            } else if (state == UiState.CREATE_CREDENTIAL) {
                Credential c = new Credential(
                        nameField.getText(),
                        loginField.getText(),
                        emailField.getText(),
                        new String(passwordField.getPassword()),
                        websiteField.getText(),
                        (String) categoryComboBox.getSelectedItem()
                );

                db.addCredential(c);

                try {
                    db.update();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(mainPanel, "Failed to save changes");
                }

                listModel.addElement(c.name);
                credentialInfoList.setSelectedIndex(listModel.getSize() - 1);
                enterUiState(UiState.DISPLAY_CREDENTIAL);
            } else {
                int index = credentialInfoList.getSelectedIndex();
                Credential c = db.getCredential(index);
                c.name = nameField.getText();
                c.login = loginField.getText();
                c.email = emailField.getText();
                c.password = new String(passwordField.getPassword());
                c.website = websiteField.getText();
                c.category = (String) categoryComboBox.getSelectedItem();
                listModel.set(credentialInfoList.getSelectedIndex(), c.name);

                if (!categories.contains(c.category)) {
                    comboModel.addElement(c.category);
                }

                db.updateCredential(credentialInfoList.getSelectedIndex(), c);

                try {
                    db.update();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(mainPanel, "Failed to save changes");
                }

                enterUiState(UiState.DISPLAY_CREDENTIAL);
            }
        });
        deleteButton.addActionListener(e -> {
            if (state == UiState.CREATE_CREDENTIAL) {
                if (listModel.getSize() > 0) {
                    credentialInfoList.setSelectedIndex(0);
                }

                enterUiState(UiState.DISPLAY_CREDENTIAL);
            } else if (state == UiState.EDIT_CREDENTIAL) {
                int index = credentialInfoList.getSelectedIndex();
                Credential c = db.getCredential(index);

                c.name = nameField.getText();
                c.login = loginField.getText();
                c.email = emailField.getText();
                c.password = new String(passwordField.getPassword());
                c.website = websiteField.getText();
                c.category = (String) categoryComboBox.getSelectedItem();
                listModel.set(credentialInfoList.getSelectedIndex(), c.name);

                enterUiState(UiState.DISPLAY_CREDENTIAL);
            } else {
                int idx = credentialInfoList.getSelectedIndex();
                listModel.remove(idx);

                if (listModel.getSize() > 0) {
                    credentialInfoList.setSelectedIndex(max(idx - 1, 0));
                }

                db.removeCredential(idx);

                try {
                    db.update();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(mainPanel, "Failed to save changes!");
                }
            }
        });
        credentialInfoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = credentialInfoList.getSelectedIndex();

                if (idx != -1) {
                    Credential c = db.getCredential(idx);

                    nameField.setText(c.name);
                    loginField.setText(c.login);
                    emailField.setText(c.email);
                    passwordField.setText(c.password);
                    websiteField.setText(c.website);
                    categoryComboBox.setSelectedItem(c.category);
                }
            }
        });
        copyLoginButton.addActionListener(e -> copyToClipboard(loginField.getText()));
        copyEmailButton.addActionListener(e -> copyToClipboard(emailField.getText()));
        copyPasswordButton.addActionListener(e -> copyToClipboard(new String(passwordField.getPassword())));
        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.getText().equals("Show")) {
                passwordField.setEchoChar((char) 0);
                showPasswordButton.setText("Hide");
            } else {
                passwordField.setEchoChar('•');
                showPasswordButton.setText("Show");
            }
        });
        searchButton.addActionListener(e -> {

        });
        openButton.addActionListener(e -> {
            String website = websiteField.getText();

            if (!website.isEmpty()) {
                try {
                    Desktop.getDesktop().browse(URI.create(website));
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(mainPanel, "Failed to open web browser");
                }
            }
        });

        databaseLabel.setText("Database File: " + db.getFile().getName());

        if (credentialInfoList.getModel().getSize() != 0) {
            credentialInfoList.setSelectedIndex(0);
        }

        enterUiState(UiState.DISPLAY_CREDENTIAL);
    }

    private void copyToClipboard(String value) {
        StringSelection selection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);

        JOptionPane.showMessageDialog(mainPanel, "Copied to clipboard!");
    }

    private void enterUiState(UiState newState) {
        switch (newState) {
            case CREATE_CREDENTIAL -> {
                // Button texts
                editButton.setText("Create");
                deleteButton.setText("Cancel");
                // Edit fields
                nameField.setEditable(true);
                loginField.setEditable(true);
                emailField.setEditable(true);
                passwordField.setEditable(true);
                passwordField.setEchoChar((char) 0);
                websiteField.setEditable(true);
                categoryComboBox.setEditable(true);
                // Disabled widgets
                addButton.setEnabled(false);
                searchButton.setEnabled(false);
                searchTextField.setEnabled(false);
                credentialInfoList.setEnabled(false);
                categoryComboBox.setEnabled(true);
                // Focus
                nameField.requestFocus();
                credentialInfoList.clearSelection();
            }
            case DISPLAY_CREDENTIAL -> {
                // Button texts
                editButton.setText("Edit");
                deleteButton.setText("Delete");
                // Edit fields
                nameField.setEditable(false);
                loginField.setEditable(false);
                emailField.setEditable(false);
                passwordField.setEditable(false);
                passwordField.setEchoChar('•');
                websiteField.setEditable(false);
                categoryComboBox.setEditable(false);
                // Disabled widgets
                addButton.setEnabled(true);
                deleteButton.setEnabled(true);
                searchButton.setEnabled(true);
                searchTextField.setEnabled(true);
                credentialInfoList.setEnabled(true);
                categoryComboBox.setEnabled(false);
                // Focus
                searchTextField.requestFocus();
            }
            case EDIT_CREDENTIAL -> {
                // Button texts
                editButton.setText("Save");
                // Edit fields
                nameField.setEditable(true);
                loginField.setEditable(true);
                emailField.setEditable(true);
                passwordField.setEditable(true);
                passwordField.setEchoChar((char) 0);
                websiteField.setEditable(true);
                categoryComboBox.setEditable(true);
                // Disabled widgets
                credentialInfoList.setEnabled(false);
                searchButton.setEnabled(false);
                searchTextField.setEnabled(false);
                addButton.setEnabled(false);
                deleteButton.setEnabled(false);
                categoryComboBox.setEnabled(true);
                // Focus
                nameField.requestFocus();
            }
        }

        state = newState;
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
        loginField.setEditable(false);
        panel1.add(loginField, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Password");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordField = new JPasswordField();
        passwordField.setEditable(false);
        panel1.add(passwordField, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Name");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameField = new JTextField();
        nameField.setEditable(false);
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
        databaseLabel.setText("Database File: None");
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
