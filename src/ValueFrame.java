import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.jfree.data.time.Millisecond;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.*;

public class ValueFrame extends JFrame {


    public void setdTime(double dTime) {
        this.dTime = dTime;
    }


    public void setColl(DBCollection coll) {
        this.coll = coll;
    }

    private JTable j1;
    private double dTime;
    private DBCollection coll;
    public static boolean isShow = false;
    private static Date dtPre = new Date(1900);


    public void frameShow() {
        //int num = V_No.size();
        JFrame f = new JFrame("曲线值查看");
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

        f.setSize(300, 80);
        f.setResizable(false);

        Map map1 = new HashMap();
        Date dt00 = new Date((long) dTime);
        Date dt01 = new Date((long) dTime - 1000);
        BasicDBObject b2 = new BasicDBObject();
        b2.put("$gte", dt01);
        b2.put("$lte", dt00);

        DBCursor cursor = coll.find(new BasicDBObject("_id", b2)).sort(new BasicDBObject("_id", 1)).limit(2);

        while (cursor.hasNext()) {
            DBObject dbo = cursor.next();
            Date arrDate = (Date) dbo.get("_id");
            if (dtPre.getTime() != 1900) {
                long ds = dtPre.getTime();
                long de = arrDate.getTime();
                map1.put(arrDate.toString(), (double) (de - ds - 500));
            }
            dtPre = arrDate;
        }
        if (!map1.isEmpty()) {
            isShow = true;

            JPanel p1 = new JPanel();
            final Vector v0 = new Vector();
            v0.add("时间");
            v0.add("差值");
            Vector v1 = new Vector();

            NumberFormat formate = NumberFormat.getNumberInstance();
            formate.setMaximumFractionDigits(4);//设定小数最大为数   ，那么显示的最后会四舍五入的
            Set set1 = map1.entrySet();
            Iterator it1 = set1.iterator();
            while (it1.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it1.next();
                Vector v2 = new Vector();
                v2.add(entry.getKey());
                v2.add(entry.getValue());
                v1.add(v2);
            }
            final JTable jtable = new JTable(v1, v0);
            jtable.setEnabled(false);
            p1.removeAll();
            p1.add(new JScrollPane(jtable));
            p1.validate();
            f.add(p1);
            f.pack();
            f.setVisible(true);
            f.validate();
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    isShow = false;
                }
            });
        }
    }


    // }
}
