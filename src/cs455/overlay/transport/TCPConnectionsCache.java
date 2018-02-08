package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import cs455.overlay.node.Node;

// Singleton

public class TCPConnectionsCache {
	
	// Sockets created, unknown info about socket, holding until message is received about what node on the other side
	ArrayList<TCPConnection> existingTCPConnections = new ArrayList<TCPConnection>();
	
	// Integer represents nodeID, once a socket is known and associated with a certain node it is moved to clientConnections
	// TreeMap<Integer, TCPConnection> clientConnections = new TreeMap<Integer, TCPConnection>();
	
	private ArrayList<TCPConnection> clientConns = new ArrayList<TCPConnection>();
	
	//Node node;

	private TCPConnectionsCache() {
		
	}
	
	private static class TCPConnectionsCacheHolder {
		
		private static final TCPConnectionsCache INSTANCE = new TCPConnectionsCache();
		
	}
	
	public static TCPConnectionsCache getInstance() {
		return TCPConnectionsCacheHolder.INSTANCE;
	}
	
	
	// TODO Check and maybe delete
	// May not be a needed method, only generateID was using it, now uses clientIdExists
	public ArrayList<TCPConnection> getClientConnections() {
		return clientConns;
	}
	
	public int getIndexOfClientId(int id) {
		
		for(int i=0; i< clientConns.size(); i++) {
			
			if( clientConns.get(i).nodeID == id) {
				return i;
			}
			
		}
		return -1;

		
	}
	
	public ArrayList<TCPConnection> getExistingSockets() {
		return existingTCPConnections;
	}
	
	public TCPConnection getTCPConByIpAddr(InetAddress inetAddress) {
		
		TCPConnection tcpConnection = null;
		
//		for( TCPConnection con : existingTCPConnections) {
//			if( con.getInetAddress().equals(inetAddress)) {
//				tcpConnection = con;
//			}
//		}
		
		
		Iterator<TCPConnection> iterator = existingTCPConnections.iterator();
		while( iterator.hasNext() ) {
			TCPConnection temp = iterator.next();
			if( temp.getInetAddress().equals(inetAddress) ) {
				tcpConnection = temp;
			}
			
		}
		
		return tcpConnection;
	}
	
	
	public Socket createTCPConnection(String host, int port) {
		
		// TODO Need Error checking so connection isn't made to node that already has established connection
		Socket newConnection = null;
		try {
			
			newConnection = new Socket(host, port);
			addTCPConnection(newConnection);
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newConnection;
		
		
	}
	
	public Socket createTCPConnection(InetAddress addr, int port) {
		
		Socket newConnection = null;
		
		try {
			System.out.println(addr.getHostAddress());
			System.out.println(port);
			newConnection = new Socket(addr, port);
			addTCPConnection(newConnection);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return newConnection;
	}
	
	public void addTCPConnection(Socket clientSocket) throws IOException {
		
		TCPConnection newTCP = new TCPConnection(clientSocket);
		existingTCPConnections.add(newTCP);
	}
	
	public TCPConnection getClientByIpAddr(InetAddress inetAddress) {
		
		// Iterate through clientConnections HashMap and get InetAddress
		// Compare address to argument
		
		TCPConnection tcpConnection = null;
		
		for( TCPConnection clientConnection : clientConns ) {
			if(clientConnection.getInetAddress().equals(inetAddress)) {
				tcpConnection = clientConnection;
			}
			
		}
		
//		Iterator<TCPConnection> iterator = clientConns.iterator();
//		while( iterator.hasNext() ) {
//			TCPConnection temp = iterator.next();
//			if(temp.getInetAddress().equals(inetAddress)) {
//				tcpConnection = temp;
//			}
//			
//		}
		
		return tcpConnection;
		
	}
	
	// Add a client connection to the arraylist to keep it in order or nodeIDs
	public void addClientConnection(int id, TCPConnection conn) {
		
		conn.setNodeID(id);
		
		// Base Case - nothing is in client Connections so we just add it to the fron
		if(clientConns.size() == 0) {
			clientConns.add(conn);
		}
		else {
			
			// iterate through client Conns (which should be sorted) until we find one bigger than we take its spot
			
			int index = findIndexOfLarger(id);
			
			if(index == -1) {
				clientConns.add(conn);
			}
			else {
				clientConns.add(index, conn);
			}
			
		}
		
	}
	
	public int findIndexOfLarger(int id) {
		
		for(int i=0; i< clientConns.size(); i++) {
			
			if( clientConns.get(i).nodeID > id) {
				return i;
			}
			
		}
		return -1;
		
		
	}
	
	public void sendMessage(int nodeID, byte[] msg) {
		int index = getIndexOfClientId(nodeID);
		
		try {
			clientConns.get(index).sendTCPMessage(msg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public int getClientCount() {
		return clientConns.size();
	}
	
	public ArrayList<Integer> getIdList(int currentId) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		
		for(int i=0; i< clientConns.size(); i++) {
			
			if( clientConns.get(i).nodeID != currentId) {
				idList.add(clientConns.get(i).nodeID);
			}
			
		}
		
		return idList;
		
	}
	
	public void sendMsgToAllClients(byte[] msg) throws IOException {
		
		Iterator<TCPConnection> iterator = clientConns.iterator();
		while( iterator.hasNext() ) {
			TCPConnection temp = iterator.next();
			temp.sendTCPMessage(msg);
			
		}
		
	}

}
