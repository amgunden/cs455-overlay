package cs455.overlay.transport;

import java.net.Socket;
import java.util.HashMap;

public class TCPConnectionsCache {
	
	HashMap<Integer, Socket> clientSockets = new HashMap<Integer, Socket>();

	public TCPConnectionsCache() {
		// TODO Auto-generated constructor stub
		
	}

}
