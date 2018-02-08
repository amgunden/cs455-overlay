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
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

public class Registry implements Node{
	
	private int numOfEntriesInRouting;
	private TCPConnectionsCache tcpConCache;

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
		// TODO Auto-generated method stub
		
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
			
			handleTaskReports( (OverlayNodeReportsTaskFinished) event);
			
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
			
			if( tcpConnection == null) {
				System.out.println("Returned TCPConnection is null");
			}
			
			tcpConnection.setServerSocketPort(nodeRegistration.getServerSocketPort());
			/*
			 Exception in thread "Thread-2" java.lang.NullPointerException
				at cs455.overlay.node.Registry.registerNode(Registry.java:90)
				at cs455.overlay.node.Registry.onEvent(Registry.java:56)
				at cs455.overlay.wireformats.EventFactory.getEvent(EventFactory.java:38)
				at cs455.overlay.transport.TCPConnection.handleReceivedMessage(TCPConnection.java:89)
				at cs455.overlay.transport.TCPConnection$TCPReceiverThread.run(TCPConnection.java:115)
				at java.lang.Thread.run(Thread.java:748) 
			 */
			
			InetAddress socketAddr = tcpConnection.getInetAddress();
			InetAddress givenAddr = nodeRegistration.getInetAddress();
			
			// TODO change to .equals ?
			if( socketAddr != givenAddr) {
				// Send failure message as there is a mismatch in the address specified vs addr in socket
				
			}
			
			// Check if node is already in ClientConnections HashMap
			if( tcpConCache.getClientByIpAddr(givenAddr) != null) {
				// The node had previously registered and has a valid entry in its registry.
			}
			
			// IF NO ERROR
			// registry generates a unique identifier (between 0-127) for the node while
			// ensuring that there are no duplicate IDs being assigned.
			int newNodeID = generateUniqueID();
						
			// Add socket and connection info to clientConnections
			// TODO check
			tcpConCache.addClientConnection(newNodeID, tcpConnection);
			
			int clientCount = tcpConCache.getClientCount();
			
			RegistryReportsRegistrationStatus regMsg = new RegistryReportsRegistrationStatus(newNodeID, "Registration request successful. The number of messaging nodes currently constituting the overlay is " + clientCount);
			
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
				
				ArrayList<Integer> idList = tcpConCache.getIdList(i);
				
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
			//System.out.println("Count: "+ count);
			
			// TODO check casting and ints relationship with pow
			//System.out.println("i = " + i);
			int nextOffset = (int) Math.pow(2, i);
			//System.out.println("nextOffSet: " + nextOffset + " currentNodeIndex: " + currentNodeIndex);
			//int count = TCPConnectionsCache.getInstance().getClientCount();
			//System.out.println("Count: "+ nodeCount);
			//System.out.println("NodeCount from instance: " + count);
			int nextClient = (currentNodeIndex + nextOffset) % nodeCount;
			//System.out.println("nextClient index: "+ nextClient);
			//System.out.println();
			
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
		
		RegistryRequestsTaskInitiate taskInit = new RegistryRequestsTaskInitiate(numOfPackets);
		
		tcpConCache.sendMsgToAllClients( taskInit.getBytes());
		
	}
	
	public void handleTaskReports(OverlayNodeReportsTaskFinished msg) {
		
		System.out.println("Node "+msg.getNodeId()+" has reported their task is finished.");
		
	}

}
