package key_wallet.controllers;

import javax.swing.*;

public class MasterPasswordController {
    private static MasterPasswordController instance;
    private MasterPasswordController() {}

    public static MasterPasswordController getInstance() {
        if (instance == null) {
            instance = new MasterPasswordController();
        }
        return instance;
    }

    private String getMasterPasswordFromUser() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter the master password to unlock");
        JPasswordField password = new JPasswordField(10);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(password);

        String[] options = new String[] { "Ok", "Cancel" };
        int option = JOptionPane.showOptionDialog(
                null,
                panel,
                "Master Password Prompt",
                JOptionPane.NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (option == 0) {
            char[] masterPassword = password.getPassword();
            return new String(masterPassword);
        } else {
            throw new RuntimeException("User aborted password prompt");
        }
    }

    private boolean validateMasterPassword(String masterPassword) {
        // TODO: validate somehow (eg Stored hash)
        return true;
    }

    public String getMasterPassword() {
        String userInput = getMasterPasswordFromUser();

        if (validateMasterPassword(userInput)) {
            return userInput;
        } else {
            throw new RuntimeException("Invalid master password entered");
        }
    }
}
