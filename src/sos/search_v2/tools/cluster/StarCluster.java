package sos.search_v2.tools.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.search_v2.tools.SearchUtils;
import sos.tools.Utils;

/**
 * @author Salim,Yoosef
 * @param <E>
 */
public class StarCluster<E extends Human> extends MapClusterType<E> {

	public static int STAR_TALES = 8;

	public static final int STAR_SUB_ZONES = 2;

	private StarZone[] starZones;

	public StarCluster(SOSAgent<E> me, List<E> agents) {
		super(me, agents);
	}

	@Override
	public void startClustering(SOSWorldModel model) {

		ArrayList<Building>[] starAreas = new ArrayList[STAR_TALES + 1];
		Point2D[] centPoints = new Point2D[STAR_TALES + 1];
		setStarZones(new StarZone[STAR_TALES + 1]);

		float taleAngle = 360 / STAR_TALES;

		//initializing starAreas
		Pair<Integer, Integer> meanCenter = getMeanCenter();
		int cx = meanCenter.first();
		int cy = meanCenter.second();

		for (int i = 0; i < starAreas.length; i++) {
			starAreas[i] = new ArrayList<Building>();
			centPoints[i] = new Point2D(0, 0);
		}

		double maxDistance = getMaxDistance(cx, cy);

		for (Building b : me.model().buildings()) {
			double distance = Utils.distance(b.getX(), b.getY(), cx, cy);
			if (distance < maxDistance / 3) {
				starAreas[0].add(b);
				centPoints[0].setCoordinations(centPoints[0].getX() + b.getX(), centPoints[0].getY() + b.getY());
			} else {
				int index = ((int) (SearchUtils.getAngle(b, cx, cy) / taleAngle)) + 1;
				starAreas[index].add(b);
				centPoints[index].setCoordinations(centPoints[index].getX() + b.getX(), centPoints[index].getY() + b.getY());
			}
		}

		// Finilizing centeral points
		for (int i = 0; i < centPoints.length; i++) {
			centPoints[i].setCoordinations(centPoints[i].getX() / starAreas[i].size(), centPoints[i].getY() / starAreas[i].size());
		}

		//creating subZones
		for (int i = 0; i < starAreas.length; i++) {
			getStarZones()[i] = new StarZone(STAR_SUB_ZONES, centPoints[i].getX(), centPoints[i].getY(), i);
			getStarZones()[i].createSubZones(starAreas[i], centPoints[i].getX(), centPoints[i].getY());
		}

		//		// setting centeral gather point of map
		//		getGatheringArea(starAreas, cx, cy);
		//

		//		setGatheringAreaOfStarZones();
		/////////////YOOSEF

		//		int index = 0;
		//		for (int i = 0; i < getStarZones().length; i++) {
		//			clusters.put(agents.get(i), new ClusterData(i, getStarZones()[i], me, index));
		//			index++;
		//		}
	}

	//	private void setGatheringAreaOfStarZones() {
	//		double[] maxScores = new double[getStarZones().length];
	//		Road[] gatheringAreas = new Road[getStarZones().length];
	//		for (Road r : me.model().roads()) {
	//			for (int j = 0; j < getStarZones().length; j++) {
	//				double distance = Math.max(Utils.distance(r.getX(), r.getY(), getStarZones()[j].getCx(), getStarZones()[j].getCy()), 1);
	//				double nCount = me.model().searchWorldModel.getSearchRoad(r).getNotEntranceNeighborCount();
	//				double height = r.getGeomArea().getBounds2D().getHeight();
	//				double score = (height * nCount) / (distance * distance);
	//				if (score > maxScores[j]) {
	//					maxScores[j] = score;
	//					gatheringAreas[j] = r;
	//				}
	//			}
	//		}
	//		for (int i = 0; i < gatheringAreas.length; i++) {
	//			getStarZones()[i].setGatheringRoad(gatheringAreas[i]);
	//		}
	//	}
	//
	public void setStarZones(StarZone[] starZones) {
		this.starZones = starZones;
	}

	public StarZone[] getStarZones() {
		return starZones;
	}

	//	public double getArea(Road r) {
	//		return r.getGeomArea().getBounds2D().getWidth() * r.getGeomArea().getBounds2D().getHeight();
	//	}
	//
	//	public void getGatheringArea(ArrayList<Building>[] starAreas, int cx, int cy) {
	//
	//		double meanDistance = 0;
	//		double meanArea = 0;
	//		double meanHeight = 0;
	//		double meanNeighbours = 0;
	//		double meanAreaScale = 0;
	//		int validNeighbourRoads = 0;
	//
	//		for (Building b : starAreas[0]) {
	//			double distance = Utils.distance(b.getX(), b.getY(), cx, cy);
	//			meanDistance += distance;
	//		}
	//		meanDistance /= starAreas[0].size();
	//
	//		for (Road r : me.model().roads()) {
	//			meanArea += getArea(r);
	//			if (me.model().searchWorldModel.getSearchRoad(r).getNotEntranceNeighborCount() > 2) {
	//				meanNeighbours += me.model().searchWorldModel.getSearchRoad(r).getNotEntranceNeighborCount();
	//				validNeighbourRoads++;
	//			}
	//			meanHeight += r.getGeomArea().getBounds2D().getHeight();
	//
	//		}
	//		for (Road r : me.model().roads()) {
	//			meanAreaScale += (getArea(r) / meanArea);
	//
	//		}
	//		meanArea /= me.model().roads().size();
	//		meanHeight /= me.model().roads().size();
	//		meanNeighbours /= validNeighbourRoads;
	//		meanAreaScale /= me.model().roads().size();
	//
	//		double maxScore = Double.MIN_VALUE;
	//		double minDistance = 0;
	//		for (Road r : me.model().roads()) {
	//			if (r.getNeighbours().size() > 2) {
	//				double distance = Utils.distance(r.getX(), r.getY(), cx, cy);
	//				if (distance < meanDistance)
	//					distance = meanDistance;
	//
	//				double area = getArea(r);
	//				if (area > meanArea)
	//					area = meanArea;
	//
	//				double nCount = me.model().searchWorldModel.getSearchRoad(r).getNotEntranceNeighborCount();
	//				if (nCount > meanNeighbours * getNormalizationCoeficient())
	//					nCount = meanNeighbours * getNormalizationCoeficient();
	//
	//				double height = r.getGeomArea().getBounds2D().getHeight();
	//
	//				if (height > meanHeight * getNormalizationCoeficient())
	//					height = meanHeight * getNormalizationCoeficient();
	//
	//				double width = r.getGeomArea().getBounds2D().getWidth();
	//
	//				double areaScale = (height * width) / meanArea;
	//				if (areaScale > meanAreaScale)
	//					areaScale = meanAreaScale;
	//
	//				double score = (height * areaScale * nCount) / (distance * distance * distance);
	//				if (score > maxScore || (score == maxScore && Utils.distance(r.getX(), r.getY(), cx, cy) < minDistance)) {
	//					maxScore = score;
	//					Road gatheringArea = r;
	//					minDistance = Utils.distance(r.getX(), r.getY(), cx, cy);
	//				}
	//			}
	//		}
	//
	//	}

	public int getNormalizationCoeficient() {
		return 3;
	}

	public double getMaxDistance(int cx, int cy) {
		double maxDistance = -1;
		for (Building b : me.model().buildings()) {
			double distance = Utils.distance(b.getX(), b.getY(), cx, cy);
			if (distance > maxDistance)
				maxDistance = distance;
		}
		return maxDistance;
	}

	public Pair<Integer, Integer> getMeanCenter() {
		int cx = 0;
		int cy = 0;

		for (Building b : me.model().buildings()) {
			cx += b.getX();
			cy += b.getY();

		}
		return new Pair<Integer, Integer>(cx / me.model().buildings().size(), cy / me.model().buildings().size());
	}

	@Override
	public Collection<ClusterData> allClusters() {
		return null;
	}

}
