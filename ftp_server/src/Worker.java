/**
 * Created by Kent Sinclair and Evan Stedman on 10/10/16.
 */
import java.io.*;
import java.net.*;

final class Worker implements Runnable{
    private static final int BUFFERSIZE = 1024;
    private static final int COMMANDLENGTH = 4;

    int dataPort = 5544;
    int messageSize = 0;
    byte[] byteBuffer = new byte[BUFFERSIZE];
    byte[] cmdBuffer = new byte[BUFFERSIZE];
    String secondArg = ("");
    FileOutputStream fos = null;
    FileInputStream fis = null;

    Socket controlConn;

    public Worker(Socket socket) throws Exception{
        this.controlConn = socket;
    }

    // implementing runnable interface
    public void run(){
        try{
            handleProcess();
        } catch (Exception e){
            System.out.println(e);
        }
    }
    private void handleProcess() throws Exception{
        InputStream inFromClient_Control = controlConn.getInputStream();
        OutputStream outToClient_Control = controlConn.getOutputStream();


        System.out.println("*** Accept Client Request ***");

        while ((messageSize = inFromClient_Control.read(byteBuffer)) != -1) {

            String cmdReceived = new String(byteBuffer,0,COMMANDLENGTH);
            if(cmdReceived.equals("stor") || cmdReceived.equals("retr")) {
                secondArg = new String(byteBuffer, COMMANDLENGTH, messageSize);

            }

            Socket dataConnection = new Socket(controlConn.getInetAddress(), dataPort);
            System.out.println("Preparing to open connection on port" + dataPort);

            InputStream inFromClient_Data = dataConnection.getInputStream();
            OutputStream outToClient_Data = dataConnection.getOutputStream();
            System.out.println("Connection established\n");

            if (cmdReceived.toLowerCase().equals("list")) {

                System.out.println("Processing " + cmdReceived + " command\n");
                File directory = new File(".");
                File[] listOfFiles = directory.listFiles();
                String fileName = new String("");

                for (int i = 0; i < listOfFiles.length; i++) {
                    // get File name
                    if (listOfFiles[i].isFile()) {
                        fileName = listOfFiles[i].getName();
                    } else if (listOfFiles[i].isDirectory()) {
                        fileName = listOfFiles[i].getName()+"\n";
                    }

                    outToClient_Data.write(fileName.getBytes());
                }
                outToClient_Data.close();

            } else if (cmdReceived.toLowerCase().equals("retr")) {

                System.out.println("Processing " + cmdReceived + " command");
                String newFile = secondArg.trim();
                File fileToRetr = new File(newFile);
                System.out.println("file to retrieve: "+fileToRetr);

                if (fileToRetr.exists()) {
                    try {
                        messageSize = 0;
                        fis = new FileInputStream(fileToRetr);
                        byteBuffer = new byte[BUFFERSIZE];
                        while ((messageSize = fis.read(byteBuffer)) != -1) {
                            outToClient_Data.write(byteBuffer, 0, messageSize);
                        }
                    } catch (Exception e) {
                        System.out.println("Error sending file to store");
                    }
                } else {
                    String errMsg = new String("File Not Found!\n");
                    outToClient_Data.write( errMsg.getBytes() );
                }

            } else if (cmdReceived.toLowerCase().equals("stor")) {
                System.out.println("Processing " + cmdReceived + " command");
                messageSize = 0;
                try {
                    System.out.println(secondArg);
                    String newFile = secondArg.trim();
                    fos = new FileOutputStream(new File(newFile));

                    while ((messageSize = inFromClient_Data.read(byteBuffer)) != 1) {
                        fos.write(byteBuffer, 0, messageSize);
                        break;
                    }

                    System.out.println("File saved successfully");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            // outputStream.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (cmdReceived.toLowerCase().equals("quit")) {

                System.out.println("Preparing to " + cmdReceived);

                dataConnection.close();
                System.out.println("Data Socket Closed\n");

                controlConn.close();
                System.out.println("Comm Socket Closed");

                System.exit(0);
            }

            dataConnection.close();
            System.out.println("");
        }
    }
}