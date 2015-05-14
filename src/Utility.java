/*
 * Utility - some common utility functions.
 */
public class Utility {
	
	static boolean IsSameEdge(Edge e1, Edge e2) {
		return e1.start.id.equalsIgnoreCase(e2.start.id )&& e1.end.id.equalsIgnoreCase(e2.end.id);
	}
	
	static boolean IsSameVertex(Vertex v1, Vertex v2) {		
		return v1.id.equalsIgnoreCase(v2.id);
	}
}
