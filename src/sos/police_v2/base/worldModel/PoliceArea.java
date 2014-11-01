package sos.police_v2.base.worldModel;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;

public class PoliceArea {
	
	private final Area realArea;
	public short numberOfReachableTask=0;
	public short lastCheckedTime=0;
	public boolean isInFireZone=false;
	private int value;
	private int moveCost;

	public PoliceArea(Area area) {
		this.realArea = area;
	}
	
	public Area getRealArea() {
		return realArea;
	}
	
	@Override
	public String toString() {
		return "{PoliceArea: " + realArea.getID()+"}";
	}
	
	public  List<Blockade> getBlockades() {
		return realArea.getBlockades();
	}
	
	
	public ArrayList<EntityID> getBlockadesFrom(int x, int y, EntityID destination, PoliceWorldModel model) {
		// implemented by Navid(navid.it)
		//FIXME should be optimized by navid:D
		Point2D me = new Point2D(x, y);
		// -------------------------------
		ArrayList<EntityID> blocks = new ArrayList<EntityID>();
		if (getBlockades() == null)
			return blocks;
		if (getBlockades().size() == 0)
			return blocks;
		// -------------------------------
		boolean t = false;
		int j = 0;
		for (int i = 0; i < realArea.getNeighbours().size(); i++) {
			if (realArea.getNeighbours().get(i).equals(destination)) {
				j = i;
				t = true;
			}
		}
		if (!t) {
			Error e = new Error("The Entity enterd in funcion getBlockadeFrom is not a neigbour of this Road ");
			throw e;
		}
		// -------------------------------
		Edge destEdge = realArea.getEdgeTo(realArea.getNeighbours().get(j));
		Point2D dest = new Point2D((destEdge.getEndX() + destEdge.getStartX()) / 2, (destEdge.getEndY() + destEdge.getStartY()) / 2);
		Line2D meToDest = new Line2D(me, dest);
		// -------------------------------
		//	StandardEntity destEntity = model.getEntity(destination);
		// if (destEntity instanceof Road) {
		for (int i = 0; i < this.getBlockades().size(); i++) {
			Point2D block = new Point2D(((Blockade) (model.getEntity(this.getBlockades().get(i).getID()))).getX(), ((Blockade) (model.getEntity(this.getBlockades().get(i).getID()))).getY());
			Line2D meToBlock = new Line2D(me, block);
			if (!(GeometryTools2D.getRightAngleBetweenLines(meToDest, meToBlock) > 90 && GeometryTools2D.getLeftAngleBetweenLines(meToDest, meToBlock) > 90)) {
				blocks.add(this.getBlockades().get(i).getID());
			}
		}
		// }
		return blocks;
	}

	public int getValue() {
		setValue(computeValue());
		return value;
	}

	

	private int computeValue() {
		if(this.isInFireZone)
		return (1*(getRealArea().getAgent().model().time()-getRealArea().updatedtime()))-(getMoveCost()+1)/3;
		else{
			return (1000000*(getRealArea().getAgent().model().time()-getRealArea().updatedtime()))-(getMoveCost()+1)/3;
		}
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setMoveCost(int moveCost) {
		this.moveCost = moveCost;
	}

	public int getMoveCost() {
		return moveCost;
	}
	
}

