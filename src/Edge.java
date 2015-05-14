/*
 * Edge - a class for representing an edge.
 */
public class Edge {
	Vertex start;
	Vertex end;
	Edge nextOutEdge;   // next Edge in adjacency list
	Edge nextInEdge;    // next Edge in adjacency list

	public Edge(Vertex start, Vertex end) {
		this.start = start;
		this.end = end;
		this.nextOutEdge = null;
		this.nextInEdge = null;
	}
}