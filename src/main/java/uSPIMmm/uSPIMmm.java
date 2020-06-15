package uSPIMmm;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import mmcorej.CMMCore;
import org.micromanager.acquisition.AcquisitionEngine;
import org.micromanager.api.ScriptInterface;

public class uSPIMmm implements org.micromanager.api.MMPlugin{
    public static String menuName = "uSPIM Plugin";
    public static String tooltipDescription = "uSPIM Plugin";
    private CMMCore core;
    private ScriptInterface apper;
    private AcquisitionEngine acq_;
    private GUI gui;
    private int numFrames;
    private double exposureTime;


    protected JFrame frame;

    @Override
    public void dispose() {
      /*
       * you can put things that need to be run on shutdown here
       * note: if you launch a JDialog from the plugin using show(), shutdown of the dialog will not automatically call dispose()
       * You will need to add a call to dispose() from the formWindowClosing() method of your JDialog.
       */
    }

    @Override
    public void setApp(ScriptInterface app) {
        apper = app;
        numFrames = app.getAcquisitionSettings().numFrames;
        core = app.getMMCore();


        frame = new JFrame("uSPIM Plugin");
        gui = new GUI();
        gui.setMMCore(core);
        gui.setScriptInterface(app);
        frame.setContentPane(gui.getRoot());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();


    }

    @Override
    public void show() {

        numFrames = apper.getAcquisitionSettings().numFrames;
        try {
            exposureTime = core.getExposure(); //= apper.getChannelExposureTime("SPIMGroup", "SPIMPreset");
        } catch (Exception ex) {
            Logger.getLogger(uSPIMmm.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setVisible(true);
    }

    @Override
    public String getDescription() {
        return "Description:Control Engine";
    }

    @Override
    public String getInfo() {
        return "Info: Control Engine";
    }

    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public String getCopyright() {
        return "(C) 2015 Chris Buckley and Leon Lagnado, University of Sussex";
    }
}

