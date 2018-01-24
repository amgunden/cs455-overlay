package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class OverlayNodeSendsRegistration  implements Event{
	
	private int type;
	private InetAddress inetAddress;
	private int port;
	

//	private long timestamp;
	private String identifier;
//	private int tracker;


	public OverlayNodeSendsRegistration() {
		// TODO Auto-generated constructor stub
		identifier = "OVERLAY_NODE_SENDS_REGISTRATION";
		type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		
		
		
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
			
			dout.writeInt(type);
			
			byte[] inetAddrBytes = inetAddress.getAddress();
			int elementLength = inetAddrBytes.length;
			
			dout.writeInt(elementLength);
			dout.write(inetAddrBytes);
			
			dout.writeInt(port);
			
			dout.flush();
			marshalledBytes = baOutputStream.toByteArray();
			
			baOutputStream.close();
			dout.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return marshalledBytes;
		
		
	}

}
