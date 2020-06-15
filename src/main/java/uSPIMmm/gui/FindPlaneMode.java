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
import mmcorej.StrVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Observable;
import java.util.Observer;

public class FindPlaneMode implements Observer {
    private JPanel FindPlaneMode;
    private JTextField tfFpmWidthLightSheet1;
    private JTextField tfFpmWidthLightSheet2;
    private JTextField tfFpmZOffset;
    private JTextField tfFpmFramerate;
    private JTextField tfFpmConfigFile;
    private JButton btnFpmLoadConfig;
    private JButton btnFpmSaveConfig;
    private JButton btnFpmStartIllumination;
    private JButton btnFpmPause;

    private GUI gui;
    private SharedConfiguration scfg;

    public void create(GUI gui, SharedConfiguration scfg) {
        this.gui = gui;
        this.scfg = scfg;

        tfFpmConfigFile.setText(System.getProperty("user.home") + "/findplanemode_default.json");
        btnFpmLoadConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                InputStream is = null;
                try {
                    is = new FileInputStream(tfFpmConfigFile.getText());
                    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                    String line = buf.readLine();
                    StringBuilder sb = new StringBuilder();
                    while (line != null) {
                        sb.append(line).append("\n");
                        line = buf.readLine();
                    }
                    String jstr = sb.toString();
                    Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                    JsonObject jo = gson.fromJson(jstr, JsonObject.class);
                    scfg.setSheetWidth1(jo.get("LightSheetWidth1").getAsDouble());
                    scfg.setSheetWidth2(jo.get("LightSheetWidth2").getAsDouble());
                    scfg.setFramerate(jo.get("Framerate").getAsDouble());
                    tfFpmZOffset.setText(jo.get("ZOffset").getAsString());
                    gui.writeStatus("SUCCESS: Settings config loaded from " + tfFpmConfigFile.getText() + ".");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    gui.writeStatus("ERROR: Problem encountered opening " + tfFpmConfigFile.getText() + ": " + e1.getMessage() + ".");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    gui.writeStatus("ERROR: Problem encountered opening " + tfFpmConfigFile.getText() + ": " + e1.getMessage() + ".");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    gui.writeStatus("ERROR: Problem encountered opening " + tfFpmConfigFile.getText() + ": " + e1.getMessage() + ".");
                }
            }
        });
        btnFpmSaveConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                JsonObject jo = getFindPlaneModeJson();
                Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                try {
                    PrintWriter writer = new PrintWriter(tfFpmConfigFile.getText(), "UTF-8");
                    writer.print(gson.toJson(jo));
                    writer.close();
                    gui.writeStatus("SUCCESS: FindPlaneMode config saved to " + tfFpmConfigFile.getText() + ".");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    gui.writeStatus("ERROR: Could not save FindPlaneMode config to " + tfFpmConfigFile.getText() + ": " + ex.getMessage() + ".");
                }
                JsonObject jo2 = new JsonObject();

            }
        });
        {
            InputStream is = null;
            try {
                is = new FileInputStream(tfFpmConfigFile.getText());
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = buf.readLine();
                }
                String jstr = sb.toString();
                Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                JsonObject jo = gson.fromJson(jstr, JsonObject.class);
                scfg.setSheetWidth1(jo.get("LightSheetWidth1").getAsDouble());
                scfg.setSheetWidth2(jo.get("LightSheetWidth2").getAsDouble());
                scfg.setFramerate(jo.get("Framerate").getAsDouble());
                tfFpmZOffset.setText(jo.get("ZOffset").getAsString());
                gui.writeStatus("SUCCESS: Settings config loaded from " + tfFpmConfigFile.getText() + ".");
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                gui.writeStatus("ERROR: Problem encountered opening " + tfFpmConfigFile.getText() + ": " + e1.getMessage() + ".");
            } catch (IOException e1) {
                e1.printStackTrace();
                gui.writeStatus("ERROR: Problem encountered opening " + tfFpmConfigFile.getText() + ": " + e1.getMessage() + ".");
            } catch (Exception e1) {
                e1.printStackTrace();
                gui.writeStatus("ERROR: Problem encountered opening " + tfFpmConfigFile.getText() + ": " + e1.getMessage() + ".");
            }
        }
        btnFpmStartIllumination.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                boolean cameraOnline = false;
                if (scfg.getMMCore() != null) {
                    try {
                        if (scfg.getMMSf().isAcquisitionRunning()) {
                            gui.writeStatus("You appear to be running acquisition, stop acquisition before continuing.");
                            return;
                        }
                        cameraOnline = scfg.getMMSf().isLiveModeOn();
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

                        double width1 = Double.parseDouble(tfFpmWidthLightSheet1.getText());
                        double width2 = Double.parseDouble(tfFpmWidthLightSheet2.getText());
                        scfg.getMMCore().setExposure(1000.0 / Double.parseDouble(tfFpmFramerate.getText()));
                        scfg.setFramerate(1000.0 / scfg.getMMCore().getExposure());
                        scfg.setSheetWidth1(width1);
                        scfg.setSheetWidth2(width2);
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
                JsonObject nm = getFindPlaneModeCommandJson();
                req.addProperty("Mode", "FindPlaneMode");
                req.add("FindPlaneMode", nm);
                req.add("Settings", gui.configuration.getSettingsJson());
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                String request = gson.toJson(req);
                gui.cd.dispatchCommand(request + "\n");
                if (scfg.getMMCore() != null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    scfg.getMMSf().enableLiveMode(cameraOnline);
                }
            }
        });
        btnFpmPause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                gui.cd.queryAsyncPause();
            }
        });
    }

    public JsonObject getFindPlaneModeJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumVolumes", 10000000);
        jo.addProperty("LightSheetWidth1", scfg.getSheetWidth1());
        jo.addProperty("LightSheetWidth2", scfg.getSheetWidth2());
        jo.addProperty("Framerate", scfg.getFramerate());
        jo.addProperty("ZOffset", Double.parseDouble(tfFpmZOffset.getText()));
        return jo;
    }

    public JsonObject getFindPlaneModeCommandJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumVolumes", 10000000);
        jo.addProperty("LightSheetWidth1", scfg.getSheetWidth1());
        jo.addProperty("LightSheetWidth2", scfg.getSheetWidth2());
        jo.addProperty("Framerate", scfg.getFramerate());
        jo.addProperty("ZOffset", Double.parseDouble(tfFpmZOffset.getText()) + scfg.getHomeOffset());
        jo.addProperty("Masks1", "");
        jo.addProperty("Masks2", "");
        jo.addProperty("GalvoDelay1", 0);
        jo.addProperty("GalvoDelay2", 0);
        return jo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SharedConfiguration) {
            tfFpmFramerate.setText(String.valueOf(scfg.getFramerate()));
            tfFpmWidthLightSheet1.setText(String.valueOf(scfg.getSheetWidth1()));
            tfFpmWidthLightSheet2.setText(String.valueOf(scfg.getSheetWidth2()));
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
        FindPlaneMode = new JPanel();
        FindPlaneMode.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        FindPlaneMode.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Instructions:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JTextPane textPane1 = new JTextPane();
        Font textPane1Font = this.$$$getFont$$$("Arial", -1, 11, textPane1.getFont());
        if (textPane1Font != null) textPane1.setFont(textPane1Font);
        textPane1.setText("    1) Make sure Z Offset is set to 0.");
        panel1.add(textPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 20), null, 1, false));
        final JTextPane textPane2 = new JTextPane();
        Font textPane2Font = this.$$$getFont$$$("Arial", -1, 11, textPane2.getFont());
        if (textPane2Font != null) textPane2.setFont(textPane2Font);
        textPane2.setText("    2) Create lightsheet and find the very top of the zebrafish larva.");
        panel1.add(textPane2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 20), null, 1, false));
        final JTextPane textPane3 = new JTextPane();
        Font textPane3Font = this.$$$getFont$$$("Arial", -1, 11, textPane3.getFont());
        if (textPane3Font != null) textPane3.setFont(textPane3Font);
        textPane3.setText("    3) After finding the top of the sample, do not use harware controls again.\n        Use software controls to navigate through the sample in Z dimension instead.");
        panel1.add(textPane3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 20), null, 1, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        FindPlaneMode.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Light Sheet Width 1: ");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Light Sheet Width 2: ");
        panel2.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Z Offset: ");
        panel2.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Framerate:");
        panel2.add(label5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("um");
        panel2.add(label6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("um");
        panel2.add(label7, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("um");
        panel2.add(label8, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("fps");
        panel2.add(label9, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfFpmWidthLightSheet1 = new JTextField();
        tfFpmWidthLightSheet1.setText("500");
        panel2.add(tfFpmWidthLightSheet1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfFpmWidthLightSheet2 = new JTextField();
        tfFpmWidthLightSheet2.setText("500");
        panel2.add(tfFpmWidthLightSheet2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfFpmZOffset = new JTextField();
        tfFpmZOffset.setText("0");
        panel2.add(tfFpmZOffset, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfFpmFramerate = new JTextField();
        tfFpmFramerate.setText("100");
        panel2.add(tfFpmFramerate, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        FindPlaneMode.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Config:");
        panel3.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfFpmConfigFile = new JTextField();
        tfFpmConfigFile.setText("/tmp/findplanemode_default.json");
        panel3.add(tfFpmConfigFile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        FindPlaneMode.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnFpmLoadConfig = new JButton();
        btnFpmLoadConfig.setText("Load Config");
        panel4.add(btnFpmLoadConfig, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnFpmSaveConfig = new JButton();
        btnFpmSaveConfig.setText("Save Config");
        panel4.add(btnFpmSaveConfig, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnFpmStartIllumination = new JButton();
        btnFpmStartIllumination.setText("Start Illumination");
        panel4.add(btnFpmStartIllumination, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnFpmPause = new JButton();
        btnFpmPause.setText("Pause");
        panel4.add(btnFpmPause, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return FindPlaneMode;
    }
}
