package node;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs455.overlay.node.Registry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;

public class TestRegistry {
	
	Registry reg = new Registry();
	TCPConnectionsCache conCache = TCPConnectionsCache.getInstance();
	
//	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@BeforeClass
	public static void setUpOnce() throws Exception {
		
//		System.setOut(new PrintStream(outContent));
//        System.setErr(new PrintStream(errContent));
		
		TCPConnection a = new TCPConnection(null, 1000);
		TCPConnection b = new TCPConnection(null, 1001);
		TCPConnection c = new TCPConnection(null, 1002);
		TCPConnection d = new TCPConnection(null, 1003);
		TCPConnection e = new TCPConnection(null, 1004);
		TCPConnection f = new TCPConnection(null, 1005);
		TCPConnection g = new TCPConnection(null, 1006);
		TCPConnection h = new TCPConnection(null, 1007);
		TCPConnection i = new TCPConnection(null, 1008);
		TCPConnection j = new TCPConnection(null, 1009);
		TCPConnection k = new TCPConnection(null, 1010);
		
		TCPConnectionsCache.getInstance().addClientConnection(10, a);
		TCPConnectionsCache.getInstance().addClientConnection(21, b);
		TCPConnectionsCache.getInstance().addClientConnection(32, c);
		TCPConnectionsCache.getInstance().addClientConnection(43, d);
		TCPConnectionsCache.getInstance().addClientConnection(54, e);
		TCPConnectionsCache.getInstance().addClientConnection(61, f);
		TCPConnectionsCache.getInstance().addClientConnection(77, g);
		TCPConnectionsCache.getInstance().addClientConnection(87, h);
		TCPConnectionsCache.getInstance().addClientConnection(99, i);
		TCPConnectionsCache.getInstance().addClientConnection(101, j);
		TCPConnectionsCache.getInstance().addClientConnection(103, k);

	}
	

//	@Test
//	public void testOverlaySizePrintOut() {
//		String test = "There is not enough nodes to use a routing table of this size. Please use a smaller routing table.\n";
//		reg.handleOverlaySetup(15);
//		assertEquals(test, outContent.toString());
//
//	}
	
	/* 
	 * 
	 * 	Tests with routing table of size 3
	 * 
	*/
	
	
	// Test with example in assignment --> expecting table with 21, 32, 54	
	@Test
	public void testOverlayOne() throws IOException {
	
		RoutingTable table = reg.createRoutingTable(0, 3, 11);
		int[] toCompare = {21,32,54};
		int[] result = new int[3];
		
		for(int i=0; i<3;i++) {
			int temp = table.getRoutingEntries().get(i).getNodeId();
			//System.out.println(temp);
			result[i] = temp;
		}
		
		assertArrayEquals(toCompare, result);
	}
	
	// Test with example in assignment --> expecting table with 103, 10, 32
	@Test
	public void testOverlayTwo() {
		RoutingTable table = reg.createRoutingTable(9, 3, 11);
		int[] toCompare = {103,10,32};
		int[] result = new int[3];
		
		for(int i=0; i<3;i++) {
			int temp = table.getRoutingEntries().get(i).getNodeId();
			//System.out.println(temp);
			result[i] = temp;
		}
		
		assertArrayEquals(toCompare, result);
	}
	

	// Test with example in assignment --> expecting table with 10, 21, 43
	@Test
	public void testOverlayThree() {
		RoutingTable table = reg.createRoutingTable(10, 3, 11);
		int[] toCompare = {10,21,43};
		int[] result = new int[3];
		
		for(int i=0; i<3;i++) {
			int temp = table.getRoutingEntries().get(i).getNodeId();
			//System.out.println(temp);
			result[i] = temp;
		}
		
		assertArrayEquals(toCompare, result);
	}
	
	// Test with example in assignment --> expecting table with 99, 101, 10
	@Test
	public void testOverlayFour() {
		RoutingTable table = reg.createRoutingTable(7, 3, 11);
		int[] toCompare = {99,101,10};
		int[] result = new int[3];
		
		for(int i=0; i<3;i++) {
			int temp = table.getRoutingEntries().get(i).getNodeId();
			//System.out.println(temp);
			result[i] = temp;
		}
		
		assertArrayEquals(toCompare, result);
	}
	
	/*
	 * 
	 * 	Test with routing table size 4
	 * 
	*/ 
	
	// Test with example in assignment --> expecting table with 21, 32, 54, 99
	@Test
	public void testOverlayFive() {
		RoutingTable table = reg.createRoutingTable(0, 4, 11);
		int[] toCompare = {21,32,54,99};
		int[] result = new int[4];
		
		for(int i=0; i<4;i++) {
			int temp = table.getRoutingEntries().get(i).getNodeId();
			//System.out.println(temp);
			result[i] = temp;
		}
		
		assertArrayEquals(toCompare, result);
	}
	
	// Test with example in assignment --> expecting table with 101, 103, 21, 61
	@Test
	public void testOverlaySix() {
		RoutingTable table = reg.createRoutingTable(8, 4, 11);
		int[] toCompare = {101,103,21,61};
		int[] result = new int[4];
		
		for(int i=0; i<4;i++) {
			int temp = table.getRoutingEntries().get(i).getNodeId();
			//System.out.println(temp);
			result[i] = temp;
		}
		
		assertArrayEquals(toCompare, result);
	}
	
	
	
//    @After
//    public void cleanUpStreams() {
//        System.setOut(null);
//        System.setErr(null);
//    }

}
