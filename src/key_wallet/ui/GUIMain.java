package key_wallet.ui;
import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;


public class GUIMain
{
    public static void main(String[] args){

        //Construction of string and close on exit setting
        JFrame mainframe = new JFrame("MainFrame");
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setTitle("key-wallet");

        //border Settings
        Border border = new LineBorder(Color.black);

        //menubar
        JMenuBar menubar = new JMenuBar();
        menubar.setBorder(border);
        //menu content
        JMenu menunew = new JMenu("New");
        menubar.add(menunew);
        //add menu item to menu content
        JMenuItem test = new JMenuItem("test");
        menunew.add(test);
        //seperates different items in menu
        JSeparator sep = new JSeparator();
        menunew.add(sep);
        JMenuItem test2 = new JMenuItem("test2");
        menunew.add(test2);

        JMenu menuedit = new JMenu("Edit");
        menubar.add(menuedit);
        JMenu menuview = new JMenu("View");
        menubar.add(menuview);
        JMenu menuhelp = new JMenu("Help");
        menubar.add(menuhelp);
        JMenu menuclose = new JMenu("Close");
        menubar.add(menuclose);
        mainframe.setJMenuBar(menubar);

        //popupmenu = right mouse click menu
        /// TODO: 09.04.2021 Add event Listener for Right Click
        JPopupMenu rmenu = new JPopupMenu();
        rmenu.setLocation(200,200);
        rmenu.add("Hallo");
        rmenu.add("hier");
        rmenu.add("steht");
        rmenu.add("ein");
        rmenu.add("text");

        //test panel
        JPanel  pwlist = new JPanel();
        pwlist.setBackground(Color.green);
        ///TODO ADD PW list
        pwlist.add(new JLabel("Dies ist ein Test."));

        JScrollPane scrollPane;
        scrollPane = new JScrollPane (pwlist);
        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(2000, 600));
        Dimension minimumSize = new Dimension(600, 600);
        pwlist.setMinimumSize(minimumSize);
        mainframe.add(scrollPane);

        JPanel  pwcontent = new JPanel();
        pwcontent.setBackground(Color.red);

        //Splitpane
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(pwlist);
        splitPane.setRightComponent(pwcontent);
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);
        mainframe.add(splitPane);

        //Set width and hight
        mainframe.setSize(800,600);

        //show contents
        mainframe.setVisible(true);
        rmenu.setVisible(true);

        //Labels
        JLabel text = new JLabel ("Test");

        //frameContent
        mainframe.add(text);

        //

    }
}
