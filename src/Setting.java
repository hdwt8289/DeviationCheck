
import com.mongodb.DB;
import com.mongodb.Mongo;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class Setting extends JPanel {
    private JLabel lblName;
    private JTextField txtIp;
    private JLabel lblNo;
    private JTextField txtPort;
    private JLabel lblDb;
    private JComboBox txtDb;
    private JLabel lblColl;
    private JComboBox cmbColl;
    private JButton btnOk;
    private JButton btnCancel;


    private String paramName;
    private String paramPort;
    private String paramDb;
    private String paramColl;

    private Mongo m1 = null;
    private DB db = null;

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


    public void setFrame() {
        final JFrame f = new JFrame("数据源配置");
        //获取屏幕分辨率的工具集
        Toolkit tool = Toolkit.getDefaultToolkit();
        //利用工具集获取屏幕的分辨率
        Dimension dim = tool.getScreenSize();
        //获取屏幕分辨率的高度
        int height = (int) dim.getHeight();
        //获取屏幕分辨率的宽度
        int width = (int) dim.getWidth();
        //设置位置
        f.setLocation((width - 300) / 2, (height - 400) / 2);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        f.setContentPane(this);
        f.setSize(320, 180);
        f.setResizable(false);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //addMenuBar.iSetting = 0;
            }
        });

        lblName = new JLabel("主机名");
        txtIp = new JTextField(20);
        try {
            InetAddress addr = InetAddress.getLocalHost();
            txtIp.setText(addr.getHostAddress().toString());
        } catch (Exception ex) {
        }

        lblNo = new JLabel("端口");
        txtPort = new JTextField(20);
        txtPort.setText("27017");

        lblDb = new JLabel("工程名");
        txtDb = new JComboBox();
        txtDb.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                txtDb.removeAllItems();
                try {
                    m1 = new Mongo(txtIp.getText().toString(), 27017);
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                for (String name : m1.getDatabaseNames()) {
                    txtDb.addItem(name);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        lblColl = new JLabel("显示时常(Min)");
        cmbColl = new JComboBox();
        cmbColl.setEditable(true);
        cmbColl.addItem(30);
        cmbColl.addItem(60);
        cmbColl.addItem(90);
        cmbColl.addItem(120);
        cmbColl.addItem(180);
        cmbColl.addItem(360);


        btnOk = new JButton("确定");
        btnOk.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                V_DbParam.clear();
                paramName = txtIp.getText().toString();
                V_DbParam.add(paramName);
                paramPort = txtPort.getText().toString();
                V_DbParam.add(paramPort);
                paramDb = txtDb.getSelectedItem().toString();
                V_DbParam.add(paramDb);
                paramColl = cmbColl.getSelectedItem().toString();
                V_DbParam.add(paramColl);
                f.dispose();
                AddMenuBar.item0.setText("连接" + paramDb + "成功");

                if (V_DbParam.size() > 0) {
                    plmain.removeAll();
                    Plot plot = new Plot(V_DbParam);
                    plot.setPlmain(plmain);
                    plot.setpChart(pChart);
                    plot.setpShow(pShow);
                    plot.addPanel();
                }
            }
        });
        btnCancel = new JButton("取消");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });


        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(5, 2));
        p1.add(lblName);
        p1.add(txtIp);

        p1.add(lblNo);
        p1.add(txtPort);

        p1.add(lblDb);
        p1.add(txtDb);

        p1.add(lblColl);
        p1.add(cmbColl);

        p1.add(btnOk);
        p1.add(btnCancel);

        p1.validate();

        f.add(p1);
        f.validate();
    }
}
