package sos.fire_v2.base.tools;

import java.awt.Point;

import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.util.IntList;
import sos.fire_v2.base.worldmodel.FireWorldModel;

import com.infomatiq.jsi.Rectangle;

public class FireBuilding {
	private Building building;
	private BuildingBlock buildingBlock;
	private BuildingBlock island;
	public FireWorldModel world;
	private ExtinguishableArea extinguishableArea = null;

	// Morteza2011*****************************************************************
	public FireBuilding(FireWorldModel world, Building b) {
		this.setBuilding(b);
		this.world = world;
	}

	// Morteza2011*****************************************************************
	public void setBuilding(Building b) {
		this.building = b;
	}

	// Morteza2011*****************************************************************
	public Building building() {
		return building;
	}

	// Morteza2011*****************************************************************
	public void setBuildingBlock(BuildingBlock buildingBlock) {
		this.buildingBlock = buildingBlock;
	}

	// Morteza2011*****************************************************************
	public BuildingBlock buildingBlock() {
		return buildingBlock;
	}

	// Morteza2011*****************************************************************
	public void setIsland(BuildingBlock buildingBlock) {
		this.island = buildingBlock;
	}

	// Morteza2011*****************************************************************
	public BuildingBlock island() {
		return island;
	}

	public boolean hasANeighborThatAreIntersectedWithARoad() {
		for (Building n : building.realNeighbors_Building())
			if (hasRoadBetweenThisAnd(n))
				return true;
		return false;
	}

	public boolean hasRoadBetweenThisAnd(Building neighbor) {
		Point p = null, a, b, c, d;
		a = new Point(this.building.getX(), this.building.getY());
		b = new Point(neighbor.x(), neighbor.y());
		Rectangle rect = new Rectangle(building.x(), building.y(), neighbor.x(), neighbor.y());
		IntList rs = building.model().getRoadIndexInRectangle(rect);
		for (int r = 0; r < rs.size(); r++) {
			//				boolean roadSet = false;
			Road road = building.model().roads().get(rs.get(r));
			for (Edge edge : road.getEdges()) {
				c = new Point(edge.getStartX(), edge.getStartY());
				d = new Point(edge.getEndX(), edge.getEndY());
				p = FireUtill.intersect(a, b, c, d);
				if (p != null) {
					//						neighbor.setIntersectedRoadIndex(r);
					//						neighbor.isIntersectedWithRoad = true;
					//						roadSet = true;
					//						break;
					return true;
				} else {
					p = FireUtill.intersectionZJU(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
					if (p.x != -1 && p.y != -1) {
						//							neighbor.setIntersectedRoadIndex(r);
						//							neighbor.isIntersectedWithRoad = true;
						return true;
						//							roadSet = true;
						//							break;
					}
				}
			}
		}
		return false;
	}

	// Morteza2011*****************************************************************
	@Override
	public String toString() {
		return "FireBuilding[" + building.getID().getValue() + "]";
	}

	public ExtinguishableArea getExtinguishableArea() {
		if (extinguishableArea == null) {
			extinguishableArea = new ExtinguishableArea(building, world);

		}
		return extinguishableArea;

	}
}
