package key_wallet.ui;

import key_wallet.data.Credential;

import javax.swing.*;
import java.awt.*;

public class CredentialInfoPanel extends JPanel {
    private final JLabel usernameLbl;
    private final JLabel descriptionLbl;
    private final JLabel passwordLbl;

    public CredentialInfoPanel() {
        usernameLbl = new JLabel();
        descriptionLbl = new JLabel();
        passwordLbl = new JLabel();

        //setLayout(new BorderLayout());

        add(usernameLbl);
        add(descriptionLbl);
        add(passwordLbl);
    }

    public void setDisplayedCredential(Credential credential) {
        usernameLbl.setText(credential.username);
        descriptionLbl.setText(credential.description);
        passwordLbl.setText(credential.password);
    }
}
