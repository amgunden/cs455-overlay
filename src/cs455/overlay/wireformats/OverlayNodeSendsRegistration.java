package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OverlayNodeSendsRegistration  implements Event{
	
	private int messageType;
	private byte[] inetAddress;
	private int serverSocketPort;
	

//	private long timestamp;
	private String identifier;
//	private int tracker;


	public OverlayNodeSendsRegistration(InetAddress inetAddr, int port) {
		// Constructor used to create message to send
		identifier = "OVERLAY_NODE_SENDS_REGISTRATION";
		messageType = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		
		inetAddress = inetAddr.getAddress();
		this.serverSocketPort = port;
		
	}
	
	public OverlayNodeSendsRegistration(byte[] message) throws IOException {
		// Constructor used to create message from received bytes
		identifier = "OVERLAY_NODE_SENDS_REGISTRATION";
		messageType = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		
		extractMessage(message);
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		
		int identifierLength = din.readInt();
		byte[] inetBytes = new byte[identifierLength];
		din.readFully(inetBytes);
		
		inetAddress = inetBytes;
		
		serverSocketPort = din.readInt();
		
		baInputStream.close();
		din.close();
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			/*
			 * 	byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION)
				byte: length of following "IP address" field
				byte[^^]: IP address; from InetAddress.getAddress()
				int: Port number
			 */
			
			dout.writeInt(messageType);
			
			byte[] inetAddrBytes = inetAddress;
			int elementLength = inetAddrBytes.length;
			
			dout.writeInt(elementLength);
			dout.write(inetAddrBytes);
			
			dout.writeInt(serverSocketPort);
			
			dout.flush();
			marshalledBytes = baOutputStream.toByteArray();
			
			baOutputStream.close();
			dout.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return marshalledBytes;
		
		
	}

	@Override
	public int getType() {

		return messageType;
	}
	
	public InetAddress getInetAddress() throws UnknownHostException {
		InetAddress temp = InetAddress.getByAddress(inetAddress);
		
		return temp;
	}
	
	public int getServerSocketPort() {
		return serverSocketPort;
	}

}
