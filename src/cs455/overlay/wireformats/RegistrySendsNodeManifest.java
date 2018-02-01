package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;

public class RegistrySendsNodeManifest  implements Event{
	
	private int messageType = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
	private String identifier = "REGISTRY_SENDS_NODE_MANIFEST";
	
	private int routingTableSize;
	private RoutingTable routingTable;
	private ArrayList<Integer> nodeIdList;
	
	
	
	public int getRoutingTableSize() {
		return routingTableSize;
	}

	public RoutingTable getRoutingTable() {
		return routingTable;
	}

	public ArrayList<Integer> getNodeIdList() {
		return nodeIdList;
	}

	public RegistrySendsNodeManifest(int size, RoutingTable routingTable, ArrayList<Integer> idList) {
		// TODO Auto-generated constructor stub
		routingTableSize = size;
		this.routingTable = routingTable;
		nodeIdList = idList;
	}
	
	public RegistrySendsNodeManifest(byte[] message) throws IOException {
		
		extractMessage(message);
		
	}
	
	public void extractMessage(byte[] message) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		messageType = din.readInt();
		routingTableSize = din.readInt();
		
		// First iteration; moved to using RoutingTable object to encapsulate
		//routingEntries = new RoutingEntry[routingTableSize];
		
		routingTable = new RoutingTable();
		
		for(int i=0; i<routingTableSize; i++) {
						
			int id = din.readInt();
			//routingEntries[i].setNodeId(id);
			
			int identifierLength = din.readInt();
			byte[] inetBytes = new byte[identifierLength];
			din.readFully(inetBytes);
			
			InetAddress addr = InetAddress.getByAddress(inetBytes);
			//routingEntries[i].setInetAddr(addr);
			
			int port = din.readInt();
			//routingEntries[i].setPort(port);
			
			RoutingEntry toAdd = new RoutingEntry(id, addr, port);
			
			routingTable.addEntry(toAdd);
		}
		
		int numOfOtherNodes = din.readInt();
		nodeIdList = new ArrayList<Integer>();
				
		//new int[numOfOtherNodes];
		
		for(int j=0; j<numOfOtherNodes; j++) {
			nodeIdList.add( din.readInt());
			
		}
		
		
		baInputStream.close();
		din.close();
		
	}

	@Override
	public byte[] getBytes() {
		/*
		 * 	byte: Message type; REGISTRY_SENDS_NODE_MANIFEST
			byte: routing table size NR
			
			int: Node ID of node 1 hop away
			byte: length of following "IP address" field
			byte[^^]: IP address of node 1 hop away; from InetAddress.getAddress()
			int: Port number of node 1 hop away
			
			int: Node ID of node 2 hops away
			byte: length of following "IP address" field
			byte[^^]: IP address of node 2 hops away; from InetAddress.getAddress()
			int: Port number of node 2 hops away
			int: Node ID of node 4 hops away
			byte: length of following "IP address" field
			byte[^^]: IP address of node 4 hops away; from InetAddress.getAddress()
			int: Port number of node 4 hops away
			
			byte: Number of node IDs in the system
			int[^^]: List of all node IDs in the system [Note no IPs are included]
		 */
		
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		try {
			
			dout.writeInt(messageType);
			
			dout.writeInt(routingTableSize);
			
			for(int i=0; i<routingTableSize; i++) {
				
				// TODO add methods to routing table so calls to routing table for info arent so long
				
				//int nodeID = routingEntries[i].getNodeId();
				int nodeID = routingTable.getRoutingEntries().get(i).getNodeId();
				dout.writeInt(nodeID);
				
				//byte[] inetAddrBytes = routingEntries[i].getInetAddr().getAddress();
				byte[] inetAddrBytes = routingTable.getRoutingEntries().get(i).getInetAddr().getAddress();
				int elementLength = inetAddrBytes.length;
				
				dout.writeInt(elementLength);
				dout.write(inetAddrBytes);
				
				//int port = routingEntries[i].getPort();
				int port = routingTable.getRoutingEntries().get(i).getPort();
				
				dout.writeInt(port);
				
				
			}
			
			dout.writeInt(nodeIdList.size());
			
			for(int j=0; j<nodeIdList.size(); j++) {
				
				dout.writeInt(nodeIdList.get(j));
				
			}
			
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
	

}
