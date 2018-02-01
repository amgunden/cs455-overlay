package cs455.overlay.util;

import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

public class InteractiveCommandParser implements Runnable{
	
	Node node;

	public InteractiveCommandParser(Node node) {
		// TODO Auto-generated constructor stub
		this.node = node;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (true) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter command: ");
			String inputCmd = scanner.nextLine();
			if (node instanceof Registry) {
				parseRegisterCmd(inputCmd);
			} else {
				parseMsgNodeCmd(inputCmd);
			} 
		}
		
	}
	
	public void parseRegisterCmd(String cmd) {
		
		String[] splitCmd = cmd.split(" "); 
		
		if(splitCmd[0].equals(new String("list-messaging-nodes"))) {
			listMsgNodes();
		}
		else if( splitCmd[0].equals(new String("setup-overlay"))) {
			// TODO add error checking for command with no number ---> default 3
			if(splitCmd[1].equals(null)) {
				setupOverlay(3);
			}
			else {
				setupOverlay(Integer.parseInt(splitCmd[1]));
			}
		}
		else if( splitCmd[0].equals( new String("list-routing-tables"))) {
			listRoutingTables();
		}
		else if( splitCmd[0].equals(new String("start"))) {
			// TODO add error checking for command with no number
			start(Integer.parseInt(splitCmd[1]));
		}
		else {
			System.out.println("Command not recognized. Please re-enter the command");
		}
		
		
	}
	
	public void parseMsgNodeCmd(String cmd) {
		
		if( cmd.equals(new String("print-counters-and-diagnostics"))) {
			printCtrAndDiag();
		}
		else if( cmd.equals(new String("exit-overlay"))) {
			exitOverlay();
		}
		else {
			System.out.println("Command not recognized. Please re-enter the command");
		}
		
	}
	
	public void listMsgNodes() {
		System.out.println("List Message Nodes Command Received");
	}
	
	public void setupOverlay(int numEntries) {
		System.out.println("Setup Overlay Command Received for number of entries: "+ numEntries);
		((Registry) node).handleOverlaySetup(numEntries);
	}
	
	public void listRoutingTables() {
		System.out.println("List Routing Table Command Received");
	}
	
	public void start(int numOfMsgs) {
		System.out.println("Start Command Received. Number of messages: "+ numOfMsgs);
	}
	
	public void printCtrAndDiag() {
		System.out.println("Print Counters and Diagnostics Command Received");
	}
	
	public void exitOverlay() {
		System.out.println("Exit Overlay Command Received");
	}

}
