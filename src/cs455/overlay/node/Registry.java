package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;

public class Registry implements Node{
	

	public Registry(int port) throws IOException {
		// TODO Auto-generated constructor stub
		Thread serverThread = new Thread(new TCPServerThread(port));
		serverThread.start();
	}

	public static void main(String[] args) throws IOException {
		
		int portNum = Integer.parseInt(args[0]);
		Registry reg = new Registry(portNum);
		EventFactory.getInstance().setNode(reg);
		

		

	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
		if(event instanceof OverlayNodeSendsRegistration) {
			try {
				registerNode(event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
	}
	
	public void registerNode(Event nodeRegistration) throws IOException {
		
		// Checks to see if the node had previously registered and
		// ensures that the IP address in the message matches the address where the request originated.
		
		/*
		The registry issues an error message under two circumstances:
		• If the node had previously registered and has a valid entry in its registry.
		• If there is a mismatch in the address that is specified in the registration request and the IP
		address of the request (the socket’s input stream).
		*/
		
		System.out.println("OverlayNodeSendsRegistration Message Received");
		System.out.println(((OverlayNodeSendsRegistration) nodeRegistration).getPort());
		
		try {
			System.out.println(((OverlayNodeSendsRegistration) nodeRegistration).getInetAddress().toString());
			
			TCPConnection tcpConnection = TCPConnectionsCache.getInstance().getTCPConByIpAddr(((OverlayNodeSendsRegistration) nodeRegistration).getInetAddress());
			
			InetAddress socketAddr = tcpConnection.getInetAddress();
			InetAddress givenAddr = ((OverlayNodeSendsRegistration) nodeRegistration).getInetAddress();
			
			// TODO change to .equals ?
			if( socketAddr != givenAddr) {
				// Send failure message as there is a mismatch in the address specified vs addr in socket
				
			}
			
			// Check if node is already in ClientConnections HashMap
			if(TCPConnectionsCache.getInstance().getClientByIpAddr(givenAddr) != null) {
				// The node had previously registered and has a valid entry in its registry.
			}
			
			// IF NO ERROR
			// registry generates a unique identifier (between 0-127) for the node while
			// ensuring that there are no duplicate IDs being assigned.
			int newNodeID = generateUniqueID();
						
			// Add socket and connection info to clientConnections
			// TODO check
			TCPConnectionsCache.getInstance().addClientConnection(newNodeID, tcpConnection);
			
			RegistryReportsRegistrationStatus regMsg = new RegistryReportsRegistrationStatus(newNodeID, "Registration request successful. The number of messaging nodes currently constituting the overlay is X");
			
			tcpConnection.sendTCPMessage(regMsg.getBytes());
			System.out.println("RegistryReportsRegistrationStatus Message Sent");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public int generateUniqueID() {
		
		// TO-DO check to make sure there isnt 128 nodes already
		
		Random random = new Random();
		
		// Generate int between 0 - 127
		int id = random.nextInt(128);
		
		// Test if random int is a Key in the HashMap
		// if it is then a new number is generated and the while loop runs again
		while( TCPConnectionsCache.getInstance().getClientConnections().containsKey(id)) {
			id = random.nextInt(128);
		}
		
		//return unique random number
		return id;
		
	}
	

	
	

}
