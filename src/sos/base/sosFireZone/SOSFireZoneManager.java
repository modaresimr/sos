package sos.base.sosFireZone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.SOSConstant.AgentType;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.sosFireZone.util.Mergable;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;

/**
 * Writed 2012 </br>
 * Examined 2013
 * 
 * @author Yoosef
 */
public class SOSFireZoneManager {
	/**
	 * Pointer to World Model
	 */
	public SOSWorldModel model;
	/**
	 * list of pairs, each pair hold array of RealFireZone which we seen it and EstimatedFireZone that obtained from RealFirezone and each cycle we update it
	 */
	private ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> fireSites;
	public SOSAgent<?> me;
	public SOSLoggerSystem log;
	/**
	 * the last index that assigned to fireZone
	 */
	public short lastAssignedIndex = 0;

	public Mergable merger;

	public SOSFireZoneManager(SOSWorldModel model, SOSAgent<?> me, AgentType agentType) {
		this.me = me;
		this.model = model;
		log = new SOSLoggerSystem(me.me(), "SOSFireSite/SOSFireSiteManager", SOSConstant.CREATE_BASE_LOGS, OutputType.File, true);
		fireSites = new ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>>();
		me.sosLogger.addToAllLogType(log);
		merger = new SOSFireZoneMerger(this);
	}

	/**
	 * main method of fire zone manager</br>
	 * this method call each cycle to update and determine new fire zone
	 * 
	 * @param time
	 */
	public void update(int time) {
		long t1 = System.currentTimeMillis();
		log.info("size of fireSites " + getFireSites().size());
		printZoneData();
		if (me instanceof FireBrigadeAgent)
		{
			//			log.info("move updateIslands to updater ! ", null);
			((FireWorldModel) model).updateIslands();
		}
		for (Iterator<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> it = getFireSites().iterator(); it.hasNext();) {
			Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> p = it.next();
			checkEstimator(p.second());
			log.info("update fireZone  " + p);
			log.info("\tupdate estimated fireZone  " + p.second());
			p.second().update(time);
			for (SOSRealFireZone f : p.first()) {
				log.info("\tupdate real fireZone  " + f);
				f.update(time);
			}
		}
		log.info("going to create new fireZones  ");
		createNewZones(time);

		log.info("going to merge fireZones    ");
		mergeFireZones();
		printZoneData();
		//		if (!SOSConstant.IS_CHALLENGE_RUNNING)
		//			log.warn("Fire Zone Updater Finished  Time= " + (System.currentTimeMillis() - t1) + " ms ");

	}

	private void checkEstimator(SOSEstimatedFireZone second) {
		if (second.isEstimating())
			return;
		for (Building b : second.getAllBuildings()) {
			if (b.isBurning()) {
				second.setEstimating(true);
				return;
			}
		}

	}

	private void printZoneData() {
		log.info("-------------------------  FIRE ZONE DATA  ----------------------------------");
		for (Iterator<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> it = getFireSites().iterator(); it.hasNext();) {
			Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> p = it.next();
			String r = "";
			for (SOSRealFireZone sor : p.first())
				r += "  allBuilding=" + sor.allBuilding.size();
			log.info("Real " + p.first() + "     " + r);
			log.info("Estimate " + p.second() + "  " + " allbuilding=" + p.second().allBuilding.size() + " ");
			log.log("-------------------------------------------\n");
		}
		log.info("-------------------------  FIRE ZONE DATA FINISH  ----------------------------------");

	}

	/**
	 * merge the fireZones are near according to gravity
	 */
	private void mergeFireZones() {
		log.info("Fire Zone Merger Started");

		mergeEstimateFireZone();

		mergeRealFireZone();

	}

	/**
	 * merge real fire zone
	 */
	private void mergeRealFireZone() {
		log.info("start merging real fire zone");

		for (int i = 0; i < getFireSites().size(); i++) {
			ArrayList<SOSRealFireZone> realfireSites = getFireSites().get(i).first();

			if (realfireSites.size() <= 1)
				continue;//dont need to merge

			ArrayList<ArrayList<SOSAbstractFireZone>> listMergeList = getMergableFireZones(realfireSites, merger);

			for (ArrayList<SOSAbstractFireZone> mergeList : listMergeList) {

				if (mergeList.size() <= 1)
					continue;

				mergeRealFireZones(getFireSites().get(i), mergeList);
				log.info("real merged   " + getFireSites());
			}
		}

	}

	/**
	 * merge estimate fire zone
	 */
	private void mergeEstimateFireZone() {
		log.info("Estimate Fire Zone Merger Started");
		if (getFireSites().size() <= 1)
			return;// don't need to merge

		ArrayList<SOSAbstractFireZone> fireZones = new ArrayList<SOSAbstractFireZone>();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> zn : getFireSites())
			fireZones.add(zn.second());

		ArrayList<ArrayList<SOSAbstractFireZone>> listMergeList = getMergableFireZones(fireZones, merger);

		for (ArrayList<SOSAbstractFireZone> mergeList : listMergeList) {
			if (mergeList.size() <= 1)
				continue;

			ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> target = new ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>>();
			for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> ess : getFireSites())
			{
				for (SOSAbstractFireZone p : mergeList) {
					if (ess.second().equals(p))
						target.add(ess);
				}
			}
			mergeEstimateFireaite(target);

			log.info("Estimate merged   " + getFireSites());
		}

	}

	/**
	 * grouping mergable fire zones
	 */
	private ArrayList<ArrayList<SOSAbstractFireZone>> getMergableFireZones(ArrayList<? extends SOSAbstractFireZone> fireZones, Mergable merger) {
		log.info("Mergable Fire Zone Compute");

		//making graph for merge fireZone, if vertex(fire zone) u,v is mergable then (u,v) is a member of E  
		boolean[][] mergable = new boolean[fireZones.size()][fireZones.size()];
		for (int i = 0; i < fireZones.size(); i++) {
			mergable[i][i] = true;
			for (int j = 0; j < i; j++) {
				SOSAbstractFireZone fireZone1 = fireZones.get(i);
				SOSAbstractFireZone fireZone2 = fireZones.get(j);
				mergable[i][j] = merger.isMergable(fireZone1, fireZone2);
				mergable[j][i] = merger.isMergable(fireZone1, fireZone2);
			}
		}

		ArrayList<ArrayList<SOSAbstractFireZone>> listMergeList = new ArrayList<ArrayList<SOSAbstractFireZone>>();

		boolean[] visited = new boolean[fireZones.size()];
		for (int temp = 0; temp < visited.length; temp++)
			visited[temp] = false;

		for (int i = 0; i < fireZones.size(); i++) {
			if (visited[i])//is added befor 
				continue;
			Queue<Integer> queue = new LinkedList<Integer>();
			queue.add(i);

			visited[i] = true;

			ArrayList<SOSAbstractFireZone> mergeList = new ArrayList<SOSAbstractFireZone>();
			listMergeList.add(mergeList);
			while (queue.size() > 0) {//BFS for finding all the mergable firezone in graph
				int from = queue.poll();
				mergeList.add(fireZones.get(from));
				for (int to = 0; to < fireZones.size(); to++) {
					if (visited[to] == false) {//it didn't add before
						if (mergable[from][to] == true) {
							visited[to] = true;
							queue.add(to);
						}
					}
				}
			}
			log.info("mergable fire zone group (" + i + ")  " + mergeList);
		}
		return listMergeList;

	}

	/**
	 * create new fire zones by buildings that don't belong to any existence fire zones
	 * 
	 * @param time
	 */
	private void createNewZones(int time) {
		for (Iterator<Building> it = model.fieryBuildings().iterator(); it.hasNext();) {
			Building b = it.next();
			if(b.getFieryness()==0)
				continue;
			if (b.getSOSEstimateFireSite() == null) {
				SOSEstimatedFireZone estimatefz = new SOSEstimatedFireZone(lastAssignedIndex, this);
				lastAssignedIndex++;
				log.info("New Estimate Fire Zone Created  " + estimatefz + "\t" + b);
				getFireSites().add(new Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>(new ArrayList<SOSRealFireZone>(), estimatefz));
				updateNewSite(b, estimatefz);
				estimatefz.update(time);
			}

			if (b.getSOSRealFireSite() == null) {
				SOSRealFireZone realfz = new SOSRealFireZone(lastAssignedIndex, this);
				lastAssignedIndex++;
				log.info("New Real Fire Zone Created  " + realfz + "\t" + b);
				getFireZonePair(b.getSOSEstimateFireSite()).first().add(realfz);
				updateNewSite(b, realfz);
				realfz.update(time);
			}
		}
	}

	private Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> getFireZonePair(SOSEstimatedFireZone estimatfz) {
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> select : getFireSites()) {
			if (select.second().equals(estimatfz))
				return select;
		}
		return null;
	}

	private void updateNewSite(Building b, SOSAbstractFireZone f) {//TODO BFS
		log.info("updating building of new Site " + f + "     checkin for " + b);
		if (f instanceof SOSEstimatedFireZone) {
			if ((b.virtualData[0].getFieryness() > 0) && b.getSOSEstimateFireSite() == null) {
				f.addFieryBuilding(b);
				log.info(b + "  addet to fireSite " + f);
				b.setSOSEstimateFireSite((SOSEstimatedFireZone) f);
				for (Building b2 : b.realNeighbors_Building()) {
					updateNewSite(b2, f);
				}
			}
		} else if (f instanceof SOSRealFireZone) {
			if ((b.getFieryness() > 0) && b.getSOSRealFireSite() == null) {
				f.addFieryBuilding(b);
				log.info(b + "  addet to fireSite " + f);
				b.setSOSRealFireSite((SOSRealFireZone) f);
				for (Building b2 : b.realNeighbors_Building()) {
					updateNewSite(b2, f);
				}
			}
		}
	}

	/**
	 * merge array of fire zone in one fire zone
	 * 
	 * @param target
	 */
	private void mergeEstimateFireaite(ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> target) {

		ArrayList<SOSRealFireZone> realZones = new ArrayList<SOSRealFireZone>();

		getFireSites().removeAll(target);

		SOSEstimatedFireZone newEstimateFireSite = new SOSEstimatedFireZone(lastAssignedIndex++, this);
		SOSEstimatedFireZone fz;

		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> zone : target) {
			fz = zone.second();

			for (Building b : fz.getAllBuildings()) {
				if (!b.getSOSEstimateFireSite().equals(newEstimateFireSite)) {
					b.setSOSEstimateFireSite(newEstimateFireSite);
					newEstimateFireSite.getAllBuildings().add(b);
					newEstimateFireSite.updateXY(b, 1);
				}
			}

			for (Building b : fz.getEstimatorBuilding()) {
				if (!b.getEstimator().equals(newEstimateFireSite)) {
					b.addToEstimator(newEstimateFireSite);
					newEstimateFireSite.getEstimatorBuilding().add(b);
				}
			}

			realZones.addAll(zone.first());
		}
		newEstimateFireSite.update(model.time());
		getFireSites().add(new Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>(realZones, newEstimateFireSite));
	}

	/**
	 * merge real fire zones
	 * 
	 * @param targets
	 * @param mergeList
	 */
	private void mergeRealFireZones(Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> target, ArrayList<SOSAbstractFireZone> mergeList) {
		if (target == null)
			return;
		ArrayList<SOSRealFireZone> realZones = target.first();

		getFireSites().remove(target);

		SOSRealFireZone newRealFireSite = new SOSRealFireZone(lastAssignedIndex++, this);
		SOSRealFireZone real;

		for (SOSAbstractFireZone fz : mergeList) {
			real = (SOSRealFireZone) fz;
			realZones.remove(real);
			for (Building b : real.getAllBuildings()) {
				if (!b.getSOSRealFireSite().equals(newRealFireSite)) {
					b.setSOSRealFireSite(newRealFireSite);
					newRealFireSite.addFieryBuilding(b);
					newRealFireSite.updateXY(b, 1);

				}
			}
			real.update(model.time());
		}

		realZones.add(newRealFireSite);
		getFireSites().add(new Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>(realZones, target.second()));
	}

	public Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> getPairWithReal(SOSRealFireZone real) {
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> p : getFireSites()) {
			if (p.first().contains(real))
				return p;
		}
		return null;
	}

	public ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> getFireSites() {
		return fireSites;
	}

	public ArrayList<Building> getAllEstimateBuilding() {
		ArrayList<Building> targets = new ArrayList<Building>();
		for (int i = 0; i < fireSites.size(); i++) {
			targets.addAll(fireSites.get(i).second().getAllBuildings());
		}
		return targets;
	}

	public ArrayList<Building> getAllRealBuilding() {
		ArrayList<Building> targets = new ArrayList<Building>();
		for (int i = 0; i < fireSites.size(); i++) {
			for (SOSRealFireZone fs : fireSites.get(i).first()) {
				targets.addAll(fs.getAllBuildings());
			}
		}
		return targets;
	}

	public SOSEstimatedFireZone createUnknownFireZone(ArrayList<Building> building) {
		for (Building b : building) {
			b.virtualData[0].artificialFire(1);
		}
		SOSEstimatedFireZone estimatefz = null;
		for (Iterator<Building> it = building.iterator(); it.hasNext();) {
			Building b = it.next();

			if (b.getSOSEstimateFireSite() == null) {
				estimatefz = new SOSEstimatedFireZone(lastAssignedIndex, this);
				lastAssignedIndex++;
				estimatefz.setEstimating(false);
				log.info("New Estimate Fire Zone Created Unknown " + estimatefz + "\t" + b);
				getFireSites().add(new Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>(new ArrayList<SOSRealFireZone>(), estimatefz));
				updateNewSite(b, estimatefz);
				estimatefz.update(model.time());
			}
		}

		return estimatefz;

	}
}
