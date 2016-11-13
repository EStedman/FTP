import java.io.*;
import java.net.*;
import java.util.*;

import java.lang.*;

public class FTPClient {
	Socket socket = null;
	Socket dataSocket = null;
	Socket mySocket = null;
	DataInputStream dataIn, peerDataIn;
	DataOutputStream dataOut, peerDataOut;
	String myHost;
	String[] result = { "", "", "", "", "", "", "" };
	int portNumber;
	// FTPServer server;

	public FTPClient(int portNum) throws IOException {
		portNumber = portNum;
		System.out.println("" + portNumber);

	}

	private void otherClientsConnections() throws IOException {
		ServerSocket serverListenSocket = new ServerSocket(portNumber);
		ServerSocket dataListenSocket = new ServerSocket();

		while (!socket.isClosed()) {
			Socket clientDataSocket = serverListenSocket.accept();
			System.out.println("Connection recieved");
			DataInputStream in = new DataInputStream(clientDataSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientDataSocket.getOutputStream());
			String file = in.readLine();
			System.out.println(file);
			File f = new File(System.getProperty("user.dir") + file);

			if (f.exists() && !f.isDirectory()) {
				FileInputStream fis = null;
				fis = new FileInputStream(f);
				try {
					System.out.println("Sending File...");
					byte[] buffer = new byte[1024];
					int bytes = 0;
					while ((bytes = fis.read(buffer)) != -1) {
						System.out.println("Writing File...");
						out.write(buffer, 0, bytes);
					}
					fis.close();
					System.out.println("Sent File: " + f);
				} catch (Exception e) {
					System.out.println("ERROR in FILE TRANSFER.");
				}
			}
			if (!f.exists()) {
				System.out.println("Doesn't exist!");
			}

			ClientCommands("disconnect");
		}
		if (mySocket.isClosed()) {
			System.out.println("Ready to connect to a peer");
		}
	}

	public void ClientCommands(String command) throws IOException {
		if (command.length() != 0) {
			StringTokenizer cmdStatement = new StringTokenizer(command);
			String currentCMD = cmdStatement.nextToken();
			String peerIP;
			int peerPort;

			if (currentCMD.toLowerCase().equals("connect")) {
				try {
					peerIP = cmdStatement.nextToken();
					peerPort = Integer.parseInt(cmdStatement.nextToken());
					mySocket = new Socket(peerIP, peerPort);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (currentCMD.toLowerCase().equals("retr")) {
				if (mySocket.isConnected()) {

					peerDataOut = new DataOutputStream(mySocket.getOutputStream());
					peerDataIn = new DataInputStream(mySocket.getInputStream());
					currentCMD = cmdStatement.nextToken();
					FileOutputStream fos = null;
					File file = new File(currentCMD);
					fos = new FileOutputStream(file);
					System.out.println("Receiving File...");
					byte[] b = new byte[8096];
					peerDataOut.writeBytes(currentCMD + "\n");
					int amount_read = dataIn.read(b);

					System.out.println("Copying File...");

					fos.write(b, 0, amount_read);
				}
			}
			if (currentCMD.toLowerCase().equals(("dc")) || currentCMD.toLowerCase().equals("disconnect")) {
				mySocket.close();
				peerDataOut.close();
				peerDataIn.close();
				System.out.println("Connection closed");
			}
		}
	}

	public void connect(String hostName, int portNumber, String userName, String myIP, String speed) throws Exception {
		myHost = hostName;
		// Create socket that is connected to server on specified port
		socket = new Socket(hostName, portNumber);
		dataSocket = new Socket(hostName, 1079);

		dataOut = new DataOutputStream(dataSocket.getOutputStream());
		dataIn = new DataInputStream(dataSocket.getInputStream());
		System.out.println("Connecting to main server HUB." + "\nServer: " + hostName + "\nPort: " + 1079);
		System.out.println("The connection information for this machine is: " + socket.getLocalAddress() + "\nPort: "
				+ socket.getLocalPort());
		File f = new File(System.getProperty("user.dir") + "/filelist.xml");

		if (f.exists() && !f.isDirectory()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			try {
				dataOut.writeBytes(userName + "\n");
				dataOut.writeBytes(myIP + "\n");
				dataOut.writeBytes(portNumber + "\n");
				dataOut.writeBytes(speed + "\n");
				System.out.println("Sending File...");
				byte[] buffer = new byte[1024];
				int bytes = 0;
				while ((bytes = fis.read(buffer)) != -1) {
					System.out.println("Writing File...");
					dataOut.write(buffer, 0, bytes);
				}
				fis.close();
				System.out.println("Sent File: " + f);
				new Thread() {
					public void run() {
						try {
							otherClientsConnections();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
				new Thread() {
					public void run() {
						try {
							ClientCommands("");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();

			} catch (Exception e) {
				System.out.println("ERROR in FILE TRANSFER.");
			}
		}
		if (!f.exists()) {
			System.out.println("Doesn't exist!");
		}
		dataSocket.close();
	}

	public String getHostName() {
		return socket.getLocalAddress().toString();
	}

	public void search(String text) {
		ArrayList<userInformation> found = new ArrayList<userInformation>();
		if (!socket.isClosed()) {
			try {

				dataSocket = new Socket(myHost, 1079);

				dataOut = new DataOutputStream(dataSocket.getOutputStream());
				dataIn = new DataInputStream(dataSocket.getInputStream());
				// dataSocket = new Socket(myHost, 1079);
				if (text.equals(""))
					text = " ";
				dataOut.writeBytes(text + "\n");
				// dataOut.writeBytes(text+"\n");
				String size = dataIn.readLine();
				System.out.println("" + size);
				for (int i = 0; i < Integer.parseInt(size); i++) {
					result = dataIn.readLine().split("::");
					userInformation user = new userInformation(result[0], result[1], result[2], result[3], result[4],
							result[5]);
					System.out.println(result[4]);
					found.add(user);
				}
				dataSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error");
				System.out.println(e.getStackTrace());
			}
		}

	}

}