import java.util.ArrayList;
import java.util.List;

/*
 * Vertex - a class for representing a vertex.
 */
public class Vertex {
	String id;
	Edge outEdges; // adjacency list, sorted by edge cost
	Edge inEdges;   // edges that end at this vertex
	
	Vertex next; // next Vertex in linked list
	
	boolean done;	// used in isConnected() method.
	Vertex parent;   // preceding vertex in path from root used in isConnected() method,
	List<Double> VAFs;
	String geneNames;

	public Vertex(String id) {
		this.id = id;	
		this.VAFs = new ArrayList<Double>();
	}

	public void addVAF(double vaf) {
		this.VAFs.add(vaf);
	}
	
	public void setVAFs(List<Double> vafs) {
		this.VAFs = new ArrayList<Double>();
		for(Double vaf: vafs) {
			this.VAFs.add(vaf);
		}
	}
	
	/*
	 * reinit - reset the starting values of the fields used by the various
	 * algorithms, removing any values set by previous uses of the algorithms.
	 */
	public void reinit() {
		done = false;
		parent = null;

	}

	/*
	 * addToAdjacencyList - add the specified edge to the adjacency list for
	 * this vertex.
	 */
	public void addToOutEdgeList(Edge e) {
		/* Add to the front of the list. */
		e.nextOutEdge = outEdges;
		outEdges = e;
	}

	public void addToInEdgeList(Edge e) {
		/* Add to the front of the list. */
		e.nextInEdge = inEdges;
		inEdges = e;
	}

	public void removeOutEdge(String endVertexId) {
		boolean isFound = false;

		Edge curr = this.outEdges;
		if (curr.end.id.equals(endVertexId)) {
			this.outEdges = this.outEdges.nextOutEdge;
			isFound = true;
		} else {
			Edge prev = curr;
			curr = curr.nextOutEdge;
			while (curr != null) {
				if (curr.end.id.equals(endVertexId)) {
					prev.nextOutEdge = curr.nextOutEdge;
					isFound = true;
					break;
				} else {
					prev = curr;
					curr = curr.nextOutEdge;
				}
			}
		}

		if (isFound == false) {
			System.out.printf("Warning: cannot find edge (%s, %s)\n", this.id,
					endVertexId);
		}
	}
	
	public void removeInEdge(String startVertexId) {
		boolean isFound = false;

		Edge curr = this.inEdges;
		if (curr.start.id.equals(startVertexId)) {
			this.inEdges = this.inEdges.nextInEdge;
			isFound = true;
		} else {
			Edge prev = curr;
			curr = curr.nextInEdge;
			while (curr != null) {
				if (curr.start.id.equals(startVertexId)) {
					prev.nextInEdge = curr.nextInEdge;
					isFound = true;
					break;
				} else {
					prev = curr;
					curr = curr.nextInEdge;
				}
			}
		}

		if (isFound == false) {
			System.out.printf("Warning: cannot find edge (%s, %s)\n", startVertexId, this.id);
		}
	}
	
	/*
	 * pathString - returns a string that specifies the path from the root of
	 * the current spanning tree (if there is one) to this vertex. If this
	 * method is called after performing Dijkstra's algorithm, the returned
	 * string will specify the shortest path.
	 */
	public String pathString() {
		String str;

		if (parent == null)
			str = id; /* base case: this vertex is the root */
		else
			str = parent.pathString() + " -> " + id;

		return str;
	}

	/*
	 * toString - returns a string representation of the vertex of the following
	 * form: vertex v: edge to vi (cost = c1i) edge to vj (cost = c1j) ...
	 */
	public String toString() {
		String str = "vertex " + id + ":\n";

		/*
		 * Iterate over the edges, adding a line to the string for each of them.
		 */
		Edge e = outEdges;
		while (e != null) {
			// Note: we have to use just the id field of the
			// end vertex, or else we'll get infinite recursion!
			str += "\tedge to " + e.end.id;

			e = e.nextOutEdge;
		}

		return str;
	}	

}