package cs455.overlay.wireformats;

/*
 * The Message Types that have been specified could be part of an interface, say cs455.overlay.wireformats.Protocol 
 * and have values specified there. This way you are not hard coding values in different portions of your code.
 */

public interface Protocol {
	
	public static final int OVERLAY_NODE_SENDS_REGISTRATION = 2;
	public static final int REGISTRY_REPORTS_REGISTRATION_STATUS = 3;
	
	public static final int OVERLAY_NODE_SENDS_DEREGISTRATION = 4;
	public static final int REGISTRY_REPORTS_DEREGISTRATION_STATUS = 5;
	
	public static final int REGISTRY_SENDS_NODE_MANIFEST = 6;
	public static final int NODE_REPORTS_OVERLAY_SETUP_STATUS = 7;
	
	public static final int REGISTRY_REQUESTS_TASK_INITIATE = 8;
	public static final int OVERLAY_NODE_SENDS_DATA = 9;
	public static final int OVERLAY_NODE_REPORTS_TASK_FINISHED = 10;
	
	public static final int REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 11; 
	public static final int OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 12;

}