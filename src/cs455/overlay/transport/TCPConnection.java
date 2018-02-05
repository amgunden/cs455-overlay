package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

public class TCPConnection {
	
	// Variables for Junit use only
	InetAddress inetAddress;
	int port;
	
	//Node node;
	
	int nodeID;
	int serverSocketPort;
	
	Socket socket;
	
	//TCPReceiverThread tcpReceiver;
	TCPSender tcpSender;
	
	public int getServerSocketPort() {
		return serverSocketPort;
	}

	public void setServerSocketPort(int serverSocketPort) {
		this.serverSocketPort = serverSocketPort;
	}
	
	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}

	public Socket getSocket() {
		return socket;
	}
	
	public TCPConnection(Socket socket) throws IOException {
		
		this.socket = socket;
		
		Thread tcpReceiverThread = new Thread( new TCPReceiverThread(this.socket));
		tcpReceiverThread.start();
		
		tcpSender = new TCPSender(this.socket);
		
	}
	

	/*
	public TCPConnection(InetAddress inetAddr, Socket socket, int port) throws IOException {
		// TODO Auto-generated constructor stub
		
		this.inetAddress = inetAddr;
		this.port = port;
		this.socket = socket;
		
		Thread tcpReceiverThread = new Thread( new TCPReceiverThread(this.socket));
		tcpReceiverThread.start();
		
		tcpSender = new TCPSender(this.socket);
	}
	*/
	
	// Junit constructor
	public TCPConnection(InetAddress inetAddr, int port) throws IOException {
		// TODO Auto-generated constructor stub
		
		this.inetAddress = inetAddr;
		this.port = port;

	}
	
	
	public void sendTCPMessage(byte[] dataToSend) throws IOException {
		
		tcpSender.sendData(dataToSend);
		
	}
	
	public void handleReceivedMessage(byte[] message) throws IOException {
		
		EventFactory.getInstance().getEvent(message);
		
		
	}
	
	public class TCPReceiverThread implements Runnable {
		
		private Socket socket;
		private DataInputStream din;

		public TCPReceiverThread(Socket socket) throws IOException {

			this.socket = socket;
			din = new DataInputStream(socket.getInputStream());
		}
		
		public void run() {
			int dataLength;
			
			while (socket != null) {
				try {
					dataLength = din.readInt();
					
					byte[] data = new byte[dataLength];
					din.readFully(data, 0, dataLength);
					
					handleReceivedMessage(data);
					
				} catch (SocketException se) {
					System.out.println(se.getMessage());
					break;
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage()) ;
					break;
				}
			}
		}

	}
	
	public class TCPSender {
		
		private Socket socket;
		private DataOutputStream dout;
		
		public TCPSender(Socket socket) throws IOException {
			this.socket = socket;
			dout = new DataOutputStream(socket.getOutputStream());
		}
		
		public void sendData(byte[] dataToSend) throws IOException {
			int dataLength = dataToSend.length;
			dout.writeInt(dataLength);
			dout.write(dataToSend, 0, dataLength);
			dout.flush();
		}
	}
	

}
