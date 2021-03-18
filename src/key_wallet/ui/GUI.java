package key_wallet.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {

    public  GUI() {         //constructor

        JFrame frame = new JFrame();
        JButton log_in_button = new JButton("Log in");
        log_in_button.setBounds(10,10,10,10);
        JLabel EnterP = new JLabel("Enter Master Password:");
        JPasswordField Password = new JPasswordField("Password");
        JLabel login_message = new JLabel("");
        login_message.setBounds(100,100,100,100);
        log_in_button.addActionListener(new GUI());

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
        new GUI();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("test");
    }
}
