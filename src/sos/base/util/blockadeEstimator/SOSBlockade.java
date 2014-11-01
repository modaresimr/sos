package sos.base.util.blockadeEstimator;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.reachablity.ExpandBlockade;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.SOSArea;
import sos.base.util.SOSGeometryTools;
import sos.base.util.geom.SOSShape;

/**
 * @author Ali
 */
public class SOSBlockade implements BlockadeInterface {
	static final double REPAIR_COST_FACTOR = 0.000001; // Converts square mm to square m.
	private SOSShape shape;
	private final Road position;
	private byte clearedTime = 0;

	public SOSBlockade(Area geomArea, Road position) {
		this(AliGeometryTools.getApexes(geomArea), position);

	}

	public SOSBlockade(int[] apexes, Road position) {
		this.position = position;
		setApexes(apexes);
	}

	@Override
	public int getX() {

		return shape.getCenterX();
	}

	@Override
	public int getY() {
		return shape.getCenterY();
	}

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public int[] getApexes() {
		return shape.getApexes();
	}

	@Override
	public String toString() {
		return "SOSBlockade(in: " + getPosition() + ")";
	}

	@Override
	public Road getPosition() {
		return position;
	}

	@Override
	public int getRepairCost() {
		return (int) (SOSGeometryTools.computeArea(getApexes()) * REPAIR_COST_FACTOR);
	}

	@Override
	public void setApexes(int[] apexes) {
		shape = new SOSShape(apexes);
		if (apexes.length >= 2 && getRepairCost() > 0) {
			try {
				setExpandedBlock(true);
			} catch (Exception e) {
				System.err.println("[ERROR] in Middle Blockade");
			}
		}
	}

	public void setClearedTime(int clearedTime) {
		this.clearedTime = (byte) clearedTime;
	}

	public int getClearedTime() {
		return clearedTime;
	}

	// ########################## {{ REACHABLITY }} ################################

	// Morteza2011*****************************************************************
	public List<Edge> getEdges() {
		return AliGeometryTools.getEdges(getApexes());
	}

	// Morteza2011*****************************************************************
	public SOSArea setExpandedBlock(boolean isNewBlockade) {
		SOSArea area = new SOSArea(getEdges());
		ArrayList<Blockade> b = new ArrayList<Blockade>();
		area.setReachablityBlockades(b);
		if (isNewBlockade)
			return ExpandBlockade.expandBlock(area, ReachablityConstants.BLOCK_EXPAND_WIDTH + getPosition().getExtraDistanceForNewExpand());
		else
			return ExpandBlockade.expandBlock(area, ReachablityConstants.BLOCK_EXPAND_WIDTH);
	}

	// Morteza2011*****************************************************************
	public SOSArea getExpandedBlock() {
		return setExpandedBlock(true);
	}

	@Override
	public Point2D getCenteroid() {
		return shape.getCenterPoint();
	}

	// ################### {{ END OF REACHABLITY }} ##################################

}
