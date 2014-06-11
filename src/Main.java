import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.jfree.chart.plot.JThermometer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class Main {
    static JFrame frame;
    static JPanel plmain = new JPanel();
    static JPanel pChart = new JPanel();
    static JPanel pShow = new JPanel();
    static Vector V_DbParam = new Vector();


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            frame = new JFrame("偏差监测");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.pack();
            frame.setSize(500, 500);
            AddMenuBar addmenu = new AddMenuBar();
            addmenu.setV_DbParam(V_DbParam);
            addmenu.setpShow(pShow);
            addmenu.setpChart(pChart);
            addmenu.setPlmain(plmain);
            frame.setJMenuBar(addmenu.addMenu());
            frame.add(plmain);
            frame.setContentPane(plmain);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.validate();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        } catch (Exception ex) {

        }
    }


}
