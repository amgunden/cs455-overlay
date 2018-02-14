package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class OverlayNodeSendsDeregistration  implements Event{
	
	private int messageType = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
	private String identifier = "OVERLAY_NODE_SENDS_DEREGISTRATION";
	
	private byte[] inetAddress;
	
	private int port;
	private int nodeId;

	public OverlayNodeSendsDeregistration(InetAddress inet, int port, int nodeId) {
		this.inetAddress = inet.getAddress();
		this.port = port;
		this.nodeId = nodeId;
	}
	
	public OverlayNodeSendsDeregistration(byte[] msg) throws IOException {
		extractMessage(msg);
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		
		int identifierLength = din.readInt();
		byte[] inetBytes = new byte[identifierLength];
		din.readFully(inetBytes);
		
		inetAddress = inetBytes;
		
		port = din.readInt();
		
		nodeId = din.readInt();
		
		baInputStream.close();
		din.close();
	}

	@Override
	public byte[] getBytes() {
		
		/*
		 	byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
			byte: length of following "IP address" field
			byte[^^]: IP address; from InetAddress.getAddress()
			int: Port number
			int: assigned Node ID
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			
			byte[] inetAddrBytes = inetAddress;
			int elementLength = inetAddrBytes.length;
			
			dout.writeInt(elementLength);
			dout.write(inetAddrBytes);
			
			dout.writeInt(port);
			dout.writeInt(nodeId);
			
			dout.flush();
			marshalledBytes = baOutputStream.toByteArray();
			
			baOutputStream.close();
			dout.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return marshalledBytes;
	}

	@Override
	public int getType() {
		return messageType;
	}
	
	public byte[] getInetAddress() {
		return inetAddress;
	}

	public int getPort() {
		return port;
	}

	public int getNodeId() {
		return nodeId;
	}

}
