package sample;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.WorldModel;
import rescuecore2.worldmodel.WorldModelListener;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Center;
import sos.base.entities.Civilian;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.PoliceForce;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.entities.StandardWorldModel;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.search_v2.worldModel.SearchWorldModel;
import sun.management.resources.agent;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Rectangle;

/**
 * Default implementation of a WorldModel.
 *
 * @param <T>
 *            The subclass of Entity that this world model holds.
 */
public class SampleWorldModel extends StandardWorldModel {
	/* ///////////////////S.O.S instants////////////////// */
	/* ////////////////////End of S.O.S/////////////////// */
	/* ////////////////////////BASE/////////////////////// */
	/**/private ArrayList<Civilian> civilians = new ArrayList<Civilian>(70);
	/**/private List<FireBrigade> fireBrigades = new ArrayList<FireBrigade>();
	/**/private List<PoliceForce> policeForces = new ArrayList<PoliceForce>();
	/**/private List<AmbulanceTeam> ambulanceTeams = new ArrayList<AmbulanceTeam>();
	/**/private ArrayList<Human> humans = new ArrayList<Human>(100);
	/**/private List<Human> agents = new ArrayList<Human>();
	/**/private List<Center> centers = new ArrayList<Center>();
	/**/private List<Building> buildings = new ArrayList<Building>(1500);
	/**/private List<Road> roads = new ArrayList<Road>(1500);
	/**/private ArrayList<Refuge> refuges = new ArrayList<Refuge>();
	/**/private ArrayList<Blockade> blockades = new ArrayList<Blockade>();
	/**/private List<Area> areas = new ArrayList<Area>(2000);// aramik
	/**/private int time = 0;
	private Point mapCenter = null;
	private final SOSAbstractSampleAgent<?> agent;

	// DON'T ADD ANY instant or method HEAR!!!!!!
	public SampleWorldModel(SOSAbstractSampleAgent<?> agent) {
		super();
		this.agent = agent;
		addWorldModelListener(new entityAddRemoveListener());

	}


	public void precompute() {
		numerizeRealObjects();
	}
	private void numerizeRealObjects() {
		// ******Sorted by their x
		Collections.sort(buildings, new SortComparator<Building>());
		buildings=Collections.unmodifiableList(buildings);
		Collections.sort(roads, new SortComparator<Road>());
		roads=Collections.unmodifiableList(roads);
		Collections.sort(areas, new SortComparator<Area>());
		areas=Collections.unmodifiableList(areas);
		// ******Sorted by their Id
		Collections.sort(fireBrigades, new SortComparator<FireBrigade>());
		fireBrigades=Collections.unmodifiableList(fireBrigades);
		Collections.sort(centers, new SortComparator<Center>());
		centers=Collections.unmodifiableList(centers);
		Collections.sort(ambulanceTeams, new SortComparator<AmbulanceTeam>());
		ambulanceTeams=Collections.unmodifiableList(ambulanceTeams);
		Collections.sort(policeForces, new SortComparator<PoliceForce>());
		policeForces=Collections.unmodifiableList(policeForces);
		Collections.sort(agents, new SortComparator<Human>());
		agents=Collections.unmodifiableList(agents);
		// ***************************************************Setting index to
		// objects
		for (int i = 0; i < buildings().size(); i++)
			buildings().get(i).setBuildingIndex((short) i);
		for (int i = 0; i < roads().size(); i++)
			roads().get(i).setRoadIndex((short) i);
		for (int i = 0; i < areas().size(); i++)
			areas().get(i).setAreaIndex((short) i);
		// fire section
		for (int i = 0; i < fireBrigades().size(); i++)
			fireBrigades().get(i).setFireIndex((short) i);
		// ambulance section
		for (int i = 0; i < ambulanceTeams().size(); i++)
			ambulanceTeams().get(i).setAmbIndex((short) i);
		// police section
		for (int i = 0; i < policeForces().size(); i++)
			policeForces().get(i).setPoliceIndex((short) i);
		// centers section
		for (int i = 0; i < centers().size(); i++)
			centers().get(i).setCenterIndex((short) i);
		// ALL AGENTS
		for (int i = 0; i < agents().size(); i++)
			agents().get(i).setAgentIndex((short) i);
	}
	// ------------------------------------------------------------------------------------------------//
	public final class SortComparator<C extends StandardEntity> implements java.util.Comparator<C>, java.io.Serializable {
		private static final long serialVersionUID = -123456789123525L;

		@Override
		public int compare(C ro1, C ro2) {
			if (ro1 instanceof Human)
				if (ro1.getID().getValue() > ro2.getID().getValue())
					return 1;
			if (!(ro1 instanceof Human))
				if (ro1.getLocation().first() > ro2.getLocation().first() || ro1.getLocation().first() == ro2.getLocation().first() && ro1.getLocation().second() > ro2.getLocation().second())
					return 1;
			return -1;
		}
	}

	public SOSLoggerSystem log() {
		return agent.sosLogger;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	@Override
	public void merge(ChangeSet changeSet, int time) {
		try {
			long startUpdate = System.currentTimeMillis();
			agent().updater.update(changeSet, time); // aramik
			agent().sosLogger.info("Updating by sense time=" + (System.currentTimeMillis() - startUpdate) + " ms");
		} catch (Exception e) {
			agent().sosLogger.fatal(e);
		}

	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	public void setTime(int time) {
		this.time = time;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	@Override
	public int time() {
		return time;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	@Override
	public ArrayList<Civilian> civilians() {
		return civilians;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	@Override
	public ArrayList<Human> humans() {
		return humans;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link agent}
	 */
	@Override
	public List<Human> agents() {
		return agents;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link FireBrigade}
	 */
	@Override
	public List<FireBrigade> fireBrigades() {
		return fireBrigades;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link PoliceForce}
	 */
	@Override
	public List<PoliceForce> policeForces() {
		return policeForces;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link AmbulanceTeam}
	 */
	@Override
	public List<AmbulanceTeam> ambulanceTeams() {
		return ambulanceTeams;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link Center}
	 */
	@Override
	public List<Center> centers() {
		return centers;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link Building}
	 */
	@Override
	public List<Building> buildings() {
		return buildings;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link Road}
	 */
	@Override
	public List<Road> roads() {
		return roads;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	@Override
	public List<Refuge> refuges() {
		return refuges;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	@Override
	public ArrayList<Blockade> blockades() {
		return blockades;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 *
	 * @return Unmodifiable {@link Area}
	 */
	@Override
	public List<Area> areas() {
		return areas;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	public SOSAbstractSampleAgent<?> agent() {
		return agent;
	}

	// DON'T ADD ANY instant or method HEAR!!!!!!
	private class entityAddRemoveListener implements WorldModelListener<StandardEntity> {
		// DON'T ADD ANY instant or method HEAR!!!!!!
		@Override
		public void entityAdded(WorldModel<? extends StandardEntity> model, StandardEntity e) {
			if (e instanceof Human) {
				humans.add((Human) e);
				if (!(e instanceof Civilian))
					agents.add((Human) e);

				if (e instanceof FireBrigade)
					fireBrigades.add((FireBrigade) e);
				else if (e instanceof PoliceForce)
					policeForces.add((PoliceForce) e);
				else if (e instanceof AmbulanceTeam)
					ambulanceTeams.add((AmbulanceTeam) e);
				else if (e instanceof Civilian) {
					civilians.add((Civilian) e);
				}
			} else {
				if (e instanceof Area)
					areas.add((Area) e);
				if (e instanceof Building) {
					buildings.add((Building) e);
					if (e instanceof Refuge)
						refuges.add((Refuge) e);
					else if (e instanceof Center)
						centers.add((Center) e);
				} else {
					if (e instanceof Road)
						roads.add((Road) e);
					else if (e instanceof Blockade)
						blockades.add((Blockade) e);
				}

			}
		}

		// DON'T ADD ANY instant or method HEAR!!!!!!
		@Override
		public void entityRemoved(WorldModel<? extends StandardEntity> model, StandardEntity e) {
			// TODO Auto-generated method stub
			if (e instanceof Human) {
				humans.remove(e);
				if (e instanceof FireBrigade)
					fireBrigades.remove(e);
				else if (e instanceof PoliceForce)
					policeForces.remove(e);
				else if (e instanceof AmbulanceTeam)
					ambulanceTeams.remove(e);
				else if (e instanceof Civilian)
					civilians.remove(e);
			} else {
				if (e instanceof Building) {
					buildings.remove(e);
					if (e instanceof Refuge)
						refuges.remove(e);
					else if (e instanceof Center)
						centers.remove(e);
				} else {
					if (e instanceof Road)
						roads.remove(e);
					else if (e instanceof Blockade)
						blockades.remove(e);
				}
			}
		}
	}

	/* /////////////////////END of BASE///////////////////// */
	/* ////////////////////S.O.S Methods////////////////// */

	// Ali
	public void removeBlockade(Blockade blockade) {
		if (blockade == null)
			return;
		removeStandardEntity(blockade, blockades());
		blockade.setRepairCost(-1);
		removeStandardEntity(blockade, (ArrayList<Blockade>) blockade.getPosition().getBlockades());
	}

	private void removeStandardEntity(StandardEntity se, ArrayList<? extends StandardEntity> arr) {
		int index = -1;
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getID().getValue() == se.getID().getValue()) {
				index = i;
				break;
			}
		}
		if (index == -1)
			return;
		arr.remove(index);
	}

	public Point mapCenter() {
		if (mapCenter == null)
			mapCenter = new Point((int) getBounds().getCenterX(), (int) getBounds().getCenterY());
		return mapCenter;
	}

	//Ali
	public <A extends StandardEntity> Collection<A> getObjectsInRectangle(java.awt.Rectangle rectangle, final Class<A> type) {
		return getObjectsInRectangle(new Rectangle((float) rectangle.getMinX(), (float) rectangle.getMinY(), (float) rectangle.getMaxX(), (float) rectangle.getMaxY()), type);
	}
	//Ali
	public <A extends StandardEntity> Collection<A> getObjectsInRectangle(Rectangle rectangle, final Class<A> type) {
		if (!indexed) {
			index();
		}
		if(type.getSuperclass()== Human.class)
			new Error("You can use object in range for human!!!!").printStackTrace();
		final Collection<A> result = new ArrayList<A>();
		index.intersects(rectangle, new IntProcedure() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean execute(int id) {
				StandardEntity e = getEntity(new EntityID(id));
				if (e != null && type.isInstance(e)) {
					result.add((A) e);
				}
				return true;
			}
		});

		return result;
	}
	//Ali
	public <A extends StandardEntity> Collection<A> getObjectsInRange(EntityID entity, int range, Class<A> type) {
		return getObjectsInRange(getEntity(entity), range, type);
	}
	//Ali
	public <A extends StandardEntity> Collection<A> getObjectsInRange(StandardEntity entity, int range, Class<A> type) {
		Pair<Integer, Integer> location = entity.getLocation();
		if (location == null) {
			return new HashSet<A>();
		}
		return getObjectsInRange(location.first(), location.second(), range, type);
	}
	//Ali
	public <A extends StandardEntity> Collection<A> getObjectsInRange(int x, int y, int range, Class<A> type) {
		Rectangle r = new Rectangle(x - range, y - range, x + range, y + range);
		return getObjectsInRectangle(r, type);
	}

	/* ////////////////////End of S.O.S/////////////////// */
	///////Yoosef
	public SearchWorldModel<? extends Human> searchWorldModel;




	@Override
	public StandardEntity me() {
		return agent.me();
	}

}