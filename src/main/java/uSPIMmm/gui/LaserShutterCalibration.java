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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class LaserShutterCalibration implements Observer {
    private JTextField tfApFramerate;
    private JTextField tfApWidthLightSheet1;
    private JTextField tfApWidthLightSheet2;
    private JTextField tfApGalvoDelay1;
    private JButton btnApGalvoDelay1M1;
    private JButton btnApGalvoDelay1M01;
    private JButton btnApGalvoDelay1M001;
    private JButton btnApGalvoDelay1P001;
    private JButton btnApGalvoDelay1P01;
    private JButton btnApGalvoDelay1P1;
    private JButton btnApGalvoDelay2P1;
    private JButton btnApGalvoDelay2P01;
    private JButton btnApGalvoDelay2P001;
    private JButton btnApGalvoDelay2M001;
    private JButton btnApGalvoDelay2M01;
    private JButton btnApGalvoDelay2M1;
    private JTextField tfApGalvoDelay2;
    private JPanel laserShutterCalibration;
    private JButton btnApStartIllumination;

    private GUI gui;
    private SharedConfiguration scfg;
    private int galvo_id;

    public void create(GUI gui, SharedConfiguration scfg, int galvo_id) {
        this.gui = gui;
        this.scfg = scfg;
        this.galvo_id = galvo_id;

        if (galvo_id == 1) {
            btnApGalvoDelay2M1.setEnabled(false);
            btnApGalvoDelay2M01.setEnabled(false);
            btnApGalvoDelay2M001.setEnabled(false);
            btnApGalvoDelay2P1.setEnabled(false);
            btnApGalvoDelay2P01.setEnabled(false);
            btnApGalvoDelay2P001.setEnabled(false);
            tfApGalvoDelay2.setEnabled(false);
            tfApWidthLightSheet2.setEnabled(false);
        } else {
            btnApGalvoDelay1M1.setEnabled(false);
            btnApGalvoDelay1M01.setEnabled(false);
            btnApGalvoDelay1M001.setEnabled(false);
            btnApGalvoDelay1P1.setEnabled(false);
            btnApGalvoDelay1P01.setEnabled(false);
            btnApGalvoDelay1P001.setEnabled(false);
            tfApGalvoDelay1.setEnabled(false);
            tfApWidthLightSheet1.setEnabled(false);
        }

        btnApStartIllumination.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateLightSheet();
            }
        });
        btnApGalvoDelay1M1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay1(scfg.getGalvoDelay1() - 1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay1M01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay1(scfg.getGalvoDelay1() - 0.1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay1M001.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay1(scfg.getGalvoDelay1() - 0.01);
                updateLightSheet();
            }
        });
        btnApGalvoDelay1P001.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay1(scfg.getGalvoDelay1() + 0.01);
                updateLightSheet();
            }
        });
        btnApGalvoDelay1P01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay1(scfg.getGalvoDelay1() + 0.1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay1P1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay1(scfg.getGalvoDelay1() + 1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay2M1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay2(scfg.getGalvoDelay2() - 1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay2M01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay2(scfg.getGalvoDelay2() - 0.1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay2M001.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay2(scfg.getGalvoDelay2() - 0.01);
                updateLightSheet();
            }
        });
        btnApGalvoDelay2P001.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay2(scfg.getGalvoDelay2() + 0.01);
                updateLightSheet();
            }
        });
        btnApGalvoDelay2P01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay2(scfg.getGalvoDelay2() + 0.1);
                updateLightSheet();
            }
        });
        btnApGalvoDelay2P1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                scfg.setGalvoDelay2(scfg.getGalvoDelay2() + 1);
                updateLightSheet();
            }
        });
    }

    private void updateLightSheet() {
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
                /*
                if (gui.cbCfgShutterControl.isSelected()) {
                    scfg.getMMCore().setShutterOpen(true);
                }
                */
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
        JsonObject nm = getAdjustParamJson();
        req.addProperty("Mode", "FindPlaneMode");
        req.add("FindPlaneMode", nm);
        req.add("Settings", gui.configuration.getSettingsJson());
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String request = gson.toJson(req);
        gui.cd.dispatchCommand(request + "\n");
    }

    private JsonObject getAdjustParamJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumVolumes", 1000000000);
        jo.addProperty("LightSheetWidth1", scfg.getSheetWidth1());
        jo.addProperty("LightSheetWidth2", scfg.getSheetWidth2());
        jo.addProperty("Framerate", scfg.getFramerate());
        jo.addProperty("ZOffset", 0);
        jo.addProperty("GalvoDelay1", scfg.getGalvoDelay1());
        jo.addProperty("GalvoDelay2", scfg.getGalvoDelay2());
        if (this.galvo_id == 1) {
            jo.addProperty("Masks1", "adjustment_mask");
            jo.addProperty("Masks2", "disable");
        } else if (this.galvo_id == 2) {
            jo.addProperty("Masks1", "disable");
            jo.addProperty("Masks2", "adjustment_mask");
        }
        return jo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SharedConfiguration) {
            tfApFramerate.setText(String.valueOf(scfg.getFramerate()));
            tfApWidthLightSheet1.setText(String.valueOf(scfg.getSheetWidth1()));
            tfApWidthLightSheet2.setText(String.valueOf(scfg.getSheetWidth2()));
            tfApGalvoDelay1.setText(String.valueOf(scfg.getGalvoDelay1()));
            tfApGalvoDelay2.setText(String.valueOf(scfg.getGalvoDelay2()));
        }
    }

    public LaserShutterCalibration() {
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
        laserShutterCalibration = new JPanel();
        laserShutterCalibration.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(7, 7, new Insets(0, 0, 0, 0), -1, -1));
        laserShutterCalibration.add(panel1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Framerate:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Light Sheet Width 1: ");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Light Sheet Width 2: ");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Galvo Delay 1:");
        panel1.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Galvo Delay 2:");
        panel1.add(label5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfApFramerate = new JTextField();
        tfApFramerate.setText("100");
        panel1.add(tfApFramerate, new GridConstraints(0, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfApWidthLightSheet1 = new JTextField();
        tfApWidthLightSheet1.setText("500");
        panel1.add(tfApWidthLightSheet1, new GridConstraints(1, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfApWidthLightSheet2 = new JTextField();
        tfApWidthLightSheet2.setText("500");
        panel1.add(tfApWidthLightSheet2, new GridConstraints(2, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfApGalvoDelay1 = new JTextField();
        tfApGalvoDelay1.setText("0");
        panel1.add(tfApGalvoDelay1, new GridConstraints(3, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnApGalvoDelay1M1 = new JButton();
        btnApGalvoDelay1M1.setText("-1");
        panel1.add(btnApGalvoDelay1M1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay1M01 = new JButton();
        btnApGalvoDelay1M01.setText("-0.1");
        panel1.add(btnApGalvoDelay1M01, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay1M001 = new JButton();
        btnApGalvoDelay1M001.setText("-0.01");
        panel1.add(btnApGalvoDelay1M001, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay1P001 = new JButton();
        btnApGalvoDelay1P001.setText("+0.01");
        panel1.add(btnApGalvoDelay1P001, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay1P01 = new JButton();
        btnApGalvoDelay1P01.setText("+0.1");
        panel1.add(btnApGalvoDelay1P01, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay1P1 = new JButton();
        btnApGalvoDelay1P1.setText("+1");
        panel1.add(btnApGalvoDelay1P1, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay2P1 = new JButton();
        btnApGalvoDelay2P1.setText("+1");
        panel1.add(btnApGalvoDelay2P1, new GridConstraints(6, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay2P01 = new JButton();
        btnApGalvoDelay2P01.setText("+0.1");
        panel1.add(btnApGalvoDelay2P01, new GridConstraints(6, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay2P001 = new JButton();
        btnApGalvoDelay2P001.setText("+0.01");
        panel1.add(btnApGalvoDelay2P001, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay2M001 = new JButton();
        btnApGalvoDelay2M001.setText("-0.01");
        panel1.add(btnApGalvoDelay2M001, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay2M01 = new JButton();
        btnApGalvoDelay2M01.setText("-0.1");
        panel1.add(btnApGalvoDelay2M01, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnApGalvoDelay2M1 = new JButton();
        btnApGalvoDelay2M1.setText("-1");
        panel1.add(btnApGalvoDelay2M1, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfApGalvoDelay2 = new JTextField();
        tfApGalvoDelay2.setText("0");
        panel1.add(tfApGalvoDelay2, new GridConstraints(5, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        laserShutterCalibration.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        laserShutterCalibration.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("    1) Select parameters you will use for acquisition.");
        panel2.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label7 = new JLabel();
        label7.setText("    2) Launch the adjustment lightsheet protocol");
        panel2.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label8 = new JLabel();
        label8.setText("    3) Adjust the galvo delay to achieve  state displayed by accompanying illustrations.");
        panel2.add(label8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label9 = new JLabel();
        label9.setText("    4) Changing framerate, light sheet width or parameters in configuration may require readjustment.");
        panel2.add(label9, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$(null, Font.BOLD, 14, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("Instructions:");
        panel2.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        laserShutterCalibration.add(spacer2, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        laserShutterCalibration.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnApStartIllumination = new JButton();
        btnApStartIllumination.setText("Start Illumination");
        laserShutterCalibration.add(btnApStartIllumination, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return laserShutterCalibration;
    }
}
