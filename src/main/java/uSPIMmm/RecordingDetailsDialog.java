package uSPIMmm;

import com.google.gson.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

public class RecordingDetailsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfSampleDpf;
    private JTextField tfSampleExpression;
    private JTextArea taSampleDescription;
    private JTextField tfRecordingFps;
    private JTextArea taRecordingDescription;
    private JComboBox cbRecordingScanMode;
    private JTextArea taExperimentDescription;
    private JSpinner spSampleId;
    private JSpinner spRecordingVolumePlanes;
    private JSpinner spRecordingFlybackPlanes;
    private JSpinner spSampleDpf;
    private JLabel lNFrames;
    private JTextField tfRecordingVolumeStart;
    private JTextField tfRecordingVolumeEnd;
    private JRadioButton rbProcessingRenameCopy;
    private JRadioButton rbProcessingRenameMove;
    private JTextField tfProcessingRenameDir;
    private JCheckBox cbProcessingRenameScratch;
    private JCheckBox cbProcessingDownscaleScratch;
    private JTextField tfProcessingDownscaleScale;
    private JCheckBox cbProcessingRegistrationScratch;
    private JTextField tfProcessingPublishDfofName;
    private JTextArea taProcessingPublishDfofDesc;
    private JTextField tfRecordingName;
    private JCheckBox cbProcessingPublishDfofScratch;
    private JCheckBox cbProcessingDownscaleStrip;
    private JTextField tfExperimentType;
    private JTextField tfExperimentTags;
    private String jsonString;
    private JsonObject jo;
    private ActionListener cb;

    String filename = System.getProperty("user.home") + "/.control_engine.recording_details.json";
    Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();


    public RecordingDetailsDialog(double volumeStart, double volumeEnd, int volumePlanes, int flybackPlanes, double fps, int scanmode, int nframes) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        cbRecordingScanMode.addItem("Bidirectioinal Scan");
        cbRecordingScanMode.addItem("Scan Up");
        cbRecordingScanMode.addItem("Scan Down");

        try {
            InputStream is = new FileInputStream(filename);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            String jstr = sb.toString();
            Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jo = gson.fromJson(jstr, JsonObject.class);
            JsonObject sampleJo = jo.getAsJsonObject("sample");
            spSampleDpf.setValue((Object) sampleJo.get("dpf").getAsInt());
            spSampleId.setValue((Object) sampleJo.get("id").getAsInt());
            tfSampleExpression.setText(sampleJo.get("expression").getAsString());
            taSampleDescription.setText(sampleJo.get("description").getAsString());

            JsonObject recordingJo = jo.getAsJsonObject("recording");
            JsonObject volumeJo = recordingJo.getAsJsonObject("volume");
            tfRecordingVolumeStart.setText(Double.toString(volumeJo.get("start").getAsDouble()));
            tfRecordingVolumeEnd.setText(Double.toString(volumeJo.get("end").getAsDouble()));
            spRecordingVolumePlanes.setValue((Object) volumeJo.get("planes").getAsInt());
            spRecordingFlybackPlanes.setValue((Object) volumeJo.get("flyback").getAsInt());
            tfRecordingName.setText(recordingJo.get("name").getAsString());
            taRecordingDescription.setText(recordingJo.get("description").getAsString());
            tfRecordingFps.setText(recordingJo.get("fps").getAsString());
            if (recordingJo.get("scanMode").getAsString().equalsIgnoreCase("bidirectional")) {
                cbRecordingScanMode.setSelectedIndex(0);
            } else if (recordingJo.get("scanMode").getAsString().equalsIgnoreCase("scanUp")) {
                cbRecordingScanMode.setSelectedIndex(1);
            } else if (recordingJo.get("scanMode").getAsString().equalsIgnoreCase("scanDown")) {
                cbRecordingScanMode.setSelectedIndex(2);
            }

            JsonObject experimentJo = jo.getAsJsonObject("experiment");
            taExperimentDescription.setText(experimentJo.get("description").getAsString());
            String tags = "";
            for (JsonElement jp : experimentJo.get("tags").getAsJsonArray()) {
                tags += jp.getAsString() + ",";
            }
            if (tags.length() > 0) {
                tags = tags.substring(0, tags.length() - 1);
            }
            tfExperimentTags.setText(tags);
            tfExperimentType.setText(experimentJo.get("type").getAsString());


        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        } catch (Exception e) {

        }
        tfRecordingVolumeStart.setText(Double.toString(volumeStart));
        tfRecordingVolumeEnd.setText(Double.toString(volumeEnd));
        spRecordingVolumePlanes.setValue(volumePlanes);
        spRecordingFlybackPlanes.setValue(flybackPlanes);
        tfRecordingFps.setText(Double.toString(fps));
        cbRecordingScanMode.setSelectedIndex(scanmode);
        lNFrames.setText(Integer.toString(nframes));


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
                cb.actionPerformed(e);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        rbProcessingRenameCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rbProcessingRenameMove.setSelected(false);
            }
        });
        rbProcessingRenameMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rbProcessingRenameCopy.setSelected(false);
            }
        });
    }

    public void setCallback(ActionListener cb) {
        this.cb = cb;
    }

    private String buildString(String template, LocalDateTime ldt) {
        //Date & Time
        template = template.replaceAll("\\{\\{date\\}\\}", ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        template = template.replaceAll("\\{\\{time\\}\\}", ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        template = template.replaceAll("\\{\\{time_dir\\}\\}", ldt.format(DateTimeFormatter.ofPattern("HHmmss")));

        //Sample
        template = template.replaceAll("\\{\\{sample_dpf\\}\\}", new Integer((Integer) spSampleDpf.getValue()).toString());
        template = template.replaceAll("\\{\\{sample_id\\}\\}", new Integer((Integer) spSampleId.getValue()).toString());
        template = template.replaceAll("\\{\\{sample_expression\\}\\}", tfSampleExpression.getText());
        template = template.replaceAll("\\{\\{sample_description\\}\\}", taSampleDescription.getText());

        //Recording
        template = template.replaceAll("\\{\\{recording_name\\}\\}", tfRecordingName.getText());
        if (Double.parseDouble(tfRecordingVolumeStart.getText()) == Double.parseDouble(tfRecordingVolumeStart.getText())) {
            template = template.replaceAll("\\{\\{recording_type\\}\\}", "plane");
        } else {
            template = template.replaceAll("\\{\\{recording_type\\}\\}", "volume");
        }
        template = template.replaceAll("\\{\\{recording_fps\\}\\}", tfRecordingFps.getText());
        template = template.replaceAll("\\{\\{recording_frames\\}\\}", lNFrames.getText());
        template = template.replaceAll("\\{\\{recording_vol_start\\}\\}", tfRecordingVolumeStart.getText());
        template = template.replaceAll("\\{\\{recording_vol_end\\}\\}", tfRecordingVolumeEnd.getText());
        template = template.replaceAll("\\{\\{recording_vol_planes\\}\\}", new Integer((Integer) spRecordingVolumePlanes.getValue()).toString());
        template = template.replaceAll("\\{\\{recording_flyback_planes\\}\\}", new Integer((Integer) spRecordingFlybackPlanes.getValue()).toString());
        if (cbRecordingScanMode.getSelectedIndex() == 0) {
            template = template.replaceAll("\\{\\{recording_scan_mode\\}\\}", "bidirectional");
        } else if (cbRecordingScanMode.getSelectedIndex() == 1) {
            template = template.replaceAll("\\{\\{recording_scan_mode\\}\\}", "scan-up");
        } else if (cbRecordingScanMode.getSelectedIndex() == 2) {
            template = template.replaceAll("\\{\\{recording_scan_mode\\}\\}", "scan-down");
        }
        template = template.replaceAll("\\{\\{recording_description\\}\\}", taRecordingDescription.getText());

        //Experiment
        template = template.replaceAll("\\{\\{experiment_description\\}\\}", taExperimentDescription.getText());
        //Processing
        return template;
    }

    private JsonObject sampleJson() {
        JsonObject sampleJo = new JsonObject();
        sampleJo.addProperty("dpf", (Integer) spSampleDpf.getValue());
        sampleJo.addProperty("id", (Integer) spSampleId.getValue());
        sampleJo.addProperty("expression", tfSampleExpression.getText());
        sampleJo.addProperty("description", taSampleDescription.getText());
        return sampleJo;
    }

    private JsonObject recordingJson() {
        JsonObject recordingJo = new JsonObject();
        JsonObject volumeJo = new JsonObject();
        volumeJo.addProperty("start", Double.parseDouble(tfRecordingVolumeStart.getText()));
        volumeJo.addProperty("end", Double.parseDouble(tfRecordingVolumeStart.getText()));
        volumeJo.addProperty("planes", (Integer) spRecordingVolumePlanes.getValue());
        volumeJo.addProperty("flyback", (Integer) spRecordingFlybackPlanes.getValue());
        volumeJo.addProperty("n", Integer.parseInt(lNFrames.getText()));
        recordingJo.add("volume", volumeJo);
        recordingJo.addProperty("name", tfRecordingName.getText());
        recordingJo.addProperty("description", taRecordingDescription.getText());
        recordingJo.addProperty("fps", Double.parseDouble(tfRecordingFps.getText()));
        recordingJo.addProperty("frames", Integer.parseInt(lNFrames.getText()));
        if (cbRecordingScanMode.getSelectedIndex() == 0) {
            recordingJo.addProperty("scanMode", "bidirectional");
        } else if (cbRecordingScanMode.getSelectedIndex() == 1) {
            recordingJo.addProperty("scanMode", "scanUp");
        } else if (cbRecordingScanMode.getSelectedIndex() == 2) {
            recordingJo.addProperty("scanMode", "scanDown");
        }
        return recordingJo;
    }

    private JsonObject experimentJson() {
        JsonObject experimentJo = new JsonObject();
        experimentJo.addProperty("description", taExperimentDescription.getText());
        JsonArray ja = new JsonArray();
        for (String tag : tfExperimentTags.getText().split(",")) {
            if (tag.length() != 0) {
                ja.add(new JsonPrimitive(tag));
            }
        }
        experimentJo.add("tags", ja);
        experimentJo.addProperty("type", tfExperimentType.getText());
        return experimentJo;
    }

    private JsonObject processingJson(LocalDateTime ldt) {
        JsonObject processingJo = new JsonObject();
        List<JsonObject> processingChain = new ArrayList<JsonObject>();
        JsonObject actionRename = new JsonObject();
        actionRename.addProperty("action", "rename");
        actionRename.addProperty("scratch", cbProcessingRenameScratch.isSelected());
        actionRename.addProperty("done", false);
        {
            JsonObject paramsJo = new JsonObject();
            if (rbProcessingRenameMove.isSelected()) {
                paramsJo.addProperty("method", "move");
            } else {
                paramsJo.addProperty("method", "copy");
            }
            paramsJo.addProperty("targetDir", buildString(tfProcessingRenameDir.getText(), ldt));
            actionRename.add("parameters", paramsJo);
        }
        processingChain.add(actionRename);
        JsonObject actionDownscale = new JsonObject();
        actionDownscale.addProperty("action", "downscale");
        actionRename.addProperty("scratch", cbProcessingDownscaleScratch.isSelected());
        actionDownscale.addProperty("done", false);
        {
            JsonObject paramsJo = new JsonObject();
            paramsJo.addProperty("strip", cbProcessingDownscaleStrip.isSelected());
            paramsJo.addProperty("scale", tfProcessingDownscaleScale.getText());
            actionDownscale.add("parameters", paramsJo);
        }
        processingChain.add(actionDownscale);
        if (Double.parseDouble(tfRecordingVolumeStart.getText()) == Double.parseDouble(tfRecordingVolumeStart.getText())) {
            JsonObject actionRegister = new JsonObject();
            actionRegister.addProperty("action", "register-plane");
            actionRename.addProperty("scratch", cbProcessingRegistrationScratch.isSelected());
            actionRegister.addProperty("done", false);
            {
                JsonObject paramsJo = new JsonObject();
                actionRegister.add("parameters", paramsJo);
            }
            processingChain.add(actionRegister);
            JsonObject actionPublishYt = new JsonObject();
            actionPublishYt.addProperty("action", "publish-yt");
            actionRename.addProperty("scratch", cbProcessingPublishDfofScratch.isSelected());
            actionPublishYt.addProperty("done", false);
            {
                JsonObject paramsJo = new JsonObject();
                paramsJo.addProperty("name", buildString(tfProcessingPublishDfofName.getText(), ldt));
                paramsJo.addProperty("description", buildString(taProcessingPublishDfofDesc.getText(), ldt));
                actionPublishYt.add("parameters", paramsJo);
            }
            processingChain.add(actionPublishYt);
            JsonObject actionProcess = new JsonObject();
            actionProcess.addProperty("action", "process-plane");
            actionRename.addProperty("scratch", false);
            actionProcess.addProperty("done", false);
            {
                JsonObject paramsJo = new JsonObject();
                actionProcess.add("parameters", paramsJo);
            }
            processingChain.add(actionProcess);
        } else {
            JsonObject actionRegister = new JsonObject();
            actionRegister.addProperty("action", "register-volume");
            actionRename.addProperty("scratch", cbProcessingRegistrationScratch.isSelected());
            actionRegister.addProperty("done", false);
            {
                JsonObject paramsJo = new JsonObject();
                actionRegister.add("parameters", paramsJo);
            }
            processingChain.add(actionRegister);
            JsonObject actionPublishYt = new JsonObject();
            actionPublishYt.addProperty("action", "publish-yt");
            actionRename.addProperty("scratch", cbProcessingPublishDfofScratch.isSelected());
            actionPublishYt.addProperty("done", false);
            {
                JsonObject paramsJo = new JsonObject();
                paramsJo.addProperty("name", buildString(tfProcessingPublishDfofName.getText(), ldt));
                paramsJo.addProperty("description", buildString(taProcessingPublishDfofDesc.getText(), ldt));
                actionPublishYt.add("parameters", paramsJo);
            }
            processingChain.add(actionPublishYt);
            JsonObject actionProcess = new JsonObject();
            actionProcess.addProperty("action", "process-volume");
            actionRename.addProperty("scratch", false);
            actionProcess.addProperty("done", false);
            {
                JsonObject paramsJo = new JsonObject();
                actionProcess.add("parameters", paramsJo);
            }
            processingChain.add(actionProcess);
        }
        processingJo.add("chain", gson.toJsonTree(processingChain.toArray()));
        return processingJo;
    }

    private void onOK() {
        LocalDateTime ldt = LocalDateTime.now(ZoneOffset.UTC);
        JsonObject sampleJo = sampleJson();
        JsonObject recordingJo = recordingJson();
        JsonObject experimentJo = experimentJson();
        JsonObject processingJo = processingJson(ldt);

        jo = new JsonObject();
        jo.addProperty("version", "1.0.1");
        jo.addProperty("id", (new ObjectId()).toString());
        jo.addProperty("datetime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt));
        jo.add("sample", sampleJo);
        jo.add("recording", recordingJo);
        jo.add("experiment", experimentJo);
        jo.add("processing", processingJo);
        //System.out.println((new GsonBuilder().setPrettyPrinting().create()).toJson(jo));
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(gson.toJson(jo));
        writer.close();
        dispose();
    }

    public JsonObject getJson() {
        return jo;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    public static void main(String[] args) {
        RecordingDetailsDialog dialog = new RecordingDetailsDialog(0, 0, 1, 1, 98.5, 0, 10000);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 12, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Sample Details");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("dpf:");
        panel4.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Expression:");
        panel4.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfSampleExpression = new JTextField();
        tfSampleExpression.setColumns(10);
        panel4.add(tfSampleExpression, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        taSampleDescription = new JTextArea();
        panel4.add(taSampleDescription, new GridConstraints(1, 2, 2, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Description:");
        panel4.add(label4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spSampleId = new JSpinner();
        panel4.add(spSampleId, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, -1), new Dimension(70, -1), new Dimension(70, -1), 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Sample ID:");
        panel4.add(label5, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        spSampleDpf = new JSpinner();
        panel4.add(spSampleDpf, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, -1), new Dimension(70, -1), new Dimension(70, -1), 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(6, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label6 = new JLabel();
        Font label6Font = this.$$$getFont$$$(null, Font.BOLD, 12, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setText("Recording Details");
        panel5.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel5.add(spacer3, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Volume End (µm):");
        panel5.add(label7, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Volume Start (µm):");
        panel5.add(label8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Volume Planes:");
        panel5.add(label9, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Flyback Planes:");
        panel5.add(label10, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spRecordingVolumePlanes = new JSpinner();
        panel5.add(spRecordingVolumePlanes, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, -1), new Dimension(70, -1), new Dimension(70, -1), 0, false));
        spRecordingFlybackPlanes = new JSpinner();
        panel5.add(spRecordingFlybackPlanes, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, -1), new Dimension(70, -1), new Dimension(70, -1), 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("FPS:");
        panel5.add(label11, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfRecordingFps = new JTextField();
        panel5.add(tfRecordingFps, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Description:");
        panel5.add(label12, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        taRecordingDescription = new JTextArea();
        taRecordingDescription.setToolTipText("Description:");
        panel5.add(taRecordingDescription, new GridConstraints(4, 1, 2, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        cbRecordingScanMode = new JComboBox();
        panel5.add(cbRecordingScanMode, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Scan Mode");
        panel5.add(label13, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("frames");
        panel5.add(label14, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lNFrames = new JLabel();
        lNFrames.setText("0");
        panel5.add(lNFrames, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfRecordingVolumeStart = new JTextField();
        panel5.add(tfRecordingVolumeStart, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        tfRecordingVolumeEnd = new JTextField();
        panel5.add(tfRecordingVolumeEnd, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        tfRecordingName = new JTextField();
        panel5.add(tfRecordingName, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Name:");
        panel5.add(label15, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label16 = new JLabel();
        Font label16Font = this.$$$getFont$$$(null, Font.BOLD, 12, label16.getFont());
        if (label16Font != null) label16.setFont(label16Font);
        label16.setText("Experiment Details");
        panel6.add(label16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        taExperimentDescription = new JTextArea();
        panel6.add(taExperimentDescription, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Type:");
        panel6.add(label17, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfExperimentType = new JTextField();
        panel6.add(tfExperimentType, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(11, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel7, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label18 = new JLabel();
        Font label18Font = this.$$$getFont$$$(null, Font.BOLD, 12, label18.getFont());
        if (label18Font != null) label18.setFont(label18Font);
        label18.setText("Processing Details");
        panel7.add(label18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Downscale");
        panel7.add(label19, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbProcessingRenameCopy = new JRadioButton();
        rbProcessingRenameCopy.setSelected(true);
        rbProcessingRenameCopy.setText("Copy");
        panel7.add(rbProcessingRenameCopy, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbProcessingRenameMove = new JRadioButton();
        rbProcessingRenameMove.setText("Move");
        panel7.add(rbProcessingRenameMove, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel7.add(spacer5, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Method:");
        panel7.add(label20, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label21 = new JLabel();
        label21.setText("Directory:");
        panel7.add(label21, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfProcessingRenameDir = new JTextField();
        tfProcessingRenameDir.setText("/{{date}}/sample_{{sample_id}}/{{recording_type}}_{{time_dir}}");
        panel7.add(tfProcessingRenameDir, new GridConstraints(3, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label22 = new JLabel();
        label22.setText("Rename");
        panel7.add(label22, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbProcessingRenameScratch = new JCheckBox();
        cbProcessingRenameScratch.setText("To scratch");
        panel7.add(cbProcessingRenameScratch, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbProcessingDownscaleScratch = new JCheckBox();
        cbProcessingDownscaleScratch.setText("To scratch");
        panel7.add(cbProcessingDownscaleScratch, new GridConstraints(4, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label23 = new JLabel();
        label23.setText("Scale:");
        panel7.add(label23, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfProcessingDownscaleScale = new JTextField();
        tfProcessingDownscaleScale.setText("50%");
        panel7.add(tfProcessingDownscaleScale, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final JLabel label24 = new JLabel();
        label24.setText("Registration");
        panel7.add(label24, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbProcessingRegistrationScratch = new JCheckBox();
        cbProcessingRegistrationScratch.setText("To scratch");
        panel7.add(cbProcessingRegistrationScratch, new GridConstraints(7, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label25 = new JLabel();
        label25.setText("Publish DFoF");
        panel7.add(label25, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbProcessingPublishDfofScratch = new JCheckBox();
        cbProcessingPublishDfofScratch.setEnabled(false);
        cbProcessingPublishDfofScratch.setSelected(true);
        cbProcessingPublishDfofScratch.setText("To scratch");
        panel7.add(cbProcessingPublishDfofScratch, new GridConstraints(8, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label26 = new JLabel();
        label26.setText("Name:");
        panel7.add(label26, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfProcessingPublishDfofName = new JTextField();
        tfProcessingPublishDfofName.setText("{{date}} {{time}} - Sample {{sample_id}} - {{recording_name}} ({{recording_type}})");
        panel7.add(tfProcessingPublishDfofName, new GridConstraints(9, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label27 = new JLabel();
        label27.setText("Description:");
        panel7.add(label27, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel7.add(scrollPane1, new GridConstraints(10, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        taProcessingPublishDfofDesc = new JTextArea();
        taProcessingPublishDfofDesc.setText("Name: {{recording_name}}\nDate: {{date}} {{time}}\n\nSample: \nExpression: {{sample_expression}}\ndpf: {{sample_dpf}}\nID: {{sample_id}}\n\nRecording:\nVolume Start: {{recording_vol_start}}\nVolume End: {{recording_vol_end}}\nVolume Planes: {{recording_vol_planes}}\nVolume Planes: {{recording_flyback_planes}}\nFrames: {{recording_frames}}@{{recording_fps}}Hz\nScan Mode: {{recording_scan_mode}}");
        scrollPane1.setViewportView(taProcessingPublishDfofDesc);
        cbProcessingDownscaleStrip = new JCheckBox();
        cbProcessingDownscaleStrip.setSelected(true);
        cbProcessingDownscaleStrip.setText("Strip meta information");
        panel7.add(cbProcessingDownscaleStrip, new GridConstraints(5, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label28 = new JLabel();
        label28.setText("Tags:");
        panel3.add(label28, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfExperimentTags = new JTextField();
        panel3.add(tfExperimentTags, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
