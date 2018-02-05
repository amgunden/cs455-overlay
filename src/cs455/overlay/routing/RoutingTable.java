package cs455.overlay.routing;

import java.util.ArrayList;

public class RoutingTable {
	
	private ArrayList<RoutingEntry> routingEntries;

	public RoutingTable() {
		// TODO Auto-generated constructor stub
		routingEntries =  new ArrayList<RoutingEntry>();
	}
	
	public void addEntry(RoutingEntry entry) {
		routingEntries.add(entry);
	}
	
	public ArrayList<RoutingEntry> getRoutingEntries(){
		return routingEntries;
	}
	

}
