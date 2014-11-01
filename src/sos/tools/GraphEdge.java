package sos.tools;

import sos.base.SOSConstant.GraphEdgeState;

public abstract class GraphEdge {
	protected short index;
	protected GraphEdgeState state;
	protected short headIndex;
	protected short tailIndex;
	protected boolean haveTraffic = false;
	
	public GraphEdge(short Index, short headIndex, short tailIndex) {
		this.index = Index;
		this.headIndex = headIndex;
		this.tailIndex = tailIndex;
	}
	
	public boolean haveTraffic() {
		return this.haveTraffic;
	}
	
	public void setHaveTraffic() {
		this.haveTraffic = true;
	}
	
	public void setFreeTraffic() {
		this.haveTraffic = false;
	}
	
	public void setIndex(short index) {
		this.index = index;
	}
	
	public short getIndex() {
		return index;
	}
	
	public short getTailIndex() {
		return tailIndex;
	}
	
	public void setTailIndex(short tailIndex) {
		this.tailIndex = tailIndex;
	}
	
	public short getHeadIndex() {
		return headIndex;
	}
	
	public void setHeadIndex(short headIndex) {
		this.headIndex = headIndex;
	}
	
	public GraphEdgeState getState() {
		return state;
	}
	
	public void setState(GraphEdgeState state) {
		this.state = state;
	}
	
	public short getNextNodeIndex(short HeadIndex) {
		return HeadIndex == getHeadIndex() ? getTailIndex() : getHeadIndex();
	}
	
	public abstract int getLenght();
	
	public abstract String fullDescription();
}
