package cs455.overlay.routing;

import java.net.InetAddress;

public class RoutingEntry {
	
	private int nodeId;
	private InetAddress addr;
	private int port;
	
	
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setInetAddr(InetAddress addr) {
		this.addr = addr;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getNodeId() {
		return nodeId;
	}

	public InetAddress getInetAddr() {
		return addr;
	}

	public int getPort() {
		return port;
	}

	public RoutingEntry(int id, InetAddress address, int port) {
		// TODO Auto-generated constructor stub
		
		nodeId = id;
		addr=address;
		this.port = port;
		
	}
	
	

}
