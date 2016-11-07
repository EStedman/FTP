import java.io.*;
import java.net.*;
import java.util.*;


public class FTPServer {
	
	public FTPServer(int listenPort) throws IOException {
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
			this.in = new DataInputStream (clientSocket.getInputStream());
			this.out = new DataOutputStream(clientSocket.getOutputStream());
		}

		public void run() {
			try {
				String cmd = new String("");
				String[] args;

				System.out.println("Handling Request...\n" + 
					"Host: " + clientSocket.getInetAddress().getHostAddress() + 
					"\nPort: " + clientSocket.getPort());

				// while the still connected to client
				while (!clientSocket.isClosed()) {
					// read command
					cmd = in.readUTF();
					args = cmd.split(" ");

					// accept the connection of data socket
					Socket clientDataSocket = dataSocket.accept();
					dataIn = new DataInputStream (clientDataSocket.getInputStream());
					dataOut = new DataOutputStream(clientDataSocket.getOutputStream());


					switch (args[0].toLowerCase()) {
					
					case "retr" :
						String fileName = args[1];
						//fileName = "./" + fileName;
						
					
						FileInputStream fis = null;
						boolean fileExists = true ;
						try {
							fis = new FileInputStream(fileName);
							System.out.println("File Found.");

						} catch (FileNotFoundException e) {
							fileExists = false ;
						}

						//Send the File
						if (fileExists) {
							try {
								sendFile(fis, dataOut);
								fis.close();
								System.out.println("Sent File: " + fileName);
								
							} catch (Exception e) {
								System.out.println("ERROR in FILE TRANSFER.");
							}
						} else {
							System.out.println("file doesn't exist.");
						}
						
						dataIn.close();
						dataOut.close();
						clientDataSocket.close();
						break;


					case "quit" :

						dataIn.close();
						dataOut.close();
						clientDataSocket.close();

						in.close();
						out.close();
						clientSocket.close();
						
						System.out.println("Session Ended.");
						break;

					default :
						System.out.println("Invalid command.");
						cmd = " ";
						dataIn.close();
						dataOut.close();
						clientDataSocket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("Error handling the thread.");
				System.out.println(e);
			}
		}

		private List<String> files(String pwd) {
			ArrayList<String> files = new ArrayList<String>();

			try {
				// Launch external process
				String[] commands = {"/bin/bash", "-c", "ls " + pwd};
				Process p = Runtime.getRuntime().exec(commands);

				// Place generated lines in a List
				Scanner input = new Scanner(p.getInputStream());
				while (input.hasNext()) {
					files.add(input.nextLine());
				}
				
				input.close();
				
			} catch (IOException e) {
				files.add("There was a problem: " + e);
			}
			return files;
		}

		void sendFile(FileInputStream fis, DataOutputStream os) throws Exception {
			System.out.println("Sending File...");
			byte[] buffer = new byte[1024];
			int bytes = 0;
			
			while ((bytes = fis.read(buffer)) != -1) {
				System.out.println("Writing File...");
				os.write(buffer, 0, bytes);
			}
		}

		private void copyFile(DataInputStream is, FileOutputStream fos) throws Exception{
			System.out.println("Receiving File...");
			byte[] b = new byte [1024];
			int amount_read;
		
			while ((amount_read = is.read(b)) != -1) {
				System.out.println("Copying File...");
				fos.write(b, 0, amount_read);
			}
		}
	}

	public void sendFile(DataInputStream in, DataOutputStream out) {
		// TODO Auto-generated method stub
		
	}
}