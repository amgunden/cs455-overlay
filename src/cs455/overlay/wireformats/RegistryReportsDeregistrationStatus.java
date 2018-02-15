package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryReportsDeregistrationStatus  implements Event{

	private int messageType = Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
	private String identifier = "REGISTRY_REPORTS_DEREGISTRATION_STATUS";
	
	private int regStatus;
	private String infoMessage;
	
	public RegistryReportsDeregistrationStatus(int regStatus, String infoMessage) {
		this.regStatus = regStatus;
		this.infoMessage = infoMessage;
	}
	
	public RegistryReportsDeregistrationStatus(byte[] msg) throws IOException {
		extractMessage(msg);
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		regStatus = din.readInt();
		
		int infoMsgLength = din.readInt();
		byte[] infoMsgBytes = new byte[infoMsgLength];
		din.readFully(infoMsgBytes);
		
		infoMessage = new String(infoMsgBytes);

		
		baInputStream.close();
		din.close();
		
	}

	@Override
	public byte[] getBytes() {
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			/*
			 * 	byte: Message type (REGISTRY_REPORTS_DEREGISTRATION_STATUS)
				int: Success status; Assigned ID if successful, -1 in case of a failure
				byte: Length of following "Information string" field
				byte[^^]: Information string; ASCII charset
			 */
			
			dout.writeInt(messageType);
			
			dout.writeInt(regStatus);
			
			byte[] infoMsgBytes = infoMessage.getBytes();
			int elementLength = infoMsgBytes.length;
			
			dout.writeInt(elementLength);
			dout.write(infoMsgBytes);
			
			
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
	
	public int getMessageType() {
		return messageType;
	}

	public int getRegStatus() {
		return regStatus;
	}

	public String getInfoMessage() {
		return infoMessage;
	}

}
