import java.io.*;
import java.util.*;

/**
 * An implementation of a Graph ADT and MaxTree algorithm.
 */
public class Graph {

	/******* Graph instance variables and method start here. *********/

	/* A linked list of the vertices in the graph. */
	private Vertex vertices;
	private FrontierStack F = null;
	private PartialTree T = null;
	private List<PartialTree> L = null;
	private Vertex root = null;
	private int numberVertexAccessibleFromRoot = 0;

	/*
	 * reinitVertices - private helper method that resets the starting state of
	 * all of the vertices in the graph, removing any values set by previous
	 * uses of the algorithms.
	 */
	private void reinitVertices() {
		Vertex v = vertices;
		while (v != null) {
			v.reinit();
			v = v.next;
		}
	}

	/*
	 * getVertex - private helper method that returns a reference to the vertex
	 * with the specified id. If the vertex isn't in the graph, it returns null.
	 */
	private Vertex getVertex(String id) {
		Vertex v = vertices;
		while (v != null && !v.id.equals(id))
			v = v.next;

		return v;
	}

	/*
	 * addVertex - private helper method that adds a vertex with the specified
	 * id and returns a reference to it.
	 */
	private Vertex addVertex(String id) {
		Vertex v = new Vertex(id);

		/* Add to the front of the list. */
		v.next = vertices;
		vertices = v;
		return v;
	}

	/**
	 * addEdge - add an edge with the specified cost, and with start and end
	 * vertices that have the specified IDs. The edge will be stored in the
	 * adjacency list of the start vertex. If a specified vertex isn't already
	 * part of the graph, it will be added, too.
	 */
	public Edge addEdge(String startVertexID, String endVertexID) {
		Vertex start = getVertex(startVertexID);
		if (start == null)
			start = addVertex(startVertexID);
		Vertex end = getVertex(endVertexID);
		if (end == null)
			end = addVertex(endVertexID);

		Edge e = new Edge(start, end);
		start.addToOutEdgeList(e);
		end.addToInEdgeList(e);
		return e;
	}

	/**
	 * toString - returns a concatenation of the string representations of all
	 * of the vertices in the graph.
	 */
	public String toString() {
		String str = "";

		Vertex v = vertices;
		while (v != null) {
			str += v;
			v = v.next;
		}

		return str;
	}

	/**
	 * initFromFile - initialize a graph using the information in the specified
	 * file. The file should be a text file consisting of lines that specify the
	 * edges of the graph in the following form: <start vertex data> <end vertex
	 * data> <cost>
	 */
	public void initFromFile(String fileName) {
		String lineString = "";
		boolean startVAF = false;
		try {
			/* This Scanner will scan the file, one line at a time. */
			Scanner file = new Scanner(new File(fileName));

			/* Parse the file, one line at a time. */
			while (file.hasNextLine()) {
				lineString = file.nextLine();
				if (lineString.startsWith("STARTVAF")) {
					startVAF = true;
					continue;
				}

				if (!startVAF) {
					Scanner line = new Scanner(lineString);
					String startID = line.next();
					String endID = line.next();
					addEdge(startID, endID);
					line.close();
				} else {
					Scanner line = new Scanner(lineString);
					String vertexId = line.next();
					Vertex v = this.getVertex(vertexId);
					while (line.hasNext()) {
						double vaf = Double.parseDouble(line.next());
						v.addVAF(vaf);
					}
					line.close();
				}
			}
			file.close();
		} catch (IOException e) {
			System.out.println("Error accessing " + fileName);
			System.exit(1);
		} catch (NoSuchElementException e) {
			System.out.println("invalid input line: " + lineString);
			System.exit(1);
		}
	}

	/*
	 * dfTrav - a recursive method used to perform a depth-first traversal,
	 * starting from the specified vertex v. The parent parameter specifies v's
	 * parent in the depth-first spanning tree. In the initial invocation, the
	 * value of parent should be null, because the starting vertex is the root
	 * of the spanning tree.
	 */
	private static int dfTrav(Vertex v, Vertex parent, boolean shouldPrint) {
		/* Visit v. */
		if (shouldPrint)
			System.out.println("\t" + v.id
					+ (parent == null ? "" : " (parent = " + parent.id + ")"));
		v.done = true;
		int numOfVertex = 1;
		v.parent = parent;

		Edge e = v.outEdges;
		while (e != null) {
			Vertex w = e.end;
			if (!w.done) {
				int numOfChildren = dfTrav(w, v, shouldPrint);
				numOfVertex += numOfChildren;
			}
			e = e.nextOutEdge;
		}
		return numOfVertex;
	}

	public int getNumOfVertexAccessFromRoot() {
		reinitVertices();
		return dfTrav(this.root, null, false);
	}

	public boolean isConnected() {
		reinitVertices();
		Vertex start = null;
		if (this.root != null) {
			start = this.root;
		} else {
			start = vertices;
		}
		/*
		 * Perform a depth-first traversal that begins with the root (if root
		 * exist) or the first vertex in the list and doesn't print the vertices
		 * as it visits them.
		 */
		dfTrav(start, null, false);

		/*
		 * If all vertices were visited, the graph is connected, otherwise it
		 * isn't.
		 */
		Vertex v = vertices;
		while (v != null) {
			if (!v.done)
				return false;
			v = v.next;
		}

		return true;
	}

	private void addVertexEdgesToFrontier(Vertex v) {
		Edge curr = v.outEdges;
		while (curr != null) {
			if (!T.contains(curr.end.id)) {
				F.push(curr);
			}
			curr = curr.nextOutEdge;
		}
	}

	public void removeEdge(String startVertexID, String endVertexID) {
		Vertex start = getVertex(startVertexID);
		start.removeOutEdge(endVertexID);

		Vertex end = getVertex(endVertexID);
		end.removeInEdge(startVertexID);
	}

	// GROW finds all spanning trees rooted at root containing partialTree_T;
	private void grow() {
		if ((L.size() == 0 && T.size() > 0)) {
			PartialTree tmp = T.copy();
			L.add(tmp);
		} else if (T.size() == L.get(0).size()) {

			Boolean isDuplicated = false;
			for (PartialTree t : L) {
				if (T.isDuplicate(t)) {
					System.out.println("Duplcate found");
					isDuplicated = true;
					break;
				}
			}

			if (!isDuplicated) {
				PartialTree tmp = T.copy();
				L.add(tmp);
			}
			
		} else if (T.size() > L.get(0).size()) {
			L.clear();
			PartialTree tmp = T.copy();
			L.add(tmp);
		}

		// if T has V vertices
		if (T.size() == this.numberVertexAccessibleFromRoot) {
			return;
		}

		Stack<FrontierElement> FF = new Stack<FrontierElement>();
		while (!F.isEmpty()) {

			// pop an edge e from F;
			Edge e = F.pop();
			Vertex v = e.end;

			if (T.isAddedEdgeMeetSumCondition(e.start.id, e.start.VAFs,
					e.end.id, e.end.VAFs)) {
				// make a copy of current frontier
				FrontierStack F_copy = F.copy();
				
				// add e to T
				T.addEdge(e.start.id, e.start.VAFs, e.end.id, e.end.VAFs);

				// push each edge (v,w), w not in T, onto F
				addVertexEdgesToFrontier(v);
				
				// remove each edge (w,v),
				F.removeAll(T, v);

				grow();

				F = F_copy;

				// remove e from T
				T.removeEdge(e.start.id, e.end.id);
			}

			// remove e from G
			this.removeEdge(e.start.id, e.end.id);
			// this.numberVertexAccessibleFromRoot -= 1;

			// add e to FF
			FF.push(new FrontierElement(e.start.id, e.end.id));
		}

		while (!FF.isEmpty()) {
			FrontierElement ffElem = FF.pop();

			// add e back to graph
			Edge e = this.addEdge(ffElem.startId, ffElem.endId);
			// this.numberVertexAccessibleFromRoot += 1;

			// add e back to F
			F.push(e);
		}

	}

	public List<PartialTree> spanningTreeGaborMeyer(String rootId) {
		List<Double> rootVAFs = getVertex(rootId).VAFs;
		this.F = new FrontierStack();
		this.T = null;
		this.L = new LinkedList<PartialTree>();
		this.root = null;
		this.root = getVertex(rootId);
		this.numberVertexAccessibleFromRoot = getNumOfVertexAccessFromRoot();

		this.T = new PartialTree(this.root.id, rootVAFs);
		addVertexEdgesToFrontier(this.root);
		grow();

		return this.L; // return the lastly-found spanning tree
	}

	public void printLargestSpanningTree() {
		// spanningTreeGaborMeyer("1");
		// spanningTreeGaborMeyer("1");
		List<PartialTree> tmpTree = null;
		List<PartialTree> maxTree = null;
		Vertex v = this.vertices;
		while (v != null) {
			// System.out.println("now root is " + v.id);
			tmpTree = null;
			tmpTree = spanningTreeGaborMeyer(v.id);
			if (maxTree == null) {
				maxTree = tmpTree;
			} else {
				if (tmpTree != null
						&& maxTree.get(0).size() < tmpTree.get(0).size()) {
					maxTree = tmpTree;
				}
			}

			v = v.next;
		}

		System.out.printf("\n\nThe largest arboresence is (count = %d): \n\n",
				maxTree.size());
		if (maxTree != null) {
			for (PartialTree t : maxTree) {
				t.printToFile();
			}
		}

		return;
	}
}
