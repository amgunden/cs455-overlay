package cs455.overlay.transport;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

// Possibly make this a singleton

public class TCPConnectionsCache {
	
	HashMap<Integer, TCPConnection> clientConnections = new HashMap<Integer, TCPConnection>();

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

}
