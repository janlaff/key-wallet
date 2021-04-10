package key_wallet.ui;
import javax.swing.*;

public class GUIfilechooser {
    public static void main(String[] args) {

        // Constructor
        JFileChooser chooser = new JFileChooser();

        // show open file dialoge
        chooser.showOpenDialog(null);

        //print selected file
        System.out.println("selected file is: " +
                chooser.getSelectedFile().getName());
    }
}
