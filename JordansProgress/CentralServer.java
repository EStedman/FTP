import java.io.*;
import java.net.*;
import java.util.*;

public class CentralServer {
	int count = 0;
	ArrayList <userInformation> users = new ArrayList<userInformation>();
	public static void main(String args[]) throws IOException {
		if (args.length != 1) {
			throw new RuntimeException("Syntax: java FTPServer <listen_port>\nSuggested port: 2597");
		}
		new CentralServer(Integer.parseInt(args[0]));
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
					System.out.println("WOW1");
					// accept the connection of data socket
					Socket clientDataSocket = dataSocket.accept();
					File file = new File("filelist"+count+".xml");
					count++;
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(file);
						copyFile(dataIn, fos);
						fos.close();
						
					} catch (Exception e) {
						System.out.println("\nERROR in COPYING FILE.");
					}

					System.out.println("File " + file + " retrieved.");

			            
					dataIn.close();
					dataOut.close();
					clientDataSocket.close();
					dataIn = new DataInputStream(clientDataSocket.getInputStream());
					dataOut = new DataOutputStream(clientDataSocket.getOutputStream());
					
				}
			} catch (IOException e) {
				System.out.println("Error handling the thread.");
				System.out.println(e);
			}
		}

	}
	private static void copyFile(DataInputStream is, FileOutputStream fos) throws Exception{
		System.out.println("Receiving File...");
		byte[] b = new byte [8096];
		int amount_read;
	
		while ((amount_read = is.read(b)) != -1) {
			System.out.println("Copying File...");
			fos.write(b, 0, amount_read);
		}
	}
}

