package uSPIMmm.gui;

import uSPIMmm.*;
import com.google.gson.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mmcorej.StrVector;
import org.micromanager.api.SequenceSettings;
import org.micromanager.utils.MMScriptException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DataAcquisition implements Observer {
    public JTextField tfNmNVolumes;
    public JSpinner spNmNReturn;
    public JTextField tfNmWidthLightSheet1;
    public JSpinner spNmNPlanes;
    private JButton btnNmZStartUp10;
    private JButton btnNmZStartUp1;
    private JButton btnNmZStartDown1;
    private JButton btnNmZStartDown10;
    private JButton btnNmZEndUp10;
    private JButton btnNmZEndUp1;
    private JButton btnNmZEndDown1;
    private JButton btnNmZEndDown10;
    private JLabel lNmInfo;
    private JTextField tfNmConfigFile;
    private JButton btnNmLoadConfig;
    private JButton btnNmSaveConfig;
    private JButton btnNmStartIllumination;
    private JButton btnNmPause;
    public JTextField tfNmWidthLightSheet2;
    public JComboBox cbNmScanMode;
    public JTextField tfNmZStart;
    public JTextField tfNmZEnd;
    public JTextField tfNmFramerate;
    private JPanel DataAcquisition;
    public JCheckBox cbNmAdaptationPhase;
    public JTextField tfNmAdaptationPhaseDuration;
    private JLabel tNmAdaptationPhaseDur;
    private JLabel tNmAdaptationPhaseMin;
    private JTable tSeqAcq;
    private JCheckBox cbSeqAcq;
    private JButton bSeqAcqAdd;
    private JButton bSeqAcqRm;
    private JButton bSeqAcqMUp;
    private JButton bSeqAcqMDown;

    private Color defaultColor;

    private GUI gui;
    private SharedConfiguration scfg;
    private DataAcquisitionHandler dah;
    private List<DataAcquisitionHandler.Payload> payloads;

    public DataAcquisition() {
        payloads = new ArrayList<>();
        cbNmAdaptationPhase.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (cbNmAdaptationPhase.isSelected()) {
                    tNmAdaptationPhaseMin.setEnabled(true);
                    tNmAdaptationPhaseDur.setEnabled(true);
                    tfNmAdaptationPhaseDuration.setEnabled(true);
                } else {
                    tNmAdaptationPhaseMin.setEnabled(false);
                    tNmAdaptationPhaseDur.setEnabled(false);
                    tfNmAdaptationPhaseDuration.setEnabled(false);
                }
                updateTable();
            }
        });

        cbSeqAcq.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (cbSeqAcq.isSelected()) {
                    tSeqAcq.setEnabled(true);
                    bSeqAcqAdd.setEnabled(true);
                    updateTable();
                    recalcluateInfo();
                } else {
                    tSeqAcq.setEnabled(false);
                    bSeqAcqAdd.setEnabled(false);
                    updateTable();
                    recalcluateInfo();
                }
            }
        });
        bSeqAcqAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (recalcluateInfo() < 0) {
                    return;
                }
                final RecordingDetailsDialog rdd = new RecordingDetailsDialog(
                        Double.parseDouble(tfNmZStart.getText()),
                        Double.parseDouble(tfNmZEnd.getText()),
                        ((Integer) spNmNPlanes.getValue()).intValue(),
                        ((Integer) spNmNReturn.getValue()).intValue(),
                        Double.parseDouble(tfNmFramerate.getText()),
                        cbNmScanMode.getSelectedIndex(),
                        recalcluateInfo()
                );
                rdd.setCallback(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DataAcquisitionHandler.Payload p = new DataAcquisitionHandler.Payload();
                        p.metaJo = rdd.getJson();

                        p.width1 = Double.parseDouble(tfNmWidthLightSheet1.getText());
                        p.width2 = Double.parseDouble(tfNmWidthLightSheet2.getText());
                        p.isAdaptation = false;
                        try {
                            p.framerate = 1000.0 / scfg.getMMCore().getExposure();
                        } catch (Exception e1) {
                            gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                        p.nVolumes = Integer.parseInt(tfNmNVolumes.getText());
                        p.nPlanes = (Integer) spNmNPlanes.getValue() + (Integer) spNmNReturn.getValue();
                        p.nReturn = (Integer) spNmNReturn.getValue();
                        p.zStart = Double.parseDouble(tfNmZStart.getText());
                        p.zEnd = Double.parseDouble(tfNmZEnd.getText());
                        p.scanMode = cbNmScanMode.getSelectedIndex();
                        p.framerate = scfg.getFramerate();
                        p.galvoDelay1 = scfg.getGalvoDelay1();
                        p.galvoDelay2 = scfg.getGalvoDelay2();
                        p.masks1 = scfg.getMasks1();
                        p.masks2 = scfg.getMasks2();
                        payloads.add(p);
                        updateTable();
                        recalcluateInfo();
                    }
                });
                rdd.pack();
                rdd.setVisible(true);
            }
        });
    }

    public void create(GUI gui, SharedConfiguration scfg) {
        this.gui = gui;
        this.scfg = scfg;
        dah = new DataAcquisitionHandler(gui, scfg, this);

        this.defaultColor = tfNmNVolumes.getBackground();

        tfNmConfigFile.setText(System.getProperty("user.home") + "/normalmode_default.json");

        btnNmPause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                scfg.getMMSf().enableLiveMode(false);
                gui.cd.queryAsyncPause();
            }
        });

        btnNmStartIllumination.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (cbSeqAcq.isSelected()) { //Sequential Acquisition
                    List<DataAcquisitionHandler.Payload> pls = new ArrayList<>();
                    DataAcquisitionHandler.Payload prep = new DataAcquisitionHandler.Payload();
                    prep.isAdaptation = true;
                    prep.masks1 = payloads.get(0).masks1;
                    prep.masks2 = payloads.get(0).masks2;
                    prep.galvoDelay1 = payloads.get(0).galvoDelay1;
                    prep.galvoDelay2 = payloads.get(0).galvoDelay2;
                    prep.width1 = payloads.get(0).width1;
                    prep.width2 = payloads.get(0).width2;
                    prep.framerate = payloads.get(0).framerate;
                    prep.nPlanes = 1;
                    prep.nReturn = 0;
                    prep.nVolumes = 1;
                    prep.zStart = 0;
                    prep.zEnd = 0;
                    prep.scanMode = 2;
                    prep.metaJo = null;
                    pls.add(prep);
                    for (DataAcquisitionHandler.Payload p : payloads) {
                        pls.add(p);
                    }
                    dah.seqAcquisition(pls);

                } else { //Single Acquisition
                    if (recalcluateInfo() < 0) {
                        return;
                    }
                    final RecordingDetailsDialog rdd = new RecordingDetailsDialog(
                            Double.parseDouble(tfNmZStart.getText()),
                            Double.parseDouble(tfNmZEnd.getText()),
                            ((Integer) spNmNPlanes.getValue()).intValue(),
                            ((Integer) spNmNReturn.getValue()).intValue(),
                            Double.parseDouble(tfNmFramerate.getText()),
                            cbNmScanMode.getSelectedIndex(),
                            recalcluateInfo()
                    );
                    rdd.setCallback(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            dah.singleAcquisition(rdd.getJson());

                        }
                    });
                    rdd.pack();
                    rdd.setVisible(true);
                }
            }
        });

        btnNmSaveConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                JsonObject jo = getNormalModeJson();
                Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                try {
                    PrintWriter writer = new PrintWriter(tfNmConfigFile.getText(), "UTF-8");
                    writer.print(gson.toJson(jo));
                    writer.close();
                    gui.writeStatus("SUCCESS: NormalMode config saved to " + tfNmConfigFile.getText() + ".");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    gui.writeStatus("ERROR: Could not save NormalMode config to " + tfNmConfigFile.getText() + ": " + ex.getMessage() + ".");
                }
                JsonObject jo2 = new JsonObject();
            }
        });
        btnNmLoadConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                InputStream is = null;
                try {
                    is = new FileInputStream(tfNmConfigFile.getText());
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
                    tfNmNVolumes.setText(jo.get("NumVolumes").getAsString());
                    spNmNPlanes.setValue(jo.get("NumPlanes").getAsInt() - jo.get("NumReturn").getAsInt());
                    spNmNReturn.setValue(jo.get("NumReturn").getAsInt());
                    tfNmWidthLightSheet1.setText(jo.get("LightSheetWidth1").getAsString());
                    tfNmWidthLightSheet2.setText(jo.get("LightSheetWidth2").getAsString());
                    tfNmFramerate.setText(jo.get("Framerate").getAsString());
                    tfNmZEnd.setText(Double.toString(jo.get("ZOffset").getAsDouble() + jo.get("ZStep").getAsDouble() * (Integer) spNmNPlanes.getValue()));
                    tfNmZStart.setText(jo.get("ZOffset").getAsString());
                    cbNmScanMode.setSelectedIndex(jo.get("ScanMode").getAsInt());
                    gui.writeStatus("SUCCESS: Settings config loaded from " + tfNmConfigFile.getText() + ".");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    gui.writeStatus("ERROR: Problem encountered opening " + tfNmConfigFile.getText() + ": " + e1.getMessage() + ".");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    gui.writeStatus("ERROR: Problem encountered opening " + tfNmConfigFile.getText() + ": " + e1.getMessage() + ".");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    gui.writeStatus("ERROR: Problem encountered opening " + tfNmConfigFile.getText() + ": " + e1.getMessage() + ".");
                }


            }
        });

        cbNmScanMode.addItem("Bidirectioinal");
        cbNmScanMode.addItem("Scan Up");
        cbNmScanMode.addItem("Scan Down");
        cbNmScanMode.setSelectedIndex(2);
        {
            InputStream is = null;
            try {
                is = new FileInputStream(tfNmConfigFile.getText());
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
                tfNmNVolumes.setText(jo.get("NumVolumes").getAsString());
                spNmNPlanes.setValue(jo.get("NumPlanes").getAsInt() - jo.get("NumReturn").getAsInt());
                spNmNReturn.setValue(jo.get("NumReturn").getAsInt());
                tfNmWidthLightSheet1.setText(jo.get("LightSheetWidth1").getAsString());
                tfNmWidthLightSheet2.setText(jo.get("LightSheetWidth2").getAsString());
                tfNmFramerate.setText(jo.get("Framerate").getAsString());
                tfNmZEnd.setText(Double.toString(jo.get("ZOffset").getAsDouble() + jo.get("ZStep").getAsDouble() * (Integer) spNmNPlanes.getValue()));
                tfNmZStart.setText(jo.get("ZOffset").getAsString());
                cbNmScanMode.setSelectedIndex(jo.get("ScanMode").getAsInt());
                gui.writeStatus("SUCCESS: Settings config loaded from " + tfNmConfigFile.getText() + ".");
            } catch (FileNotFoundException e1) {
                gui.writeStatus("ERROR: Problem encountered opening " + tfNmConfigFile.getText() + ": " + e1.getMessage() + ".");
                e1.printStackTrace();
            } catch (IOException e1) {
                gui.writeStatus("ERROR: Problem encountered opening " + tfNmConfigFile.getText() + ": " + e1.getMessage() + ".");
                e1.printStackTrace();
            } catch (Exception e1) {
                gui.writeStatus("ERROR: Problem encountered opening " + tfNmConfigFile.getText() + ": " + e1.getMessage() + ".");
                e1.printStackTrace();
            }
        }

        tfNmZStart.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reposition(true);
            }
        });

        tfNmZEnd.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reposition(false);
            }
        });

        btnNmZEndUp1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tfNmZEnd.isEnabled()) {
                    tfNmZEnd.setText(Double.toString(Double.parseDouble(tfNmZEnd.getText()) - 1));
                    reposition(false);
                }
            }
        });
        btnNmZEndUp10.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tfNmZEnd.isEnabled()) {
                    tfNmZEnd.setText(Double.toString(Double.parseDouble(tfNmZEnd.getText()) - 10));
                    reposition(false);
                }
            }
        });
        btnNmZStartUp1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                tfNmZStart.setText(Double.toString(Double.parseDouble(tfNmZStart.getText()) - 1));
                reposition(true);
            }
        });
        btnNmZStartUp10.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                tfNmZStart.setText(Double.toString(Double.parseDouble(tfNmZStart.getText()) - 10));
                reposition(true);
            }
        });
        btnNmZEndDown1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tfNmZEnd.isEnabled()) {
                    tfNmZEnd.setText(Double.toString(Double.parseDouble(tfNmZEnd.getText()) + 1));
                    reposition(false);
                }
            }
        });
        btnNmZEndDown10.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tfNmZEnd.isEnabled()) {
                    tfNmZEnd.setText(Double.toString(Double.parseDouble(tfNmZEnd.getText()) + 10));
                    reposition(false);
                }
            }
        });
        btnNmZStartDown1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                tfNmZStart.setText(Double.toString(Double.parseDouble(tfNmZStart.getText()) + 1));
                reposition(true);
            }
        });
        btnNmZStartDown10.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                tfNmZStart.setText(Double.toString(Double.parseDouble(tfNmZStart.getText()) + 10));
                reposition(true);
            }
        });

        cbNmScanMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateZPlaneCounts();
            }
        });


        spNmNPlanes.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (cbNmScanMode.getSelectedIndex() == 0) {
                    spNmNReturn.setValue(spNmNPlanes.getValue());
                }
                recalcluateInfo();
            }
        });

        spNmNReturn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                recalcluateInfo();
            }
        });

        tfNmNVolumes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcluateInfo();
            }
        });

        tfNmFramerate.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcluateInfo();
            }
        });

        tfNmZStart.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcluateInfo();
            }
        });

        tfNmZEnd.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcluateInfo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcluateInfo();
            }
        });

        updateTable();

        recalcluateInfo();
    }

    public void updateTable() {
        String[] columnNames = {
                "Type",
                "Mode",
                "Volumes",
                "Planes",
                "Return",
                "Z Start",
                "Z End",
                "Duration",
                "Acquire?"};

        List<Object[]> rowData = new ArrayList<>();
        if (cbSeqAcq.isSelected()) {
            if (cbNmAdaptationPhase.isSelected()) {
                rowData.add(new Object[]{"Adapt.", "n/a", "n/a", "n/a", "n/a", "0", "0", tfNmAdaptationPhaseDuration.getText(), new Boolean(false)});
            }

            for (DataAcquisitionHandler.Payload p : payloads) {
                if (p.isAdaptation) {
                    rowData.add(new Object[]{
                            (p.nPlanes == 1 && p.nReturn == 0) ? "Plane" : "Volume",
                            p.scanMode == 0 ? "Bi" : (p.scanMode == 1 ? "Up" : "Down"),
                            "n/a",
                            p.nPlanes,
                            p.nReturn,
                            p.zStart,
                            p.zEnd,
                            p.nVolumes * (p.nReturn + p.nPlanes) / p.framerate / 60.0,
                            new Boolean(false)});
                } else {
                    rowData.add(new Object[]{
                            p.zStart == p.zEnd ? "Plane" : "Volume",
                            p.scanMode == 0 ? "Bi" : (p.scanMode == 1 ? "Up" : "Down"),
                            p.nVolumes,
                            p.nPlanes,
                            p.nReturn,
                            p.zStart,
                            p.zEnd,
                            p.nVolumes * (p.nReturn + p.nPlanes) / p.framerate / 60.0,
                            new Boolean(true)});
                }
            }
        }

        AbstractTableModel tm = new AbstractTableModel() {
            public String getColumnName(int col) {
                return columnNames[col].toString();
            }

            public int getRowCount() {
                return rowData.size();
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }

            public Object getValueAt(int row, int col) {
                return rowData.get(row)[col];
            }

            public boolean isCellEditable(int row, int col) {
                return col == 8;
            }

            public void setValueAt(Object value, int row, int col) {
                if (col == 8) {
                    int trueRow = row;
                    if (cbNmAdaptationPhase.isSelected()) {
                        trueRow -= 1;
                    }
                    payloads.get(trueRow).isAdaptation = !payloads.get(trueRow).isAdaptation;
                    updateTable();

                }
                //rowData.get(row)[col] = value;
                //fireTableCellUpdated(row, col);
            }
        };
        tSeqAcq.setModel(tm);
    }

    protected void reposition(boolean start) {
        try {
            double z = start ? Double.parseDouble(tfNmZStart.getText()) : Double.parseDouble(tfNmZEnd.getText());
            JsonObject req = new JsonObject();
            JsonObject nm = getFindPlaneModeJson();
            nm.addProperty("ZOffset", z + scfg.getHomeOffset());
            req.addProperty("Mode", "FindPlaneMode");
            req.add("FindPlaneMode", nm);
            req.add("Settings", gui.configuration.getSettingsJson());
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String request = gson.toJson(req);
            gui.cd.dispatchCommand(request + "\n");
        } catch (Exception e) {
        }
    }

    private void updateZPlaneCounts() {
        if (cbNmScanMode.getSelectedIndex() == 0) {
            spNmNReturn.setEnabled(false);
            spNmNReturn.setValue(spNmNPlanes.getValue());
        } else {
            spNmNReturn.setEnabled(true);
        }
    }

    protected void recolorSpinner(JSpinner spinner, Color co) {
        JComponent editor = spinner.getEditor();
        int n = editor.getComponentCount();
        for (int i = 0; i < n; i++) {
            Component c = editor.getComponent(i);
            if (c instanceof JTextField) {
                c.setBackground(co);
            }
        }
    }

    public int recalcluateInfo() {
        double fps = 0;
        int planes = 0;
        int ret = 0;
        int volumes = 0;
        double zStart = 0;
        double zEnd = 0;

        if (cbSeqAcq.isSelected() && payloads.size() == 0) {
            btnNmStartIllumination.setEnabled(false);
        } else {
            btnNmStartIllumination.setEnabled(true);
        }

        tfNmFramerate.setBackground(this.defaultColor);
        if (!tfNmFramerate.isEnabled()) {
            tfNmFramerate.setBackground(null);
        }
        tfNmNVolumes.setBackground(this.defaultColor);
        if (!tfNmNVolumes.isEnabled()) {
            tfNmNVolumes.setBackground(null);
        }
        tfNmZEnd.setBackground(this.defaultColor);
        if (!tfNmZEnd.isEnabled()) {
            tfNmZEnd.setBackground(null);
        }
        tfNmZStart.setBackground(this.defaultColor);
        if (!tfNmZStart.isEnabled()) {
            tfNmZStart.setBackground(null);
        }
        recolorSpinner(spNmNReturn, this.defaultColor);
        if (!spNmNReturn.isEnabled()) {
            recolorSpinner(spNmNReturn, null);
        }
        recolorSpinner(spNmNPlanes, this.defaultColor);
        if (!spNmNPlanes.isEnabled()) {
            recolorSpinner(spNmNPlanes, null);
        }

        try {
            fps = Double.parseDouble(tfNmFramerate.getText());
        } catch (NumberFormatException e) {
            lNmInfo.setText("Make sure FPS value is formatted correctly!");
            lNmInfo.setForeground(Color.RED);
            tfNmFramerate.setBackground(Color.PINK);
            return -1;
        }
        if (fps <= 0.0) {
            lNmInfo.setText("FPS must be a positive value!");
            lNmInfo.setForeground(Color.RED);
            tfNmFramerate.setBackground(Color.PINK);
            return -1;
        }
        try {
            planes = (Integer) spNmNPlanes.getValue();
        } catch (NumberFormatException e) {
            lNmInfo.setText("Make sure the number of planes is formatted correctly!");
            lNmInfo.setForeground(Color.RED);
            recolorSpinner(spNmNPlanes, Color.PINK);
            return -1;
        }
        if (planes <= 0) {
            lNmInfo.setText("Number of planes must be a positive value!");
            lNmInfo.setForeground(Color.RED);
            recolorSpinner(spNmNPlanes, Color.PINK);
            return -1;
        }
        try {
            ret = (Integer) spNmNReturn.getValue();
        } catch (NumberFormatException e) {
            lNmInfo.setText("Make sure the number of return planes is formatted correctly!");
            lNmInfo.setForeground(Color.RED);
            recolorSpinner(spNmNReturn, Color.PINK);
            return -1;
        }
        if (ret < 0) {
            lNmInfo.setText("Number of return planes must be a non-negative value!");
            lNmInfo.setForeground(Color.RED);
            recolorSpinner(spNmNReturn, Color.PINK);
            return -1;
        }
        if (ret > planes) {
            lNmInfo.setText("Number of return planes shall not be higher than number of acquisition planes!");
            lNmInfo.setForeground(Color.RED);
            spNmNPlanes.setBackground(Color.PINK);
            recolorSpinner(spNmNReturn, Color.PINK);
            return -1;
        }
        try {
            volumes = Integer.parseInt(tfNmNVolumes.getText());
        } catch (NumberFormatException e) {
            lNmInfo.setText("Make sure the number of volumes is formatted correctly");
            lNmInfo.setForeground(Color.RED);
            tfNmNVolumes.setBackground(Color.PINK);
            return -1;
        }
        try {
            zStart = Double.parseDouble(tfNmZStart.getText());
        } catch (NumberFormatException e) {
            lNmInfo.setText("Make sure Z Start value is formatted correctly!");
            lNmInfo.setForeground(Color.RED);
            tfNmZStart.setBackground(Color.PINK);
            return -1;
        }
        try {
            zEnd = Double.parseDouble(tfNmZEnd.getText());
        } catch (NumberFormatException e) {
            lNmInfo.setText("Make sure Z End value is formatted correctly!");
            lNmInfo.setForeground(Color.RED);
            tfNmZEnd.setBackground(Color.PINK);
            return -1;
        }

        if (ret == 0) {
            tfNmZEnd.setEnabled(false);

            //SwingUtilities.invokeLater(new Runnable() {
            //    @Override
            //    public void run() {
            //       tfNmZEnd.setText(tfNmZStart.getText());
            //   }
            //});
        } else {
            tfNmZEnd.setEnabled(true);
            if (zStart == zEnd) {
                lNmInfo.setText("Use 0 return planes for plane acquisition (Z Start and End position should not be the same for volumes).");
                lNmInfo.setForeground(Color.RED);
                tfNmZEnd.setBackground(Color.PINK);
                recolorSpinner(spNmNReturn, Color.PINK);
                return -1;
            }
            if (zEnd < zStart) {
                lNmInfo.setText("Z End position must be greater than Z Start position.");
                lNmInfo.setForeground(Color.RED);
                tfNmZEnd.setBackground(Color.PINK);
                return -1;
            }
        }
        int frames = volumes * (planes + ret);
        float mins = (float) frames / (float) fps / 60.0f;
        lNmInfo.setForeground(Color.BLACK);
        lNmInfo.setText("Aquisition will produce " + String.valueOf(frames) + " frames ("
                + String.valueOf(volumes) + " volumes consisting of "
                + String.valueOf(planes) + " planes and " + String.valueOf(ret) + " for return) and take "
                + String.format("%.4f", mins) + "min@" + String.format("%.2f", fps) + "fps.");
        return frames;
    }

    public JsonObject getNormalModeJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumVolumes", Integer.parseInt(tfNmNVolumes.getText()));
        jo.addProperty("NumPlanes", (Integer) spNmNPlanes.getValue() + (Integer) spNmNReturn.getValue());
        jo.addProperty("NumReturn", (Integer) spNmNReturn.getValue());
        jo.addProperty("LightSheetWidth1", Double.parseDouble(tfNmWidthLightSheet1.getText()));
        jo.addProperty("LightSheetWidth2", Double.parseDouble(tfNmWidthLightSheet2.getText()));
        jo.addProperty("ZStep", (Double.parseDouble(tfNmZEnd.getText()) - Double.parseDouble(tfNmZStart.getText())) / ((double) ((Integer) spNmNPlanes.getValue())));
        jo.addProperty("ZOffset", Double.parseDouble(tfNmZStart.getText()));
        jo.addProperty("ScanMode", cbNmScanMode.getSelectedIndex());
        jo.addProperty("Framerate", scfg.getFramerate());
        return jo;
    }

    public JsonObject getNormalModeCommandJson(boolean isAdaptation) {
        JsonObject jo = new JsonObject();
        double frate = Double.parseDouble(tfNmFramerate.getText());
        jo.addProperty("NumVolumes", isAdaptation ? (Double.parseDouble(tfNmAdaptationPhaseDuration.getText()) * 60000 / (1000.0 / frate * 2)) : Integer.parseInt(tfNmNVolumes.getText()));
        jo.addProperty("NumPlanes", isAdaptation ? 1 : ((Integer) spNmNPlanes.getValue() + (Integer) spNmNReturn.getValue()));
        jo.addProperty("NumReturn", isAdaptation ? 1 : ((Integer) spNmNReturn.getValue()));
        jo.addProperty("LightSheetWidth1", scfg.getSheetWidth1());
        jo.addProperty("LightSheetWidth2", scfg.getSheetWidth2());
        jo.addProperty("Framerate", frate);
        double zStp = 0.0;
        if (((Integer) spNmNReturn.getValue()) > 0) {
            zStp = ((Double.parseDouble(tfNmZEnd.getText()) - Double.parseDouble(tfNmZStart.getText())) / ((double) ((Integer) spNmNPlanes.getValue())));
        }
        jo.addProperty("ZStep", zStp);
        jo.addProperty("ZOffset", scfg.getHomeOffset() + (isAdaptation ? 0.0 : Double.parseDouble(tfNmZStart.getText())));
        jo.addProperty("ScanMode", cbNmScanMode.getSelectedIndex());
        jo.addProperty("Masks1", scfg.getMasks1());
        jo.addProperty("Masks2", scfg.getMasks2());
        jo.addProperty("GalvoDelay1", scfg.getGalvoDelay1());
        jo.addProperty("GalvoDelay2", scfg.getGalvoDelay2());
        jo.addProperty("BlankReposition", !cbNmAdaptationPhase.isSelected());
        jo.addProperty("TriggerOut", !isAdaptation);
        return jo;
    }

    public JsonObject getFindPlaneModeJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("NumVolumes", 10000000);
        jo.addProperty("LightSheetWidth1", Double.parseDouble(tfNmWidthLightSheet1.getText()));
        jo.addProperty("LightSheetWidth2", Double.parseDouble(tfNmWidthLightSheet2.getText()));
        jo.addProperty("Framerate", Double.parseDouble(tfNmFramerate.getText()));
        jo.addProperty("Masks1", scfg.getMasks1());
        jo.addProperty("Masks2", scfg.getMasks2());
        jo.addProperty("GalvoDelay1", scfg.getGalvoDelay1());
        jo.addProperty("GalvoDelay2", scfg.getGalvoDelay2());
        return jo;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SharedConfiguration) {
            tfNmFramerate.setText(String.valueOf(scfg.getFramerate()));
            tfNmWidthLightSheet1.setText(String.valueOf(scfg.getSheetWidth1()));
            tfNmWidthLightSheet2.setText(String.valueOf(scfg.getSheetWidth2()));
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
        DataAcquisition = new JPanel();
        DataAcquisition.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 9, new Insets(0, 0, 0, 0), -1, -1));
        DataAcquisition.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Number of volumes:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfNmNVolumes = new JTextField();
        tfNmNVolumes.setText("1000");
        panel1.add(tfNmNVolumes, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Z Start");
        panel1.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Z End");
        panel1.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("um");
        panel1.add(label4, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("um");
        panel1.add(label5, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(3, 4, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnNmZStartUp10 = new JButton();
        btnNmZStartUp10.setText("-10");
        panel2.add(btnNmZStartUp10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        btnNmZStartUp1 = new JButton();
        btnNmZStartUp1.setText("-1");
        panel2.add(btnNmZStartUp1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        btnNmZStartDown1 = new JButton();
        btnNmZStartDown1.setText("+1");
        panel2.add(btnNmZStartDown1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        btnNmZStartDown10 = new JButton();
        btnNmZStartDown10.setText("+10");
        panel2.add(btnNmZStartDown10, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(4, 4, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnNmZEndUp10 = new JButton();
        btnNmZEndUp10.setText("-10");
        panel3.add(btnNmZEndUp10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        btnNmZEndUp1 = new JButton();
        btnNmZEndUp1.setText("-1");
        panel3.add(btnNmZEndUp1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        btnNmZEndDown1 = new JButton();
        btnNmZEndDown1.setText("+1");
        panel3.add(btnNmZEndDown1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        btnNmZEndDown10 = new JButton();
        btnNmZEndDown10.setText("+10");
        panel3.add(btnNmZEndDown10, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), new Dimension(80, -1), 0, false));
        tfNmZStart = new JTextField();
        tfNmZStart.setHorizontalAlignment(11);
        tfNmZStart.setText("0");
        panel1.add(tfNmZStart, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        tfNmZEnd = new JTextField();
        tfNmZEnd.setHorizontalAlignment(11);
        tfNmZEnd.setText("50");
        panel1.add(tfNmZEnd, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Light Sheet Width: ");
        panel1.add(label6, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfNmWidthLightSheet1 = new JTextField();
        tfNmWidthLightSheet1.setEnabled(false);
        tfNmWidthLightSheet1.setHorizontalAlignment(11);
        tfNmWidthLightSheet1.setText("500");
        panel1.add(tfNmWidthLightSheet1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        tfNmWidthLightSheet2 = new JTextField();
        tfNmWidthLightSheet2.setEnabled(false);
        tfNmWidthLightSheet2.setHorizontalAlignment(11);
        tfNmWidthLightSheet2.setText("500");
        panel1.add(tfNmWidthLightSheet2, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Framerate:");
        panel1.add(label7, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("um");
        panel1.add(label8, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("fps");
        panel1.add(label9, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfNmFramerate = new JTextField();
        tfNmFramerate.setEnabled(false);
        tfNmFramerate.setHorizontalAlignment(11);
        tfNmFramerate.setText("100");
        panel1.add(tfNmFramerate, new GridConstraints(1, 5, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(110, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Scan mode:");
        panel1.add(label10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbNmScanMode = new JComboBox();
        panel1.add(cbNmScanMode, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Number of planes:\n");
        panel1.add(label11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spNmNPlanes = new JSpinner();
        panel1.add(spNmNPlanes, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("and");
        panel1.add(label12, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spNmNReturn = new JSpinner();
        panel1.add(spNmNReturn, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), new Dimension(100, -1), 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("return");
        panel1.add(label13, new GridConstraints(2, 5, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        DataAcquisition.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lNmInfo = new JLabel();
        lNmInfo.setText("Aquisition will produce N frames (N volumes consisting of N planes and N for return).");
        panel4.add(lNmInfo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        DataAcquisition.add(panel5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnNmLoadConfig = new JButton();
        btnNmLoadConfig.setText("Load Config");
        panel5.add(btnNmLoadConfig, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNmSaveConfig = new JButton();
        btnNmSaveConfig.setText("Save Config");
        panel5.add(btnNmSaveConfig, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNmStartIllumination = new JButton();
        btnNmStartIllumination.setText("Start Acquisition");
        panel5.add(btnNmStartIllumination, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNmPause = new JButton();
        btnNmPause.setText("Pause");
        panel5.add(btnNmPause, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        DataAcquisition.add(panel6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Config:");
        panel6.add(label14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfNmConfigFile = new JTextField();
        tfNmConfigFile.setText("/tmp/normalmode_default.json");
        panel6.add(tfNmConfigFile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        DataAcquisition.add(panel7, new GridConstraints(2, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Advanced"));
        cbNmAdaptationPhase = new JCheckBox();
        cbNmAdaptationPhase.setText("");
        panel7.add(cbNmAdaptationPhase, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfNmAdaptationPhaseDuration = new JTextField();
        tfNmAdaptationPhaseDuration.setEnabled(false);
        tfNmAdaptationPhaseDuration.setHorizontalAlignment(11);
        tfNmAdaptationPhaseDuration.setText("10");
        panel7.add(tfNmAdaptationPhaseDuration, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        tNmAdaptationPhaseMin = new JLabel();
        tNmAdaptationPhaseMin.setEnabled(false);
        tNmAdaptationPhaseMin.setText("min");
        panel7.add(tNmAdaptationPhaseMin, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tNmAdaptationPhaseDur = new JLabel();
        tNmAdaptationPhaseDur.setEnabled(false);
        tNmAdaptationPhaseDur.setText("Duration:");
        panel7.add(tNmAdaptationPhaseDur, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel7.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        Font label15Font = this.$$$getFont$$$(null, Font.BOLD, -1, label15.getFont());
        if (label15Font != null) label15.setFont(label15Font);
        label15.setText("Light Adaptation Phase:");
        panel7.add(label15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        Font label16Font = this.$$$getFont$$$(null, Font.BOLD, -1, label16.getFont());
        if (label16Font != null) label16.setFont(label16Font);
        label16.setText("Sequential Acquisition:");
        panel7.add(label16, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbSeqAcq = new JCheckBox();
        cbSeqAcq.setText("");
        panel7.add(cbSeqAcq, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(1, 2, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        bSeqAcqAdd = new JButton();
        bSeqAcqAdd.setEnabled(false);
        bSeqAcqAdd.setText("Add");
        panel8.add(bSeqAcqAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel8.add(spacer3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        bSeqAcqRm = new JButton();
        bSeqAcqRm.setEnabled(false);
        bSeqAcqRm.setText("Remove");
        panel8.add(bSeqAcqRm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bSeqAcqMUp = new JButton();
        bSeqAcqMUp.setEnabled(false);
        bSeqAcqMUp.setText("Move Up");
        panel8.add(bSeqAcqMUp, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bSeqAcqMDown = new JButton();
        bSeqAcqMDown.setEnabled(false);
        bSeqAcqMDown.setText("Move Down");
        panel8.add(bSeqAcqMDown, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel7.add(scrollPane1, new GridConstraints(2, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tSeqAcq = new JTable();
        tSeqAcq.setEnabled(false);
        scrollPane1.setViewportView(tSeqAcq);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return DataAcquisition;
    }
}
