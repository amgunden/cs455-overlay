package cs455.overlay.routing;

import java.net.InetAddress;

public class RoutingEntry {
	
	private int nodeId;
	private InetAddress addr;
	private int serverSocketPort;
	
	
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setInetAddr(InetAddress addr) {
		this.addr = addr;
	}

	public void setPort(int port) {
		this.serverSocketPort = port;
	}

	public int getNodeId() {
		return nodeId;
	}

	public InetAddress getInetAddr() {
		return addr;
	}

	public int getPort() {
		return serverSocketPort;
	}

	public RoutingEntry(int id, InetAddress address, int port) {
	
		nodeId = id;
		addr=address;
		this.serverSocketPort = port;
		
	}
	
	

}
