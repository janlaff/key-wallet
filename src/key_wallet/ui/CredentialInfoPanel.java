package key_wallet.ui;

import key_wallet.data.Credential;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CredentialInfoPanel extends JPanel {
    private final JLabel usernameLbl;
    private final JLabel descriptionLbl;
    private final JLabel passwordLbl;

    public CredentialInfoPanel() {
        usernameLbl = new JLabel();
        descriptionLbl = new JLabel();
        passwordLbl = new JLabel();

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.LINE_AXIS));
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        controls.add(editBtn);
        controls.add(delBtn);

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.add(descriptionLbl, BorderLayout.LINE_START);
        header.add(controls, BorderLayout.LINE_END);
        header.setBorder(new EmptyBorder(0, 10, 0, 0));

        setLayout(new BorderLayout());

        add(header, BorderLayout.PAGE_START);
        //add(usernameLbl);
        //add(descriptionLbl);
        //add(passwordLbl);
    }

    public void setDisplayedCredential(Credential credential) {
        usernameLbl.setText(credential.username);
        descriptionLbl.setText(credential.description);
        passwordLbl.setText(credential.password);
    }
}
