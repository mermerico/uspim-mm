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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class MirrorCalibration implements Observer {
    private GUI gui;
    private JPanel mirrorCalibration;
    private JTextField tfCalibCalibrationSamples;
    private JTextField tfCalibFramerate;
    private JTextField tfCalibWidthLightSheet2;
    private JTextField tfCalibVolumeStart;
    private JTextField tfCalibVolumeEnd;
    private JTextField tfCalibWidthLightSheet1;
    private JButton btnCalibStartCalibration;
    private JButton btnCalibDown1;
    private JButton btnCalibDown5;
    private JButton btnCalibDown20;
    private JButton btnCalibUpdate;
    private JTextField tfCalibOffset;
    private JButton btnCalibNext;
    private JTextField tfCalibProgress;
    private JButton btnCalibUp20;
    private JButton btnCalibUp5;
    private JButton btnCalibUp1;
    private JTextField tfPcCalibProgress;
    private JLabel lLinear;
    private JLabel lPcLinear;
    private int mirror_id;
    private SharedConfiguration scfg;


    public MirrorCalibration() {
    }

    public void create(GUI gui, SharedConfiguration scfg, int mirror_id) {
        this.gui = gui;
        this.scfg = scfg;
        this.mirror_id = mirror_id;

        tfPcCalibProgress.setVisible(false);
        lPcLinear.setVisible(false);
        lLinear.setVisible(false);

        btnCalibStartCalibration.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                tfPcCalibProgress.setVisible(false);
                lPcLinear.setVisible(false);
                lLinear.setVisible(false);
                if (scfg.getMMCore() != null) {
                    try {
                        scfg.getMMCore().setConfig("CameraTrigger", "Internal");
                        scfg.getMMCore().setConfig("ScanMode", "Normal");
                        //MMCore.setConfig("VisibleLaser", "On");
                        // Set exposure needs to be last or the exposure must not be
                        //  in any of the config groups.
                        scfg.getMMCore().setExposure(1000.0 / Double.parseDouble(tfCalibFramerate.getText()));
                        scfg.setFramerate(1000.0 / scfg.getMMCore().getExposure());

                        Thread.sleep(2000);
                        //TODO: shutter control
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
                req.addProperty("Mode", "CalibrationMode");
                req.add("CalibrationMode", getCalibrationModeJson());
                req.add("Settings", gui.configuration.getSettingsJson());
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                String request = gson.toJson(req);
                gui.cd.dispatchCommand(request + "\n");
                super.mouseReleased(e);
            }
        });
        btnCalibDown1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationDecrementVoltage1\n");
            }
        });
        btnCalibDown5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationDecrementVoltage5\n");
            }
        });
        btnCalibDown20.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationDecrementVoltage20\n");
            }
        });
        btnCalibUp1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationIncrementVoltage1\n");
            }
        });
        btnCalibUp5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationIncrementVoltage5\n");
            }
        });
        btnCalibUp20.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationIncrementVoltage20\n");
            }
        });
        btnCalibUpdate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationSetVoltage" + tfCalibOffset.getText() + "\n");
            }
        });
        btnCalibNext.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.dispatchCommand("ASYNCcalibrationCommit\n");
            }
        });
    }

    public JsonObject getCalibrationModeJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumCalibrationSamples", Integer.parseInt(tfCalibCalibrationSamples.getText()));
        jo.addProperty("LightSheetWidth1", scfg.getSheetWidth1());
        jo.addProperty("LightSheetWidth2", scfg.getSheetWidth2());
        jo.addProperty("LocationStart", Double.parseDouble(tfCalibVolumeStart.getText()));
        jo.addProperty("LocationEnd", Double.parseDouble(tfCalibVolumeEnd.getText()));
        jo.addProperty("Framerate", scfg.getFramerate());
        if (mirror_id == 1) {
            jo.addProperty("Masks1", "");
            jo.addProperty("Masks2", "disable");
        } else if (mirror_id == 2) {
            jo.addProperty("Masks1", "disable");
            jo.addProperty("Masks2", "");
        }
        return jo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SharedConfiguration) {
            tfCalibFramerate.setText(String.valueOf(scfg.getFramerate()));
            tfCalibWidthLightSheet1.setText(String.valueOf(scfg.getSheetWidth1()));
            tfCalibWidthLightSheet2.setText(String.valueOf(scfg.getSheetWidth2()));
        }
    }

    public void updateOffset(String offset) {
        tfCalibOffset.setText(offset);
    }

    public void progress(String msg) {
        String[] msgs = msg.split(",");
        if (msgs[0].equalsIgnoreCase("done")) {
            lPcLinear.setVisible(true);
            lLinear.setVisible(true);
            tfPcCalibProgress.setVisible(true);
            this.tfCalibProgress.setText(msgs[1].split("=")[1]);
            this.tfPcCalibProgress.setText(msg.split("zMirrorScalePc=")[1]);

        } else {
            this.tfCalibProgress.setText(msg);
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
        mirrorCalibration = new JPanel();
        mirrorCalibration.setLayout(new GridLayoutManager(4, 6, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        mirrorCalibration.add(panel1, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Welcome to the Calibration assistant.");
        panel1.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Light Sheet Width 1 (um):");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCalibWidthLightSheet1 = new JTextField();
        tfCalibWidthLightSheet1.setText("400");
        panel1.add(tfCalibWidthLightSheet1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Number of calibraiton samples: ");
        panel1.add(label3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Volume start (um): ");
        panel1.add(label4, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Volume end (um): ");
        panel1.add(label5, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCalibCalibrationSamples = new JTextField();
        tfCalibCalibrationSamples.setText("2");
        panel1.add(tfCalibCalibrationSamples, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        tfCalibVolumeStart = new JTextField();
        tfCalibVolumeStart.setText("0");
        panel1.add(tfCalibVolumeStart, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        tfCalibVolumeEnd = new JTextField();
        tfCalibVolumeEnd.setText("200");
        panel1.add(tfCalibVolumeEnd, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Light Sheet Width 2 (um):");
        panel1.add(label6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCalibWidthLightSheet2 = new JTextField();
        tfCalibWidthLightSheet2.setText("400");
        panel1.add(tfCalibWidthLightSheet2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Framerate (um):");
        panel1.add(label7, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCalibFramerate = new JTextField();
        tfCalibFramerate.setText("100");
        panel1.add(tfCalibFramerate, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mirrorCalibration.add(spacer1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mirrorCalibration.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        mirrorCalibration.add(panel2, new GridConstraints(3, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Offset (um)");
        panel2.add(label8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCalibDown1 = new JButton();
        btnCalibDown1.setText("DOWN (1um)");
        panel2.add(btnCalibDown1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        btnCalibDown5 = new JButton();
        btnCalibDown5.setText("DOWN (5um)");
        panel2.add(btnCalibDown5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        btnCalibDown20 = new JButton();
        btnCalibDown20.setText("DOWN (20um)");
        panel2.add(btnCalibDown20, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        btnCalibUpdate = new JButton();
        btnCalibUpdate.setText("Set Voltage");
        panel2.add(btnCalibUpdate, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCalibOffset = new JTextField();
        tfCalibOffset.setText("1");
        panel2.add(tfCalibOffset, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(50, -1), null, 0, false));
        btnCalibNext = new JButton();
        btnCalibNext.setText("Next");
        panel2.add(btnCalibNext, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCalibProgress = new JTextField();
        tfCalibProgress.setHorizontalAlignment(0);
        tfCalibProgress.setText("Waiting for start...");
        panel2.add(tfCalibProgress, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnCalibUp20 = new JButton();
        btnCalibUp20.setText("UP (20um)");
        panel2.add(btnCalibUp20, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        btnCalibUp5 = new JButton();
        btnCalibUp5.setText("UP (5um)");
        panel2.add(btnCalibUp5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        btnCalibUp1 = new JButton();
        btnCalibUp1.setText("UP (1um)");
        panel2.add(btnCalibUp1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        tfPcCalibProgress = new JTextField();
        tfPcCalibProgress.setHorizontalAlignment(0);
        tfPcCalibProgress.setText("");
        panel2.add(tfPcCalibProgress, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lLinear = new JLabel();
        lLinear.setText("Linear coefficient:");
        panel2.add(lLinear, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lPcLinear = new JLabel();
        lPcLinear.setText("Piece-wise Linear coefficient:");
        panel2.add(lPcLinear, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        mirrorCalibration.add(separator1, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnCalibStartCalibration = new JButton();
        btnCalibStartCalibration.setText("Start Calibration");
        mirrorCalibration.add(btnCalibStartCalibration, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mirrorCalibration;
    }
}
