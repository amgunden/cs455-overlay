package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Registry {
	
	HashMap<Integer, Socket> clientSockets = new HashMap<Integer, Socket>();
	
	

	public Registry() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Read in port number from arguments
		int portNum = Integer.parseInt(args[0]);

		
		try {
			ServerSocket serverSocket = new ServerSocket(portNum);
			Socket clientSocket = serverSocket.accept();

			
		}catch(IOException exception) {
			
		}finally {
			
		}
		
		
	}
	
	private void acceptConnection(Socket clientSocket) {
		
		// assign ID to socket
		Integer id = 0;
		// add id, socket to the hashmap
		clientSockets.put(id, clientSocket);
		
	}
	
	

}
