package cs455.overlay.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerThread implements Runnable{
	
	private ServerSocket serverSocket;

	public TCPServerThread(int portNum) throws IOException {
		// TODO Auto-generated constructor stub

		serverSocket = new ServerSocket(portNum);
		
	}
	
	public int generateUniqueID() {
		
		Random random = new Random();
		
		// Generate int between 0 - 127
		int id = random.nextInt(128);
		
		// Test if random int is a Key in the HashMap
		// if it is then a new number is generated and the while loop runs again
		while( TCPConnectionsCache.getInstance().clientConnections.containsKey(id)) {
			id = random.nextInt(128);
		}
		
		//return unique random number
		return id;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("\r\nRunning Server: " + "Host=" + serverSocket.getInetAddress().getHostAddress() +" Port=" + serverSocket.getLocalPort());
		
		try {
			while (true) {

				Socket clientSocket = serverSocket.accept();
				
				String clientAddress = clientSocket.getInetAddress().getHostAddress();
		        System.out.println("\r\nNew connection from " + clientAddress);
				
				InetAddress clientAddr = clientSocket.getInetAddress();
				// ERROR CHECK
				// The registry issues an error message under two circumstances:
				//	• If the node had previously registered and has a valid entry in its registry.
				if (TCPConnectionsCache.getInstance().ipAddressExists(clientAddr)) {

					// The IP Address is already in the connections list 
					// Send error
				}
				

				//	• If there is a mismatch in the address that is specified in the registration request and the IP
				//	address of the request (the socket’s input stream)

				// 	IF NO ERROR
				// registry generates a unique identifier (between 0-127) for the node while
				// ensuring that there are no duplicate IDs being assigned.
				int newNodeID = generateUniqueID();

				// Save  to TCPConnectionsCache hashmap
				TCPConnection connectionToStore = new TCPConnection(newNodeID, clientAddr, clientSocket.getPort(),
						clientSocket);
				TCPConnectionsCache.getInstance().clientConnections.put(newNodeID, connectionToStore);

			} 
		} catch (IOException e) {
			// TODO: handle exception
		}
		
	}

}
