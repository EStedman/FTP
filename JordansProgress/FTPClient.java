import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class FTPClient {
	Socket socket;
	Socket dataSocket;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	// FTPServer server;

	public FTPClient() throws IOException {
		//socket = new Socket("127.0.0.1", 2597);
		String cmd = new String("");
		String[] arguments;
		int recvMsgSize; // Size of received message
		byte[] byteBuffer;

		// predefine these variables

	}

	public void connect(String hostName, int portNumber, String userName, String myIP, String speed) throws Exception {
		// Create socket that is connected to server on specified port
		socket = new Socket(hostName, portNumber);
		dataSocket = new Socket(hostName, 1079);
		
		dataOut = new DataOutputStream(dataSocket.getOutputStream());
		System.out.println("Connecting to main server HUB." + "\nServer: " + hostName + "\nPort: " + portNumber);
		File f = new File(System.getProperty("user.dir") + "/filelist.xml");
		
		if (f.exists() && !f.isDirectory()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			try {
				sendFile(fis, dataOut);
				fis.close();
				System.out.println("Sent File: " + f);

			} catch (Exception e) {
				System.out.println("ERROR in FILE TRANSFER.");
			}
		}
		if (!f.exists()) {
			System.out.println("Doesn't exist!");
		}
	}

	private static void sendFile(FileInputStream fis, DataOutputStream os) throws Exception {
		System.out.println("Sending File...");
		byte[] buffer = new byte[1024];
		int bytes = 0;

		while ((bytes = fis.read(buffer)) != -1) {
			System.out.println("Writing File...");
			os.write(buffer, 0, bytes);
		}
	}

	public String getHostName() {
		return socket.getLocalAddress().toString();
	}

}