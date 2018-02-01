package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cs455.overlay.node.Node;

public class EventFactory {
	
	Node node;
	
	// Singelton based off code pattern from https://sourcemaking.com/design_patterns/singleton/java/1
	
	private EventFactory() {};

	private static class EventFactoryHolder {
		private static final EventFactory INSTANCE = new EventFactory();
	}
	
	public static EventFactory getInstance() {
		return EventFactoryHolder.INSTANCE;
	}
	
	public void getEvent(byte[] incomingMessage) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(incomingMessage);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		int type = din.readInt();
		
		din.close();
		baInputStream.close();
		
		
		if(type == 2) {
			node.onEvent( new OverlayNodeSendsRegistration(incomingMessage) );
			//return new OverlayNodeSendsRegistration(incomingMessage);
		}
		else if(type == 3) {
			node.onEvent( new RegistryReportsRegistrationStatus(incomingMessage) );
			//return new RegistryReportsRegistrationStatus();
		}
		else if(type == 4) {
			node.onEvent( new OverlayNodeSendsDeregistration() );
			//return new OverlayNodeSendsDeregistration();
		}
		else if(type == 5) {
			node.onEvent( new RegistryReportsDeregistrationStatus() );
			//return new RegistryReportsDeregistrationStatus();
		}
		else if(type == 6) {
			node.onEvent( new RegistrySendsNodeManifest(incomingMessage) );
			//return new RegistrySendsNodeManifest();
		}
		else if(type == 7) {
			node.onEvent( new NodeReportsOverlaySetupStatus() );
			//return new NodeReportsOverlaySetupStatus();
		}
		else if(type == 8) {
			node.onEvent( new RegistryRequestsTaskInitiate() );
			//return new RegistryRequestsTaskInitiate();
		}
		else if(type == 9) {
			node.onEvent( new OverlayNodeSendsData() );
			//return new OverlayNodeSendsData();
		}
		else if(type == 10) {
			node.onEvent( new OverlayNodeReportsTaskFinished() );
			//return new OverlayNodeReportsTaskFinished();
		}
		else if(type == 11) {
			node.onEvent( new RegistryRequestsTrafficSummary());
			//return new RegistryRequestsTrafficSummary();
		}
		else if(type == 12) {
			node.onEvent( new OverlayNodeReportsTrafficSummary() );
			//return new OverlayNodeReportsTrafficSummary();
		}

		
	}
	
	public void setNode(Node node) {
		
		this.node = node;
		
	}

}
