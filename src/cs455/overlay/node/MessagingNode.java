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
	String registry;
	
	public MessagingNode(String regHostname, int port) throws IOException {

		// 0 to use a port number that is automatically allocated.
		Thread serverThread = new Thread(new TCPServerThread(0));
		serverThread.start();
		
		registry = regHostname;
		
		register(registry, port);

	}
	
	public static void main(String[] args) throws IOException {
		
		MessagingNode messageNode = new MessagingNode(args[0], Integer.parseInt(args[1]));
		
		EventFactory.getInstance().setNode(messageNode);
		
	
		System.out.println("Test");

		
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
		System.out.println("onEvent entered");
		
		if(event instanceof RegistryReportsRegistrationStatus) {
			System.out.println("RegistryReportsRegistrationStatus Message Received");
			handleRegStatus( (RegistryReportsRegistrationStatus) event);
		} 
		
	}
	
	public void handleRegStatus(RegistryReportsRegistrationStatus regStatus) {
		System.out.println( regStatus.getInfoMessage());
		System.out.println( regStatus.getRegStatus());
		
		if(regStatus.getRegStatus() == -1) {
			// Registration Error
			// TODO Error Handling
			System.out.println(regStatus.getInfoMessage());
		}
		else {
			nodeID = regStatus.getRegStatus();
		}
		
		
	}
	
	public void register(String registryName, int port) {
		// Initiate registration to Registry -- null refers to localhost
		Socket regSocket = TCPConnectionsCache.getInstance().createTCPConnection(registryName, port);
		//if regSocket is null there is an Error

		// Add Registry node to the clientConnections Hashmap
		TCPConnection regConn = TCPConnectionsCache.getInstance().getTCPConByIpAddr( regSocket.getInetAddress());
		TCPConnectionsCache.getInstance().addClientConnection(-1, regConn);

		//Build msg with local details to send to Registry
		OverlayNodeSendsRegistration regMessage = new OverlayNodeSendsRegistration(regSocket.getLocalAddress(), regSocket.getLocalPort());
		TCPConnectionsCache.getInstance().sendMessage(-1, regMessage.getBytes());
	}
	


}
