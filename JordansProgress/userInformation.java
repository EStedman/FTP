import java.util.ArrayList;

public class userInformation {
	private String ClientIP;
	private int ClientPort;
	ArrayList<String> Files = new ArrayList<String>();
	
	public userInformation(String IP, int port, Object other)
	{
		ClientIP = IP;
		ClientPort = port;
	}
}
