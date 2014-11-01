package sos.police_v2;

import java.util.ArrayList;
import java.util.HashMap;

import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.TimeNamayangar;
import sos.police_v2.base.AbstractPoliceForceAgent;
import sos.police_v2.base.clearablePointToReachable.GeoClearPointReachablity;
import sos.police_v2.base.clearablePointToReachable.GeoDegreeClearPointToReachable;
import sos.police_v2.state.DummyPoliceState;
import sos.police_v2.state.OpenCivilianState;
import sos.police_v2.state.PoliceAbstractState;
import sos.police_v2.state.SearchState;
import sos.police_v2.state.intrupt.ClearEntrances;
import sos.police_v2.state.intrupt.DamagedState;
import sos.police_v2.state.intrupt.HeavyStockedHandlerState;
import sos.police_v2.state.intrupt.LockInBlockadeState;
import sos.police_v2.state.intrupt.PoliceAbstractIntruptState;
import sos.police_v2.state.intrupt.StockHandlerState;

/**
 * SOS police force agent.
 */
public class PoliceForceAgent extends AbstractPoliceForceAgent {
	private ArrayList<PoliceAbstractState> states = new ArrayList<PoliceAbstractState>();
	private ArrayList<PoliceAbstractIntruptState> intruptStates = new ArrayList<PoliceAbstractIntruptState>();
	private HashMap<AmbulanceTeam, Human> ambulanceTeamTarget;
	private PoliceAbstractState lastCycle;

	@Override
	protected void preCompute() {
		super.preCompute();// it's better to call first!
		PoliceConstants.STANDARD_OF_MAP = (int) model().getBounds().getWidth() / 200000;
		log.debug("Standard of Map:", PoliceConstants.STANDARD_OF_MAP);

		TimeNamayangar tm = new TimeNamayangar("Police PreCompute");
		clearableToPoint = new GeoClearPointReachablity(this);
		degreeClearableToPoint = new GeoDegreeClearPointToReachable(this);
		makeStates();
		makeIntruptsStates();
		doStatePrecomputes();
		sosLogger.base.consoleInfo("PolicePreCompute " + tm.stop());
		ambulanceTeamTarget = new HashMap<AmbulanceTeam, Human>();
		clearWidth = getConfig().getIntValue("clear.repair.rad", 1250);
	}

	private void makeStates() {
		long start = System.currentTimeMillis();
		states.add(new OpenCivilianState(this));
		states.add(new SearchState(this));
		states.add(new DummyPoliceState(this));
		long time = (System.currentTimeMillis() - start);
		log.info("police state making got:" + time + "ms", time > 5);
	}

	private void makeIntruptsStates() {
		long start = System.currentTimeMillis();
		intruptStates.add(new LockInBlockadeState(this));
		intruptStates.add(new StockHandlerState(this));
		intruptStates.add(new HeavyStockedHandlerState(this));
		intruptStates.add(new DamagedState(this));
		intruptStates.add(new ClearEntrances(this));
		long time = (System.currentTimeMillis() - start);
		log.info("Police Interrupt state making  got:" + time + "ms", time > 5);
	}

	private void doStatePrecomputes() {
		ArrayList<PoliceAbstractState> allStates = new ArrayList<PoliceAbstractState>(intruptStates);
		allStates.addAll(states);
		for (PoliceAbstractState state : allStates) {
			long start = System.currentTimeMillis();
			state.precompute();
			long time = (System.currentTimeMillis() - start);
			boolean consoleLog = time > 10 || !(state instanceof PoliceAbstractIntruptState);
			log.info(state + " got:" + time + "ms", consoleLog);
		}

	}

	@Override
	protected void prepareForThink() {
		super.prepareForThink();
	}
		@Override
	protected void think() throws SOSActionException {
		super.think();
		lastCycleState = lastState;
		doIntrupt();
		doStateAct();
		doBugState();
	}

	private void doBugState() throws SOSActionException {
		log.warn("No Act done till here!!! WHY?");
		try {
			getState(SearchState.class).act();
		} catch (SOSActionException e1) {
			throw e1;
		} catch (Exception e) {
			sosLogger.agent.fatal(e);
		}
		try {
			getState(DummyPoliceState.class).act();
		} catch (SOSActionException e1) {
			throw e1;
		} catch (Exception e) {
			sosLogger.agent.fatal(e);
		}
		log.warn("NO ACT");
		problemRest("Exception occured");
	}

	private void doStateAct() throws SOSActionException {
		TimeNamayangar tm = new TimeNamayangar();
		for (PoliceAbstractState lastState : states) {
			log.info("acting as " + lastState + "==================================================================={{{{{{{");
			try {
				tm.reset();
				tm.start();
				super.lastState = lastState + "";
				lastState.act();
				tm.finish();
				sosLogger.act.debug(lastState + " do nothing but take " + tm);
			} catch (SOSActionException e) {
				abstractStateLogger.logln(time() + ":" + lastState.getClass().getSimpleName() + "\t\t\t : action=" + e.getMessage() + "\t\t\t" + tm);
				sosLogger.act.debug(lastState + "doing " + e.getMessage() + " take " + tm);
				lastCycle = lastState;
				throw e;
			} catch (Exception e) {
				sosLogger.error("State:" + lastState + " failed...");
				sosLogger.error(e);
			}
			log.info("state" + lastState + " finished}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
		}
	}

	private void doIntrupt() throws SOSActionException {
		for (PoliceAbstractIntruptState intruptState : intruptStates) {
			lastState = intruptState.getClass().getSimpleName();
			try {
				log.trace(intruptState + " checking for can intrupt?");
				if (intruptState.canMakeIntrupt()) {
					log.trace(intruptState + " can make intrupt");
					intruptState.act();
				}
			} catch (SOSActionException e) {
				sosLogger.act.debug(lastState + "doing " + e.getMessage());
				abstractStateLogger.logln(time() + ":" + lastState + "\t\t\t : action=" + e.getMessage() + "\t\t\t");
				lastCycle = intruptState;
				throw e;
			} catch (Exception e) {
				sosLogger.error("State:" + lastState + " failed...");
				sosLogger.error(e);
			}
		}
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {
		log.warn("An unhandeled exception occured.... act as bug state");
		doBugState();
	}

	@Override
	protected void finalizeThink() {
		super.finalizeThink();
	}

	@SuppressWarnings("unchecked")
	public <T extends PoliceAbstractState> T getState(Class<T> stateClass) {
		for (PoliceAbstractState state : states) {
			if (stateClass.isInstance(state))
				return (T) state;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends PoliceAbstractState> T getInteruptState(Class<T> stateClass) {
		for (PoliceAbstractIntruptState state : intruptStates) {
			if (stateClass.isInstance(state))
				return (T) state;
		}
		return null;
	}

	public HashMap<AmbulanceTeam, Human> getAmbulanceTeamTarget() {
		return ambulanceTeamTarget;
	}

	public PoliceAbstractState getLastCycleState() {
		return lastCycle;
	}

	/*
	 * Ali: Please keep it at the end!!!!(non-Javadoc)
	 */
	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray, sender, channel);
		for (PoliceAbstractState state : states) 
			state.hear(header, data, dynamicBitArray, sender, channel);
	}

}