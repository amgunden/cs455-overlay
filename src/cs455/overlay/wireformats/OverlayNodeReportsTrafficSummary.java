package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary  implements Event{
	
	private int messageType = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
	private String identifier = "OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY";
	
	private int nodeId;
	private int packetsSent;
	private int packetsRelayed;
	private long sumOfSent;
	private int packetsReceived;
	private long sumOfReceived; 

	public int getNodeId() {
		return nodeId;
	}

	public int getPacketsSent() {
		return packetsSent;
	}

	public int getPacketsRelayed() {
		return packetsRelayed;
	}

	public long getSumOfSent() {
		return sumOfSent;
	}

	public int getPacketsReceived() {
		return packetsReceived;
	}

	public long getSumOfReceived() {
		return sumOfReceived;
	}

	public OverlayNodeReportsTrafficSummary(int id, int sent, int relayed, long sumSent, int received, long sumReceived) {
		this.nodeId = id;
		this.packetsSent = sent;
		this.packetsRelayed = relayed;
		this.sumOfSent = sumSent;
		this.packetsReceived = received;
		this.sumOfReceived = sumReceived;
	}
	
	public OverlayNodeReportsTrafficSummary(byte[] msg) throws IOException {
		extractMessage(msg);
	}
	
	public void extractMessage(byte[] msg) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(msg);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		
		nodeId = din.readInt();
		packetsSent = din.readInt();
		packetsRelayed = din.readInt();
		sumOfSent = din.readLong();
		packetsReceived = din.readInt();
		sumOfReceived = din.readLong();
		
		baInputStream.close();
		din.close();
		
	}

	@Override
	public byte[] getBytes() {
		
		/*
		 	byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
			int: Assigned node ID
			int: Total number of packets sent (only the ones that were started/initiated by the node)
			int: Total number of packets relayed (received from a different node and forwarded)
			long: Sum of packet data sent (only the ones that were started by the node)
			int: Total number of packets received (packets with this node as final destination)
			long: Sum of packet data received (only packets that had this node as final destination)
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			dout.writeInt(nodeId);
			dout.writeInt(packetsSent);
			dout.writeInt(packetsRelayed);
			
			dout.writeLong(sumOfSent);
			
			dout.writeInt(packetsReceived);
			
			dout.writeLong(sumOfReceived);
			
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
