package key_wallet.ui;

import key_wallet.data.Credential;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class CredentialSelectPanel extends JPanel {
    private final CredentialInfoPanel infoPanel;
    private JList credentialList;

    public CredentialSelectPanel(CredentialInfoPanel infoPanel, List<Credential> credentials) {
        this.infoPanel = infoPanel;

        DefaultListModel<Credential> listModel = new DefaultListModel<>();
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

        JScrollPane scrollPane = new JScrollPane(credentialList);

        if (!credentials.isEmpty()) {
            credentialList.setSelectedIndex(0);
        }

        JButton addBtn = new JButton("Add Credential");

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(addBtn, BorderLayout.PAGE_END);
    }
}
