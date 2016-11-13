import java.io.*;
import java.net.*;
import java.util.*;

public class CentralServer {
	int count = 0;
	ArrayList<userInformation> users = new ArrayList<userInformation>();
	ArrayList<userInformation> feed = new ArrayList<userInformation>();
	static int portNumb = 2597;

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

	class HandlerFTP extends Thread {

		Socket clientSocket;
		ServerSocket dataSocket;
		DataInputStream in;
		DataOutputStream out;

		DataInputStream dataIn;
		DataOutputStream dataOut;

		// Constructor
		public HandlerFTP(Socket socket, ServerSocket datalistener) throws IOException {
			this.clientSocket = socket;
			this.dataSocket = datalistener;
			this.in = new DataInputStream(clientSocket.getInputStream());
			this.out = new DataOutputStream(clientSocket.getOutputStream());
		}

		public void run() {
			try {

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
					if (WholeLine.contains("USER")) {
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
							// amount_read = dataIn.read(b);

							amount_read = dataIn.read(b);

							System.out.println("Copying File...");

							fos.write(b, 0, amount_read);
							fos.flush();
							fos.close();
							BufferedReader br = new BufferedReader(new FileReader("filelist" + count + ".xml"));
							try {
								String line = br.readLine(); // waste
								String fileName;
								String fileDescription;
								// line = br.readLine(); // actual stuff
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

										}
									}

								}
							} finally {
								String line = br.readLine();
								br.close();
							}

						} catch (Exception e) {
							System.out.println(e.getStackTrace().toString());
						}
						count++;
						System.out.println("File " + file + " retrieved.");
					} else {
						if (WholeLine.toLowerCase().equals("Q")) {
							clientDataSocket.close();
							users.clear();
						}

						for (userInformation desc : users) {
							if (desc.getClientFileDescription().toLowerCase().contains(WholeLine.toLowerCase())) {
								feed.add(desc);

							}
						}

						dataOut.writeBytes(feed.size() + "\n");
						System.out.println("First " + feed.size());
						ArrayList<userInformation> feed2 = feed;
						for (userInformation sending : feed) {

							dataOut.writeBytes(sending.getClientName() + "::" + sending.getClientIP() + "::"
									+ sending.getClientPort() + "::" + sending.getClientSpeed() + "::"
									+ sending.getClientFiles() + "::" + sending.getClientFileDescription() + "\n");
						}
						feed.clear();
					}

					dataIn.close();
					dataOut.close();
				}
			} catch (IOException e) {
				System.out.println("Error handling the thread.");
				System.out.println(e);
			}
		}

	}

}