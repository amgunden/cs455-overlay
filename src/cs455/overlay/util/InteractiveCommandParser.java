package cs455.overlay.util;

import java.io.IOException;
import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

public class InteractiveCommandParser implements Runnable{
	
	Node node;

	public InteractiveCommandParser(Node node) {
		this.node = node;
	}

	@Override
	public void run() {
		
		while ( !Thread.currentThread().isInterrupted() ) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter command:  ");
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
			( (Registry) node).printMessagingNodes();
		}
		else if( splitCmd[0].equals(new String("setup-overlay"))) {
			// DONE add error checking for command with no number ---> default 3
			if(splitCmd.length == 1) {
				((Registry) node).handleOverlaySetup(3);
			}
			else {
				int routeTableSize = Integer.parseInt(splitCmd[1]);
				if( routeTableSize < 1) {
					System.out.println("Routing Table must have atleast one entry. Please re-enter the command");
				}
				else {
					((Registry) node).handleOverlaySetup(routeTableSize);
				}
			}
		}
		else if( splitCmd[0].equals( new String("list-routing-tables"))) {
			( (Registry) node).printRoutingTables();

		}
		else if( splitCmd[0].equals(new String("start"))) {
			// DONE add error checking for command with no number
			
			if( splitCmd.length == 1) {
				System.out.println("The start command requires the number of messages be specified. Please re-enter the command");
			}
			else if( Integer.parseInt(splitCmd[1]) <1) {
				System.out.println("One or more messages must be sent. Please re-enter the command");
			}
			else {
				try {
					( (Registry) node).handleStartCmd( Integer.parseInt(splitCmd[1]));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("Command not recognized. Please re-enter the command");
		}
		
		
	}
	
	public void parseMsgNodeCmd(String cmd) {
		
		if( cmd.equals(new String("print-counters-and-diagnostics"))) {
			( (MessagingNode) node).printCtrAndDiag();
		}
		else if( cmd.equals(new String("exit-overlay"))) {
			System.out.println("Send dereg cmd receiver");
			( (MessagingNode) node).sendDeregistrationMsg();
		}
		else {
			System.out.println("Command not recognized. Please re-enter the command");
		}
		
	}
	


}
