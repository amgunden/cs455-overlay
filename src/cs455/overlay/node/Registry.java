package cs455.overlay.node;

import java.io.IOException;
import java.net.UnknownHostException;

import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

public class Registry implements Node{
	

	public Registry(int port) throws IOException {
		// TODO Auto-generated constructor stub
		Thread serverThread = new Thread(new TCPServerThread(port));
		serverThread.start();
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
			
			System.out.println("OverlayNodeSendsRegistration Message Received");
			
			System.out.println(((OverlayNodeSendsRegistration) event).getPort());
			try {
				System.out.println(((OverlayNodeSendsRegistration) event).getInetAddress().toString());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	

	
	

}
