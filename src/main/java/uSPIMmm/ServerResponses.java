package uSPIMmm;

public interface ServerResponses {
    public void onStatusResponse(String msg);
    public void onCalibrationStatusResponse(String msg);
    public void onCalibrationOffsetResponse(String msg);
    public void onDaqStatus(String msg);
    public void onTaskDone();
}
