package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTrafficSummary  implements Event{
	
	private int messageType = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
	private String identifier = "REGISTRY_REQUESTS_TRAFFIC_SUMMARY";

	public RegistryRequestsTrafficSummary() {
		
	}
	
	public RegistryRequestsTrafficSummary(byte[] msg) {
		
	}
	
	public void extractMessage(byte[] msg) {
		
	}

	@Override
	public byte[] getBytes() {
		
		/*
			byte: Message Type; REGISTRY_REQUESTS_TRAFFIC_SUMMARY
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			
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
		return 0;
	}

}
