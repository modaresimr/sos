package sos.base.util;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardWorldModel;
import sos.base.reachablity.Reachablity.ReachablityState;

public class Utils {
	/**
	 * @author salim
	 */
	public static int AGENT_RADIUS = 500;

	public static int isInNeighbours(Road e1, Entity e2) {
		for (int i = 0; i < e1.getNeighbours().size(); i++) {
			if (e1.getNeighbours().get(i).equals(e2.getID()))
				return i;
		}
		return -1;
	}

	public static double distance(double x1, double x2, double y1, double y2) {
		//implemented by salim(salim.malakouti@gmail)
		return Math.sqrt((Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));

	}

	public static List<EntityID> getEntitiesOfProperty(List<EntityID> list, SOSWorldModel world, String property) {
		List<EntityID> results = new ArrayList<EntityID>();
		for (EntityID id : list) {
			Entity e = world.getEntity(id);
			if (e == null)
				continue;
			if (e.getProperty(property) == null)
				continue;
			results.add(id);
		}
		return results;
	}

	//	public static int getPassableLines(Blockade b, Road r) {
	//		if (b == null)
	//			return 1;
	//		if (r == null)
	//			return 1;
	//		Area a1 = new Area(b.getShape());
	//		Area a2 = new Area(SOSGeometryTools.getShape(r));
	//		a2.subtract(a1);
	//		int pLines = -1;
	//		Area res = null;
	//		int mm = 2 * SOSGeometryTools.AGENT_RADIUS;
	//		while (a2.isSingular()) {
	//			pLines++;
	//			res = SOSGeometryTools.expandArea(b.getApexes(), mm, b.getX(), b.getY());
	//			a2.subtract(res);
	//			mm *= 2;
	//		}
	//		return pLines;
	//
	//	}

	public static int max(int x, int y) {
		return x >= y ? x : y;
	}

	public static int min(int x, int y) {
		return x <= y ? x : y;
	}

	public static ArrayList<EntityID> getEntitiesOFType(StandardWorldModel model, List<EntityID> list, String urn) {
		ArrayList<EntityID> results = new ArrayList<EntityID>();
		for (EntityID id : list) {
			if (model.getEntity(id).getURN().equals(urn)) {
				results.add(id);
			}
		}
		return results;
	}

	public static ArrayList<Edge> invert(List<Edge> list) {
		ArrayList<Edge> results = new ArrayList<Edge>(list.size());
		for (int i = list.size() - 1; i > -1; i--) {
			results.add(list.get(i));
		}
		return results;

	}
	public static int roundToTop(float f){
		if(f>(int)f)return (int) (f+1);
		return (int) f;
	}
	
	// ARAMIK
	public static GraphEdgeState convertReachabilityStatesToGraphEdgeStates(ReachablityState rs) {
		switch (rs) {
		case Open:
			return GraphEdgeState.Open;
		case Close:
			return GraphEdgeState.Block;
		case FoggyClose:
			return GraphEdgeState.FoggyBlock;
		case FoggyOpen:
			return GraphEdgeState.FoggyOpen;
		}
		return null;
	}
	
	/**
	 * @author Salim
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static boolean areNeighbour(Area a1, Area a2) {
		for (EntityID id : a1.getNeighboursID()) {
			if (id.getValue() == a2.getID().getValue())
				return true;
		}
		return false;
	}
}
