import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Thinkpad
 * Date: 13-7-22
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */
public class AddMenuBar {
    public static JMenuItem item0;

    public void setpShow(JPanel pShow) {
        this.pShow = pShow;
    }

    public void setpChart(JPanel pChart) {
        this.pChart = pChart;
    }

    public void setPlmain(JPanel plmain) {
        this.plmain = plmain;
    }

    private JPanel plmain;

    private JPanel pShow;
    private JPanel pChart;

    public void setV_DbParam(Vector v_DbParam) {
        V_DbParam = v_DbParam;
    }

    private Vector V_DbParam;

    public JMenuBar addMenu() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("数据源配置");
        menu.setMnemonic('F');
        menubar.add(menu);

        item0 = new JMenuItem("连接");
        menu.add(item0);
        menu.addSeparator();
        JMenuItem item2 = new JMenuItem("取消");
        menu.add(item2);

        //配置数据源
        item0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Setting set = new Setting();
                set.setPlmain(plmain);
                set.setpChart(pChart);
                set.setpShow(pShow);
                set.setV_DbParam(V_DbParam);
                set.setFrame();
            }


        });

        return menubar;
    }
}
