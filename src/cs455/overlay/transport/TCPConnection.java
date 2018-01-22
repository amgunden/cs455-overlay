package cs455.overlay.transport;

import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection {
	
	int nodeID;
	InetAddress inetAddress;
	int port;
	Socket socket;
	
	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}


	public TCPConnection(int nodeID, InetAddress inetAddr, int port, Socket socket) {
		// TODO Auto-generated constructor stub
		this.nodeID = nodeID;
		this.inetAddress = inetAddr;
		this.port = port;
		this.socket = socket;
	}
	
	

}
