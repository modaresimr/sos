package sos.base.reachablity;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;

public class OtherReachablityTools {

	// Morteza2011*****************************************************************
	public static ArrayList<Edge> isReachableToArea(Road area, SOSArea area1) {
		ArrayList<SOSArea> areaList = new ArrayList<SOSArea>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		for (SOSArea rp : area.getReachableParts()) {
			boolean added = false;
			loop: for (Edge e : rp.getEdges()) {
				for (Edge e1 : area1.getEdges()) {
					if (Utility.getIntersect(e, e1) != null) {
						added = true;
						areaList.add(rp);
						break loop;
					}
				}
			}
			if (!added) {
				Point2D p1 = rp.getEdges().get(0).getStart();
				Point2D p2 = area1.getEdges().get(0).getStart();
				if (area1.getShape().contains(p1.getX(), p1.getY()))
					areaList.add(rp);
				else if (rp.getShape().contains(p2.getX(), p2.getY()))
					areaList.add(rp);
			}
		}
		if (areaList.size() == 0)
			return edgeList;
		for (SOSArea a : areaList) {
			for (Edge e : a.getEdges()) {
				if (e.getReachablityIndex() >= 0)
					if (area.getEdges().get(e.getReachablityIndex()).isPassable())
						edgeList.add(area.getEdges().get(e.getReachablityIndex()));
			}
		}
		return edgeList;
	}
	
	// Morteza2011*****************************************************************
	public static int getReachablePart(Road r, Point2D p) {
		for (int i = 0; i < r.getReachableParts().size(); i++) {
			SOSArea rp = r.getReachableParts().get(i);
			if (rp.getShape().contains(p.getX(), p.getY()))
				return i;
		}
		if (r.isBlockadesDefined()) {
			for (Blockade b : r.getBlockades()) {
				if(b==null)
					System.err.println("road "+r+" has a null blockade");
				if(b.getShape()==null)
					System.err.println("road "+r+" has a null blockade shape");
				if(p==null)
					System.err.println("point is null ");
				if (b.getShape().contains(p.getX(), p.getY()))
					return -1;
			}
		} else {
//			for (SOSBlockade b : r.getMiddleBlockades()) {
//				if (b.getShape().contains(p.getX(), p.getY()))
					return -1;
//			}
		}
		for (int i = 0; i < r.getReachableParts().size(); i++) {
			SOSArea rp = r.getReachableParts().get(i);
			if (rp.getEdges().size() == 0)
				continue;
			try {
				SOSArea exrp = ExpandBlockade.expandBlock(rp, ReachablityConstants.AGENT_WIDTH / 2);
				if (exrp.getShape().contains(p.getX(), p.getY()))
					return i;
			} catch (Exception e) {
				continue;
			}
		}
		return -1;
	}
}
