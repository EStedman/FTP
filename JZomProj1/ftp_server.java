import java.net.*; // for Socket
import java.util.StringTokenizer;
import java.io.*; // for IOException and Input/OutputStream

public class ftp_server {
	private static final int BUFSIZE = 32768;

	
	public static void main(String args[]) throws IOException {
		int controlPort = 1078;
		int dataPort = 1079;
		int recvMsgSize; // Size of received message
		byte[] byteBuffer = new byte[BUFSIZE];
		
		// Establish the listen socket.
		ServerSocket controlListen = new ServerSocket(controlPort);
		
		// Listen for a TCP connection request.
		Socket Controlsocket = controlListen.accept();
		// Input/Output streams
		InputStream inFromClientC = Controlsocket.getInputStream();
		OutputStream outToClientC = Controlsocket.getOutputStream();

		//Who connected?
		System.out.println("Connection Started: " + Controlsocket.getInetAddress().toString());
		
		
		Socket dataConnection = null;
		while ((recvMsgSize = inFromClientC.read(byteBuffer)) != -1) {
			String line = new String(byteBuffer, 0, recvMsgSize);
			String returnedString = "";

			String currentToken;
			StringTokenizer tokens = new StringTokenizer(line);

			// data connection socket
			dataConnection = new Socket(Controlsocket.getInetAddress(), dataPort);
			System.out.println("Data line started");

			// data Input/Output streams
			InputStream inFromClient_Data = dataConnection.getInputStream();
			OutputStream outToClient_Data = dataConnection.getOutputStream();
			while (tokens.hasMoreTokens()) {
				currentToken = tokens.nextToken();
				if (currentToken.equals("LIST")) {
					File folder = new File(System.getProperty("user.dir"));
					File[] listOfFiles = folder.listFiles();
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							returnedString += listOfFiles[i].getName() + "\t";
						}
					}
					returnedString = returnedString + "\n";
					outToClient_Data.write(returnedString.getBytes());
				} else if (currentToken.equals("RETR")) {
					currentToken = tokens.nextToken();
					//currentToken = tokens.nextToken();
					File myFile = new File(currentToken);
					// if File exists write file, otherwise write error message
					if (myFile.exists()) {
						try {
							// declare variables for converting file to byte[]
							FileInputStream fileInputStream = new FileInputStream(myFile);
							byte[] fileByteArray = new byte[(int) myFile.length()];

							// convert file
							fileInputStream.read(fileByteArray);
							fileInputStream.close();

							// write to client over DATA line
							outToClient_Data.write(fileByteArray);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						// write error message to client over DATA line
						String errMsg = new String("File does not exist.");
						outToClient_Data.write(errMsg.getBytes());
						outToClient_Data.flush();
					}
				} else if (currentToken.equals("STOR")) {
					
					String fileRequestedString = tokens.nextToken();

						try {
							recvMsgSize = inFromClient_Data.read(byteBuffer);
							inFromClient_Data.read(byteBuffer, 0, recvMsgSize);
							File fileRequested = new File(fileRequestedString);
							FileOutputStream fileOutputStream = new FileOutputStream(fileRequested);
							fileOutputStream.write(byteBuffer,0,recvMsgSize);
							fileOutputStream.flush();
							fileOutputStream.close();
						} catch (Exception e) {
							System.out.println("Error trying to write file.");
						}
					
				} else if (currentToken.equals("QUIT")) {
					Controlsocket.close();
					dataConnection.close();
				} else {
					outToClient_Data.write(("Not a valid command").getBytes());

				}

			}
			
			System.out.println("Done processing commands");
			dataConnection.close();


		}
	}
}