import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 
 * @author zomerlej Hey this is the CentralServer
 */
public class CentralServer {
	// Global counter, sorta useless
	int count = 0;
	// Two user lists, one total list, one the searched one
	ArrayList<userInformation> users = new ArrayList<userInformation>();
	ArrayList<userInformation> feed = new ArrayList<userInformation>();

	// Port number to listen on
	static int portNumb = 2597;

	// Main method, start the centralServerwith either default port or one
	// within args[]
	public static void main(String args[]) throws IOException {
		if (args.length == 0) {
			new CentralServer(portNumb);
		} else if (args.length != 1) {
			throw new RuntimeException("Syntax: java FTPServer <listen_port>\nSuggested port: 2597");
		}
		if (args.length == 1) {
			new CentralServer(Integer.parseInt(args[0]));
		}

	}

	/**
	 * CentralServer receives the port to listen on. Constantly listen for new
	 * connections, start threads as we get new connections.
	 * 
	 * @param listenPort
	 * @throws IOException
	 */
	public CentralServer(int listenPort) throws IOException {
		// Establish the listen socket.
		ServerSocket serverListenSocket = new ServerSocket(listenPort);
		ServerSocket dataListenSocket = new ServerSocket(1079);
		while (true) {
			// Listen for a TCP connection request.
			Socket clientSocket = serverListenSocket.accept();

			HandlerFTP handlerForClient = new HandlerFTP(clientSocket, dataListenSocket);
			handlerForClient.start();
		}
	}

	/**
	 * 
	 * The thread that handles each connection
	 * 
	 */
	class HandlerFTP extends Thread {

		Socket clientSocket;
		ServerSocket dataSocket;
		DataInputStream in;
		DataOutputStream out;

		DataInputStream dataIn;
		DataOutputStream dataOut;

		// Constructor is sorta odd, dataListener isn't needed but lets not
		// remove it
		public HandlerFTP(Socket socket, ServerSocket datalistener) throws IOException {
			this.clientSocket = socket;
			this.dataSocket = datalistener;
			this.in = new DataInputStream(clientSocket.getInputStream());
			this.out = new DataOutputStream(clientSocket.getOutputStream());
		}

		// Run the thread!
		public void run() {
			try {
				// This thread's user's within the main list of files
				ArrayList<userInformation> connectionUsers = new ArrayList<userInformation>();
				System.out.println("Handling Request...\n" + "Client: " + clientSocket.getInetAddress().getHostAddress()
						+ "\nPort: " + clientSocket.getPort());

				// while the still connected to client
				while (!clientSocket.isClosed()) {
					// accept the connection of data socket

					Socket clientDataSocket = dataSocket.accept();
					dataIn = new DataInputStream(clientDataSocket.getInputStream());
					dataOut = new DataOutputStream(clientDataSocket.getOutputStream());
					File file = new File("filelist" + count + ".xml");

					String WholeLine = dataIn.readLine();
					// If weren't first connecting, let's figure out who we are
					if (WholeLine.startsWith("USER")) {
						try {
							FileOutputStream fos = null;
							fos = new FileOutputStream(file);
							System.out.println("Receiving File...");
							byte[] b = new byte[8096];
							int amount_read;
							String name = WholeLine.substring(4);
							String hostIP = dataIn.readLine();
							String portInformation = dataIn.readLine();
							String speed = dataIn.readLine();

							amount_read = dataIn.read(b);
							// Receive a new filelist.xml
							System.out.println("Copying File...");

							fos.write(b, 0, amount_read);
							fos.flush();
							fos.close();
							BufferedReader br = new BufferedReader(new FileReader("filelist" + count + ".xml"));
							// Go through the filelist.xml and build stuff
							try {
								String line = br.readLine(); // waste
								String fileName;
								String fileDescription;

								line = br.readLine();
								if (line.equals("<filelist>")) {

									while (!line.equals("</filelist>")) {
										line = br.readLine().trim();
										if (line.equals("<file>")) {
											line = br.readLine().trim();
											fileName = line.substring(6);
											fileName = fileName.substring(0, fileName.length() - 7);
											line = br.readLine().trim();
											fileDescription = line.substring(13);
											fileDescription = fileDescription.substring(0,
													fileDescription.length() - 15);
											userInformation user = new userInformation(name, hostIP, portInformation,
													speed, fileName, fileDescription);
											users.add(user);
											connectionUsers.add(user);
										}
									}

								}
							} finally {
								// close the reader
								br.close();
							}

						} catch (Exception e) {
							// error catch
							System.out.println(e.getStackTrace().toString());
						}
						// Count it up but it actually doesn't matter, the files
						// are deleted anyways.
						count++;
						System.out.println("File " + file + " retrieved.");
						file.delete();
					} else if (WholeLine.contains("DCFROMSERVERPLS")) {
						// Close the socket/disconnect the client from the
						// server(exit the while loop I'm in)

						clientSocket.close();
					} else {
						// If it's any other command received, it has to be the
						// keyword search
						if ("".equals(WholeLine)) {

						} else {
							// If the keyword appears within the descriptor of a
							// particular file on the search, build the feed
							for (userInformation desc : users) {
								if (desc.getClientFileDescription().toLowerCase().contains(WholeLine.toLowerCase())) {
									feed.add(desc);

								}
							}
							// Tell the client how many files it is about to
							// list
							dataOut.writeBytes(feed.size() + "\n");
							// Send those files to the client
							for (userInformation sending : feed) {

								dataOut.writeBytes(sending.getClientName() + "::" + sending.getClientIP() + "::"
										+ sending.getClientPort() + "::" + sending.getClientSpeed() + "::"
										+ sending.getClientFiles() + "::" + sending.getClientFileDescription() + "\n");
							}
							// Clear the feed
							feed.clear();
						}

					}
					// After every command send, close the dataIn and dataOut
					dataIn.close();
					dataOut.close();
				}
				// If we leave the while, remove the users that the connection
				// did add to the overall user list.
				if (clientSocket.isClosed()) {
					for (userInformation me : connectionUsers) {
						if (users.contains(me)) {
							users.remove(me);
							System.out.println("Removed user's file listed on server");
						}
					}
				}
			} catch (IOException e) {
			}
		}

	}

}