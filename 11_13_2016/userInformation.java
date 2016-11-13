import java.util.ArrayList;

public class userInformation {
	private String ClientIP;
	private String ClientPort;
	private String ClientFiles;
	private String ClientName;
	private String ClientSpeed;
	private String ClientFileDescription;

	public userInformation(String username, String IP, String portInformation, String speed, String filename,
			String filedescription) {
		ClientName = username;
		ClientFiles = filename;
		ClientIP = IP;
		ClientPort = portInformation;
		ClientSpeed = speed;
		ClientFileDescription = filedescription;
	}

	public String getClientFiles() {
		return ClientFiles;
	}

	public String getClientName() {
		return ClientName;
	}

	public String getClientIP() {
		return ClientIP;
	}

	public String getClientPort() {
		return ClientPort;
	}

	public String getClientSpeed() {
		return ClientSpeed;
	}

	public String getClientFileDescription() {
		return ClientFileDescription;
	}
}
