package uSPIMmm;

import uSPIMmm.gui.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mmcorej.CMMCore;
import org.micromanager.api.ScriptInterface;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class GUI implements ServerResponses {
    public JTabbedPane tpMain;
    protected JPanel panRoot;
    protected JTextArea taStatus;
    protected JScrollPane spStatus;
    protected ImagePanel imagePanel1;
    protected ImagePanel imagePanel2;
    protected ImagePanel imagePanel3;
    protected ImagePanel imagePanel4;
    private MirrorCalibration zMirror1Calibration;
    private MirrorCalibration zMirror2Calibration;
    private FindPlaneMode findPlaneMode;
    private MaskDesign maskDesign1;
    public Configuration configuration;
    private JPanel panFindPlane;
    private MaskDesign maskDesign2;
    private JPanel panNormalMode;
    private DataAcquisition dataAcquisition;
    private JPanel panCalibration;
    private JPanel panConfig;
    private SpimStatus spimStatus;
    private LaserShutterCalibration galvoCalibration1;
    private LaserShutterCalibration galvoCalibration2;
    public CommandDispatcher cd;
    private SharedConfiguration scfg;


    public void createUIComponents() {
        imagePanel1 = new ImagePanel("img/placeholder.png");
        imagePanel2 = new ImagePanel("img/placeholder.png");
        imagePanel3 = new ImagePanel("img/placeholder.png");
        imagePanel4 = new ImagePanel("img/placeholder.png");
    }

    public GUI() {

        $$$setupUI$$$();

        scfg = new SharedConfiguration();
        cd = new CommandDispatcher("localhost", this);

        zMirror1Calibration.create(this, scfg, 1);
        zMirror2Calibration.create(this, scfg, 2);
        findPlaneMode.create(this, scfg);
        configuration.create(this, scfg);
        maskDesign1.create(this, scfg, 1);
        maskDesign2.create(this, scfg, 2);
        galvoCalibration1.create(this, scfg, 1);
        galvoCalibration2.create(this, scfg, 2);

        dataAcquisition.create(this, scfg);
        spimStatus.create(this, scfg);



        /*
        class QueryCEStatus extends TimerTask {
            public void run() {
                if (cd.isConnected()) {
                    System.out.println("Querying Control Engine status.");
                    cd.queryStatus();
                } else {
                    System.out.println("Control Engine is not connected.");
                    writeStatus("ERROR: TCP Socket connection to Control Engine is closed.");
                }
            }
        }

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new QueryCEStatus(), 1000, 500);
        */

        DefaultCaret caret = (DefaultCaret) taStatus.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scfg.addObserver(findPlaneMode);
        scfg.addObserver(dataAcquisition);
        scfg.addObserver(zMirror1Calibration);
        scfg.addObserver(zMirror2Calibration);
        scfg.addObserver(galvoCalibration1);
        scfg.addObserver(galvoCalibration2);
        scfg.setFramerate(scfg.getFramerate());//Trigger update
    }

    @Override
    public void onDaqStatus(String msg) {
        spimStatus.updateStatus(msg);
    }

    @Override
    public void onStatusResponse(String msg) {
        writeStatus("SERVER: " + msg);
    }

    @Override
    public void onCalibrationStatusResponse(String msg) {
        this.zMirror1Calibration.progress(msg);
        this.zMirror2Calibration.progress(msg);
    }

    @Override
    public void onCalibrationOffsetResponse(String msg) {
        zMirror1Calibration.updateOffset(msg);
        zMirror2Calibration.updateOffset(msg);
    }

    @Override
    public void onTaskDone() {
        //TODO: shutter control?
        /*
        if (MMCore != null && cbCfgShutterControl.isSelected()) {
            try {
                MMCore.setShutterOpen(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
    }


    public void setMMCore(CMMCore MMCore) {
        this.scfg.setMMCore(MMCore);
    }

    public void setScriptInterface(ScriptInterface sf) {
        this.scfg.setMMSf(sf);
    }

    public void writeStatus(String status) {
        taStatus.append("\n" + status);
        taStatus.setCaretPosition(taStatus.getDocument().getLength());

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("uSPIM Plugin");
        frame.setContentPane(new GUI().panRoot);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getRoot() {
        return panRoot;
    }


    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panRoot = new JPanel();
        panRoot.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        spStatus = new JScrollPane();
        spStatus.setHorizontalScrollBarPolicy(30);
        spStatus.setVerticalScrollBarPolicy(22);
        panRoot.add(spStatus, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        taStatus = new JTextArea();
        taStatus.setEditable(true);
        taStatus.setRows(5);
        spStatus.setViewportView(taStatus);
        tpMain = new JTabbedPane();
        panRoot.add(tpMain, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(200, 224), null, 0, false));
        panFindPlane = new JPanel();
        panFindPlane.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tpMain.addTab("1. Find Plane", panFindPlane);
        findPlaneMode = new FindPlaneMode();
        panFindPlane.add(findPlaneMode.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panFindPlane.add(spacer1, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panFindPlane.add(spacer2, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panFindPlane.add(spacer3, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        tpMain.addTab("2. Design Mask 1", panel1);
        maskDesign1 = new MaskDesign();
        panel1.add(maskDesign1.$$$getRootComponent$$$(), new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel1.add(spacer6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tpMain.addTab("3. Design Mask 2", panel2);
        maskDesign2 = new MaskDesign();
        panel2.add(maskDesign2.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel2.add(spacer7, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel2.add(spacer8, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel2.add(spacer9, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panNormalMode = new JPanel();
        panNormalMode.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tpMain.addTab("4. Acquire Data", panNormalMode);
        dataAcquisition = new DataAcquisition();
        panNormalMode.add(dataAcquisition.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panNormalMode.add(spacer10, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        panNormalMode.add(spacer11, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer12 = new Spacer();
        panNormalMode.add(spacer12, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panCalibration = new JPanel();
        panCalibration.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tpMain.addTab("Calibration", panCalibration);
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        panCalibration.add(tabbedPane1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Z-Mirror 1", panel3);
        zMirror1Calibration = new MirrorCalibration();
        panel3.add(zMirror1Calibration.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer13 = new Spacer();
        panel3.add(spacer13, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer14 = new Spacer();
        panel3.add(spacer14, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer15 = new Spacer();
        panel3.add(spacer15, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Z-Mirror 2", panel4);
        zMirror2Calibration = new MirrorCalibration();
        panel4.add(zMirror2Calibration.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer16 = new Spacer();
        panel4.add(spacer16, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer17 = new Spacer();
        panel4.add(spacer17, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer18 = new Spacer();
        panel4.add(spacer18, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Laser Shutter 1", panel5);
        galvoCalibration1 = new LaserShutterCalibration();
        panel5.add(galvoCalibration1.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer19 = new Spacer();
        panel5.add(spacer19, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer20 = new Spacer();
        panel5.add(spacer20, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer21 = new Spacer();
        panel5.add(spacer21, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Laser Shutter 2", panel6);
        galvoCalibration2 = new LaserShutterCalibration();
        panel6.add(galvoCalibration2.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer22 = new Spacer();
        panel6.add(spacer22, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer23 = new Spacer();
        panel6.add(spacer23, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer24 = new Spacer();
        panel6.add(spacer24, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panConfig = new JPanel();
        panConfig.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tpMain.addTab("Configuration", panConfig);
        configuration = new Configuration();
        panConfig.add(configuration.$$$getRootComponent$$$(), new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer25 = new Spacer();
        panConfig.add(spacer25, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer26 = new Spacer();
        panConfig.add(spacer26, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer27 = new Spacer();
        panConfig.add(spacer27, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        spimStatus = new SpimStatus();
        panRoot.add(spimStatus.$$$getRootComponent$$$(), new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer28 = new Spacer();
        panRoot.add(spacer28, new GridConstraints(1, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panRoot;
    }
}
