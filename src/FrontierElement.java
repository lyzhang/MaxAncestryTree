/*
 * FrontierElement - a class for representing an edge in Frontier Stack.
 */
public class FrontierElement {
	String startId;
	String endId;
	
	FrontierElement(String start, String end) {
		startId = start;
		endId = end;
	}
}
