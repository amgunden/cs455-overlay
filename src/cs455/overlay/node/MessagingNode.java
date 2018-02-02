package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;

public class MessagingNode implements Node{
	
	int nodeID;
	
	public MessagingNode() throws IOException {

		// 0 to use a port number that is automatically allocated.
		Thread serverThread = new Thread(new TCPServerThread(0));
		serverThread.start();
		
		// Initiate registration to Registry -- null refers to localhost
		Socket regSocket = TCPConnectionsCache.getInstance().createTCPConnection(null, 55555);
		//if regSocket is null there is an Error
		
		InetAddress addr = regSocket.getLocalAddress();
		
		int port = regSocket.getLocalPort();
		
		TCPConnection regConn = TCPConnectionsCache.getInstance().getTCPConByIpAddr(addr);
		
		TCPConnectionsCache.getInstance().addClientConnection(-1, regConn);

		OverlayNodeSendsRegistration regMessage = new OverlayNodeSendsRegistration(addr, port);
		
		TCPConnectionsCache.getInstance().sendMessage(-1, regMessage.getBytes());

	}
	
	public static void main(String[] args) throws IOException {
		
		MessagingNode messageNode = new MessagingNode();
		
		EventFactory.getInstance().setNode(messageNode);
		
	
		System.out.println("Test");

		
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
		System.out.println("onEvent entered");
		
		if(event instanceof RegistryReportsRegistrationStatus) {
			
			System.out.println("RegistryReportsRegistrationStatus Message Received");
			System.out.println( ((RegistryReportsRegistrationStatus) event).getInfoMessage());
			System.out.println(((RegistryReportsRegistrationStatus) event).getRegStatus());
			
		} 
		
	}
	


}
