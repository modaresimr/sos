package sos.fire_v2.base.tools;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.search_v2.tools.SearchUtils;
import sos.tools.Utils;

/**
 * @author Yoosef
 * @param <E>
 */
public class FireStarCluster {

	public static final int STAR_SUB_ZONES = 2;

	private FireStarZone[] starZones;

	private SOSAgent agent;

	public FireStarCluster(SOSAgent agent) {
		this.agent = agent;
	}

	public void startClustering(int regSize) {

		ArrayList<Building>[] starAreas = new ArrayList[regSize];
		Point2D[] centPoints = new Point2D[regSize];
		setStarZones(new FireStarZone[regSize]);

		float taleAngle = 360f / ((regSize - 1));

		//initializing starAreas
		Pair<Integer, Integer> meanCenter = getMeanCenter();
		int cx = meanCenter.first();
		int cy = meanCenter.second();

		for (int i = 0; i < starAreas.length; i++) {
			starAreas[i] = new ArrayList<Building>();
			centPoints[i] = new Point2D(0, 0);
		}

		double maxDistance = getMaxDistance(cx, cy);

		for (Building b : agent.model().buildings()) {
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
			getStarZones()[i] = new FireStarZone(STAR_SUB_ZONES, centPoints[i].getX(), centPoints[i].getY(), i);
			getStarZones()[i].createSubZones(starAreas[i], centPoints[i].getX(), centPoints[i].getY());
		}

	}

	public void setStarZones(FireStarZone[] starZones) {
		this.starZones = starZones;
	}

	public FireStarZone[] getStarZones() {
		return starZones;
	}

	public int getNormalizationCoeficient() {
		return 3;
	}

	public double getMaxDistance(int cx, int cy) {
		double maxDistance = -1;
		for (Building b : agent.model().buildings()) {
			double distance = Utils.distance(b.getX(), b.getY(), cx, cy);
			if (distance > maxDistance)
				maxDistance = distance;
		}
		return maxDistance;
	}

	public Pair<Integer, Integer> getMeanCenter() {
		int cx = 0;
		int cy = 0;

		for (Building b : agent.model().buildings()) {
			cx += b.getX();
			cy += b.getY();

		}
		return new Pair<Integer, Integer>(cx / agent.model().buildings().size(), cy / agent.model().buildings().size());
	}

}
