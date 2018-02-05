package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiate  implements Event{
	
	private int messageType = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
	private String identifier = "REGISTRY_REQUESTS_TASK_INITIATE";

	private int numOfPackets;
	
	public RegistryRequestsTaskInitiate(int numOfPackets) {
		this.numOfPackets = numOfPackets;
	}
	
	public RegistryRequestsTaskInitiate(byte[] msg) {
		
	}
	
	public int getNumOfPackets() {
		return numOfPackets;
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		numOfPackets = din.readInt();

		
		baInputStream.close();
		din.close();
		
	}

	@Override
	public byte[] getBytes() {
		
		/*
		 	byte: Message type; REGISTRY_REQUESTS_TASK_INITIATE
			int: Number of data packets to send
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			dout.writeInt(numOfPackets);
			
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
