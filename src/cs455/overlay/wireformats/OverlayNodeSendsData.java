package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OverlayNodeSendsData  implements Event{
	
	private int messageType = Protocol.OVERLAY_NODE_SENDS_DATA;
	private String identifier = "OVERLAY_NODE_SENDS_DATA";
	
	int destId;
	int sourceId;
	
	int payload;
	
	ArrayList<Integer> hops = new ArrayList<Integer>();

	public OverlayNodeSendsData(int dest, int source, int payload) {
		this.destId = dest;
		this.sourceId = source;
		this.payload = payload;
		
	}

	public OverlayNodeSendsData(byte[] message) throws IOException {
		
		extractMessage(message);
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		destId = din.readInt();
		sourceId = din.readInt();
		payload = din.readInt();
		
		int numOfHops = din.readInt();
		
		for(int i=0; i<numOfHops; i++) {
			int temp = din.readInt();
			hops.add(temp);
		}
		
		baInputStream.close();
		din.close();
		
	}
	
	public int getDestId() {
		return destId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public int getPayload() {
		return payload;
	}

	public ArrayList<Integer> getHops() {
		return hops;
	}
	
	public void addHop(int nodeId) {
		hops.add(nodeId);
	}

	@Override
	public byte[] getBytes() {
		
		/*
		 * 	byte: Message type; OVERLAY_NODE_SENDS_DATA
			int: Destination ID
			int: Source ID
			int: Payload
			int: Dissemination trace field length (number of hops)
			int[^^]: Dissemination trace comprising nodeIDs that the packet traversed through
		 */

		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			
			dout.writeInt(destId);
			dout.writeInt(sourceId);
			
			dout.writeInt(payload);
			
			int numOfHops = hops.size();
			
			dout.writeInt(numOfHops);
			
			for(int i=0; i<numOfHops; i++) {
				dout.writeInt(hops.get(i));
			}
			
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
