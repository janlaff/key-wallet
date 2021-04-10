package key_wallet.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class GUI implements ActionListener {

    public  GUI() {         //constructor

        JFrame frame = new JFrame();
        JButton log_in_button = new JButton("Log in");
        log_in_button.setBounds(10,10,10,10);
        JLabel EnterP = new JLabel("Enter Master Password:");
        JPasswordField Password = new JPasswordField("Password");
        JLabel login_message = new JLabel("Welcome to key-wallet!");
        login_message.setBounds(100,100,100,100);
        log_in_button.addActionListener(new Action() {
            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void putValue(String key, Object value) {

            }

            @Override
            public void setEnabled(boolean b) {

            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("test");
            }
        });

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100,100, 100, 100));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(EnterP);
        panel.add(Password);
        panel.add(log_in_button);
        panel.add(login_message);


        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Key Wallet");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GUILogin();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("test");
    }
}
