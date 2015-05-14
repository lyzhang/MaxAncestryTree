import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * PartialTree - a class for representing the partial Tree T.
 *  Author:  Liyun Zhang
 */
public class PartialTree {
	
	private Vertex vertices;
	private int totalVertexNumber = 0;	
	private String rootId = null;
	
	private Vertex getVertex(String id) {
		Vertex v = vertices;
		while (v != null && !v.id.equals(id))
			v = v.next;

		return v;
	}
	
	private Vertex addVertex(String id) {
		Vertex v = new Vertex(id);

		/* Add to the front of the list. */
		v.next = vertices;
		vertices = v;
		totalVertexNumber++;
		return v;
	}
	
	private void removeVertex(String id) {
		Vertex v = vertices;
		if( v.id.equals(id)) {
			this.vertices = v.next;		
			totalVertexNumber--;
		} else {
			Vertex prev = v;
			Vertex curr = prev.next;
			while(curr != null) {
				if(curr.id.equals(id)) {
					prev.next = curr.next;
					totalVertexNumber--;
					break;
				} else {
					prev = curr;
					curr = curr.next;
				}
				
			}
		}
	}
	
	public PartialTree(String rootId, List<Double> rootVAFs) {
		this.rootId = rootId;
		addVertex(rootId);
		getVertex(rootId).setVAFs(rootVAFs);
	}
	
	public void addEdge(String startVertexID, List<Double> startVAFs, String endVertexID, List<Double> endVAFs) {
		Vertex start = getVertex(startVertexID);
		if (start == null) {  // addEdge is used by copy() and it is possible start = null when copy() is called
			start = addVertex(startVertexID);		
			start.setVAFs(startVAFs);
			System.out.println("Error5");
			System.exit(1);	
		} 

		Vertex end = getVertex(endVertexID);
		if (end == null) {
			end = addVertex(endVertexID);
			end.setVAFs(endVAFs);
		} else {
			System.out.println("Error6-----");
			System.out.printf("start=%s, end=%s\n", startVertexID, endVertexID );
			System.exit(1);
			return;
		}
		
		Edge e = new Edge(start, end);
		start.addToOutEdgeList(e);		
	}
	
	public void addEdgeWithoutCheck(String startVertexID, List<Double> startVAFs, String endVertexID, List<Double> endVAFs) {
		Vertex start = getVertex(startVertexID);
		if (start == null) {  // addEdge is used by copy() and it is possible start = null when copy() is called
			start = addVertex(startVertexID);		
			start.setVAFs(startVAFs);
		} 

		Vertex end = getVertex(endVertexID);
		if (end == null) {
			end = addVertex(endVertexID);
			end.setVAFs(endVAFs);
		} 
		
		Edge e = new Edge(start, end);
		start.addToOutEdgeList(e);		
	}	
	
	public boolean isAddedEdgeMeetSumCondition(String startVertexID, List<Double> startVAFs, String endVertexID, List<Double> endVAFs) {
		Vertex start = getVertex(startVertexID);
		if (start == null) {
			System.out.println("Error3");
			System.exit(1);
		}
		
		Edge outEdge = null;
		
		int sampleNum = start.VAFs.size();
		for(int i = 0; i < sampleNum; i++) {
			outEdge = start.outEdges;
			double sumOfChildren = 0;
			while(outEdge != null) {
				sumOfChildren += outEdge.end.VAFs.get(i);
				outEdge = outEdge.nextOutEdge;
			}
			if(sumOfChildren + endVAFs.get(i) > startVAFs.get(i)) {
				return false;
			}
		}
		
		return true;
		
	}
	
	public void removeEdge(String startVertexID, String endVertexID) {
		Vertex start = getVertex(startVertexID);
		start.removeOutEdge(endVertexID);		
		removeVertex(endVertexID);   // Since this is tree, removing an edge from the frontier of tree means removing the end vertex of the edge from the tree.
	}
	
	public String toString() {
		String str = "";

		Vertex v = vertices;
		while (v != null) {
			str += v;
			v = v.next;
		}

		return str;
	}
	
	public boolean contains(String vertexId) {	
		return null != getVertex(vertexId);	
	}
	
	public int size() {
		return this.totalVertexNumber;
	}
	
	public PartialTree copy() {
		String rootId = this.rootId;
		PartialTree copiedTree = new PartialTree(rootId, this.getVertex(rootId).VAFs);
		Vertex curr = this.vertices;
		while(curr != null) {
			Edge edge = curr.outEdges;
			while(edge != null) {
				copiedTree.addEdgeWithoutCheck(edge.start.id, edge.start.VAFs, edge.end.id, edge.end.VAFs);
				edge = edge.nextOutEdge;
			}
			curr = curr.next;
		}
		return copiedTree;
	}	
	
	public String getRootVertexId() {
		return this.rootId;
	}
	
	public Set<String> getDescedents(String id) {
		Set<String> vertexIdSet = new HashSet<String>();
		
		Vertex v = this.getVertex(id);
		Edge e = v.outEdges;
		while(e != null) {
			Vertex end = e.end;
			vertexIdSet.addAll(getSubtreeVertexIdSet(end));
			e = e.nextOutEdge;
		}
		
		return vertexIdSet;
	}
	
	private Set<String> getSubtreeVertexIdSet(Vertex v) {
		Set<String> vertexIdSet = new HashSet<String>();
		vertexIdSet.add(v.id);
		Edge e = v.outEdges;
		while(e != null) {
			Vertex end = e.end;
			vertexIdSet.addAll(getSubtreeVertexIdSet(end));
			e = e.nextOutEdge;
		}
		
		return vertexIdSet;
	}
	
	public void printToFile() {
		// if there is no edge in partial tree, return
		if(this.totalVertexNumber <= 1) return;		
		
		System.out.printf("---------Arboresence Rooted at %s with size %d---------\n", rootId, this.size());
		System.out.printf("Edges:\n");
		Vertex v = vertices;
		while( v!=null ) {
			Edge e = v.outEdges;
			while(e!=null) {
				System.out.printf(" (%s, %s)\n", e.start.id, e.end.id);
				e = e.nextOutEdge;
			}
			v = v.next;
		}
		System.out.printf("\n");
	}
	
	public boolean containEdge(String startId, String endId) {
		Vertex curr = this.vertices;
		while(curr != null) {
			Edge edge = curr.outEdges;
			while(edge != null) {
				if(startId.equals(edge.start.id) && endId.equals(edge.end.id)) {
					return true;
				}
				edge = edge.nextOutEdge;
			}
			curr = curr.next;
		}
		
		return false;
	}
	public boolean isDuplicate(PartialTree T1) {
		if(this.size() != T1.size()) return false;
		Vertex curr = this.vertices;
		while(curr != null) {
			Edge edge = curr.outEdges;
			while(edge != null) {
				if(!T1.containEdge(edge.start.id, edge.end.id))
					return false;
				edge = edge.nextOutEdge;
			}
			curr = curr.next;
		}
		
		return true;		
	}
}
