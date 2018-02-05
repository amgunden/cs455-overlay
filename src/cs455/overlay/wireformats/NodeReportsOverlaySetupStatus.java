package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeReportsOverlaySetupStatus implements Event{

	private int messageType = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
	private String identifier = "NODE_REPORTS_OVERLAY_SETUP_STATUS";
	
	private String infoMsg;
	private int successStatus;
	
	
	public NodeReportsOverlaySetupStatus(int successStatus, String infoMsg) {
		
		this.successStatus = successStatus;
		this.infoMsg = infoMsg;
		
	}
	
	public NodeReportsOverlaySetupStatus(byte[] message) throws IOException {
		
		extractMessage(message);
		
	}

	public int getSuccessStatus() {
		return successStatus;
	}

	public void setSuccessStatus(int successStatus) {
		this.successStatus = successStatus;
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		successStatus = din.readInt();
		
		int infoMsgLength = din.readInt();
		byte[] infoMsgBytes = new byte[infoMsgLength];
		din.readFully(infoMsgBytes);
		
		infoMsg = new String(infoMsgBytes);

		
		baInputStream.close();
		din.close();
		
	}
	
	@Override
	public byte[] getBytes() {
		
		/*
		  	byte: Message type (NODE_REPORTS_OVERLAY_SETUP_STATUS)
			int: Success status; Assigned ID if successful, -1 in case of a failure
			byte: Length of following "Information string" field
			byte[^^]: Information string; ASCII charset
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			
			dout.writeInt(successStatus);
			
			byte[] infoMsgBytes = infoMsg.getBytes();
			int elementLength = infoMsgBytes.length;
			
			dout.writeInt(elementLength);
			dout.write(infoMsgBytes);
			
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
