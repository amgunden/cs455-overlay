package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsData;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

public class MessagingNode implements Node{
	
	int nodeID;
	
	public int getNodeID() {
		return nodeID;
	}

	int serverSocketPort;
	String registryHostname;
	RoutingTable routingTable;
	ArrayList<Integer> nodeIdList;
	
	private TCPConnectionsCache tcpConCache;
	
	private Thread serverThread;
	private Thread cmdParser;
	
	StatisticsCollectorAndDisplay stats;
	
	public MessagingNode(String regHostname, int port) throws IOException {

		tcpConCache = TCPConnectionsCache.getInstance();
		
		// 0 to use a port number that is automatically allocated.
		TCPServerThread tcpServer = new TCPServerThread(0);
		
		// Get port that the ServerSocket of this node is listening on
		serverSocketPort = tcpServer.getServerSocketPort();
		
		serverThread = new Thread(tcpServer);
		serverThread.start();
		
		cmdParser = new Thread(new InteractiveCommandParser(this));
		cmdParser.start();
		
		registryHostname = regHostname;
		
		register(registryHostname, port);

	}
	
	public static void main(String[] args) throws IOException {
		
		MessagingNode messageNode = new MessagingNode(args[0], Integer.parseInt(args[1]));
		
		EventFactory.getInstance().setNode(messageNode);
		
		
	
		//System.out.println("Test");

		
	}

	@Override
	public void onEvent(Event event) {
		
		//System.out.println("onEvent entered");
		
		if(event instanceof RegistryReportsRegistrationStatus) {
			//System.out.println("RegistryReportsRegistrationStatus Message Received");
			handleRegStatus( (RegistryReportsRegistrationStatus) event);
		} 
		else if(event instanceof RegistrySendsNodeManifest) {
			//System.out.println("RegistrySendsNodeManifest Message Received");
			try {
				handleNodeManifest( (RegistrySendsNodeManifest) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event instanceof RegistryRequestsTaskInitiate) {
			//System.out.println("RegistryRequestsTaskInitiate Message Received");
			try {
				handleTaskInitiate( (RegistryRequestsTaskInitiate) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event instanceof OverlayNodeSendsData) {
			//System.out.println("OverlayNodeSendsData Message Received");
			try {
				handleReceivedData( (OverlayNodeSendsData) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event instanceof RegistryRequestsTrafficSummary) {
			try {
				handleSummaryRequest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event instanceof RegistryReportsDeregistrationStatus) {
			try {
				handleDeregistrationStatus( (RegistryReportsDeregistrationStatus) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void handleRegStatus(RegistryReportsRegistrationStatus regStatus) {
		System.out.println( regStatus.getInfoMessage());
		//System.out.println( regStatus.getRegStatus());
		
		if(regStatus.getRegStatus() == -1) {
			// Registration Error
			// Error Handling
			System.out.println("Registration error: "+regStatus.getInfoMessage());
		}
		else {
			nodeID = regStatus.getRegStatus();
		}
		
		
	}
	
	public void handleNodeManifest(RegistrySendsNodeManifest manifest) throws IOException {
		
		stats =  new StatisticsCollectorAndDisplay(nodeID);
		
		routingTable = manifest.getRoutingTable();
		
		nodeIdList = manifest.getNodeIdList();
		
		boolean nodeConnectionSuccessful = connectToTableNodes();
		
		if(nodeConnectionSuccessful) {
			System.out.println("Overlay successfully setup");
		}
		else {
			System.out.println("Overlay not successfully setup");
		}
		
		sendManifestReply(nodeConnectionSuccessful);
		
		
		
	}
	
	public void handleTaskInitiate(RegistryRequestsTaskInitiate taskInit) throws IOException {
		//System.out.println("handleTaskInitiate method entered. Packet number: "+ taskInit.getNumOfPackets());
		int numOfPackets = taskInit.getNumOfPackets();
		sendPackets(numOfPackets);
		
	}
	
	public boolean connectToTableNodes() {
		
		boolean successful = true;
		
		for(int i=0; i<routingTable.getRoutingEntries().size(); i++) {
			
			// Initiate registration to MessagingNode
			Socket regSocket = TCPConnectionsCache.getInstance().createTCPConnection(routingTable.getRoutingEntries().get(i).getInetAddr(), routingTable.getRoutingEntries().get(i).getPort());;
			
			// Error detection
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
		int regIndex = tcpConCache.getIndexOfClientId(-1);
		
		// Get TCPConnection related to the registry
		TCPConnection regCon = tcpConCache.getClientConnections().get(regIndex);
		
		if( successful) {
			
			setupStatus = new NodeReportsOverlaySetupStatus(nodeID, "Overlay setup was successful");
			
			regCon.sendTCPMessage( setupStatus.getBytes());
		}
		else {
			setupStatus = new NodeReportsOverlaySetupStatus(-1, "Overlay setup was not successful");
			
			regCon.sendTCPMessage( setupStatus.getBytes());
		}
		
	}
	
	public void register(String registryName, int port) {
		// Initiate registration to Registry -- null refers to localhost
		Socket regSocket = tcpConCache.createTCPConnection(registryName, port);
		//if regSocket is null there is an Error

		// Add Registry node to the clientConnections Hashmap
		TCPConnection regConn = tcpConCache.getTCPConByIpAddr( regSocket.getInetAddress());
		tcpConCache.addClientConnection(-1, regConn);

		sendRegistrationMsg(regSocket);
	}
	
	
	public void sendRegistrationMsg( Socket regSocket) {
		
		//Build msg with local details to send to Registry, address of this node and port that server socket is listening on
		OverlayNodeSendsRegistration regMessage = new OverlayNodeSendsRegistration(regSocket.getLocalAddress(), serverSocketPort);
		tcpConCache.sendMessage(-1, regMessage.getBytes());
		
	}
		
	public void sendDeregistrationMsg() {
		//System.out.println("Enter sendDereg");
		
		int regIndex = tcpConCache.getIndexOfClientId(-1);
		TCPConnection regCon = tcpConCache.getClientConnections().get(regIndex);
		
		OverlayNodeSendsDeregistration deRegMessage = new OverlayNodeSendsDeregistration( regCon.getSocket().getLocalAddress(), regCon.getSocket().getLocalPort(), nodeID);
		tcpConCache.sendMessage(-1, deRegMessage.getBytes());
		//System.out.println("Deregistration Message sent to Reg");
		
	}
	
	public void handleDeregistrationStatus(RegistryReportsDeregistrationStatus msg) throws IOException {
		
		if(msg.getRegStatus() == -1) {
			// error
			System.out.println("De-Registration error: "+msg.getInfoMessage());
		}
		else {
			// exit and terminate process
			cmdParser.interrupt();
			serverThread.interrupt();
			tcpConCache.interuptAllTcpReceivers();
			tcpConCache.closeStreamTcpSenders();
			System.exit(0);
		}
	}
	
	public void sendPackets(int numOfPackets) throws IOException {
		
		//System.out.println("sendPackets entered. numOfPackets: "+numOfPackets);
		
		int numOfOtherNodes = nodeIdList.size();
		Random random = new Random();
		
		for(int i=0; i<numOfPackets; i++) {
			//System.out.println("Sending packet "+(i+1)+" of "+numOfPackets);
			
			int randIndex = random.nextInt(numOfOtherNodes);
			int destNodeId = nodeIdList.get(randIndex);
			
			int payload = random.nextInt();
			
			// destination, source, payload
			OverlayNodeSendsData newMsg = new OverlayNodeSendsData(destNodeId, nodeID, payload);
			
			RoutingEntry nextHop = findNextHop(destNodeId);
			
			int clientCon = tcpConCache.getIndexOfClientId( nextHop.getNodeId() );
			
			// Send message
			tcpConCache.getClientConnections().get(clientCon).sendTCPMessage(newMsg.getBytes());
			
			stats.incrementSend();
			stats.addToSendSum(payload);
			
			//System.out.println("Sending packet to "+ destNodeId+"; next hop "+ nextHop.getNodeId());
			//System.out.println();
			
		}
		
		sendTaskComplete();
		
	}
	
	public RoutingEntry findNextHop(int nodeId) {
		
		ArrayList<RoutingEntry> routingEntries = routingTable.getRoutingEntries();
		int entSize = routingEntries.size();
		//System.out.println("Entered findNextHop() to find next hop to node: "+nodeId);
		
		for(int i=0; i< entSize; i++) {
			
			//System.out.println("Entered findNextHop For loop");
			
			// comparing nodeId of entry at i to nodeId and entry at i+1. Want to find where i is less than nodeId and i+1 is more
			if(routingEntries.get(i).getNodeId() == nodeId) {
				//System.out.print("Return entry the is = to dest");
				return routingEntries.get(i);
			}
			else if(routingEntries.get(i).getNodeId() < nodeId && nodeId < routingEntries.get((i+1)%routingEntries.size()).getNodeId() ) {
				//System.out.println("Returning entry that is the closest without going over");
				return routingEntries.get(i);
			}
			
		}
		//System.out.println("Returning last in array");
		return routingEntries.get( entSize-1 );
		
	}
	
	public void handleReceivedData(OverlayNodeSendsData dataMsg) throws IOException {
		
		if( dataMsg.getDestId() == nodeID) {
			
			stats.incrementReceive();
			stats.addToReceiveSum(dataMsg.getPayload());
			//System.out.println("Received packet from node "+ dataMsg.getSourceId());
			//System.out.println();
			
		}
		else {
			
			RoutingEntry nextHop = findNextHop(dataMsg.getDestId());
			//System.out.println("NextHop node: "+nextHop.getNodeId());
			int clientCon = tcpConCache.getIndexOfClientId( nextHop.getNodeId());
			
			dataMsg.addHop(nodeID);
			
			tcpConCache.getClientConnections().get(clientCon).sendTCPMessage(dataMsg.getBytes());
			
			stats.incrementRelay();
			
			//System.out.println("Relaying: src "+ dataMsg.getSourceId()+"; dest "+dataMsg.getDestId()+"; next Node "+ nextHop.getNodeId());
			//System.out.println();
		}
		
	}
	
	public void sendTaskComplete() throws IOException {
		
		TCPConnection con = tcpConCache.getClientConnections().get(0);
		
		OverlayNodeReportsTaskFinished msg = new OverlayNodeReportsTaskFinished( con.getInetAddress(), con.getPort(), nodeID);
		
		con.sendTCPMessage(msg.getBytes());
		
		//System.out.println("Sent Task Complete Message");
		
	}
	
	public void handleSummaryRequest() throws IOException {
		
		/*
	 	byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
		int: Assigned node ID
		int: Total number of packets sent (only the ones that were started/initiated by the node)
		int: Total number of packets relayed (received from a different node and forwarded)
		long: Sum of packet data sent (only the ones that were started by the node)
		int: Total number of packets received (packets with this node as final destination)
		long: Sum of packet data received (only packets that had this node as final destination)
		 */
		
		OverlayNodeReportsTrafficSummary msg = new OverlayNodeReportsTrafficSummary( nodeID, stats.getSendTracker(), stats.getRelayTracker(), stats.getSendSummation(), stats.getReceiveTracker(), stats.getReceiveSummation());
		
		TCPConnection con = tcpConCache.getClientConnections().get(0);
		
		con.sendTCPMessage( msg.getBytes());
		
		printCtrAndDiag();
		
		stats.resetCounters();
		
		System.out.println("----------------------------Reseting Counters-------------------------");
	}
	
	public void printCtrAndDiag() {
		System.out.println();	
		System.out.println("Node ID: "+nodeID+"\nPackets Sent: "+stats.getSendTracker()+"\nPackets Relayed: "+stats.getRelayTracker()+"\nPackets Received: "+stats.getReceiveTracker());
		System.out.println("Sent Sum: "+stats.getSendSummation()+"\nReceived Sum: "+stats.getReceiveSummation());
	}


}
