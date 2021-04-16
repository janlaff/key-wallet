package key_wallet.ui;

import key_wallet.core.Database;
import key_wallet.data.Credential;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class CredentialSelectPanel extends JPanel {
    private final CredentialInfoPanel infoPanel;
    private JList credentialList;

    public CredentialSelectPanel(CredentialInfoPanel infoPanel, Database db) {
        this.infoPanel = infoPanel;

        DefaultListModel<Credential> listModel = new DefaultListModel<>();
        List<Credential> credentials = db.getCredentials();
        listModel.addAll(credentials);

        credentialList = new JList(listModel);
        credentialList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((Credential)value).description, index, isSelected, cellHasFocus);
            }
        });

        credentialList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Credential c = (Credential)credentialList.getSelectedValue();
                    infoPanel.setDisplayedCredential(c);
                }
            }
        });

        if (!credentials.isEmpty()) {
            credentialList.setSelectedIndex(0);
        }

        JScrollPane scrollPane = new JScrollPane(credentialList);
        JButton addBtn = new JButton("Add Credential");

        JPanel btnSpacer = new JPanel();
        btnSpacer.setLayout(new BorderLayout());
        btnSpacer.setBorder(new EmptyBorder(10, 0, 0, 10));
        btnSpacer.add(addBtn);

        JPanel listSpacer = new JPanel();
        listSpacer.setLayout(new BorderLayout());
        listSpacer.setBorder(new EmptyBorder(0, 0, 0, 10));
        listSpacer.add(scrollPane);

        setLayout(new BorderLayout());
        add(listSpacer, BorderLayout.CENTER);
        add(btnSpacer, BorderLayout.PAGE_END);
    }
}
