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
	private TCPConnectionsCache tcpConCache;

	public TCPServerThread(int portNum) throws IOException {
		serverSocket = new ServerSocket(portNum);
		tcpConCache = TCPConnectionsCache.getInstance();
		
	}
	
	public int getServerSocketPort() {
		return serverSocket.getLocalPort();
	}
	
	@Override
	public void run() {
		
		System.out.println("\r\nRunning ServerSocket Thread: " + "Host=" + serverSocket.getInetAddress().getHostAddress() +" Port=" + serverSocket.getLocalPort());
		
		try {
			while (!Thread.currentThread().isInterrupted()) {

				Socket clientSocket = serverSocket.accept();
				//int port = clientSocket.getPort();
				//InetAddress inetAddr = clientSocket.getInetAddress();
				
		        tcpConCache.addTCPConnection(clientSocket);
		        
		        String clientAddress = clientSocket.getInetAddress().getHostAddress();
		        System.out.println("\r\nNew connection from " + clientAddress);
				
		        /*
		         * 
		         
				InetAddress clientAddr = clientSocket.getInetAddress();
				// ERROR CHECK
				// The registry issues an error message under two circumstances:
				//	• If the node had previously registered and has a valid entry in its registry.
				if (TCPConnectionsCache.getInstance().connectionFromIpAddress(clientAddr) == null) {

					// The IP Address is already in the connections list 
					// Send error
				}
				

				//	• If there is a mismatch in the address that is specified in the registration request and the IP
				//	address of the request (the socket’s input stream)

				// 	IF NO ERROR
				// registry generates a unique identifier (between 0-127) for the node while
				// ensuring that there are no duplicate IDs being assigned.
				int newNodeID = generateUniqueID();

				System.out.println("New node ID is: " + newNodeID);
				
				// Save  to TCPConnectionsCache hashmap
				TCPConnectionsCache.getInstance().addConnectionToCache(null, clientAddr, clientSocket, clientSocket.getPort());
				*/
				
				
			} 
			
			serverSocket.close();
			
		} catch (IOException e) {
			// TODO: handle exception
		}
		
	}

}
