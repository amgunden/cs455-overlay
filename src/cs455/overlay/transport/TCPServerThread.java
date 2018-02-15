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
		
		//System.out.println("\r\nRunning ServerSocket Thread: " + "Host=" + serverSocket.getInetAddress().getHostAddress() +" Port=" + serverSocket.getLocalPort());
		
		try {
			while (!Thread.currentThread().isInterrupted()) {

				Socket clientSocket = serverSocket.accept();
				//int port = clientSocket.getPort();
				//InetAddress inetAddr = clientSocket.getInetAddress();
				
		        tcpConCache.addTCPConnection(clientSocket);
		        
		        String clientAddress = clientSocket.getInetAddress().getHostAddress();
		        //System.out.println("\r\nNew connection from " + clientAddress);
							
			} 
			
			serverSocket.close();
			
		} catch (IOException e) {
			// TODO: handle exception
		}
		
	}

}
