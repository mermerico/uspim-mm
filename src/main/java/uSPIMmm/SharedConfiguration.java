package uSPIMmm;

import mmcorej.CMMCore;
import org.micromanager.api.ScriptInterface;

import java.util.Observable;

public class SharedConfiguration extends Observable {
    private double framerate = 100.00;
    private double galvoDelay1 = 0.0;
    private double galvoDelay2 = 0.0;
    private double sheetWidth1 = 100.0;
    private double sheetWidth2 = 100.0;
    private double homeOffset = 0.0;
    private boolean propertyControl = true;
    private String fastAqDir = "./";
    private String masks1 = "";
    private String masks2 = "";

    public CMMCore MMCore;
    public ScriptInterface MMSf;


    //SETTERS
    public void setGalvoDelay1(double galvoDelayl) {
        this.galvoDelay1 = galvoDelayl;
        setChanged();
        notifyObservers();
    }

    public void setGalvoDelay2(double galvoDelay2) {
        this.galvoDelay2 = galvoDelay2;
        setChanged();
        notifyObservers();
    }
    public void setFramerate(double framerate) {
        this.framerate = framerate;
        setChanged();
        notifyObservers();
    }

    public void setPropertyControl(boolean propertyControl) {
        this.propertyControl = propertyControl;
        setChanged();
        notifyObservers();
    }

    public void setMMCore(CMMCore MMCore) {
        this.MMCore = MMCore;
        setChanged();
        notifyObservers();
    }

    public void setMMSf(ScriptInterface MMSf) {
        this.MMSf = MMSf;
        setChanged();
        notifyObservers();
    }

    public void setSheetWidth1(double sheetWidth1) {
        this.sheetWidth1 = sheetWidth1;
        setChanged();
        notifyObservers();
    }

    public void setSheetWidth2(double sheetWidth2) {
        this.sheetWidth2 = sheetWidth2;
        setChanged();
        notifyObservers();
    }

    public void setHomeOffset(double homeOffset) {
        this.homeOffset = homeOffset;
        setChanged();
        notifyObservers();
    }

    public void setFastAqDir(String fastAqDir) {
        this.fastAqDir = fastAqDir;
        setChanged();
        notifyObservers();
    }

    public void setMasks1(String masks1) {
        this.masks1 = masks1;
        setChanged();
        notifyObservers();
    }

    public void setMasks2(String masks2) {
        this.masks2 = masks2;
        setChanged();
        notifyObservers();
    }

    //GETTERS
    public double getFramerate() {
        return this.framerate;
    }

    public double getGalvoDelay1() {
        return galvoDelay1;
    }

    public double getGalvoDelay2() {
        return galvoDelay2;
    }

    public boolean isPropertyControl() {
        return propertyControl;
    }

    public double getSheetWidth1() {
        return sheetWidth1;
    }


    public CMMCore getMMCore() {
        return MMCore;
    }

    public ScriptInterface getMMSf() {
        return MMSf;
    }
    public double getSheetWidth2() {
        return sheetWidth2;
    }

    public double getHomeOffset() {
        return homeOffset;
    }

    public String getFastAqDir() {
        return fastAqDir;
    }

    public String getMasks1() {
        return masks1;
    }

    public String getMasks2() {
        return masks2;
    }
}
