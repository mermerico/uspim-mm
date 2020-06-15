package uSPIMmm.gui;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class Configuration {
    private JTextField tfCfgHomeOffset;
    private JTextField tfCfgZMin;
    private JTextField tfCfgZMax;
    private JTextField tfCfgPifocScale;
    private JTextField tfCfgCyclesPerFrame;
    private JTextField tfCfgZMirrorScale2;
    private JTextField tfCfgZMirrorScale1;
    private JTextField tfCfgXMirrorScale1;
    private JTextField tfCfgXMirrorScale2;
    private JTextField tfCfgFastAqDir;
    private JTextField tfCfgConfigFile;
    private JPanel Configuration;
    private JButton btnCfgLoadConfig;
    private JButton btnCfgSaveConfig;
    private JCheckBox cbPropertyControl;

    private SharedConfiguration scfg;
    private GUI gui;

    public Configuration() {
        $$$setupUI$$$();
        cbPropertyControl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                scfg.setPropertyControl(cbPropertyControl.isSelected());
            }
        });
    }

    public void create(GUI gui, SharedConfiguration scfg) {
        this.scfg = scfg;
        tfCfgConfigFile.setText(System.getProperty("user.home") + "/settings_default.json");
        btnCfgSaveConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                JsonObject jo = getSettingsJson();
                Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                try {
                    PrintWriter writer = new PrintWriter(tfCfgConfigFile.getText(), "UTF-8");
                    writer.print(gson.toJson(jo));
                    writer.close();
                    gui.writeStatus("SUCCESS: Settings config saved to " + tfCfgConfigFile.getText() + ".");
                } catch (Exception ex) {
                    gui.writeStatus("ERROR: Problem saving Settings config to " + tfCfgConfigFile.getText() + ": " + ex.getMessage() + ".");
                    ex.printStackTrace();
                }
                scfg.setHomeOffset(Double.parseDouble(tfCfgHomeOffset.getText()));
            }
        });
        btnCfgLoadConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                InputStream is = null;
                try {
                    is = new FileInputStream(tfCfgConfigFile.getText());
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
                    tfCfgPifocScale.setText(jo.get("ScalePifoc").getAsString());
                    tfCfgXMirrorScale1.setText(jo.get("ScaleXMirror1").getAsString());
                    tfCfgXMirrorScale2.setText(jo.get("ScaleXMirror2").getAsString());
                    tfCfgZMirrorScale1.setText(jo.get("ScaleZMirror1").getAsString());
                    tfCfgZMirrorScale2.setText(jo.get("ScaleZMirror2").getAsString());
                    tfCfgHomeOffset.setText(jo.get("HomeOffset").getAsString());
                    scfg.setHomeOffset(Double.parseDouble(tfCfgHomeOffset.getText()));
                    tfCfgZMax.setText(jo.get("ZMax").getAsString());
                    tfCfgZMin.setText(jo.get("ZMin").getAsString());
                    tfCfgCyclesPerFrame.setText(jo.get("CyclesPerFrame").getAsString());
                    //cbCfgShutterControl.setSelected(jo.get("ShutterControl").getAsBoolean());
                    cbPropertyControl.setSelected(jo.get("PropertyControl").getAsBoolean());
                    tfCfgFastAqDir.setText(jo.get("FastAqDir").getAsString());
                    scfg.setFastAqDir(jo.get("FastAqDir").getAsString());
                    //if (gui.MMCore != null) {
                    //gui.MMCore.setAutoShutter(!cbCfgShutterControl.isSelected());
                    //}
                    gui.writeStatus("SUCCESS: Settings config loaded from " + tfCfgConfigFile.getText() + ".");
                } catch (FileNotFoundException e1) {
                    gui.writeStatus("ERROR: Problem encountered opening " + tfCfgConfigFile.getText() + ": " + e1.getMessage() + ".");
                    e1.printStackTrace();
                } catch (IOException e1) {
                    gui.writeStatus("ERROR: Problem encountered opening " + tfCfgConfigFile.getText() + ": " + e1.getMessage() + ".");
                    e1.printStackTrace();
                } catch (Exception e1) {
                    gui.writeStatus("ERROR: Problem encountered opening " + tfCfgConfigFile.getText() + ": " + e1.getMessage() + ".");
                    e1.printStackTrace();
                }
            }

        });
        tfCfgHomeOffset.getDocument().addDocumentListener(new DocumentListener() {
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
                changed();
            }

            void changed() {
                try {
                    scfg.setHomeOffset(Double.parseDouble(tfCfgHomeOffset.getText()));
                } catch (Exception e) {

                }
            }
        });
        {
            InputStream is = null;
            try {
                is = new FileInputStream(tfCfgConfigFile.getText());
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
                tfCfgPifocScale.setText(jo.get("ScalePifoc").getAsString());
                tfCfgXMirrorScale1.setText(jo.get("ScaleXMirror1").getAsString());
                tfCfgXMirrorScale2.setText(jo.get("ScaleXMirror2").getAsString());
                tfCfgZMirrorScale1.setText(jo.get("ScaleZMirror1").getAsString());
                tfCfgZMirrorScale2.setText(jo.get("ScaleZMirror2").getAsString());
                tfCfgHomeOffset.setText(jo.get("HomeOffset").getAsString());
                tfCfgCyclesPerFrame.setText(jo.get("CyclesPerFrame").getAsString());
                scfg.setFastAqDir(jo.get("FastAqDir").getAsString());
                //cbCfgShutterControl.setSelected(jo.get("ShutterControl").getAsBoolean());
                cbPropertyControl.setSelected(jo.get("PropertyControl").getAsBoolean());
                tfCfgFastAqDir.setText(jo.get("FastAqDir").getAsString());
                //if (gui.MMCore != null) {
                //gui.MMCore.setAutoShutter(!cbCfgShutterControl.isSelected());
                //if (cbCfgShutterControl.isSelected()) {
                //gui.MMCore.setShutterOpen(false);
                //}
                //}
                gui.writeStatus("SUCCESS: Settings config loaded from " + tfCfgConfigFile.getText() + ".");
            } catch (FileNotFoundException e1) {
                gui.writeStatus("ERROR: Problem encountered opening " + tfCfgConfigFile.getText() + ": " + e1.getMessage() + ".");
                e1.printStackTrace();
            } catch (IOException e1) {
                gui.writeStatus("ERROR: Problem encountered opening " + tfCfgConfigFile.getText() + ": " + e1.getMessage() + ".");
                e1.printStackTrace();
            } catch (Exception e1) {
                gui.writeStatus("ERROR: Problem encountered opening " + tfCfgConfigFile.getText() + ": " + e1.getMessage() + ".");
                e1.printStackTrace();
            }
        }

        gui.tpMain.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (gui.tpMain.getSelectedIndex() == 6) {
                    tfCfgPifocScale.setEnabled(false);
                    tfCfgZMirrorScale1.setEnabled(false);
                    tfCfgZMirrorScale2.setEnabled(false);
                    tfCfgXMirrorScale1.setEnabled(false);
                    tfCfgXMirrorScale2.setEnabled(false);
                    tfCfgHomeOffset.setEnabled(false);
                    tfCfgCyclesPerFrame.setEnabled(false);
                    tfCfgConfigFile.setEnabled(false);
                    tfCfgZMax.setEnabled(false);
                    tfCfgZMin.setEnabled(false);
                    btnCfgLoadConfig.setEnabled(false);
                    btnCfgSaveConfig.setEnabled(false);
                    JFrame frame = new JFrame();
                    String[] options = new String[2];
                    options[0] = new String("Disagree");
                    options[1] = new String("Agree");
                    int res = JOptionPane.showOptionDialog(frame.getContentPane(), "I have no idea what I am doing:", "You probably dont want to do this.", 0, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

                    if (res == 1) {
                        tfCfgPifocScale.setEnabled(false);
                        tfCfgZMirrorScale1.setEnabled(false);
                        tfCfgZMirrorScale2.setEnabled(false);
                        tfCfgXMirrorScale1.setEnabled(false);
                        tfCfgXMirrorScale2.setEnabled(false);
                        tfCfgHomeOffset.setEnabled(false);
                        tfCfgCyclesPerFrame.setEnabled(false);
                        tfCfgConfigFile.setEnabled(false);
                        tfCfgZMax.setEnabled(false);
                        tfCfgZMin.setEnabled(false);
                        btnCfgLoadConfig.setEnabled(false);
                        btnCfgSaveConfig.setEnabled(false);
                    } else if (res == 0) {
                        tfCfgPifocScale.setEnabled(true);
                        tfCfgZMirrorScale1.setEnabled(true);
                        tfCfgZMirrorScale2.setEnabled(true);
                        tfCfgXMirrorScale1.setEnabled(true);
                        tfCfgXMirrorScale2.setEnabled(true);
                        tfCfgHomeOffset.setEnabled(true);
                        tfCfgCyclesPerFrame.setEnabled(true);
                        tfCfgConfigFile.setEnabled(true);
                        tfCfgZMax.setEnabled(true);
                        tfCfgZMin.setEnabled(true);
                        btnCfgLoadConfig.setEnabled(true);
                        btnCfgSaveConfig.setEnabled(true);
                    }
                }
            }
        });
    }

    public JsonObject getSettingsJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("CyclesPerFrame", Integer.parseInt(tfCfgCyclesPerFrame.getText()));
        jo.addProperty("ScalePifoc", Double.parseDouble(tfCfgPifocScale.getText()));
        jo.addProperty("ScaleXMirror1", Double.parseDouble(tfCfgXMirrorScale1.getText()));
        jo.addProperty("ScaleXMirror2", Double.parseDouble(tfCfgXMirrorScale2.getText()));
        try {
            jo.addProperty("ScaleZMirror1", Double.parseDouble(tfCfgZMirrorScale1.getText()));
        } catch (NumberFormatException e) {
            JsonObject convertedObject = new Gson().fromJson(tfCfgZMirrorScale1.getText(), JsonObject.class);
            jo.add("ScaleZMirror1", convertedObject);
        }

        jo.addProperty("ScaleZMirror2", Double.parseDouble(tfCfgZMirrorScale2.getText()));
        try {
            jo.addProperty("ScaleZMirror2", Double.parseDouble(tfCfgZMirrorScale2.getText()));
        } catch (NumberFormatException e) {
            JsonObject convertedObject = new Gson().fromJson(tfCfgZMirrorScale2.getText(), JsonObject.class);
            jo.add("ScaleZMirror2", convertedObject);
        }
        jo.addProperty("HomeOffset", Double.parseDouble(tfCfgHomeOffset.getText()));
        jo.addProperty("ZMin", Double.parseDouble(tfCfgZMin.getText()));
        jo.addProperty("ZMax", Double.parseDouble(tfCfgZMax.getText()));
        //jo.addProperty("ShutterControl", cbCfgShutterControl.isSelected());
        jo.addProperty("PropertyControl", cbPropertyControl.isSelected());
        jo.addProperty("FastAqDir", tfCfgFastAqDir.getText());
        return jo;
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        Configuration = new JPanel();
        Configuration.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        Configuration.add(panel1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("General");
        panel2.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("PIFOC Scale:");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Laser cycles per frame:");
        panel2.add(label3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Z Home offset:");
        panel3.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Z Min.");
        panel3.add(label5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Z Max.");
        panel3.add(label6, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCfgHomeOffset = new JTextField();
        tfCfgHomeOffset.setText("150");
        panel3.add(tfCfgHomeOffset, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        tfCfgZMin = new JTextField();
        tfCfgZMin.setText("0");
        panel3.add(tfCfgZMin, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        tfCfgZMax = new JTextField();
        tfCfgZMax.setText("400");
        panel3.add(tfCfgZMax, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        tfCfgPifocScale = new JTextField();
        tfCfgPifocScale.setText("40");
        panel2.add(tfCfgPifocScale, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        tfCfgCyclesPerFrame = new JTextField();
        tfCfgCyclesPerFrame.setText("3");
        panel2.add(tfCfgCyclesPerFrame, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        cbPropertyControl = new JCheckBox();
        cbPropertyControl.setText("Property Control");
        panel4.add(cbPropertyControl, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        Configuration.add(panel5, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, Font.BOLD, -1, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Light Path 1");
        panel5.add(label7, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("X Mirror Scale 1: ");
        panel5.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Z Mirror Scale 1:");
        panel5.add(label9, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCfgZMirrorScale1 = new JTextField();
        tfCfgZMirrorScale1.setText("-961.797");
        panel5.add(tfCfgZMirrorScale1, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfCfgXMirrorScale1 = new JTextField();
        tfCfgXMirrorScale1.setText("195.5");
        panel5.add(tfCfgXMirrorScale1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        Configuration.add(panel6, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$(null, Font.BOLD, -1, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("Light Path 2");
        panel6.add(label10, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Z Mirror Scale 2:");
        panel6.add(label11, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("X Mirror Scale 2: ");
        panel6.add(label12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCfgZMirrorScale2 = new JTextField();
        tfCfgZMirrorScale2.setText("-961.797");
        panel6.add(tfCfgZMirrorScale2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfCfgXMirrorScale2 = new JTextField();
        tfCfgXMirrorScale2.setText("195.5");
        panel6.add(tfCfgXMirrorScale2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        Configuration.add(panel7, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("FastAq. directory:");
        panel7.add(label13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        Font label14Font = this.$$$getFont$$$(null, Font.BOLD, -1, label14.getFont());
        if (label14Font != null) label14.setFont(label14Font);
        label14.setText("Acquisition");
        panel7.add(label14, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCfgFastAqDir = new JTextField();
        tfCfgFastAqDir.setText("E:\\SPIMData");
        panel7.add(tfCfgFastAqDir, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        Configuration.add(panel8, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Config:");
        panel8.add(label15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfCfgConfigFile = new JTextField();
        tfCfgConfigFile.setText("/tmp/settings_default.json");
        panel8.add(tfCfgConfigFile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        Configuration.add(panel9, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnCfgLoadConfig = new JButton();
        btnCfgLoadConfig.setText("Load Config");
        panel9.add(btnCfgLoadConfig, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCfgSaveConfig = new JButton();
        btnCfgSaveConfig.setText("Save Config");
        panel9.add(btnCfgSaveConfig, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Configuration;
    }
}
