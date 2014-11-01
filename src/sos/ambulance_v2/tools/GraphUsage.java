package sos.ambulance_v2.tools;

import java.util.Collection;

import sos.ambulance_v2.base.AmbulanceConstants;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.util.SOSGeometryTools;

/**
 * Created by IntelliJ IDEA.
 * User: ara
 * To change this template use File | Settings | File Templates.
 */
public class GraphUsage {
	SOSAgent<?> ownerAgent;

	// LoggingSystem log;

	public GraphUsage(SOSAgent<?> agent) {
		this.ownerAgent = agent;
		// log = new LoggingSystem(Constants.ModeType.Heavy, Constants.OutputType.File, ownerAgent.self(),
		// Constants.SystemType.GraphUsage, false);
	}

	public Area getGeometryMinDistanceArea(Area from, Collection<Area> goals) {
		int min = Integer.MAX_VALUE;
		int dis;
		Area result = null;
		for (Area to : goals) {
			dis = SOSGeometryTools.distance(from.getX(), from.getY(), to.getX(), to.getY());
			if (min > dis) {
				min = dis;
				result = to;
			}
		}
		return result;
	}

	public int getFoolMoveTimeFromTo2(Area from, Area to) {//TODO maybe change to dis=(x-x')+(y-y')
		int dis = SOSGeometryTools.distance(from.getX(), from.getY(), to.getX(), to.getY());
		double twoRadical = 1.41421356237;
		int cycle = getFoolMoveTime((int) Math.round(twoRadical * dis));
		return cycle;
	}

	public int getFoolMoveTimeFromTo(Area from, Area to) {//TODO maybe change to dis=(x-x')+(y-y')
		int dis = Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
		int cycle = getFoolMoveTime(dis);
		return cycle;
	}

	public int getFoolMoveTime(long lenght) {
		return (int) Math.round(((double) lenght) / AmbulanceConstants.AVERAGE_MOVE_PER_CYCLE);
	}
	/**
	 * gives us nearest goal from a collection of goals
	 * 
	 * @param origin
	 * @param goals
	 * @return
	 */
	// public Area BFS_special(Area origin, Collection goals) {
	// HashMap<Node, Integer> color = new HashMap<Node, Integer>();
	// HashMap<Area, Area> mp;
	// if (goals.contains(origin)) {
	// return origin;
	// }
	// Vector<Node> queue = new Vector<Node>();
	// Node v = (Node) getOutsideOrigin(origin);
	// mp = getOutsideGoals(origin, goals);
	// queue.add(v); // and put in a queue
	// color.put(v, 1); //turn the color to gray
	//
	// while (!queue.isEmpty()) {
	// Node u = (Node) queue.firstElement();
	// queue.removeElement(u); // extract a vertex from the queue
	// if (mp.containsKey(u)) {
	// return mp.get(u);
	// }
	// Road[] neigbours = ownerAgent.world.edges[u.index];
	// for (int i = 0; i < neigbours.length; i++) {
	// Road ver = ownerAgent.world.edges[u.index][i];
	// if (ver == null)
	// continue;
	// Node x = ownerAgent.world.getNode(i);
	//
	// if (!color.containsKey(x)) {
	// queue.addElement(x);
	// color.put(x, 1);
	// }
	// }
	// color.put(u, 2); //black color
	// //System.out.print(u.toString()+"   ");
	// }
	// return null;
	// }

}
