package sos.base.worldGraph;

import sos.base.SOSConstant.GraphEdgeState;
import sos.tools.GraphEdge;

/**
 * 
 * @author Aramik
 * 
 */
public class WorldGraphEdge extends GraphEdge {
	
	private short insideAreaIndex;
	private int lenght;
	
	
	public WorldGraphEdge(short index, short areaIndex, short headIndex, short tailIndex, int lenght, GraphEdgeState state) {
		super(index, headIndex, tailIndex);
		this.insideAreaIndex = areaIndex;
		this.lenght = lenght;
		this.state = state;
	}
	
	@Override
	public int getLenght() {
		return lenght;
	}
	
	public short getInsideAreaIndex() {
		return insideAreaIndex;
	}
	
	public void setInsideAreaIndex(short insideAreaIndex) {
		this.insideAreaIndex = insideAreaIndex;
	}
	
	@Override
	public String fullDescription() {
		return "[WorldGraphEdge  index=" + index + " , InsideAreaIndex=" + insideAreaIndex + " , HeadIndex=" + headIndex + " , TailIndex=" + tailIndex + " , lenght=" + lenght + " , state=" + getState() + "]";
	}
	
	@Override
	public String toString() {
		return "[WorldGraphEdge  index=" + index + " , state=" + getState() + "]";
	}
	
}
