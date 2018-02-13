package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {
	
	int nodeId;
	
	int sendTracker;
	int receiveTracker;
	
	int relayTracker;
	
	long sendSummation;
	long receiveSummation;

	public StatisticsCollectorAndDisplay(int nodeId) {
		this.nodeId = nodeId;
		sendTracker = 0;
		receiveTracker = 0;
		relayTracker = 0;
		sendSummation = 0;
		receiveSummation = 0;
	}
	
	public StatisticsCollectorAndDisplay(int nodeId, int sendT, int recT, int relayT, long sendSum, long receiveSum) {
		this.nodeId = nodeId;
		sendTracker = sendT;
		receiveTracker = recT;
		relayTracker = relayT;
		sendSummation = sendSum;
		receiveSummation = receiveSum;
	}
	
	public void resetCounters() {
		sendTracker = 0;
		receiveTracker = 0;
		relayTracker = 0;
		sendSummation = 0;
		receiveSummation = 0;		
	}
	
	public synchronized void incrementSend() {
		sendTracker++;
	}
	
	public synchronized void incrementReceive() {
		receiveTracker++;
	}
	
	public synchronized void incrementRelay() {
		relayTracker++;
	}
	
	public synchronized void addToSendSum(int payload) {
		sendSummation += payload;
	}
	
	public synchronized void addToReceiveSum(int payload) {
		receiveSummation += payload;
	}

	public int getSendTracker() {
		return sendTracker;
	}

	public int getReceiveTracker() {
		return receiveTracker;
	}

	public int getRelayTracker() {
		return relayTracker;
	}

	public long getSendSummation() {
		return sendSummation;
	}

	public long getReceiveSummation() {
		return receiveSummation;
	}
	
	public void print() {
		System.out.println();
		System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %n", nodeId, sendTracker, receiveTracker, relayTracker, sendSummation, receiveSummation);
		//System.out.println(nodeId+"\t"+sendTracker+"\t"+receiveTracker+"\t"+relayTracker+"\t"+sendSummation+"\t"+receiveSummation);
	}
	

}
