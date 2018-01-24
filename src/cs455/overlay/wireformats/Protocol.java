package cs455.overlay.wireformats;

/*
 * The Message Types that have been specified could be part of an interface, say cs455.overlay.wireformats.Protocol 
 * and have values specified there. This way you are not hard coding values in different portions of your code.
 */

public interface Protocol {
	
	public static final int OVERLAY_NODE_SENDS_REGISTRATION = 1;

}
