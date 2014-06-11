import com.mongodb.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


public class Plot implements ChartMouseListener {
    public void setpShow(JPanel pShow) {
        this.pShow = pShow;
    }

    public void setpChart(JPanel pChart) {
        this.pChart = pChart;
    }

    public void setPlmain(JPanel plmain) {
        this.plmain = plmain;
    }

    private JPanel pShow;
    private JPanel pChart;
    private static String strName = "";
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private JPanel plmain;
    private static boolean iStart = false;
    private static DBCollection coll = null;

    ////定时数据
    private static TimeSeriesCollection dataset;
    private static TimeSeries timeSeries;
    private static Mongo mongo;
    private static DB db;
    private static DBCollection collin;
    private static DBCollection collout;
    private static Label jl;
    private static int count = 30;
    private static NumberAxis localNumberAxis1;
    private static StandardXYItemRenderer localStandardXYItemRenderer1;
    private static Date dtPre = new Date(1900);


    class DataGenerator extends Timer implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            chartPanel.validate();
            if(iStart) {
                Calendar calener = Calendar.getInstance();
                Date d1 = calener.getTime();
                Date d2 = new Date(calener.getTime().getTime() - 30000);
                BasicDBObject b2 = new BasicDBObject();
                b2.put("$gte", d2);
                b2.put("$lte", d1);
                DBCursor cursor = coll.find(new BasicDBObject("_id", b2)).sort(new BasicDBObject("_id", -1)).limit(2);
                while (cursor.hasNext()) {
                    DBObject dbo = cursor.next();

                    Date ds = (Date) dbo.get("_id");
                    RegularTimePeriod rtp = new Millisecond(ds);
                    int num = timeSeries.getItemCount();
                    TimeSeriesDataItem tsd = timeSeries.getDataItem(num - 1);
                    RegularTimePeriod rtp1 = tsd.getPeriod();
                    if (rtp.getFirstMillisecond() > rtp1.getFirstMillisecond()) {
                        Date arrDate = (Date) dbo.get("_id");

                        long ds1 = dtPre.getTime();
                        long de1 = arrDate.getTime();
                        if (de1 - ds1 < 1000) {
                            timeSeries.add(new Millisecond(arrDate), (double) (de1 - ds1 - 500));
                        }

                        dtPre = arrDate;

                        NumberFormat formate = NumberFormat.getNumberInstance();
                        formate.setMaximumFractionDigits(4);//设定小数最大为数   ，那么显示的最后会四舍五入的
                        String m = formate.format((double) (de1 - ds1 - 500));
                        jl.setText(m);
                        jl.validate();

                        localNumberAxis1.setLowerBound(-100);
                        localNumberAxis1.setUpperBound(100);
                        localStandardXYItemRenderer1.setSeriesPaint(0, Color.red);
                        chartPanel.validate();

                        pShow.validate();
                        RegularTimePeriod times = timeSeries.getDataItem(0).getPeriod();
                        long cmp = rtp.getFirstMillisecond() - times.getFirstMillisecond();
                        if (cmp > count * 60000) {
                            timeSeries.delete(0, 1);
                        }
                    }
                }
            }
        }

        DataGenerator(int interval) {
            super(interval, null);
            addActionListener(this);
        }


    }

    public void plot() {
        pChart.removeAll();
        pShow.removeAll();
        pChart.setLayout(new BorderLayout());

        localNumberAxis1 = new NumberAxis();
        localStandardXYItemRenderer1 = new StandardXYItemRenderer();

        ///绘制实时曲线
        //获取屏幕分辨率的工具集
        Toolkit tool = Toolkit.getDefaultToolkit();
        //利用工具集获取屏幕的分辨率
        Dimension dim = tool.getScreenSize();
        //获取屏幕分辨率的高度
        int height = (int) dim.getHeight();

        dataset = new TimeSeriesCollection();
        timeSeries = new TimeSeries("1");
        jl = new Label();
        pShow.removeAll();
        pShow.setLayout(new GridLayout(height / 25, 1));

        dataset = new TimeSeriesCollection();
        timeSeries = new TimeSeries("");
        jl = new Label();
        // jl[i].setBackground((Color)V_color.get(i));
        pShow.add(jl);

        pShow.validate();

        Calendar calener = Calendar.getInstance();
        Date d1 = calener.getTime();
        Date d2 = new Date(calener.getTime().getTime() - 60000);
        BasicDBObject b2 = new BasicDBObject();
        //b2.put("$gte", d2);
        b2.put("$lte", d1);
        if (strName.equals("DATAIN"))
            coll = collin;
        if (strName.equals("DATAOUT"))
            coll = collout;
        DBCursor cursor = coll.find(new BasicDBObject("_id", b2)).sort(new BasicDBObject("_id", 1));
        while (cursor.hasNext()) {
            DBObject dbo = cursor.next();
            Date arrDate = (Date) dbo.get("_id");
            if (dtPre.getTime() != 1900) {
                long ds = dtPre.getTime();
                long de = arrDate.getTime();
                ///if (de - ds < 1000) {
                timeSeries.add(new Millisecond(arrDate), (double) (de - ds - 500));// }
            }
            dtPre = arrDate;
        }
        if (!timeSeries.isEmpty())
            dataset.addSeries(timeSeries);
        DateAxis domain = new DateAxis("");
        NumberAxis range = new NumberAxis("");
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
        renderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        XYPlot plot = new XYPlot(dataset, domain, range, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

        ValueAxis rangeAxis = plot.getRangeAxis();
        //设置最高的一个 Item 与图片顶端的距离
        rangeAxis.setUpperMargin(0.35);
        //设置最低的一个 Item 与图片底端的距离
        rangeAxis.setLowerMargin(0.45);

        domain.setAutoRange(true);
        domain.setLowerMargin(0.0);
        domain.setUpperMargin(0.0);
        domain.setTickLabelsVisible(true);
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart = new JFreeChart("", new Font("SansSerif", Font.BOLD, 24), plot, false);
        chart.setBackgroundPaint(Color.white);
        localNumberAxis1 = new NumberAxis("");
        localNumberAxis1.setLowerBound(-100);
        localNumberAxis1.setUpperBound(100);
        plot.setRangeAxis(0, localNumberAxis1);
        XYDataset localXYDataset2 = dataset;
        plot.setDataset(0, localXYDataset2);
        plot.mapDatasetToRangeAxis(0, 0);
        localStandardXYItemRenderer1 = new StandardXYItemRenderer();
        plot.setRenderer(0, localStandardXYItemRenderer1);
        localNumberAxis1.setLabelPaint(Color.red);
        localNumberAxis1.setTickLabelPaint(Color.red);
        localStandardXYItemRenderer1.setSeriesPaint(0, Color.red);
        //控制小数点
        NumberFormat numformatter = NumberFormat.getInstance(); // 创建一个数字格式格式对象
        numformatter.setMaximumFractionDigits(1);   // 设置数值小数点后最多2位
        numformatter.setMinimumFractionDigits(1);   // 设置数值小数点后最少2位
        localNumberAxis1.setNumberFormatOverride(numformatter);    // 设置为Y轴显示数据间隔为10
        localNumberAxis1.setVisible(false);
        chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createLineBorder(Color.black)));
        chartPanel.addChartMouseListener(this);
        chartPanel.validate();
        pChart.add(chartPanel);
        pChart.validate();

    }

    public void addPanel() {
        JPanel p0 = new JPanel();
        p0.removeAll();
        p0.setLayout(new GridLayout(20, 1));
        final JCheckBox chbin = new JCheckBox("DATAIN");
        final JCheckBox chbout = new JCheckBox("DATAOUT");
        chbin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (strName.equals("DATAIN")) {
                    strName = "";
                    chbin.setSelected(false);
                    iStart = false;
                } else {
                    strName = "DATAIN";
                    if (chbout.isSelected()) {
                        chbout.setSelected(false);
                        iStart = false;
                    }
                    if (iStart == false) {
                        iStart = true;
                        plot();
                        new DataGenerator(500).start();
                    }
                }
            }
        });

        chbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (strName.equals("DATAOUT")) {
                    strName = "";
                    chbout.setSelected(false);
                    iStart = false;
                } else {
                    strName = "DATAOUT";
                    if (chbin.isSelected()) {
                        chbin.setSelected(false);
                        iStart = false;
                    }
                    if (iStart == false) {
                        iStart = true;
                        plot();
                        new DataGenerator(500).start();
                    }
                }
            }
        });
        p0.add(chbin);
        p0.add(chbout);
        p0.validate();
        pChart.removeAll();
        pChart.setLayout(new BorderLayout());
        pChart.setPreferredSize(new Dimension(1000, 800));
        pChart.validate();

        JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(1);
        splitPane1.setLeftComponent(pChart);
        splitPane1.setRightComponent(pShow);
        splitPane1.setDividerSize(0);
        splitPane1.setEnabled(false);
        splitPane1.setDividerSize(0);
        splitPane1.setResizeWeight(0.95);

        JSplitPane splMain = new JSplitPane();
        splMain.setOrientation(1);
        splMain.setLeftComponent(p0);
        splMain.setRightComponent(splitPane1);
        splMain.setDividerSize(5);
        splMain.setDividerLocation(120);
        plmain.setLayout(new GridLayout(1, 1));
        plmain.add(splMain);
        plmain.validate();
    }

    private String paramIp = null;
    private int paramPort = 0;
    private String paramDb = null;

    public Plot(Vector V_DbParam) {
        paramIp = V_DbParam.get(0).toString();
        paramPort = Integer.parseInt(V_DbParam.get(1).toString());
        paramDb = V_DbParam.get(2).toString();
        count = Integer.parseInt(V_DbParam.get(3).toString());
        try {
            mongo = new Mongo(paramIp, paramPort);
            db = mongo.getDB(paramDb);
            collin = db.getCollection("DATAIN");
            collout = db.getCollection("DATAOUT");
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }

    }

    ValueFrame frame = new ValueFrame();

    public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent) {
        int xPos = paramChartMouseEvent.getTrigger().getX();
        int yPos = paramChartMouseEvent.getTrigger().getY();
        Point2D point2D = this.chartPanel.translateScreenToJava2D(new Point(xPos, yPos));
        XYPlot xyPlot = (XYPlot) this.chart.getPlot();
        ChartRenderingInfo chartRenderingInfo = this.chartPanel.getChartRenderingInfo();
        Rectangle2D rectangle2D = chartRenderingInfo.getPlotInfo().getDataArea();
        ValueAxis valueAxis1 = xyPlot.getDomainAxis();
        RectangleEdge rectangleEdge1 = xyPlot.getDomainAxisEdge();
        ValueAxis valueAxis2 = xyPlot.getRangeAxis();
        RectangleEdge rectangleEdge2 = xyPlot.getRangeAxisEdge();
        double d1 = valueAxis1.java2DToValue(point2D.getX(), rectangle2D, rectangleEdge1);
        frame.setColl(coll);
        frame.setdTime(d1);
        if (!ValueFrame.isShow) {
            frame.frameShow();
        }
    }

    public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent) {

    }

}
