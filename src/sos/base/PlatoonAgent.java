package sos.base;

import java.util.ArrayList;
import java.util.List;

import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.namayangar.SOSWorldModelNamayangar;

/**
 * Abstract base class for SOS agents.
 *
 * @param <E>
 *            The subclass of StandardEntity this agent wants to control.
 */
public abstract class PlatoonAgent<E extends Human> extends SOSAgent<E> {

	@Override
	protected void preCompute() {
		super.preCompute();
	}

	@Override
	protected void prepareForThink() {
		super.prepareForThink();

		long t1 = System.currentTimeMillis();
		newSearch.preSearch();
		sosLogger.act.info("     presearch " + (System.currentTimeMillis() - t1) + " ms");
	}

	@Override
	public void preSuperThink() {
		super.preSuperThink();

		if (!isTimeToActFinished()&&!SOSConstant.DISABLE_FIRE_ESTIMATION) {
			long t1 = System.currentTimeMillis();
			fireEstimator.step(model().time());
			fireSiteManager.update(model().time());
			sosLogger.act.info("     fire estimator " + (System.currentTimeMillis() - t1) + " ms");
		}

		if (sampler != null) {
			long t1 = System.currentTimeMillis();
			sampler.step();
			sosLogger.act.info("     sampling got" + (System.currentTimeMillis() - t1) + " ms");
		}
	}

	@Override
	protected void think() throws SOSActionException {
		super.think();
		if (((Human) me()).getBuriedness() > 0 && !(this instanceof AmbulanceTeamAgent) ) //AT handled in IAmHurtState
			problemRest("I have Buriedness;)");
	}

	@Override
	protected void finalizeThink() {
		super.finalizeThink();
	}

	// Ali
	@Override
	public Area location() {
		return (Area) super.location();
	}

	// Ali

	/**
	 * @editedBy: Ali
	 *            Construct a random walk starting from this agent's current location.
	 *            Buildings will only be entered at the end of the walk.
	 * @return A random walk.
	 * @throws SOSActionException
	 */
	protected void dummyRandomWalk() throws SOSActionException {
		move.move(move.getBfs().getDummyRandomWalkPath());
	}

	/**
	 * @author Ali
	 *         Construct a random walk starting from this agent's current location.
	 *         Buildings will only be entered at the end of the walk.
	 * @return A random walk.
	 */
	public void randomWalk() throws SOSActionException {
		randomWalk(true);
	}

	@SuppressWarnings("unchecked")
	/**
	 * @author Ali
	 * This method provide a global access to SOSAgent
	 * @param agentClass is type of expected Agent
	 * @return
	 */
	public static PlatoonAgent<? extends Human> currentAgent() {
		return currentAgent(PlatoonAgent.class);
	}

	/**
	 * @author Ali
	 * @param doDummyRandomWalk
	 * @throws SOSActionException
	 */
	public void randomWalk(boolean doDummyRandomWalk) throws SOSActionException {
		List<Area> result = new ArrayList<Area>();
		for (Road road : model().roads()) {
			if (road.updatedtime() < 2)
				result.add(road);
		}
		if (result.isEmpty()) {
			for (Building building : model().buildings()) {
				if (building.updatedtime() < 2)
					result.addAll(building.getNeighbours());
			}
		}

		if (result.isEmpty())
			if (doDummyRandomWalk)
				dummyRandomWalk();
			else
				return;
		move.moveStandard(result);
	}

	// Morteza2011*****************************************************************
	public SOSWorldModelNamayangar getWorldModelViewer() {
		return worldmodelNamayangar;
	}

	/**
	 * Yoosef
	 *
	 * @throws SOSActionException
	 */
	public void search() throws SOSActionException {
		newSearch.search();
	}

	/*
	 * Please Keep it at the End
	 */
	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray, sender, channel);
		newSearch.hear(header, data, sender, channel);
	}
}