package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

public class MessagingNode implements Node{
	
	int nodeID;
	
	public MessagingNode(int portNum) throws IOException {
		// TODO Auto-generated constructor stub


	}
	
	public static void main(String[] args) throws IOException {
		
		int portNum = Integer.parseInt(args[0]);
		
		Thread serverThread = new Thread(new TCPServerThread(portNum));
		serverThread.start();
		
		System.out.println("Test");

		
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
	
	public void connect() {
		
	}
	


}
