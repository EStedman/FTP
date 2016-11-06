/**
 * Created by kentkent on 10/11/16.
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ftp_client {

    // Initial file size
    public final static int BUFFER = 65536;

    public static void main(String[] args) throws IOException {
        InputStream fromServer = null;
        InputStream dataFromServer = null;
        OutputStream toServer = null;
        OutputStream dataToServer = null;
        Socket mainLink = null;
        Socket dataLink = null;
        ServerSocket server = null;
        BufferedReader userInput = null;
        String lineReader = null;
        String ip = null;
        String fileName = null;
        String fileOutName = null;
        String files = null;
        int size = 0;
        int mainPort = 5543;
        int retrievedData = 0;
        byte buff[] = null;
        File outFile = null;
        File fts = null;
        FileOutputStream fos = null;
        FileInputStream fis = null;

        //Command/loop variables
        boolean quit = false;
        String[] tokens = null;
        String command = null;
        boolean validCmd = false;
        boolean connectEstablish = false;

        System.out.println("--- Welcome to Kent and Evan's FTP Server! ---");
        System.out.println("***********************************************");
        System.out.println("**** usage: connect <ip> <port> ***************");

//        try {
        while(!quit){

            System.out.print("> ");
            userInput = new BufferedReader(new InputStreamReader(System.in));
            //userInput = new Scanner(System.in);
            lineReader = userInput.readLine();
            if(lineReader == null){
                System.out.println("Command Error, try entering another command!");
                return;
            }
            else{
                tokens = lineReader.split(" ");
                command = tokens[0].toLowerCase();

                if(tokens.length == 0 || tokens.length >= 4){
                    validCmd = false;
                }
                if (command.equals("connect") || command.equals("list") ||
                        command.equals("retr") || command.equals("stor") ||
                        command.equals("quit")) {
                        validCmd = true;
                }
                else{
                    validCmd = false;
                }

                if(!validCmd){
                    System.out.println("Invalid command, try another command");
                }
                else{
                    if(connectEstablish) {
                        if (!command.equals("quit")) {
                            if (command.equals("stor") || command.equals("retr")) {
                                files = tokens[1];
                                System.out.println("sending:" + files);
                                toServer.write((command + files).getBytes());
                            } else {
                                toServer.write(command.getBytes());
                            }

                            try {
                                server = new ServerSocket(mainPort + 1);
                                System.out.println("Data socket opened for command!\n");
                            } catch (IOException exception) {
                                System.out.println("Error opening data socket");
                            }

                            dataLink = server.accept();
                            dataFromServer = dataLink.getInputStream();
                            dataToServer = dataLink.getOutputStream();
                            buff = new byte[BUFFER];
                        }
                    }

                    switch (command){
                        case "connect":
                            ip = tokens[1];
                            mainPort = Integer.parseInt(tokens[2]);
                            System.out.println("connecting to " + ip + ":" + mainPort);
                            try {
                                mainLink = new Socket(ip, mainPort);
                                fromServer = mainLink.getInputStream();
                                toServer = mainLink.getOutputStream();
                                connectEstablish = true;
                            } catch (IOException e) {
                                System.out.println("Error opening socket");
                                connectEstablish = false;
                            }
                            break;
                        case "stor":
                            fileOutName = tokens[1];
                            fts = new File(fileOutName);
                            System.out.println("trying to store file");
                            // test this
                            if (fts.exists()) {
                                try {
                                    size = 0;
                                    fis = new FileInputStream(fts);
                                    buff = new byte[BUFFER];
                                    while ((size = fis.read(buff)) != -1) {
                                        dataToServer.write(buff, 0, size);
                                    }
                                } catch (Exception e) {
                                    System.out.println("Error sending file to store");
                                }
                                if (connectEstablish){
                                    server.close();
                                }
                            } else {
                                System.out.println("File Not Found!");
                                if (connectEstablish){
                                    server.close();
                                }
                            }
                            break;
                        case "retr":
                            fileName = tokens[1];
                            size = 0;
                            String savingFile = fileName.trim();
                            System.out.println("retrieving file: "+savingFile);
                            try {
                                fos = new FileOutputStream(new File(savingFile));

                                while ((size = dataFromServer.read(buff)) != 1) {
                                    fos.write(buff, 0, size);
                                    break;
                                }

                                System.out.println("File loaded successfully");

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
                            if (connectEstablish){
                                server.close();
                            }
                            break;
                        case "list":
                            while ((retrievedData = dataFromServer.read(buff)) != -1) {
                                System.out.println(new String(buff, 0, retrievedData));
                            }
                            if (connectEstablish){
                                server.close();
                            }
                            break;
                        case "quit":
                            System.out.println("Now quitting");
                            if(connectEstablish){
                                mainLink.close();
                                server.close();
                            }
                            System.out.println("Main connection closed!");
                            quit = true;
                            break;
                    }
                }
            }
        }
    }
}
