package sos.search_v2.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.move.MoveConstants;
import sos.base.move.types.DistanceMove;
import sos.base.move.types.PoliceMove;
import sos.base.move.types.SearchMove;
import sos.base.util.mapRecognition.MapInformation;
import sos.police_v2.PoliceForceAgent;
import sos.tools.Utils;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public final class SearchUtils {
	public static Point2D getSegmentIntersectionPoint(Edge e1, Edge e2) {
		Point2D point = GeometryTools2D.getIntersectionPoint(e1.getLine(), e2.getLine());
		int e1MaxX = Math.max(e1.getStartX(), e1.getEndX());
		int e1MinX = Math.min(e1.getStartX(), e1.getEndX());
		int e1MaxY = Math.max(e1.getStartY(), e1.getEndY());
		int e1MinY = Math.min(e1.getStartY(), e1.getEndY());
		int e2MaxX = Math.max(e2.getStartX(), e2.getEndX());
		int e2MinX = Math.min(e2.getStartX(), e2.getEndX());
		int e2MaxY = Math.max(e2.getStartY(), e2.getEndY());
		int e2MinY = Math.min(e2.getStartY(), e2.getEndY());

		if (point != null) {
			if (!(point.getX() <= e1MaxX && point.getX() >= e1MinX))
				return null;
			if (!(point.getX() <= e2MaxX && point.getX() >= e2MinX))
				return null;
			if (!(point.getY() <= e1MaxY && point.getY() >= e1MinY))
				return null;
			if (!(point.getY() <= e2MaxY && point.getY() >= e2MinY))
				return null;
		}
		return point;
	}

	public static double getAngle(Area area, int cx, int cy) {
		Vector2D scale = new Vector2D(0, 1);
		Vector2D v = new Vector2D(area.getX() - cx, area.getY() - cy);
		double a = GeometryTools2D.getAngleBetweenVectors(scale, v);
		if ((v.getX() < 0 && v.getY() < 0) || (v.getX() < 0 && v.getY() >= 0))
			a = 360 - a;
		return a;
	}

	public static double minDistanceOf(ArrayList<Building> list, double cx, double cy) {
		double minDistance = Integer.MAX_VALUE;
		for (Building b : list) {
			double d = Utils.distance(cx, cy, b.getX(), b.getY());
			if (d < minDistance)
				minDistance = d;
		}
		return minDistance;
	}

	public static double decimalScale(double value, double max) {
		return value / decimalScalingBase(value);
	}

	public static int log10(double value) {
		int l = 0;
		double b = 1;
		while (b < value) {
			b *= 10;
			l++;
		}
		return l;
	}

	public static double decimalScalingBase(double value) {
		long b = 1;
		while (b <= value) {
			b *= 10;
		}
		return b;
	}

	public static List<Building> getBuildingsInRange(SOSAgent<?> me, List<Building> all, int x, int y, int range) {
		Collection<Building> buildingsInRange = me.model().getObjectsInRange(x, y, range, Building.class);
		HashSet<Building> set = new HashSet<Building>(buildingsInRange);
		ArrayList<Building> result = new ArrayList<Building>(all.size());
		for (int i = all.size() - 1; i > -1; i--) {
			if (set.contains(all.get(i)))
				result.add(all.get(i));

		}
		return result;
	}

	public static double getTimePortoion(int currentTime) {
		int tt = SIMULATION_TIME(currentTime);
		return ((double) tt - (double) currentTime) / tt;
	}

	private static boolean isNoComm(SOSAgent<?> me) {
		return me.messageSystem.type == Type.NoComunication;
	}

	public static double decimalScaleTime(int value, int max) {
		return (((double) value) / max);
	}

	public static double decimalScale(long value, int max) {
		return value / decimalScalingBase(max);
	}

	public static double decimalScaleCost(double weightTo, MapInformation mapInfo, SOSAgent<?> me) {
		if (mapInfo.isBigMap()) {
			if (me instanceof PoliceForceAgent)
				return weightTo / (1100);
			return weightTo / (1700);
		} else if (mapInfo.isMediumMap()) {
			if (me instanceof PoliceForceAgent)
				return weightTo / (600);
			return weightTo / (1000);
		} else {
			if (me instanceof PoliceForceAgent)
				return weightTo / 300;
			return weightTo / 400;
		}

	}

	public static double decimalScaleDistance(double distance, MapInformation mapInfo, SOSAgent<?> me) {
		if (mapInfo.isBigMap()) {
			return distance / (1200000);
		} else if (mapInfo.isMediumMap()) {
			return distance / (700000);
		} else {
			return distance / 300000;
		}
	}

	public static boolean isValidCivilian(Civilian civ, SOSAgent<?> me) {
		if (civ == null) {
			return false;
		}
		if (civ.isUnkonwnCivilian()) {
			return false;
		}
		if (!civ.isPositionDefined()) {
			return false;
		}
		if (!(civ.getPosition() instanceof Building)) {
			return false;
		}
		if (civ.isReallyReachableSearch()) {
			return false;
		}
		if (civ.getHP() == 0) {
			return false;
		}
		if (civ.getDamage() > 200) {
			return false;
		}
		if (SimpleDeathTime.getEasyLifeTime(civ.getHP(), civ.getDamage(), civ.updatedtime()) - me.model().time() < civ.getBuriedness() / 3 + 5) {
			return false;
		}
		return true;
	}

	public static long getWeightTo(Building b, SOSAgent<?> me) {
		if (me instanceof PoliceForceAgent) {
			return me.move.getWeightToLowProcess(b.getSearchAreas(), DistanceMove.class);
		} else
			return me.move.getWeightToLowProcess(b.getSearchAreas(), SearchMove.class);

	}

	public static long getWeightTo(Road r, SOSAgent<?> me) {
		if (me instanceof PoliceForceAgent) {
			return me.move.getWeightTo(r, PoliceMove.class);
		} else
			return me.move.getWeightTo(r, SearchMove.class);

	}

	public static boolean isReachable(Building b, SOSAgent<?> me) {
		return getWeightTo(b, me) > MoveConstants.UNREACHABLE_COST;
	}

	public static int SIMULATION_TIME(int time) {
		return (int) Math.max(300, Math.ceil(time / 100) * 100);
	}
}
