package uSPIMmm;

import uSPIMmm.gui.DataAcquisition;
import com.google.gson.*;
import mmcorej.StrVector;
import org.micromanager.api.SequenceSettings;
import org.micromanager.utils.MMScriptException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataAcquisitionHandler {
    private SharedConfiguration scfg;
    private GUI gui;
    private DataAcquisition daqi;

    public static class Payload {
        public double width1;
        public double width2;
        public double framerate;
        public int nVolumes;
        public int nPlanes;
        public int nReturn;
        public double zStart;
        public double zEnd;
        public int scanMode;
        public String masks1;
        public String masks2;
        public double galvoDelay1;
        public double galvoDelay2;
        public boolean isAdaptation;
        public JsonObject metaJo;
    }

    public DataAcquisitionHandler(GUI gui, SharedConfiguration scfg, DataAcquisition daqi) {
        this.scfg = scfg;
        this.gui = gui;
        this.daqi = daqi;
    }

    public void singleAcquisition(JsonObject metaJo) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                List<Payload> ps = new ArrayList<>();
                Payload p = new Payload();
                try {
                    p.metaJo = metaJo;

                    p.width1 = Double.parseDouble(daqi.tfNmWidthLightSheet1.getText());
                    p.width2 = Double.parseDouble(daqi.tfNmWidthLightSheet2.getText());
                    try {
                        p.framerate = 1000.0 / scfg.getMMCore().getExposure();
                    } catch (Exception e1) {
                        gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e1.getMessage());
                        e1.printStackTrace();
                    }
                    p.nVolumes = Integer.parseInt(daqi.tfNmNVolumes.getText());
                    p.nPlanes = (Integer) daqi.spNmNPlanes.getValue();
                    p.nReturn = (Integer) daqi.spNmNReturn.getValue();
                    p.zStart = Double.parseDouble(daqi.tfNmZStart.getText());
                    p.zEnd = Double.parseDouble(daqi.tfNmZEnd.getText());
                    p.scanMode = daqi.cbNmScanMode.getSelectedIndex();
                    p.framerate = scfg.getFramerate();
                    p.galvoDelay1 = scfg.getGalvoDelay1();
                    p.galvoDelay2 = scfg.getGalvoDelay2();
                    p.masks1 = scfg.getMasks1();
                    p.masks2 = scfg.getMasks2();
                    p.isAdaptation = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e.getMessage());
                    return;
                }

                if (daqi.cbNmAdaptationPhase.isSelected()) { //We have to run adaptation phase
                    Payload pa = new Payload();
                    pa.width1 = Double.parseDouble(daqi.tfNmWidthLightSheet1.getText());
                    pa.width2 = Double.parseDouble(daqi.tfNmWidthLightSheet2.getText());
                    try {
                        pa.framerate = 1000.0 / scfg.getMMCore().getExposure();
                    } catch (Exception e1) {
                        gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e1.getMessage());
                        e1.printStackTrace();
                    }
                    pa.nPlanes = 1;
                    pa.nReturn = 0;
                    pa.zStart = 0;
                    pa.zEnd = 0;
                    pa.scanMode = daqi.cbNmScanMode.getSelectedIndex();
                    pa.framerate = scfg.getFramerate();
                    pa.nVolumes = 1;
                    pa.galvoDelay1 = scfg.getGalvoDelay1();
                    pa.galvoDelay2 = scfg.getGalvoDelay2();
                    pa.masks1 = scfg.getMasks1();
                    pa.masks2 = scfg.getMasks2();
                    pa.isAdaptation = true;
                    pa.metaJo = null;
                    ps.add(pa);
                }
                ps.add(p);

                //TODO this should run on separate thread
                try {
                    disableCamera();
                    gui.cd.queryAsyncPause();
                    Thread.sleep(500);
                    for (Payload pi : ps) {
                        setText(pi);
                        acquire(pi);
                    }
                    scfg.getMMCore().setShutterOpen(false);
                    scfg.getMMCore().setAutoShutter(true);
                } catch (InterruptedException e) {
                    gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        });
        th.start();

    }

    public void setText(Payload p) {
        daqi.tfNmNVolumes.setText(Integer.toString(p.nVolumes));
        daqi.spNmNPlanes.setValue(new Integer(p.nPlanes));
        daqi.spNmNReturn.setValue(new Integer(p.nReturn));
        scfg.setSheetWidth1(p.width1);
        scfg.setSheetWidth2(p.width2);
        daqi.tfNmFramerate.setText(Double.toString(p.framerate));
        daqi.tfNmZStart.setText(Double.toString(p.zStart));
        daqi.tfNmZEnd.setText(Double.toString(p.zEnd));
        daqi.cbNmScanMode.setSelectedIndex(p.scanMode);
        scfg.setMasks1(p.masks1);
        scfg.setMasks2(p.masks2);
        scfg.setGalvoDelay1(p.galvoDelay1);
        scfg.setGalvoDelay1(p.galvoDelay2);
        gui.tpMain.repaint();
    }

    public void seqAcquisition(List<Payload> payloads) {


        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    disableCamera();
                    scfg.getMMCore().setAutoShutter(false);
                    scfg.getMMCore().setShutterOpen(true);
                    Thread.sleep(500);
                } catch (Exception e) {
                    gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e.getMessage());
                    e.printStackTrace();
                }
                for (Payload pi : payloads) {
                    System.out.print("PREPARING...");
                    if (!pi.isAdaptation) {
                        Payload prep = new Payload();
                        prep.isAdaptation = true;
                        prep.masks1 = pi.masks1;
                        prep.masks2 = pi.masks2;
                        prep.galvoDelay1 = pi.galvoDelay1;
                        prep.galvoDelay2 = pi.galvoDelay2;
                        prep.width1 = pi.width1;
                        prep.width2 = pi.width2;
                        prep.framerate = pi.framerate;
                        prep.nPlanes = 1;
                        prep.nReturn = 0;
                        prep.nVolumes = (int)(2+(1000.0)/pi.framerate);
                        prep.zStart = pi.zStart;
                        prep.zEnd = pi.zStart;
                        prep.scanMode = 2;
                        prep.metaJo = null;
                        setText(prep);
                        acquire(prep);
                    }
                    System.out.print("RUNNING...");

                    setText(pi);
                    acquire(pi);
                    while (scfg.getMMSf().isAcquisitionRunning()) {
                        try {
                            Thread.sleep(300);
                            System.out.print("WAITING");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                    scfg.getMMCore().setShutterOpen(false);
                    scfg.getMMCore().setAutoShutter(true);
                } catch (Exception e) {
                    gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    private void acquire(Payload p) {
        if (scfg.getMMCore() != null) {
            try {
                setAcquisitionState();

                scfg.setFramerate(p.framerate);
                scfg.setSheetWidth1(p.width1);
                scfg.setSheetWidth2(p.width2);
                if (daqi.recalcluateInfo() < 0) {
                    return;
                }
                if (scfg.getMMSf().isAcquisitionRunning()) {
                    gui.writeStatus("You appear to be running acquisition already, acquisition auto-start will not be executed.");
                } else if (p.isAdaptation) { //Adaptation
                    scfg.getMMCore().setAutoShutter(false);
                    scfg.getMMCore().setShutterOpen(true);
                    gui.writeStatus("Starting Adaptation Phase.");

                    if (!gui.cd.isConnected()) {
                        try {
                            gui.writeStatus("ERROR: TCP Socket connection to Control Engine is closed.");
                            gui.cd = new CommandDispatcher("localhost", gui);
                            if (!gui.cd.isConnected()) {
                                return;
                            } else {
                                gui.writeStatus("TCP Socket connection to Control Engine reopened, try rerunning the command.");
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    JsonObject req = new JsonObject();
                    JsonObject nm = daqi.getNormalModeCommandJson(true);
                    req.addProperty("Mode", "NormalMode");
                    req.add("NormalMode", nm);
                    req.add("Settings", gui.configuration.getSettingsJson());
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                    String request = gson.toJson(req);
                    gui.cd.dispatchCommand(request + "\n");
                    long sleeptime = (p.nVolumes == 1) ? ((long) (Double.parseDouble(daqi.tfNmAdaptationPhaseDuration.getText()) * 60000)) : ((long)(p.nVolumes*(p.nPlanes+p.nReturn)*p.framerate));
                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() - start < sleeptime) {
                        Thread.sleep(100);
                    }
                    gui.writeStatus("Adaptation Finished");
                } else { //Acquisition
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            acquisitionSlave(p);
                        }
                    });

                    th.start();

                    gui.writeStatus("MICROMANAGER: AutoAq starting... delaying illumination by 3 seconds.");
                    Thread.sleep(3000);

                    if (!scfg.getMMSf().isAcquisitionRunning()) {
                        gui.writeStatus("WARNING: Acquisition is still not running. Starting illumination anyways.");
                    } else {
                        gui.writeStatus("Acquisition started, starting illumination.");
                    }


                    if (!gui.cd.isConnected()) {
                        try {
                            gui.writeStatus("ERROR: TCP Socket connection to Control Engine is closed.");
                            gui.cd = new CommandDispatcher("localhost", gui);
                            if (!gui.cd.isConnected()) {
                                return;
                            } else {
                                gui.writeStatus("TCP Socket connection to Control Engine reopened, try rerunning the command.");
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    JsonObject req = new JsonObject();
                    JsonObject nm = daqi.getNormalModeCommandJson(false);
                    req.addProperty("Mode", "NormalMode");
                    req.add("NormalMode", nm);
                    req.add("Settings", gui.configuration.getSettingsJson());
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                    String request = gson.toJson(req);
                    gui.cd.dispatchCommand(request + "\n");
                    th.join();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                gui.writeStatus("ERROR: Problem setting MicroManager configuration: " + e1.getMessage());
                return;
            }
        }


    }


    private void disableCamera() {
        boolean cameraOnline = scfg.getMMSf().isLiveModeOn();
        scfg.getMMSf().enableLiveMode(false);
    }

    private void setAcquisitionState() throws Exception {
        if (scfg.isPropertyControl()) {
            StrVector configs = scfg.getMMCore().getAvailableConfigGroups();
            for (int i = 0; i < configs.size(); i++) {
                String property = configs.get(i);
                String value = scfg.getMMCore().getCurrentConfig(configs.get(i));
                if (property.equals("CameraTrigger") && !value.equals("External")) {
                    scfg.getMMCore().setConfig("CameraTrigger", "External");
                } else if (property.equals("ScanMode") && !value.equals("Normal")) {
                    scfg.getMMCore().setConfig("ScanMode", "Normal");
                } else if (property.equals("VisibleLaserFront") && !value.equals("On")) {
                    scfg.getMMCore().setConfig("VisibleLaserFront", "On");
                } else if (property.equals("VisibleLaserSide") && !value.equals("On")) {
                    scfg.getMMCore().setConfig("VisibleLaserSide", "On");
                }
            }
        }
    }

    private void acquisitionSlave(Payload p) {
        try {
            JsonObject req = new JsonObject();
            JsonObject nm = daqi.getNormalModeCommandJson(false);
            req.addProperty("Mode", "NormalMode");
            req.add("NormalMode", nm);
            req.add("Settings", gui.configuration.getSettingsJson());
            Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            SequenceSettings ss = new SequenceSettings();
            ss.numFrames = Integer.parseInt(daqi.tfNmNVolumes.getText()) * ((Integer) daqi.spNmNPlanes.getValue() + (Integer) daqi.spNmNReturn.getValue());
            ss.intervalMs = 0;
            ss.comment = gson.toJson(req);
            scfg.getMMSf().setAcquisitionSettings(ss);
            String aqDir = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss").format(new Date());
            //gui.writeStatus("AQ dir:" + aqDir[0] + ", root dir: " + aqRootDir);
            scfg.getMMSf().runAcquisition(aqDir, scfg.getFastAqDir());
            aqDir = scfg.getFastAqDir() + "/" + aqDir + "_1";
            //aqDir[0] = aqRootDir + scfg.getMMSf().runAcquisition();

            gui.writeStatus("Waiting 5 seconds for writes to finish. Generating meta file for:" + aqDir);
            Thread.sleep(7000);

            writeMetaFile(p, aqDir);


        } catch (MMScriptException e1) {
            gui.writeStatus(e1.getStackTrace().toString());
        } catch (Exception e2) {
            gui.writeStatus(e2.getMessage());
            e2.printStackTrace();
        }
    }

    private void writeMetaFile(Payload p, String aqDir) {

        File f = new File(aqDir);

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };

        List<String> tifFilenames = new ArrayList<String>();
        List<JsonObject> aqFrames = new ArrayList<JsonObject>();
        File[] files = f.listFiles(textFilter);
        for (File file : files) {
            if (!file.isDirectory()) try {
                InputStream is = new FileInputStream(file);
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = buf.readLine();
                }
                String jstr = sb.toString();
                String mmMetaFilename = file.getCanonicalPath();
                Gson gsn = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                JsonObject jo = gsn.fromJson(jstr, JsonObject.class);

                Set<Map.Entry<String, JsonElement>> entries = jo.entrySet();
                for (Map.Entry<String, JsonElement> entry : entries) {
                    String key = entry.getKey();
                    if (!key.equals("Summary")) {
                        boolean hasFilename = entry.getValue().getAsJsonObject().has("FileName");
                        boolean hasId = entry.getValue().getAsJsonObject().has("ImageNumber");
                        if (hasFilename && hasId) {
                            String tifFilename = entry.getValue().getAsJsonObject().get("FileName").getAsString();
                            int frameId = entry.getValue().getAsJsonObject().get("ImageNumber").getAsInt();
                            if (!tifFilenames.contains(tifFilename)) {
                                tifFilenames.add(tifFilename);
                            }
                            JsonObject frameInfo = new JsonObject();
                            frameInfo.addProperty("frameId", frameId);
                            frameInfo.addProperty("file", tifFilename);
                            aqFrames.add(frameInfo);
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        Gson gson2 = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        JsonObject jo = p.metaJo;
        jo.get("recording").getAsJsonObject().add("frames", gson2.toJsonTree(aqFrames.toArray()));
        jo.get("recording").getAsJsonObject().add("files", gson2.toJsonTree(tifFilenames.toArray()));

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(aqDir + "\\acquisition.meta.json", "UTF-8");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        writer.print(gson2.toJson(jo));
        writer.close();
    }
}
