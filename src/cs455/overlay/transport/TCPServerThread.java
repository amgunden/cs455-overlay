package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread {
	
	private ServerSocket serverSocket;

	public TCPServerThread(int portNum) throws IOException {
		// TODO Auto-generated constructor stub
		
		serverSocket = new ServerSocket(portNum);
		
		while(true) {
			
			Socket clientSocket = serverSocket.accept();
			
			//
			
		}
		
	}

}
