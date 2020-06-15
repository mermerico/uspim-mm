package uSPIMmm;

import java.io.*;
import java.net.Socket;

public class CommandDispatcher {
    private Listener listener = null;
    private Sender sender = null;
    public boolean connected = false;
    ServerResponses viewer = null;

    class Listener extends Thread {
        Socket conn = null;
        boolean listening = true;

        public Listener(Socket conn) {
            this.conn = conn;
            this.setName("JavaClientSocketListener");
            this.start();
        }

        @Override public void run() {

            InputStream instream = null;

            try {
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));


                while ( listening ) {
                    String xml = reader.readLine();
                    if ( xml == null ) {
                        // Connection lost
                        return;
                    }

                    //System.out.println("XML: " + xml);

                    // Hand off to the UI
                    if ( xml.indexOf("StatusResponse") != -1 ) {
                        viewer.onStatusResponse(xml.substring("StatusResponse".length()));
                    }
                    else if ( xml.indexOf("TASK_DONE") != -1 ) {
                        viewer.onTaskDone();
                    }
                    else if ( xml.indexOf("CalibrationStatus") != -1 ) {
                        viewer.onCalibrationStatusResponse(xml.substring("CalibraionStatus".length()+1));
                    }
                    else if ( xml.indexOf("CalibrationOffset") != -1 ) {
                        viewer.onCalibrationOffsetResponse(xml.substring("CalibraionOffset".length()+1));
                    }
                    else if ( xml.startsWith("DAQ")) { //DAQ status
                        viewer.onDaqStatus(xml);
                    }
                }
            }
            catch ( StreamCorruptedException sce) {
                // skip over the bad bytes
                try {
                    if ( instream != null )
                        instream.skip(instream.available());
                }
                catch ( Exception e1 ) {
                    listening = false;
                }
            }
            catch ( Exception e ) {
                //e.printStackTrace();
                listening = false;
            }
        }
    }

    class Sender {
        static final String REQ_QUERY_STATUS = "<Request><Name>QueryStatus</Name></Request>";
        static final String REQ_HALT = "ASYNChalt\n";
        static final String REQ_PAUSE = "ASYNCpause\n";

        Socket conn;
        BufferedOutputStream os = null;

        public Sender(Socket conn) {
            try {
                this.conn = conn;
                this.conn.setTcpNoDelay(true);
                this.os = new BufferedOutputStream( conn.getOutputStream() );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        public void queryStatus() {
            serializeAndSendMessage(REQ_QUERY_STATUS);
        }

        public void queryAsyncHalt() {
            serializeAndSendMessage(REQ_HALT);
        }

        public void queryAsyncPause() {
            serializeAndSendMessage(REQ_PAUSE);
        }

        public void dispatchCommand(String command) {
            serializeAndSendMessage(command);
        }

        private void serializeAndSendMessage(String msg) {
            try {
                os.write( msg.getBytes() );
                os.flush();
            }
            catch ( Exception e ) {
                connected = false;
                //e.printStackTrace();
            }
        }
    }

    public CommandDispatcher(String IPAddress, ServerResponses viewer) {
        try {
            // Connect to the server at the given address on port 8080
            if ( IPAddress == null || IPAddress.length() == 0 )
                IPAddress = "localhost";
            Socket conn = new Socket( IPAddress, 51337 );
            conn.setTcpNoDelay(true);
            this.listener = new Listener(conn);
            this.sender = new Sender(conn);
            this.connected = true;
            this.viewer = viewer;
        }
        catch ( Exception e ) {
            connected = false;
            //e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected && listener.listening;
    }

    public void queryStatus() {
        sender.queryStatus();
    }

    public void queryAsyncHalt() {
        sender.queryAsyncHalt();
    }

    public void queryAsyncPause() {
        sender.queryAsyncPause();
    }

    public void dispatchCommand(String command) {
        sender.dispatchCommand(command);
    }
}
