import java.io.*;
import java.net.*;
import java.util.*;

import java.lang.*;

/**
 * 
 * @author Jordan Zomerlei , Evan Stedman 
 * FTPClient is the client side of the p2p network. Everybody
 *         connected to the CentralServer is a peer to each other.
 */
@SuppressWarnings("deprecation")
public class FTPClient {
	Socket socket = null;
	Socket dataSocket = null;
	Socket mySocket = null;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	DataOutputStream peerDataOut = null;
	DataInputStream peerDataIn = null;
	String myHost;
	String[] result = { "", "", "", "", "", "", "" };
	int GportNumber;
	ServerSocket serverListenSocket = null;

	ArrayList<userInformation> found;

	// FTPServer server;
	/**
	 * The GUI creates this object. The portNum is the port that is particular
	 * client is listening on.
	 * 
	 * @param portNum
	 * @throws IOException
	 */
	public FTPClient(int portNum) throws IOException {
		GportNumber = portNum;

	}

	/**
	 * otherClientConnections handles the incoming connection from other clients
	 * 
	 * @throws IOException
	 */

	@SuppressWarnings("resource")
	private void otherClientsConnections() throws IOException {
		// set up sockets and streams to use

		Socket clientDataSocket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		// While connected to main server....
		while (!socket.isClosed()) {
			if (clientDataSocket != null) {
				// If we are connected, do nothing because we're still
				// retrieving files from this person
			} else {
				// If we aren't connected, let's wait until some one attempts to
				// connect to us.
				try {
					clientDataSocket = serverListenSocket.accept();
					System.out.println("Connection recieved");
					in = new DataInputStream(clientDataSocket.getInputStream());
					out = new DataOutputStream(clientDataSocket.getOutputStream());
				} catch (Exception C) {
					socket.close();
				} finally {

				}
			}
			if (!socket.isClosed()) {
				// The next line is USUALLY the file name but sometimes the user
				// might be disconnecting from the peer
				String file = in.readLine();

				// If the user is immediately disconnecting, let's catch this so
				// we
				// don't try to talk with a socket that is closed
				if (("Disconnecting".equals(file))) {
					clientDataSocket = null;
					// socket.close();
				} else {
					// Since the user didn't disconnect, it must be a file being
					// sent.
					File f = new File(System.getProperty("user.dir") + "//" + file);
					if (f.exists() && !f.isDirectory()) {
						// System.out.println("Sending file:" + f);
						// System.out.println("This file is being sent to the
						// user
						// on: " + clientDataSocket.getInetAddress());
						try {
							// declare variables for converting file to byte[]
							FileInputStream fileInputStream = new FileInputStream(f);
							byte[] fileByteArray = new byte[(int) f.length()];

							// convert file
							fileInputStream.read(fileByteArray);
							fileInputStream.close();

							// write to client over DATA line
							out.write(fileByteArray);
							// System.out.println("File finished being sent to
							// the
							// user on: " + clientDataSocket.getInetAddress());

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// If the file doesn't let the user who is connected to the
					// peer
					// know
					if (!f.exists()) {
						out.writeBytes("Nope.avi");
						System.out.println("Doesn't exist!");
					}
				}
			}
		}
		if(mySocket != null)
		if (mySocket.isClosed()) {
			System.out.println("Ready to connect to a peer");
		}
	}

	@SuppressWarnings("null")
	public String ClientCommands(String command) throws IOException {
		String returnString = command;
		if (command.length() != 0) {
			returnString = ">>" + command + "\n";
			StringTokenizer cmdStatement = new StringTokenizer(command);
			String currentCMD = cmdStatement.nextToken();

			String peerIP;
			int peerPort;

			if (currentCMD.toLowerCase().equals("connect")) {
				// If command is connect lets try to connect to the peer that
				// we've selected
				try {
					// If the user is already connected but uses the connect
					// command again let the user know they're already connected
					// to a peer and should disconnect first
					if (mySocket == null) {
						returnString += "Connected to peer";
						peerIP = cmdStatement.nextToken();
						peerPort = Integer.parseInt(cmdStatement.nextToken());
						mySocket = new Socket(peerIP, peerPort);
						peerDataOut = new DataOutputStream(mySocket.getOutputStream());
						peerDataIn = new DataInputStream(mySocket.getInputStream());
					} else {
						if (mySocket.isClosed() == false) {
							returnString += ("Disconnect before connecting to a new client.");
						} else {
							peerIP = cmdStatement.nextToken();
							peerPort = Integer.parseInt(cmdStatement.nextToken());
							mySocket = new Socket(peerIP, peerPort);
							peerDataOut = new DataOutputStream(mySocket.getOutputStream());
							peerDataIn = new DataInputStream(mySocket.getInputStream());
							returnString += "Connected to peer";
						}
					}
				} catch (Exception e) {
					returnString += ("Failed to connect to peer, ensure they exist.");
				}
			} else if (currentCMD.toLowerCase().equals("retr")) {
				// If the command is retr let's try to retrieve the file listed
				// If the file doesn't exist, it will let the user know, if it
				// does exist it will place the file within a new directory.
				if (mySocket != null){
					if (mySocket.isConnected()) {
						int recvMsgSize;
						byte[] b = new byte[8096];
						currentCMD = cmdStatement.nextToken();
						// Make a directory for the downloaded files(Always runs,
						// (safe))
						new File(System.getProperty("user.dir") + "//DownloadedFiles//").mkdir();
						String fileRequestedString = new String(
								(System.getProperty("user.dir") + "//DownloadedFiles//" + currentCMD).getBytes());
						peerDataOut.writeBytes(currentCMD + "\n");
						recvMsgSize = peerDataIn.read(b);
						String FileMessage = new String(b, 0, recvMsgSize);
						// If the file doesn't exist on the peer, let the user know
						// that it does't exist.
						if (("Nope.avi".equals(FileMessage))) {
							returnString += ("File doesn't exist on this peer.\n");
						} else {
							// If the file DOES exist, receive and write the bytes
							// to a file.t
							try {

								File fileRequested = new File(fileRequestedString);
								FileOutputStream fileOutputStream = new FileOutputStream(fileRequested);
								fileOutputStream.write(b, 0, recvMsgSize);
								fileOutputStream.close();
								returnString += ("Wrote the file: " + fileRequestedString);
							} catch (Exception e) {
								returnString += ("Error trying to write file.");
							}
						}
					} else {
						// Not connected? Probably ought to connect kiddo
						returnString += ("You have to connect to some one before you can retrieve a file.");
					}
				}
				// The command is dc or disconnect so please remove my files from
				// the centralserver's information so I don't exist
				else if (currentCMD.toLowerCase().equals(("dc")) || currentCMD.toLowerCase().equals("disconnect")) {
					if (mySocket.isClosed()) {

					} else {
						returnString += ("Connection closed");
						peerDataOut.writeBytes("Disconnecting\n");

						mySocket.close();
					}
				} else {
					// Everything else isn't a command.
					returnString += ("Not a valid command");
				}
			}
				}
				
		return returnString;
	}

	/**
	 * From the GUI, get the necessary information to run this function.
	 * 
	 * @param hostName
	 *            = Server's IP
	 * @param portNumber
	 *            = Server's Port Number that it is listening on
	 * @param userName
	 * @param myIP
	 * @param speed
	 *            = speed of connection(filler)
	 * @throws Exception
	 */
	public String connect(String hostName, int portNumber, String userName, String myIP, String speed)
			throws Exception {
		myHost = hostName;
		String returnMessage;
		// Create socket that is connected to server on specified port
		socket = new Socket(hostName, portNumber);
		dataSocket = new Socket(hostName, 1079);
		dataOut = new DataOutputStream(dataSocket.getOutputStream());
		dataIn = new DataInputStream(dataSocket.getInputStream());

		returnMessage = ("Connecting to main server HUB." + "\nServer: " + hostName + "\nPort: " + 1079);
		returnMessage += ("\nThe connection information for this machine is: " + socket.getLocalAddress() + "\nPort: "
				+ socket.getLocalPort());
		File f = new File(System.getProperty("user.dir") + "/filelist.xml");

		// Let's start sending filelist.xml to the Server if it exists in the
		// directory that fits the line above this one
		if (f.exists() && !f.isDirectory()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			try {
				// Send the server the user's information
				dataOut.writeBytes(userName + "\n");
				dataOut.writeBytes(myIP + "\n");
				dataOut.writeBytes(GportNumber + "\n");
				dataOut.writeBytes(speed + "\n");

				// Work on sending file
				System.out.println("Sending Filelist...");
				byte[] buffer = new byte[1024];
				int bytes = 0;
				while ((bytes = fis.read(buffer)) != -1) {
					System.out.println("Writing File...");
					dataOut.write(buffer, 0, bytes);
				}
				fis.close();
				System.out.println("Sent File: " + f);

				// Start thread for user to listen for peers
				final Thread t;
				t = new Thread(new Runnable() {
					public void run() {
						try {
							serverListenSocket = new ServerSocket();
							serverListenSocket.setReuseAddress(true);
							if (!serverListenSocket.isBound())
								serverListenSocket.bind(new InetSocketAddress(GportNumber));
							otherClientsConnections();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
				// Start thread for this client to connect to other peers
				new Thread() {
					public void run() {
						try {
							// Blank string is just to keep it simple for my own
							// sanity
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
		// If filelist.xml doesn't exist, let the user know
		if (!f.exists()) {
			System.out.println("Doesn't exist!");
		}
		// close dataSocket
		dataSocket.close();
		return returnMessage;
	}

	/**
	 * Based on the text used, search the CentralSerer for files who's
	 * description contains the text
	 * 
	 * @param text
	 */
	public void search(String text) {
		// Holder of people found from the search
		found = new ArrayList<userInformation>();
		// Can't search if the socket is closed dummy
		if (!socket.isClosed()) {
			try {
				// Socket and Input/Output streams
				dataSocket = new Socket(myHost, 1079);
				dataOut = new DataOutputStream(dataSocket.getOutputStream());
				dataIn = new DataInputStream(dataSocket.getInputStream());

				// Send the keyword
				dataOut.writeBytes(text + "\n");
				// Receive how many people I'm going to populate my holder with
				String size = dataIn.readLine();
				// For how big the size is, build the userInformation with that
				// many spots.
				for (int i = 0; i < Integer.parseInt(size); i++) {
					result = dataIn.readLine().split("::");
					userInformation user = new userInformation(result[0], result[1], result[2], result[3], result[4],
							result[5]);
					// System.out.println(result[0] + "\t" + result[1] + "\t" +
					// result[2] + "\t" + result[3] + "\t"
					// + result[4] + "\t" + result[5]);
					found.add(user);
				}

				// Close the socket
				dataSocket.close();
			} catch (IOException e) {

				System.out.println("Error some how");
			}
		}

	}

	/**
	 * Return the found files back to the GUI :)
	 * 
	 * @return
	 */
	public ArrayList<userInformation> getFileInfo() {
		if (found != null)
			return found;
		return new ArrayList<userInformation>();
	}

	/**
	 * Remove this user's files from the main server's
	 */
	public String Disconnect() {
		if (socket != null) {
			if (socket.isConnected()) {
				if (!socket.isClosed()) {
					try {

						// DC PLEASE
						dataSocket = new Socket(myHost, 1079);

						dataOut = new DataOutputStream(dataSocket.getOutputStream());
						dataIn = new DataInputStream(dataSocket.getInputStream());
						dataOut.writeBytes("DCFROMSERVERPLS\n");
						dataSocket.close();
						serverListenSocket.close();
						mySocket.close();
						return "Disconnected from the Central Server";

					} catch (Exception e) {
					}
				}
			}
		}
		return "";
	}

	public boolean getConnectionStatus() {
		if(socket == null)
			return false;
		return !socket.isClosed();
	}

}