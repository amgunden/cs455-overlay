package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import cs455.overlay.node.Node;

// Singleton

public class TCPConnectionsCache {
	
	
	// Integer represents nodeID
	HashMap<Integer, TCPConnection> clientConnections = new HashMap<Integer, TCPConnection>();
	
	//Node node;

	private TCPConnectionsCache() {
		
	}
	
	private static class TCPConnectionsCacheHolder {
		
		private static final TCPConnectionsCache INSTANCE = new TCPConnectionsCache();
		
	}
	
	public static TCPConnectionsCache getInstance() {
		return TCPConnectionsCacheHolder.INSTANCE;
	}
	
	public boolean ipAddressExists(InetAddress inetAddress) {
		
		// Iterate through clientConnections HashMap and get InetAddress
		// Compare address to argument
		
		for( TCPConnection clientConnection : clientConnections.values() ) {
			if(clientConnection.getInetAddress().equals(inetAddress)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public Socket createNewConnection(int nodeID, String host, int port) {
		
		// TO-DO Need Error checking so connection isn't made to node that already has established connection
		
		try {
			
			Socket newConnection = new Socket(host, port);
			addConnectionToCache(nodeID, newConnection.getInetAddress(), newConnection, port);
			return newConnection;
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	public void addConnectionToCache(Integer nodeID, InetAddress clientAddr, Socket socket, int port) {
		
		try {
			
			clientConnections.put(nodeID, new TCPConnection(nodeID, clientAddr, socket, port));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendMessage(int nodeID, byte[] msg) {
		
		try {
			
			clientConnections.get(nodeID).sendTCPMessage(msg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
//	public void setNode(Node node) {
//		this.node = node;
//	}

}
