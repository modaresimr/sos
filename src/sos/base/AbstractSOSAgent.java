package sos.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import rescuecore2.KernelConstants;
import rescuecore2.components.Component;
import rescuecore2.config.Config;
import rescuecore2.messages.control.KASense;
import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.messages.AKRest;
import rescuecore2.standard.messages.AKSpeak;
import rescuecore2.standard.messages.AKSubscribe;
import rescuecore2.worldmodel.EntityID;
import sos.ambulance_v2.decision.AmbulanceCenterActivity;
import sos.base.SOSConstant.AgentType;
import sos.base.entities.StandardEntity;
import sos.base.message.MessageBuffer;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.channel.Channel;
import sos.base.message.system.MessageHandler;
import sos.base.message.system.MessageSystem;
import sos.base.move.Move;
import sos.base.update.No_Comm;
import sos.base.util.SOSActionException;
import sos.base.util.blockadeEstimator.BlockadeEstimator;
import sos.base.util.information_stacker.CycleInformations;
import sos.base.util.information_stacker.InformationStacker;
import sos.base.util.information_stacker.act.NoAct;
import sos.base.util.information_stacker.act.ProblemRestAction;
import sos.base.util.information_stacker.act.RestAction;
import sos.base.util.namayangar.SOSWorldModelNamayangar;
import sos.base.util.sosLogger.SOSLogger;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireStationActivity;
import sos.police_v2.PoliceOfficeActivity;
import sos.search_v2.agentSearch.AgentSearch;
import sos.search_v2.tools.cluster.ClusterData;

/**
 * @author Ali
 * @param <E>
 */
public abstract class AbstractSOSAgent<E extends StandardEntity> extends StandardAgent<E> implements MessageConstants, MessageXmlConstant {
	public AgentSearch<?> newSearch;//yoosef
	public SOSLogger sosLogger;// @author Ali
	public MessageBuffer messages;// @author Ali
	public MessageBuffer lowCommunicationMessages;// @author Ali
	public MessageBuffer sayMessages;// @author Ali
	public No_Comm noCommunicationMessageSelector = null;
	public MessageBlock messageBlock;// @author Ali
	public MessageSystem messageSystem;// @author Ali
	public MessageHandler messageHandler;// @author Ali
	private KASense lastSense;// @author Ali
	public int CONNECT_TIME;// @author Ali
	public int FREEZE_TIME;// @author Ali
	public SOSWorldModelNamayangar worldmodelNamayangar;// @author Ali
	public int VIEW_DISTANCE = 30000;// @author Ali
	public static int BLOCKADE_REPAIR_RATE;// @author Ali
	public int THINK_TIME = 1000;// @author Ali
	public SOSLineOfSightPerception<? extends E> lineOfSightPerception;// @author Ali
	protected ArrayList<CenterActivity> centerActivities = new ArrayList<CenterActivity>(0);// @author Ali
	public BlockadeEstimator blockadeEstimator;//
	public Move move;// @author Aramik
	public Updater updater;// @author Aramik
	public Throwable lastException;// @author Ali
	public InformationStacker informationStacker = new InformationStacker(10);
	public SOSLoggerSystem abstractStateLogger;
	public String lastState;
	/**
	 * @author Ali
	 * @throws Exception
	 */
	protected abstract void preCompute();

	@Override
	public SOSWorldModel model() {
		return (SOSWorldModel) super.model();
	}

	public ClusterData getMyClusterData() {
		return newSearch.getSearchWorld().getClusterData();
	}

	@Override
	protected void postConnect() throws Exception {
		super.postConnect();
		CONNECT_TIME = config.getIntValue("kernel.startup.connect-time");// @author Ali

		VIEW_DISTANCE = config.getIntValue("perception.los.max-distance");// @author Ali
		BLOCKADE_REPAIR_RATE = config.getIntValue("clear.repair.rate");// @author Ali
		THINK_TIME = config.getIntValue("kernel.agents.think-time");
		sosLogger = new SOSLogger(me(), true, OutputType.File);// @author Ali
		sosLogger.base.trace(config);
		abstractStateLogger=new SOSLoggerSystem(me(), "State", true, OutputType.File);
		preCompute();// @author Ali
		sosLogger.timeStepFinished();
	}

	/**
	 * @author Ali
	 * @throws Exception
	 */
	protected abstract void prepareForThink();

	/**
	 * @changedBy Ali Notification that a timestep has started.
	 * @param changes
	 *            The set of changes observed this timestep.
	 * @param heard
	 *            The set of communication messages this agent heard.
	 */
	protected abstract void think() throws SOSActionException;

	protected abstract void thinkAfterExceptionOccured() throws SOSActionException;

	/**
	 * @author Ali
	 * @throws Exception
	 */
	abstract protected void finalizeThink();

	// /**
	// * @author Ali
	// * @param receiveMessageBlock
	// * @param standardEntity
	// */
	// @Deprecated
	// protected abstract void hear(ReceiveMessageBlock receiveMessageBlock, StandardEntity standardEntity);
	//
	/**
	 * @author Ali
	 */
	@Override
	public abstract void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel);

	/**
	 * @author Ali
	 * @param sense
	 *            The sense message.
	 *            Process an incoming sense message. The default implementation updates the world model and calls {@link #think}. Subclasses should generally not override this method but instead implement the {@link #think} method.
	 */
	@Override
	protected void processSense(KASense sense) {
		lastSense = sense;
		sosLogger.logCurrentTime(sense.getTime());
		sosLogger.act.info("start processing sense after" + getPassedTimeInThisCycle() + " ms cycle started");

		if (messageSystem.getMine().isUnusedAgent()) {
			if (this instanceof CenterAgent && messageSystem.type == Type.NoComunication)
				return;
		}
		

		if (informationStacker.getInformations(1) == null) {
			for (int i = 0; i < sense.getTime(); i++) {
				informationStacker.addInfo(new CycleInformations(i, new NoAct(), me().getPositionPair()));
			}
		}

		messageSystem.setChannels(); // Ali
		sosLogger.act.info("channel set in " + getPassedTimeInThisCycle() + " ms after cycle started");

		handleReceiveMessage();

		model().merge(sense.getChangeSet(), sense.getTime());

		doCenterActivities();
		doPrepareForThink();
		doPreSuperThink();
		if (!doThink())
			doDummyThink();

		if (isTimeToActFinished())
			sosLogger.error("Your Act may not received by server!!! passed time in this cycle " + getPassedTimeInThisCycle() + " ms... think time="+THINK_TIME+" tinktime*0.9="+((int)(THINK_TIME*0.9f))+" nextstepstated?"+isNextCycleReceived()+ "remained time to act:"+getRemainedTimeToAct()+"ms");

		doFinalizeAct();
		if (isNextCycleReceived()) {
			sosLogger.fatal("Cycle" + (time() + 1) + " started but you are still in cycle" + time() + "!!! " + getPassedTimeInThisCycle() + " ms...");
			lastException = new TimeoutException();
		} else
			sosLogger.base.info("Act Time:" + getPassedTimeInThisCycle());

		sosLogger.base.info("Full Act Time(from received server's timestep):" + (System.currentTimeMillis() - sense.getReceivedTime()) + " ms...");
		sosLogger.act.info("Full Act Time(from received server's timestep):" + (System.currentTimeMillis() - sense.getReceivedTime()) + " ms...");
		doNamayangarUpdate();
		sosLogger.timeStepFinished();
	}

	private void doPreSuperThink() {
		try {
			sosLogger.act.info("PreSuperThink start after " +getPassedTimeInThisCycle()+ " ms cycle started...");
			long startPrepareForThink = System.currentTimeMillis();
			preSuperThink();
			sosLogger.act.info("PreSuperThink got " + (System.currentTimeMillis() - startPrepareForThink) + " ms");
		} catch (Exception e) {
			sosLogger.fatal(e);
		}		
	}

	public abstract void preSuperThink() ;

	private void handleReceiveMessage() {
		long t1 = System.currentTimeMillis();
		messageHandler.handleReceive(lastSense.getHearing());
		sosLogger.act.info("Handle recevie message after "+getPassedTimeInThisCycle()+"ms full message time(handle and execute)=" + (System.currentTimeMillis() - t1) + " ms");
	}

	private void doDummyThink() {
		try {
			thinkAfterExceptionOccured();
			sosLogger.error("No Act sended To Server #t:" + time());
		} catch (SOSActionException e1) {
			sosLogger.act.info("Send " + e1.getMessage());
		} catch (Exception e2) {
			sosLogger.base.fatal(e2);
			informationStacker.addInfo(model(), new NoAct());
			sosLogger.error("No Act sended To Server #t:" + time());
			lastException = e2;
		}
	}

	private boolean doThink() {
		sosLogger.act.info("=============Think Started after " + getPassedTimeInThisCycle() + "ms simulation have been started...");
		long startThink = System.currentTimeMillis();
		boolean successThisCycle = false;
		try {
			if (time() < config.getIntValue(KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
				informationStacker.addInfo(model(), new NoAct());
				throw new SOSActionException("Not Started");
			}
			think();
			sosLogger.error("No act sent to Kernel");
			informationStacker.addInfo(model(), new NoAct());
		} catch (SOSActionException sosAE) {
			sosLogger.act.info("Sent " + sosAE.getMessage());
			successThisCycle = true;
			lastException = null;
		} catch (Exception e) {
			sosLogger.fatal(e);
			lastException = e;
		}
		sosLogger.act.info("=============Think got " + (System.currentTimeMillis() - startThink) + " ms and finished after " + getPassedTimeInThisCycle() + "ms simulation have been started...");
		return successThisCycle;
	}

	private void doNamayangarUpdate() {
		try {
			if (worldmodelNamayangar != null && worldmodelNamayangar.isVisible())
				worldmodelNamayangar.timestepCompleted(model(), lastSense.getChangeSet());
		} catch (Exception e) {
			sosLogger.error(e);
		}
	}

	private void doFinalizeAct() {
		try {
			long startFinalizeThink = System.currentTimeMillis();
			waitAMiliSecond();
			finalizeThink();
			sosLogger.base.info("FinalizeThink got " + (System.currentTimeMillis() - startFinalizeThink) + " ms");
		} catch (Exception e) {
			sosLogger.fatal(e);
		}

	}

	@Override
	public void shutdown() {
		sosLogger.shutDown();
		super.shutdown();
	}

	private void doPrepareForThink() {
		try {
			sosLogger.act.info("Prepare Think start after " +getPassedTimeInThisCycle()+ " ms cycle started...");
			long startPrepareForThink = System.currentTimeMillis();
			prepareForThink();
			sosLogger.act.info("PrepareForThink got " + (System.currentTimeMillis() - startPrepareForThink) + " ms");
		} catch (Exception e) {
			sosLogger.fatal(e);
		}
	}

	private void doCenterActivities() {
		for (CenterActivity center : centerActivities) {
			try {
				long centerThink = System.currentTimeMillis();
				if (time() >= config.getIntValue(KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
					center.prepareForThink();
					center.think();
					center.finalizeThink();
				}
				sosLogger.act.info("CenterActivity(" + center + ") prepareforthink+think got " + (System.currentTimeMillis() - centerThink) + " ms");
			} catch (Exception e) {
				sosLogger.fatal(e);
			}
		}
	}

	public boolean isTimeToActFinished() {
		if (isNextCycleReceived())
			return true;
		if (getRemainedTimeToAct() < 50)
			return true;
		return false;
	}

	public int getRemainedTimeToAct() {
		int invalidTime = (int) (THINK_TIME * 0.9f);
		if (isNextCycleReceived())
			invalidTime = -2000;
		return invalidTime - getPassedTimeInThisCycle();
	}

	public int getPassedTimeInThisCycle() {
		return (int) (System.currentTimeMillis() - lastSense.getReceivedTime());
	}

	public void doNoActIfTimeIsFinished() throws SOSActionException {
		if (isTimeToActFinished())
			problemRest("Time Out!!! You Consum too much time!exceeded time is:(" + (-getRemainedTimeToAct()) + "ms)");
	}

	/**
	 * @author Ali
	 * @return
	 */
	public int time() {
		return model().time();
	}

	/**
	 * @author Ali Send a rest command to the kernel.
	 * @param time
	 *            The current time.
	 * @throws SOSActionException
	 */
	public void rest() throws SOSActionException {
		send(new AKRest(getID(), time()));
		informationStacker.addInfo(model(), new RestAction());
		throw new SOSActionException("Rest");
	}

	/**
	 * @author Ali Send a rest command to the kernel.
	 * @param time
	 *            The current time.
	 * @throws SOSActionException
	 */
	public void problemRest(String problem) throws SOSActionException {
		problemRest(problem,true);
	}
	/**
	 * @author Ali Send a rest command to the kernel.
	 * @param time
	 *            The current time.
	 * @throws SOSActionException
	 */
	public void problemRest(String problem,boolean warn) throws SOSActionException {
		send(new AKRest(getID(), time()));
		if(warn)
			sosLogger.warn("Problem Rest: " + problem);
		else
			sosLogger.info("Problem Rest: " + problem);
		informationStacker.addInfo(model(), new ProblemRestAction(problem));
		throw new SOSActionException("Problem Rest because:" + problem);
	}

	/**
	 * @author Ali Send a speak command to the kernel.
	 * @param channel
	 *            The channel to speak on.
	 * @param data
	 *            The data to send.
	 */
	@Override
	public void speak(int channel, byte[] data) {
		send(new AKSpeak(getID(), model().time(), channel, data));
	}

	/**
	 * @author Ali Send a subscribe command to the kernel.
	 * @param channels
	 *            The channels to subscribe to.
	 */
	@Override
	public void subscribe(int[] channels) {

		send(new AKSubscribe(getID(), model().time(), channels));
		//		if(model().time()>3)
		send(new AKSubscribe(getID(), model().time() + 1, channels));
	}

	/**
	 * @author Ali
	 */
	public abstract AgentType type();

	@Override
	public Config getConfig() {
		return config;
	}

	/**
	 * @author Ali
	 */
	protected boolean showWorldModelNamayangar() {
		return SOSConstant.WORLD_MODEL_NAMAYANGAR;
	}

	/**
	 * @author Ali
	 */
	public void showNamayangar() {
		if (worldmodelNamayangar == null)
			worldmodelNamayangar = new SOSWorldModelNamayangar(getConfig(), model(), me().toString());

		worldmodelNamayangar.setVisible(true);
		worldmodelNamayangar.timestepCompleted(model(), lastSense == null ? null : lastSense.getChangeSet());

	}

	/**
	 * Aramik
	 */
	public void addCenterActivity(CenterActivity ca) {
		for (CenterActivity cen : this.centerActivities)
			if (cen instanceof AmbulanceCenterActivity && ca instanceof AmbulanceCenterActivity)
				return;
			else if (cen instanceof PoliceOfficeActivity && ca instanceof PoliceOfficeActivity)
				return;
			else if (cen instanceof FireStationActivity && ca instanceof FireStationActivity)
				return;
		this.centerActivities.add(ca);
	}

	public Collection<CenterActivity> getCenterActivities() {
		return Collections.unmodifiableCollection(centerActivities);
	}

	/**
	 * @author Ali
	 */
	@SuppressWarnings("unchecked")
	public <A extends StandardEntity> ArrayList<A> getVisibleEntities(Class<A> type) {
		ArrayList<A> changed = new ArrayList<A>();
		if (lastSense == null || lastSense.getChangeSet() == null)
			return changed;
		for (EntityID element : lastSense.getChangeSet().getChangedEntities()) {
			StandardEntity entity = model().getEntity(element);
			if (type.isInstance(entity))
				changed.add((A) entity);
		}
		return changed;
		//		return lineOfSightPerception.getVisibleEntities(location());
	}
	@SuppressWarnings("unchecked")
	public <A extends StandardEntity> HashSet<A> getVisibleEntitiesHash(Class<A> type) {
		HashSet<A> changed = new HashSet<A>();
		if (lastSense == null || lastSense.getChangeSet() == null)
			return changed;
		for (EntityID element : lastSense.getChangeSet().getChangedEntities()) {
			StandardEntity entity = model().getEntity(element);
			if (type.isInstance(entity))
				changed.add((A) entity);
		}
		return changed;
		//		return lineOfSightPerception.getVisibleEntities(location());
	}
	public void waitAMiliSecond() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * @author Ali
	 * This method provide a global access to SOSAgent
	 * @param agentClass is type of expected Agent
	 * @return
	 */
	public static <T extends AbstractSOSAgent<? extends StandardEntity>> T currentAgent(Class<T> agentClass) {
		Component component = currentComponent();
		if (agentClass.isInstance(component))
			return (T) component;

		throw new Error("Invalid Access! this thread component is not " + agentClass.getSimpleName() + " the component is:" + component + " type:" + component.getClass().getSimpleName());
	}
}
