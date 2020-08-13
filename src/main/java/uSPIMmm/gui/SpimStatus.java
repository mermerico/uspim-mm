package uSPIMmm.gui;

import uSPIMmm.CommandDispatcher;
import uSPIMmm.GUI;
import uSPIMmm.ImagePanel;
import uSPIMmm.SharedConfiguration;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SpimStatus {
    private JLabel lDirection;
    private JLabel lVisible;
    private JLabel lVolStart;
    private JLabel lVolEnd;
    private JLabel lPosition;
    private JPanel SpimStatus;
    private JLabel lUpdate;
    private JLabel lCeLink;
    private JLabel lAqStatus;
    private ImagePanel imLogo;

    private GUI gui;
    private SharedConfiguration scfg;

    boolean sheetVisible = false;
    boolean sheetDown = false;
    double startPos = 0;
    double endPos = 0;
    int sheetsDown = 1;
    int sheetsUp = 0;
    int zMode = 0;
    long lastUpdate = -1;

    public void createUIComponents() {
        imLogo = new ImagePanel("/img/logo.png");
    }


    public void create(final GUI gui, final SharedConfiguration scfg) {
        this.gui = gui;
        this.scfg = scfg;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (scfg.getMMSf() != null && scfg.getMMSf().isAcquisitionRunning()) {
                    lAqStatus.setText("IN PROGRESS");
                    if (lAqStatus.getForeground().getRed() > 0) {
                        lAqStatus.setForeground(new Color(0, 0, 0));
                    } else {
                        lAqStatus.setForeground(new Color(255, 0, 94));
                    }
                } else {
                    lAqStatus.setText("READY");
                    lAqStatus.setForeground(new Color(-16721076));
                }
                if (gui.cd.isConnected()) {
                    lCeLink.setText("ControlEngine link: ACTIVE");
                } else {
                    lCeLink.setText("ControlEngine link: INACTIVE");
                    gui.cd = new CommandDispatcher("localhost", gui);
                }
                if (lastUpdate >= 0) {
                    lUpdate.setText("Last update " + String.valueOf((int) ((System.currentTimeMillis() - lastUpdate) / 1000.0)) + "s ago");
                }
            }
        }, 0, 1000);
    }

    public void updateStatus(String s) {
        List<String> params = Arrays.asList(s.split(","));
        if (params.get(0).equals("DAQ")) {
            if (params.get(1).equals("PD")) { //Position-Direction
                setSheetDirection(params.get(2).equals("D"));
                setSheetVisible(!params.get(3).equals("R"));
                int sheetPos = Integer.parseInt(params.get(4));
                if (sheetDown) {
                    setSheetPos((startPos + ((endPos - startPos) / sheetsDown) * sheetPos));
                } else {
                    setSheetPos((endPos - ((endPos - startPos) / sheetsUp) * sheetPos));
                }
                updateSheet();
                lastUpdate = System.currentTimeMillis();
                lUpdate.setText("Last update 0s ago");
            } else if (params.get(1).equals("Z")) {
                startPos = Double.parseDouble(params.get(2));
                endPos = Double.parseDouble(params.get(3));
                sheetsDown = Integer.parseInt(params.get(4));
                sheetsUp = Integer.parseInt(params.get(5));
                zMode = Integer.parseInt(params.get(6));
                this.lVolStart.setText("Vol Start: " + String.format("%.2f", startPos));
                this.lVolEnd.setText("Vol End: " + String.format("%.2f", endPos));
                lastUpdate = System.currentTimeMillis();
                lUpdate.setText("Last update 0s ago");
            }
        }

    }

    public void setSheetDirection(boolean down) {
        sheetDown = down;
        updateSheet();
    }

    public void setSheetVisible(boolean visible) {
        sheetVisible = visible;
        updateSheet();
    }

    private void updateSheet() {
        if (this.sheetDown) {
            this.lDirection.setText("Direction: Down");
        } else {
            this.lDirection.setText("Direction: Up");
        }
    }

    public void setSheetPos(double pos) {
        this.lPosition.setText("Position: " + String.format("%.2f", pos));
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        SpimStatus = new JPanel();
        SpimStatus.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        SpimStatus.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Light Sheet Realtime:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lVolEnd = new JLabel();
        lVolEnd.setText("Vol End: UNK");
        panel1.add(lVolEnd, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), null, 0, false));
        lVolStart = new JLabel();
        lVolStart.setText("Vol Start: UNK");
        panel1.add(lVolStart, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), null, 0, false));
        lDirection = new JLabel();
        lDirection.setText("Direction: UNK");
        panel1.add(lDirection, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lUpdate = new JLabel();
        lUpdate.setText("Last Update: N/A");
        panel2.add(lUpdate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lPosition = new JLabel();
        lPosition.setText("Position: UNK");
        panel1.add(lPosition, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        SpimStatus.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lCeLink = new JLabel();
        lCeLink.setText("ControlEngine Link: UNK");
        panel3.add(lCeLink, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        SpimStatus.add(panel4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Acquisition:");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lAqStatus = new JLabel();
        Font lAqStatusFont = this.$$$getFont$$$(null, Font.BOLD, 24, lAqStatus.getFont());
        if (lAqStatusFont != null) lAqStatus.setFont(lAqStatusFont);
        lAqStatus.setForeground(new Color(-16721076));
        lAqStatus.setText("READY");
        panel4.add(lAqStatus, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        SpimStatus.add(separator1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        SpimStatus.add(imLogo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(182, 107), new Dimension(182, 107), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return SpimStatus;
    }
}
