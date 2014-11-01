package sos.police_v2;

 import java.awt.geom.Ellipse2D;

import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.tools.Utils;

public class OldPoliceUtils {
	public static double getDistance(StandardEntity se1, StandardEntity se2) {
		return Utils.distance(se1.getLocation().first(), se1.getLocation().second(), se2.getLocation().first(), se2.getLocation().second());
	}
	
	/**
	 * <li>Computes geometric distance Parameter between two points, <br>
	 * changes the distance(d) using a scale. then calculates 1/(d*d).<br>
	 * <br> <li>does not return answer less than 1.<br>
	 * 
	 * @param StandardEntity se1
	 * @param StandardEntity se2
	 * @return double
	 * 
	 * @author Salim
	 */
	// public static double getDistanceParameter(StandardEntity se1, StandardEntity se2) {
	// // FIXME it may change from geo distance to real distance SBO
	// double d = Utils.distance(se1.getLocation(), se2.getLocation());
	// d /= PoliceDecisionConstants.DISTANCE_SCALE;
	// if (d < 1)
	// d = 1;
	// d = (1 / (d * d));
	// if (d > 1)
	// d = 1;
	// return d;
	// }
	
	
	
	public static Blockade hasIntersectionWithBlockades(Pair<Integer, Integer> point, Area a) {
		double size = ReachablityConstants.AGENT_WIDTH * 1 * 6;
		if (!(a instanceof Road))
			return null;
		Road road = (Road) a;
		if (!road.isBlockadesDefined())
			return null;
		if (road.getBlockades().size() == 0)
			return null;
		
		
		Ellipse2D.Double expandHuman = new Ellipse2D.Double(a.getX() - size / 2, a.getY() - size / 2, size, size);
		java.awt.geom.Area area = new java.awt.geom.Area(expandHuman);
		for (Blockade b : road.getBlockades()) {
			area.intersect(new java.awt.geom.Area(b.getShape()));
			if (!area.isEmpty())
				return b;
		}
		return null;
	}
}
