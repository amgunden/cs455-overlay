package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

public class MessagingNode implements Node{
	
	int nodeID;
	int serverSocketPort;
	String registryHostname;
	RoutingTable routingTable;
	ArrayList<Integer> nodeIdList;
	
	public MessagingNode(String regHostname, int port) throws IOException {

		// 0 to use a port number that is automatically allocated.
		TCPServerThread tcpServer = new TCPServerThread(0);
		
		// Get port that the ServerSocket of this node is listening on
		serverSocketPort = tcpServer.getServerSocketPort();
		
		Thread serverThread = new Thread(tcpServer);
		serverThread.start();
		
		registryHostname = regHostname;
		
		register(registryHostname, port);

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
		else if(event instanceof RegistrySendsNodeManifest) {
			System.out.println("RegistrySendsNodeManifest Message Received");
			try {
				handleNodeManifest( (RegistrySendsNodeManifest) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public void handleNodeManifest(RegistrySendsNodeManifest manifest) throws IOException {
		
		routingTable = manifest.getRoutingTable();
		
		nodeIdList = manifest.getNodeIdList();
		
		boolean nodeConnectionSuccessful = connectToTableNodes();
		
		sendManifestReply(nodeConnectionSuccessful);
		
	}
	
	public boolean connectToTableNodes() {
		
		boolean successful = true;
		
		for(int i=0; i<routingTable.getRoutingEntries().size(); i++) {
			
			// Initiate registration to MessagingNode
			Socket regSocket = TCPConnectionsCache.getInstance().createTCPConnection(routingTable.getRoutingEntries().get(i).getInetAddr(), routingTable.getRoutingEntries().get(i).getPort());;
			
			// TODO error detection
			if(regSocket == null) {
				// Socket failed to create
				successful = false;
			}
			else {
				TCPConnection tcpConn = TCPConnectionsCache.getInstance().getTCPConByIpAddr(routingTable.getRoutingEntries().get(i).getInetAddr());
				TCPConnectionsCache.getInstance().addClientConnection(routingTable.getRoutingEntries().get(i).getNodeId(), tcpConn);
				
			}
						
		}
		
		return successful;
		
	}
	
	public void sendManifestReply(boolean successful) throws IOException {
		
		NodeReportsOverlaySetupStatus setupStatus;
		
		// Get index of registry in the clientConnections ArrayList
		int regIndex = TCPConnectionsCache.getInstance().getIndexOfClientId(-1);
		
		// Get TCPConnection related to the registry
		TCPConnection regCon = TCPConnectionsCache.getInstance().getClientConnections().get(regIndex);
		
		if( successful) {
			
			setupStatus = new NodeReportsOverlaySetupStatus(nodeID, "Overlay setup was successful");
			
			regCon.sendTCPMessage( setupStatus.getBytes());
		}
		else {
			setupStatus = new NodeReportsOverlaySetupStatus(-1, "Overlay setup was successful");
			
			regCon.sendTCPMessage( setupStatus.getBytes());
		}
		
	}
	
	public void register(String registryName, int port) {
		// Initiate registration to Registry -- null refers to localhost
		Socket regSocket = TCPConnectionsCache.getInstance().createTCPConnection(registryName, port);
		//if regSocket is null there is an Error

		// Add Registry node to the clientConnections Hashmap
		TCPConnection regConn = TCPConnectionsCache.getInstance().getTCPConByIpAddr( regSocket.getInetAddress());
		TCPConnectionsCache.getInstance().addClientConnection(-1, regConn);

		sendRegistrationMsg(regSocket);
	}
	
	
	public void sendRegistrationMsg( Socket regSocket) {
		
		//Build msg with local details to send to Registry, address of this node and port that server socket is listening on
		OverlayNodeSendsRegistration regMessage = new OverlayNodeSendsRegistration(regSocket.getLocalAddress(), serverSocketPort);
		TCPConnectionsCache.getInstance().sendMessage(-1, regMessage.getBytes());
		
		
	}


}
