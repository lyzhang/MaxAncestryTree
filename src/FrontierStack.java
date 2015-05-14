/*
 * FrontierStack - a class for representing an FrontierStack FF.
 * Author:  Liyun Zhang
 */

public class FrontierStack {
	
	private class Element {
		Edge edge;
		Element next;
		
		public Element(Edge e) {
			edge = e;
			next = null;
		}
	}
	
	private Element edges = null;
	
	public FrontierStack() {
		edges = null;
	}
	
	public void push(Edge edge) {
		Element newElem = new Element(edge);
		newElem.next = edges;
		edges = newElem;
	}
	
	public Edge pop() {
		Element elem = edges;
		edges = edges.next;
		elem.next = null;
		return elem.edge;	
	}
	
	public boolean isEmpty() {
		return edges == null;
	}
	
	public void removeAll(PartialTree T, Vertex v)  {
		Element curr = edges;
		
		while(curr != null) {			
			if(T.contains(curr.edge.start.id) && Utility.IsSameVertex(v, curr.edge.end)) {
				curr = curr.next;
			} else {
				break;
			}
		}
		edges = curr;
		if(curr == null) return;
		
		Element prev = curr;
		curr = curr.next;
		while(curr != null) {			
			if(T.contains(curr.edge.start.id) && Utility.IsSameVertex(v, curr.edge.end)) {
				prev.next = curr.next;	
				curr = curr.next;
			} else {
				prev = curr;
				curr = curr.next;
			}
		}		
		
		return;
	}
	
	public FrontierStack copy() {
		FrontierStack fs2 = new FrontierStack();
		Element newcurr = null;
		Element newprev = null;
		Element oldcurr = edges;
		while(oldcurr != null) {
			newcurr = new Element(oldcurr.edge);
			if(newprev == null) {
				fs2.edges = newcurr;
				newprev = newcurr;
			} else {
				newprev.next = newcurr;
				newprev = newcurr;
			}
			oldcurr = oldcurr.next;
		}
		
		return fs2;
	}
}
