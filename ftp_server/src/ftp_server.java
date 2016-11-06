/**
 * Created by Kent Sinclair and Evan Stedman on 10/10/16.
 */
import java.io.* ;
import java.net.* ;
import java.util.* ;

public class ftp_server {



    public static void main(String argv[]) throws Exception {
        int controlPort = 5543;
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("**** server_ftp " + ip.getHostAddress() + " port 5543 ****");
        // Establish the listen socket.
       
	    ServerSocket controlListen = new ServerSocket(controlPort);
        while(true) {
            // Listen for a TCP connection request.
            Socket controlConnection = controlListen.accept();

            // Create a new request which calls Worker function to handle new commands
            Worker request = new Worker(controlConnection);

            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();
        }
    }
}
