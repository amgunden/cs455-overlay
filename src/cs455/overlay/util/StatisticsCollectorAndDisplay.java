package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {
	
	int sendTracker;
	int receiveTracker;
	
	int relayTracker;
	
	long sendSummation;
	long receiveSummation;

	public StatisticsCollectorAndDisplay() {
		sendTracker = 0;
		receiveTracker = 0;
		relayTracker = 0;
		sendSummation = 0;
		receiveSummation = 0;
	}
	
	public void incrementSend() {
		sendTracker++;
	}
	
	public void incrementReceive() {
		receiveTracker++;
	}
	
	public void incrementRelay() {
		relayTracker++;
	}
	
	public void addToSendSum(int payload) {
		sendSummation += payload;
	}
	
	public void addToReceiveSum(int payload) {
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
	
	

}
