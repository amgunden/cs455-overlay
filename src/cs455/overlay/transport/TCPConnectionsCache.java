package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import cs455.overlay.node.Node;

// Singleton

public class TCPConnectionsCache {
	
	// Sockets created, unknown info about socket, holding until message is received about what node on the other side
	ArrayList<TCPConnection> existingTCPConnections = new ArrayList<TCPConnection>();
	
	// Integer represents nodeID, once a socket is known and associated with a certain node it is moved to clientConnections
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
	
	public HashMap<Integer, TCPConnection> getClientConnections() {
		return clientConnections;
	}
	
	public ArrayList<TCPConnection> getExistingSockets() {
		return existingTCPConnections;
	}
	
	public TCPConnection getTCPConByIpAddr(InetAddress inetAddress) {
		
		TCPConnection tcpConnection = null;
		
		for( TCPConnection con : existingTCPConnections) {
			if( con.inetAddress.equals(inetAddress)) {
				tcpConnection = con;
			}
		}
		
		return tcpConnection;
	}
	
	
	public Socket createTCPConnection(String host, int port) {
		
		// TO-DO Need Error checking so connection isn't made to node that already has established connection
		
		try {
			
			Socket newConnection = new Socket(host, port);
			addTCPConnection(newConnection.getInetAddress(), newConnection, port);
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
	
		
	public void sendMessage(int nodeID, byte[] msg) {
		
		try {
			
			clientConnections.get(nodeID).sendTCPMessage(msg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void addTCPConnection(InetAddress inetAddr, Socket client, int port) throws IOException {
		
		TCPConnection newTCP = new TCPConnection(inetAddr, client, port);
		existingTCPConnections.add(newTCP);
	}
	
	public TCPConnection getClientByIpAddr(InetAddress inetAddress) {
		
		// Iterate through clientConnections HashMap and get InetAddress
		// Compare address to argument
		
		TCPConnection tcpConnection = null;
		
		for( TCPConnection clientConnection : clientConnections.values() ) {
			if(clientConnection.getInetAddress().equals(inetAddress)) {
				tcpConnection = clientConnection;
			}
			
		}
		
		return tcpConnection;
		
	}
	
	public void addClientConnection(int key, TCPConnection tcpCon) {
		clientConnections.put(key, tcpCon);
	}
	
	
//	public void setNode(Node node) {
//		this.node = node;
//	}

}
