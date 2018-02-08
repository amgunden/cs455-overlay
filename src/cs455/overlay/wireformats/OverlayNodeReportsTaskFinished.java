package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class OverlayNodeReportsTaskFinished  implements Event{
	
	private int messageType = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
	private String identifier = "OVERLAY_NODE_REPORTS_TASK_FINISHED";
	
	private byte[] address;
	private int port;
	private int nodeId;

	public OverlayNodeReportsTaskFinished(InetAddress inetAddr, int port, int nodeId) {
		address = inetAddr.getAddress();
		this.port = port;
		this.nodeId = nodeId;
	}
	
	public OverlayNodeReportsTaskFinished(byte[] message) throws IOException {
		extractMessage(message);
	}
	
	public byte[] getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getNodeId() {
		return nodeId;
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		
		int identifierLength = din.readInt();
		byte[] inetBytes = new byte[identifierLength];
		din.readFully(inetBytes);
		
		address = inetBytes;
		
		port = din.readInt();
		nodeId = din.readInt();
		
		baInputStream.close();
		din.close();
	}

	@Override
	public byte[] getBytes() {
		
		/*
		 	byte: Message type; OVERLAY_NODE_REPORTS_TASK_FINISHED
			byte: length of following "IP address" field
			byte[^^]: Node IP address:
			int: Node Port number:
			int: nodeID
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			
			byte[] inetAddrBytes = address;
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

}
