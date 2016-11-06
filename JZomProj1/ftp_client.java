import java.net.*; // for Socket
import java.util.*;
import java.io.*; // for IOException and Input/OutputStream

public class ftp_client {
	static Socket server;
	private static final int BUFSIZE = 32768;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		byte[] byteBuffer = new byte[BUFSIZE];
		Scanner input = new Scanner(System.in);

		String ip_address = "127.0.0.1", controlPort = "1078";
//		System.out.println("Enter an IP address, loopback address is 127.0.0.1");
//		ip_address = input.next();
//		System.out.println("Enter a port, default port is 1078");
//		controlPort = input.next();
//		// Possibly add error checking for IP address
//		if (checkIP(ip_address) && checkPort(controlPort)) {
//		} else {
//			System.out.print("Not a valid ip address or port.");
//
//			System.exit(0);
//		}
		int dataPort = 1079;
		Socket controlSocket = new Socket(ip_address, Integer.parseInt(controlPort));
		// Assign user IP to socket
		InputStream inFromServer_Control = controlSocket.getInputStream();
		OutputStream outToServer_Control = controlSocket.getOutputStream();
		// InputStream inFromServer2 = clientSocket.getInputStream();
		// notify user that they are connected to server or show error
		System.out.println("Connected to server...");
		Socket dataConnection = null;
		ServerSocket dataListen = new ServerSocket(dataPort);
		boolean quit = false;
		int recvMsgSize;

		String userCommand;

		while (!quit) {
			System.out.println("Input Command\n");
			userCommand = input.nextLine();
			// Listen for a TCP connection request.

			String currentToken;
			StringTokenizer tokens = new StringTokenizer(userCommand);
			currentToken = tokens.nextToken();
			if (userCommand.contains("CONNECT") == false)
				outToServer_Control.write(userCommand.getBytes());
			if (userCommand.equals("QUIT")) {
				quit = true;
			} else if (userCommand.contains("STOR")) {
				dataConnection = dataListen.accept();
				InputStream inFromServer_Data = dataConnection.getInputStream();
				OutputStream outToServer_Data = dataConnection.getOutputStream();
				currentToken = tokens.nextToken();
				// currentToken = tokens.nextToken();
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
						outToServer_Data.write(fileByteArray);
						System.out.println("Done writing");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// write error message to client over DATA line
					String errMsg = new String("File does not exist.");
					outToServer_Data.write(errMsg.getBytes());
				}
			} else if (userCommand.contains("RETR")) {
				dataConnection = dataListen.accept();
				InputStream inFromServer_Data = dataConnection.getInputStream();
				//outToServer_Control.write(userCommand.getBytes());

				String fileRequestedString = new String(tokens.nextToken().getBytes());
				while ((recvMsgSize = inFromServer_Data.read(byteBuffer)) != -1) {
					try {
						File fileRequested = new File(fileRequestedString);
						FileOutputStream fileOutputStream = new FileOutputStream(fileRequested);
						// BufferedOutputStream bos = new
						// BufferedOutPutStream(fileOutputStream)
						fileOutputStream.write(byteBuffer, 0, recvMsgSize);
						fileOutputStream.close();
					} catch (Exception e) {
						System.out.println("Error trying to write file.");
					}
				}
			} else if (userCommand.equals("LIST")) {
				dataConnection = dataListen.accept();
				
				InputStream inFromServer_Data = dataConnection.getInputStream();
				OutputStream outToServer_Data = dataConnection.getOutputStream();
				
				while ((recvMsgSize = inFromServer_Data.read(byteBuffer)) != -1) {
					System.out.println(new String(byteBuffer, 0, recvMsgSize));
				}

			} else if (currentToken.equals("CONNECT")) {
				controlSocket.close();
				currentToken = tokens.nextToken();
				String nextOne = tokens.nextToken();
				if (checkIP(currentToken) && checkPort(nextOne)) {
					controlSocket = new Socket(ip_address, Integer.parseInt(nextOne));
				} else {
					System.out.println("Not a valid IP or Port to connect to");
				}
			} else {
//				dataConnection = dataListen.accept();
//				InputStream inFromServer_Data = dataConnection.getInputStream();
//				OutputStream outToServer_Data = dataConnection.getOutputStream();
//				System.out.println("Server command sent");
//				recvMsgSize = inFromServer_Data.read(byteBuffer);
//				String display = new String(byteBuffer, 0, recvMsgSize);
//				System.out.println(display);
			}
			dataConnection.close();
			System.out.println("Data line closed");

		}
		controlSocket.close();
		input.close();
	}

	public static boolean checkIP(String ip) {
		String[] tokens = ip.split(".");
		if (tokens.length > 4)
			return false;
		int token;
		try {
			for (int i = 0; i < 4; i++) {
				try {
					token = Integer.parseInt(tokens[i]);
				} catch (NumberFormatException e) {
					return false;
				}
				if (i != 3) {
					if (token < 0 || token > 255)
						return false;
				} else if (token < 1 || token > 254)
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// e.printStackTrace(System.out);
		}
		return true;
	}

	/*
	 * Check to make sure the input is a valid port.
	 */
	public static boolean checkPort(String port) {
		int input;
		try {
			input = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return false;
		}
		if (input < 0 || input > 65535)
			return false;
		return true;
		// return true; //or true
	}
}