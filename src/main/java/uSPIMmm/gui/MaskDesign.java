package uSPIMmm.gui;

import uSPIMmm.CommandDispatcher;
import uSPIMmm.GUI;
import uSPIMmm.SharedConfiguration;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mmcorej.StrVector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaskDesign {
    private JButton btnDmLecMagM1;
    private JButton btnDmLecMagM01;
    private JButton btnDmLecMagP01;
    private JButton btnDmLecMagP1;
    private JButton btnDmRecMagM1;
    private JButton btnDmRecMagM01;
    private JButton btnDmRecMagP01;
    private JButton btnDmRecMagP1;
    private JTextField tfDmLecMagnitude;
    private JTextField tfDmRecMagnitude;
    private JCheckBox cbDmDisplayLec;
    private JCheckBox cbDmUseLec;
    private JCheckBox cbDmUseRec;
    private JCheckBox cbDmDisplayRec;
    private JCheckBox cbDmDisplayEyem;
    private JCheckBox cbDmUseEyem;
    private JCheckBox cbDmDisplayBr;
    private JCheckBox cbDmUseBr;
    private JTextField tfDmEyemX;
    private JTextField tfDmEyemZ;
    private JTextField tfDmEyemRad;
    private JPanel MaskDesign;

    private int mask_id;
    private SharedConfiguration scfg;
    private GUI gui;


    public void create(GUI gui, SharedConfiguration scfg, int mask_id) {
        this.mask_id = mask_id;
        this.scfg = scfg;
        this.gui = gui;
        ItemListener cl = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
                    changed();
                }
            }
        };
        cbDmDisplayBr.addItemListener(cl);
        cbDmDisplayEyem.addItemListener(cl);
        cbDmDisplayLec.addItemListener(cl);
        cbDmDisplayRec.addItemListener(cl);

        btnDmLecMagP01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmLecMagnitude.getText()) + 0.1;
                nw = (nw > 100) ? 100 : nw;
                tfDmLecMagnitude.setText(Double.toString(nw));
            }
        });
        btnDmLecMagM01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmLecMagnitude.getText()) - 0.1;
                nw = (nw < 0) ? 0 : nw;
                tfDmLecMagnitude.setText(Double.toString(nw));
            }
        });
        btnDmLecMagP1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmLecMagnitude.getText()) + 1;
                nw = (nw > 100) ? 100 : nw;
                tfDmLecMagnitude.setText(Double.toString(nw));
            }
        });
        btnDmLecMagM1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmLecMagnitude.getText()) - 1;
                nw = (nw < 0) ? 0 : nw;
                tfDmLecMagnitude.setText(Double.toString(nw));
            }
        });

        btnDmRecMagP01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmRecMagnitude.getText()) + 0.1;
                nw = (nw > 100) ? 100 : nw;
                tfDmRecMagnitude.setText(Double.toString(nw));
            }
        });
        btnDmRecMagM01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmRecMagnitude.getText()) - 0.1;
                nw = (nw < 0) ? 0 : nw;
                tfDmRecMagnitude.setText(Double.toString(nw));
            }
        });
        btnDmRecMagP1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmRecMagnitude.getText()) + 1;
                nw = (nw > 100) ? 100 : nw;
                tfDmRecMagnitude.setText(Double.toString(nw));
            }
        });
        btnDmRecMagM1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                double nw = Double.parseDouble(tfDmRecMagnitude.getText()) - 1;
                nw = (nw < 0) ? 0 : nw;
                tfDmRecMagnitude.setText(Double.toString(nw));
            }
        });

        DocumentListener odl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        tfDmRecMagnitude.getDocument().addDocumentListener(odl);
        tfDmLecMagnitude.getDocument().addDocumentListener(odl);
        tfDmEyemRad.getDocument().addDocumentListener(odl);
        tfDmEyemZ.getDocument().addDocumentListener(odl);
        tfDmEyemX.getDocument().addDocumentListener(odl);
    }

    private void changed() {
        try {
            if (cbDmDisplayEyem.isSelected()) {
                Double.parseDouble(tfDmEyemRad.getText());
                Double.parseDouble(tfDmEyemX.getText());
                Double.parseDouble(tfDmEyemZ.getText());
            }
            if (cbDmDisplayLec.isSelected()) {
                Double.parseDouble(tfDmLecMagnitude.getText());
            }
            if (cbDmDisplayRec.isSelected()) {
                Double.parseDouble(tfDmRecMagnitude.getText());
            }
        } catch (Exception e) {
            return;
        }
        updateLightSheet(false);
    }

    private void updateLightSheet(boolean out) {
        if (scfg.getMMCore() != null) {
            try {
                boolean cameraOnline = scfg.getMMSf().isLiveModeOn();
                if (scfg.isPropertyControl()) {
                    StrVector configs = scfg.getMMCore().getAvailableConfigGroups();
                    for (int i = 0; i < configs.size(); i++) {
                        String property = configs.get(i);
                        String value = scfg.getMMCore().getCurrentConfig(configs.get(i));
                        if (property.equals("CameraTrigger") && !value.equals("External")) {
                            scfg.getMMSf().enableLiveMode(false);
                            scfg.getMMCore().setConfig("CameraTrigger", "External");
                        } else if (property.equals("ScanMode") && !value.equals("Normal")) {
                            scfg.getMMSf().enableLiveMode(false);
                            scfg.getMMCore().setConfig("ScanMode", "Normal");
                        } else if (property.equals("VisibleLaserFront") && !value.equals("On")) {
                            scfg.getMMSf().enableLiveMode(false);
                            scfg.getMMCore().setConfig("VisibleLaserFront", "On");
                        } else if (property.equals("VisibleLaserSide") && !value.equals("On")) {
                            scfg.getMMSf().enableLiveMode(false);
                            scfg.getMMCore().setConfig("VisibleLaserSide", "On");
                        }
                    }
                }
                scfg.getMMSf().enableLiveMode(cameraOnline);
                scfg.getMMCore().setExposure(1000.0 / scfg.getFramerate());
                scfg.setFramerate(1000.0 / scfg.getMMCore().getExposure());
                //TODO: Shutter control?
                //if (gui.cbCfgShutterControl.isSelected()) {
                //gui.MMCore.setShutterOpen(true);
                //}
            } catch (Exception e1) {
                e1.printStackTrace();
                gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e1.getMessage());
                return;
            }
        }
        if (!gui.cd.isConnected()) {
            gui.writeStatus("ERROR: TCP Socket connection to Control Engine is closed.");
            gui.cd = new CommandDispatcher("localhost", gui);
            if (!gui.cd.isConnected()) {
                return;
            } else {
                gui.writeStatus("TCP Socket connection to Control Engine reopened, try rerunning the command.");
            }
            return;
        }
        JsonObject req = new JsonObject();
        JsonObject nm = getNormalModeCommandJson();
        req.addProperty("Mode", "NormalMode");
        req.add("NormalMode", nm);
        req.add("Settings", gui.configuration.getSettingsJson());
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String request = gson.toJson(req);
        gui.cd.dispatchCommand(request + "\n");
        compileMasks();
    }

    public JsonObject getNormalModeCommandJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumVolumes", 1000000);
        //TODO
        jo.addProperty("NumPlanes", 1);
        jo.addProperty("NumReturn", 1);
        jo.addProperty("LightSheetWidth1", scfg.getSheetWidth1());
        jo.addProperty("LightSheetWidth2", scfg.getSheetWidth2());
        jo.addProperty("Framerate", scfg.getFramerate());
        jo.addProperty("ZStep", 0.0);
        jo.addProperty("ZOffset", 0.0 + scfg.getHomeOffset());
        jo.addProperty("ScanMode", 2);
        jo.addProperty("BlankReposition", true);
        jo.addProperty("TriggerOut", true);
        String masks = "";
        if (cbDmDisplayLec.isSelected()) {
            double mag = Double.parseDouble(tfDmLecMagnitude.getText());
            mag = (mag > 100.0) ? 100.0 : mag;
            mag = (mag < 0.0) ? 0.0 : mag;
            masks += "left_end_cut," + Double.toString(mag) + ";";
        }
        if (cbDmDisplayRec.isSelected()) {
            double mag = Double.parseDouble(tfDmRecMagnitude.getText());
            mag = (mag > 100.0) ? 100.0 : mag;
            mag = (mag < 0.0) ? 0.0 : mag;
            masks += "right_end_cut," + Double.toString(mag) + ";";
        }
        if (cbDmDisplayEyem.isSelected()) {
            try {
                Double.parseDouble(tfDmEyemZ.getText());
                Double.parseDouble(tfDmEyemX.getText());
                Double.parseDouble(tfDmEyemRad.getText());
                //Now we are sure everything is actually number
                //TODO
                masks += "eye_mask,"
                        + tfDmEyemX.getText() + ","
                        + Double.toString((Double.parseDouble(tfDmEyemZ.getText()) + scfg.getHomeOffset())) + ","
                        + tfDmEyemRad.getText() + ";";
            } catch (Exception e) {
            }
        }
        if (cbDmDisplayBr.isSelected()) {
            masks += "blank_return;";
        }
        if (mask_id == 1) {
            jo.addProperty("Masks1", masks);
            jo.addProperty("Masks2", "disable;");
        } else if (mask_id == 2) {
            jo.addProperty("Masks1", "disable;");
            jo.addProperty("Masks2", masks);
        }
        jo.addProperty("GalvoDelay1", scfg.getGalvoDelay1());
        jo.addProperty("GalvoDelay2", scfg.getGalvoDelay2());
        return jo;
    }

    void compileMasks() {
        String masks = "";
        if (cbDmUseLec.isSelected()) {
            double mag = Double.parseDouble(tfDmLecMagnitude.getText());
            mag = (mag > 100.0) ? 100.0 : mag;
            mag = (mag < 0.0) ? 0.0 : mag;
            masks += "left_end_cut," + Double.toString(mag) + ";";
        }
        if (cbDmUseRec.isSelected()) {
            double mag = Double.parseDouble(tfDmRecMagnitude.getText());
            mag = (mag > 100.0) ? 100.0 : mag;
            mag = (mag < 0.0) ? 0.0 : mag;
            masks += "right_end_cut," + Double.toString(mag) + ";";
        }
        if (cbDmDisplayEyem.isSelected()) {
            try {
                Double.parseDouble(tfDmEyemZ.getText());
                Double.parseDouble(tfDmEyemX.getText());
                Double.parseDouble(tfDmEyemRad.getText());
                //Now we are sure everything is actually number
                masks += "eye_mask,"
                        + tfDmEyemX.getText() + ","
                        + Double.toString((Double.parseDouble(tfDmEyemZ.getText()) + scfg.getHomeOffset())) + ","
                        + tfDmEyemRad.getText() + ";";
            } catch (Exception e) {
            }
        }
        if (cbDmDisplayBr.isSelected()) {
            masks += "blank_return;";
        }
        if (this.mask_id == 1) {
            scfg.setMasks1(masks);
        } else if (this.mask_id == 2) {
            scfg.setMasks2(masks);
        }
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
        MaskDesign = new JPanel();
        MaskDesign.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(9, 4, new Insets(0, 0, 0, 0), -1, -1));
        MaskDesign.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Display");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Use");
        panel1.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, -1, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Left End Cutoff");
        panel1.add(label3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Laser takes time to revese direction. Blanking during reversal can prevent unnecessary bleaching. Can also be use to adjust width of the sheet.");
        panel2.add(label4, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Magnitude:");
        panel2.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnDmLecMagM1 = new JButton();
        btnDmLecMagM1.setText("-1");
        panel2.add(btnDmLecMagM1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        btnDmLecMagM01 = new JButton();
        btnDmLecMagM01.setText("-0.1");
        panel2.add(btnDmLecMagM01, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        btnDmLecMagP01 = new JButton();
        btnDmLecMagP01.setText("+0.1");
        panel2.add(btnDmLecMagP01, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        btnDmLecMagP1 = new JButton();
        btnDmLecMagP1.setText("+1");
        panel2.add(btnDmLecMagP1, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        tfDmLecMagnitude = new JTextField();
        tfDmLecMagnitude.setText("10");
        panel2.add(tfDmLecMagnitude, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        Font label6Font = this.$$$getFont$$$(null, Font.BOLD, -1, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setText("Right End Cutoff");
        panel1.add(label6, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(4, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Laser takes time to revese direction. Blanking during reversal can prevent unnecessary bleaching. Can also be use to adjust width of the sheet.");
        panel3.add(label7, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Magnitude:");
        panel3.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel3.add(spacer3, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnDmRecMagM1 = new JButton();
        btnDmRecMagM1.setText("-1");
        panel3.add(btnDmRecMagM1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        btnDmRecMagM01 = new JButton();
        btnDmRecMagM01.setText("-0.1");
        panel3.add(btnDmRecMagM01, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        btnDmRecMagP01 = new JButton();
        btnDmRecMagP01.setText("+0.1");
        panel3.add(btnDmRecMagP01, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        btnDmRecMagP1 = new JButton();
        btnDmRecMagP1.setText("+1");
        panel3.add(btnDmRecMagP1, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        tfDmRecMagnitude = new JTextField();
        tfDmRecMagnitude.setText("10");
        panel3.add(tfDmRecMagnitude, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$(null, Font.BOLD, -1, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        label9.setText("Eye Mask");
        panel1.add(label9, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(6, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("Laser is disabled in circular region which can be aligned with the eye to prevent direct eye illumination during acquisition.");
        panel4.add(label10, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("X Position from center (um):");
        panel4.add(label11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Z Position from top (um):");
        panel4.add(label12, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Radius (um):");
        panel4.add(label13, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel4.add(spacer4, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        tfDmEyemX = new JTextField();
        tfDmEyemX.setText("10");
        panel4.add(tfDmEyemX, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        tfDmEyemZ = new JTextField();
        tfDmEyemZ.setText("10");
        panel4.add(tfDmEyemZ, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        tfDmEyemRad = new JTextField();
        tfDmEyemRad.setText("10");
        panel4.add(tfDmEyemRad, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label14 = new JLabel();
        Font label14Font = this.$$$getFont$$$(null, Font.BOLD, -1, label14.getFont());
        if (label14Font != null) label14.setFont(label14Font);
        label14.setText("Blank Return");
        panel1.add(label14, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(8, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        Font label15Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label15.getFont());
        if (label15Font != null) label15.setFont(label15Font);
        label15.setText("Laser is disabled during return (no effect in bidirectional mode).");
        panel5.add(label15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmDisplayLec = new JCheckBox();
        cbDmDisplayLec.setHorizontalAlignment(0);
        cbDmDisplayLec.setHorizontalTextPosition(0);
        cbDmDisplayLec.setText("");
        panel1.add(cbDmDisplayLec, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmUseLec = new JCheckBox();
        cbDmUseLec.setHorizontalAlignment(0);
        cbDmUseLec.setHorizontalTextPosition(0);
        cbDmUseLec.setText("");
        panel1.add(cbDmUseLec, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmUseRec = new JCheckBox();
        cbDmUseRec.setHorizontalAlignment(0);
        cbDmUseRec.setHorizontalTextPosition(0);
        cbDmUseRec.setText("");
        panel1.add(cbDmUseRec, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmDisplayRec = new JCheckBox();
        cbDmDisplayRec.setHorizontalAlignment(0);
        cbDmDisplayRec.setHorizontalTextPosition(0);
        cbDmDisplayRec.setText("");
        panel1.add(cbDmDisplayRec, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmDisplayEyem = new JCheckBox();
        cbDmDisplayEyem.setHorizontalAlignment(0);
        cbDmDisplayEyem.setHorizontalTextPosition(0);
        cbDmDisplayEyem.setText("");
        panel1.add(cbDmDisplayEyem, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmUseEyem = new JCheckBox();
        cbDmUseEyem.setHorizontalAlignment(0);
        cbDmUseEyem.setHorizontalTextPosition(0);
        cbDmUseEyem.setText("");
        panel1.add(cbDmUseEyem, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmDisplayBr = new JCheckBox();
        cbDmDisplayBr.setHorizontalAlignment(0);
        cbDmDisplayBr.setHorizontalTextPosition(0);
        cbDmDisplayBr.setText("");
        panel1.add(cbDmDisplayBr, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbDmUseBr = new JCheckBox();
        cbDmUseBr.setHorizontalAlignment(0);
        cbDmUseBr.setHorizontalTextPosition(0);
        cbDmUseBr.setText("");
        panel1.add(cbDmUseBr, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MaskDesign;
    }
}
