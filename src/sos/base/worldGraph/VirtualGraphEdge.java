package sos.base.worldGraph;

import sos.base.SOSConstant.GraphEdgeState;
import sos.tools.GraphEdge;

/**
 * 
 * @author Aramik
 * 
 */
public class VirtualGraphEdge extends GraphEdge {
	public VirtualGraphEdge(short Index, short headIndex, short tailIndex) {
		super(Index, headIndex, tailIndex);
		this.state = GraphEdgeState.Open;
	}
	
	@Override
	public int getLenght() {
		return 0;
	}
	
	@Override
	public String fullDescription() {
		return "[VisualEdge  index=" + index + " , HeadIndex=" + headIndex + " , TailIndex=" + tailIndex + "  state=" + getState() + "]";
	}
	
	@Override
	public String toString() {
		return "[VisualEdge  index=" + index + " , HeadIndex=" + headIndex + " , TailIndex=" + tailIndex + "  state=" + getState() + "]";
	}
	
}
