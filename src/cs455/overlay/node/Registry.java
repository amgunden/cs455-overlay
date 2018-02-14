package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

public class Registry implements Node{
	
	private int numOfEntriesInRouting;
	private TCPConnectionsCache tcpConCache;
	
	private StatisticsCollectorAndDisplay[] stats;
	
	private int nodesFinishedTask = 0;
	private int trafficSumsReported;

	public Registry(int port) throws IOException {
		
		tcpConCache = TCPConnectionsCache.getInstance();

		Thread serverThread = new Thread(new TCPServerThread(port));
		serverThread.start();
		
		Thread cmdParser = new Thread(new InteractiveCommandParser(this));
		cmdParser.start();
	}
	
	public Registry() {
		
	}

	public static void main(String[] args) throws IOException {
		
		int portNum = Integer.parseInt(args[0]);
		Registry reg = new Registry(portNum);
		EventFactory.getInstance().setNode(reg);
		

		

	}

	@Override
	public void onEvent(Event event) {
		
		if(event instanceof OverlayNodeSendsRegistration) {
			try {
				registerNode( (OverlayNodeSendsRegistration) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( event instanceof NodeReportsOverlaySetupStatus) {
			
			handleOverlaySetupStatus( (NodeReportsOverlaySetupStatus) event);
			
		}
		else if( event instanceof OverlayNodeReportsTaskFinished) {
			
			try {
				handleTaskReports( (OverlayNodeReportsTaskFinished) event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if( event instanceof OverlayNodeReportsTrafficSummary ) {
			
			handleTrafficReports( (OverlayNodeReportsTrafficSummary) event);
			
		}
		else if( event instanceof OverlayNodeSendsDeregistration) {
			
			handleDeregistration( (OverlayNodeSendsDeregistration) event);
			
		}
		
	}
	
	public void registerNode(OverlayNodeSendsRegistration nodeRegistration) throws IOException {
		
		// Checks to see if the node had previously registered and
		// ensures that the IP address in the message matches the address where the request originated.
		
		/*
		The registry issues an error message under two circumstances:
		• If the node had previously registered and has a valid entry in its registry.
		• If there is a mismatch in the address that is specified in the registration request and the IP
		address of the request (the socket’s input stream).
		*/
		
		System.out.println("OverlayNodeSendsRegistration Message Received");
		System.out.println("Nodes Server Socket is listening on: " + nodeRegistration.getServerSocketPort());
		
		try {
			System.out.println( nodeRegistration.getInetAddress().toString());
			
			TCPConnection tcpConnection = tcpConCache.getTCPConByIpAddr( nodeRegistration.getInetAddress());
			
			RegistryReportsRegistrationStatus regMsg = null;
			
			if( tcpConnection == null) {
				System.out.println("Returned TCPConnection is null");
			}
			else if( registerMismatch(nodeRegistration, tcpConnection) ) {
				regMsg = new RegistryReportsRegistrationStatus(-1, "Registration request was unsuccessful. There is a mismatch between the address that is specified in the registration request and the IP address related to this connection");
				
			}
			else if( registerDuplicate(nodeRegistration)) {
				regMsg = new RegistryReportsRegistrationStatus(-1, "Registration request was unsuccessful. This node has already been registered.");
				
			}
			else {
			
				tcpConnection.setServerSocketPort(nodeRegistration.getServerSocketPort());
	
				// IF NO ERROR
				// registry generates a unique identifier (between 0-127) for the node while
				// ensuring that there are no duplicate IDs being assigned.
				int newNodeID = generateUniqueID();
							
				// Add socket and connection info to clientConnections
				// DONE check
				tcpConCache.addClientConnection(newNodeID, tcpConnection);
				
				int clientCount = tcpConCache.getClientCount();
						
				regMsg = new RegistryReportsRegistrationStatus(newNodeID, "Registration request successful. The number of messaging nodes currently constituting the overlay is " + clientCount);
				
			}
			
			tcpConnection.sendTCPMessage(regMsg.getBytes());
			System.out.println("RegistryReportsRegistrationStatus Message Sent");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public boolean registerMismatch(OverlayNodeSendsRegistration nodeRegistration, TCPConnection tcpConnection) throws UnknownHostException {
		
		InetAddress socketAddr = tcpConnection.getInetAddress();
		InetAddress givenAddr = nodeRegistration.getInetAddress();
		
		// DONE change to .equals
		if( !socketAddr.equals(givenAddr)) {
			// Send failure message as there is a mismatch in the address specified vs addr in socket
			return true;
		}
		
		return false;
	}
	
	public boolean registerDuplicate(OverlayNodeSendsRegistration nodeRegistration) throws UnknownHostException {
		
		InetAddress givenAddr = nodeRegistration.getInetAddress();
		int port = nodeRegistration.getServerSocketPort();
		
		// DONE Check if node is already in ClientConnections
		if( tcpConCache.getClientByIpAddr(givenAddr, port) != null) {
				// The node had previously registered and has a valid entry in its registry.
			return true;
		}
		
		return false;
	}
	
	
	
	public int generateUniqueID() {
		
		// TO-DO check to make sure there isnt 128 nodes already
		
		Random random = new Random();
		
		// Generate int between 0 - 127
		int id = random.nextInt(128);
		
		// Test if random int is a Key in the HashMap
		// if it is then a new number is generated and the while loop runs again
		while( tcpConCache.getIndexOfClientId(id) != -1) {
			id = random.nextInt(128);
		}
		
		//return unique random number
		return id;
		
	}
	
	public void handleOverlaySetup(int numOfEntries) {
		
		// TODO Error if number of nodes < 2 * Nr (number of entries)
		
		numOfEntriesInRouting = numOfEntries;
		
		int numOfMsgNodes = tcpConCache.getClientCount();
		
		if( numOfMsgNodes < (2 * numOfEntries)) {
			System.out.println("There is not enough nodes to use a routing table of this size. Please use a smaller routing table.");
		}
		else {
			
			// Cycle through nodes
			
			for(int i=0; i < numOfMsgNodes; i++) {
				
				// Call method to create node's specific routing table message
				
				RoutingTable tableForNode = createRoutingTable(i, numOfEntries, numOfMsgNodes);
				
				int id = tcpConCache.getClientConnections().get(i).getNodeID();
				
				ArrayList<Integer> idList = tcpConCache.getIdList(id);
				
				sendRoutingTable(i, numOfEntries, tableForNode, idList);
				
				
			}
			
			
		}
		
	}
	
	public void sendRoutingTable(int index, int numOfEntries, RoutingTable tableForNode, ArrayList<Integer> idList) {
		
		// Create Message
		RegistrySendsNodeManifest manifestMsg = new RegistrySendsNodeManifest(numOfEntries, tableForNode, idList);
		
		byte[] msgBytes = manifestMsg.getBytes();
		
		// Send message
		TCPConnection recevingNode = tcpConCache.getClientConnections().get(index);
		
		try {
			recevingNode.sendTCPMessage(msgBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public RoutingTable createRoutingTable(int currentNodeIndex, int numInRoutingTable, int nodeCount) {
		
		// Nr = number in routing table
		// node distances, powers of 2; 2^0, 2^1, ..., 2^(Nr-1)
		// Wrap-around needed ---  (index + distance) mod (number of nodes)
			
		RoutingTable routingTable = new RoutingTable();
		
		for(int i=0; i<numInRoutingTable; i++) {

			int nextOffset = (int) Math.pow(2, i);
			int nextClient = (currentNodeIndex + nextOffset) % nodeCount;

			TCPConnection next = tcpConCache.getClientConnections().get(nextClient);
			
			RoutingEntry entry = new RoutingEntry(next.getNodeID(), next.getInetAddress(), next.getServerSocketPort());
			
			routingTable.addEntry(entry);
			
		}
		
		return routingTable;
		
	}
	
	public void handleOverlaySetupStatus(NodeReportsOverlaySetupStatus setupStatus) {
		
		if( setupStatus.getSuccessStatus() != -1) {
			System.out.println("Node "+setupStatus.getSuccessStatus()+" has successfully setup connections to the nodes in it's routing table");
		}
		else {
			System.out.println("A node has failed to connect to the nodes listed in it's routing table");
		}
		
		// Handle overlay setup failure
		
	}
	
	public void printRoutingTables() {
		
		int numOfMsgNodes = tcpConCache.getClientCount();
		
		for(int i=0; i < numOfMsgNodes; i++) {
			
			// Call method to create node's specific routing table message
			
			System.out.println();
			System.out.println("Routing Table for Node: " + tcpConCache.getClientConnections().get(i).getNodeID());
			
			RoutingTable tableForNode = createRoutingTable(i, numOfEntriesInRouting, numOfMsgNodes);
			ArrayList<RoutingEntry> routingEntries = tableForNode.getRoutingEntries();
			
			for(int j=0; j<routingEntries.size(); j++) {
				
				System.out.println("Routing Entry #" + (j+1) +": IP Address: "+ routingEntries.get(j).getInetAddr().getHostAddress()+ "   Port Number: " + routingEntries.get(j).getPort()+"   Node ID: "+ routingEntries.get(j).getNodeId()	);
				
			}
			
			System.out.println();
			System.out.println();
			
			
		}
		
	}
	
	public void printMessagingNodes() {
		
		int numOfMsgNodes = tcpConCache.getClientCount();
		System.out.println();
		System.out.println("Messaging Node List:");
		
		for(int i=0; i < numOfMsgNodes; i++) {
			
			TCPConnection tempCon = tcpConCache.getClientConnections().get(i);
			
			System.out.println("Node "+(i+1)+": Hostname: "+tempCon.getInetAddress().getHostName()+"   Port: "+tempCon.getPort()+"   Node ID: "+tempCon.getNodeID());
			
		}
		System.out.println();
		
	}
	
	public void handleStartCmd(int numOfPackets) throws IOException {
		
		nodesFinishedTask =0;
		
		RegistryRequestsTaskInitiate taskInit = new RegistryRequestsTaskInitiate(numOfPackets);
		
		tcpConCache.sendMsgToAllClients( taskInit.getBytes());
		
	}
	
	public void handleTaskReports(OverlayNodeReportsTaskFinished msg) throws IOException {
		
		System.out.println("Node "+msg.getNodeId()+" has reported their task is finished.");
		nodesFinishedTask++;
		int numClients = tcpConCache.getClientCount();
		//System.out.println(nodesFinishedTask);
		//System.out.println(numClients);
		//System.out.println(nodesFinishedTask == numClients);
		if( nodesFinishedTask == numClients ) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			requestTrafficSummary();
		}
		
	}
	
	public void requestTrafficSummary() throws IOException {
		
		System.out.println("requestTrafficSummary Entered");
		
		RegistryRequestsTrafficSummary summaryReq = new RegistryRequestsTrafficSummary();
		
		tcpConCache.sendMsgToAllClients( summaryReq.getBytes() );
		
		stats = new StatisticsCollectorAndDisplay[ tcpConCache.getClientCount() ];
		
		trafficSumsReported =0;
		
		System.out.println("requestTrafficSummary Finished");
	}
	
	public void handleTrafficReports(OverlayNodeReportsTrafficSummary msg){
		
		System.out.println("handleTrafficSummary Entered");
		
		int nodeId = msg.getNodeId();
		
		int index = tcpConCache.getIndexOfClientId(nodeId);
		
		trafficSumsReported++;
		
		StatisticsCollectorAndDisplay nodeStats = new StatisticsCollectorAndDisplay(nodeId, msg.getPacketsSent(), msg.getPacketsReceived(), msg.getPacketsRelayed(), msg.getSumOfSent(), msg.getSumOfReceived());
		
		stats[index] = nodeStats;
		
		if( trafficSumsReported == tcpConCache.getClientCount()) {
			printSummaries();
		}
		
	}
	
	public void printSummaries() {
		
		int sumSent=0;
		int sumRec=0;
		int sumRelayed=0;
		long sumPacSent=0;
		long sumPacRec=0;
		
		System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %n", "Node ID" , "Packets Sent", "PacketsReceived", "PacketsRelayed", "Sum of Packets Sent", "Sum of Packets Received");
		//System.out.println("Node ID\tPackets Sent\tPacketsReceived\tPacketsRelayed\tSum of Packets Sent\tSum of Packets Received");	
		
		for(int i=0; i< stats.length; i++) {
			sumSent += stats[i].getSendTracker();
			sumRec += stats[i].getReceiveTracker();
			sumRelayed += stats[i].getRelayTracker();
			sumPacSent += stats[i].getSendSummation();
			sumPacRec += stats[i].getReceiveSummation();
			
			stats[i].print();
			
		}
		
		System.out.println();
		System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %n", "Sum", sumSent, sumRec, sumRelayed, sumPacSent, sumPacRec);
		//System.out.println("Sum\t"+sumSent+"\t"+sumRec+"\t"+sumRelayed+"\t"+sumPacSent+"\t"+sumPacRec);
	}
	
	public void handleDeregistration(OverlayNodeSendsDeregistration msg) {
		
		// Registry should check to see that request is a valid one by checking
		// (1) where the message originated
		// (2) whether this node was previously registered
		// Error messages should be returned in case of a mismatch in the addresses or if the messaging node is not registered with the overlay.
		
		int deregNodeId = msg.getNodeId();
		
		int index = tcpConCache.getIndexOfClientId(deregNodeId);
		
		if( index == -1) {
			// TODO handle de-reg errors
			// Error: node was not found in clientConnections array
			
			// Send error de-registration status
		}
		
		TCPConnection tcpCon = tcpConCache.getClientConnections().get(index);
				
		if( msg.getInetAddress() != tcpCon.getInetAddress().getAddress() ) {
			// Error socket address doesnt match inet sent
			
		}
		
		tcpConCache.removeClient(deregNodeId);
		
		// close the socket or have msgNode close it
		
		RegistryReportsDeregistrationStatus deregStatus = new RegistryReportsDeregistrationStatus(deregNodeId, "De-Registration request successful. The number of messaging nodes currently constituting the overlay is " + tcpConCache.getClientCount());
		
		
		try {
			
			tcpCon.sendTCPMessage( deregStatus.getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tcpCon.interuptTcpReceiver();
		try {
			tcpCon.closeSenderStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("RegistryReportsDeRegistrationStatus Message Sent");
		
	}

}
