package sample;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.KernelConstants;
import rescuecore2.config.Config;
import rescuecore2.geometry.Point2D;
import rescuecore2.messages.control.KASense;
import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.messages.AKClearArea;
import rescuecore2.standard.messages.AKExtinguish;
import rescuecore2.standard.messages.AKLoad;
import rescuecore2.standard.messages.AKMove;
import rescuecore2.standard.messages.AKRescue;
import rescuecore2.standard.messages.AKRest;
import rescuecore2.standard.messages.AKSay;
import rescuecore2.standard.messages.AKSpeak;
import rescuecore2.standard.messages.AKSubscribe;
import rescuecore2.standard.messages.AKTell;
import rescuecore2.standard.messages.AKUnload;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.MessageBuffer;
import sos.base.message.ReadXml;
import sos.base.message.structure.MessageConstants.ChannelSystemType;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.channel.Channel;
import sos.base.message.system.MessageHandler;
import sos.base.message.system.MessageSystem;
import sos.base.move.Path;
import sos.base.update.No_Comm;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

public abstract class SOSAbstractSampleAgent<E extends StandardEntity> extends StandardAgent<E> {
	public SOSLoggerSystem sosLogger;// @author Ali
	public MessageBuffer messages;// @author Ali
	public MessageBuffer lowCommunicationMessages;// @author Ali
	public MessageBuffer sayMessages;// @author Ali
	public SampleUpdater updater;
	protected No_Comm noCommunicationMessageSelector = null;
	public MessageBlock messageBlock;// @author Ali
	public MessageSystem messageSystem;// @author Ali
	MessageHandler messageHandler;// @author Ali
	private KASense lastSense;// @author Ali
	public int CONNECT_TIME;// @author Ali
	public int FREEZE_TIME;// @author Ali
	public int VIEW_DISTANCE = 30000;// @author Ali
	public static int BLOCKADE_REPAIR_RATE;// @author Ali
	public int THINK_TIME = 1000;// @author Ali

	/**
	 * The search algorithm.
	 */
	protected SampleBFS search;


	@Override
	protected void postConnect() throws Exception {
		super.postConnect();
		sosLogger=new SOSLoggerSystem(me(), "Agent", true, OutputType.File);
		model().precompute();
		search = new SampleBFS(model());
		messagePrecompute();

		updater = new SampleUpdater(this);
	}
	private void messagePrecompute() {

		messageSystem = new MessageSystem(this); // Ali
		messages = new MessageBuffer(ChannelSystemType.Normal,messageSystem); // Ali
		sayMessages = new MessageBuffer(ChannelSystemType.Voice,messageSystem); // Ali
		lowCommunicationMessages= new MessageBuffer(ChannelSystemType.Low,messageSystem); // Ali
		new ReadXml(model()); // Ali
		messageHandler = new MessageHandler(this); // Ali
		noCommunicationMessageSelector = new No_Comm(this);
	}

	@Override
	public SampleWorldModel model() {
		return (SampleWorldModel) super.model();
	}

	@Override
	protected SampleWorldModel createWorldModel() {
		return new SampleWorldModel(this);
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
		move(search.getDummyRandomWalkPath(me().getAreaPosition()));
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

	/**
	 * @author Ali
	 * @param doDummyRandomWalk
	 * @throws SOSActionException
	 */
	public void randomWalk(boolean doDummyRandomWalk) throws SOSActionException {
		List<Area> result = new ArrayList<Area>();
		for (StandardEntity road : model().roads()) {
			if (road.updatedtime() < 2)
				result.add((Area) road);
		}

		if (result.isEmpty()) {
			for (StandardEntity building : model().roads()) {

				if (building.updatedtime() < 2)
					result.addAll(((Area) building).getNeighbours());

			}
		}

		if (result.isEmpty())
			if (doDummyRandomWalk)
				dummyRandomWalk();
			else
				return;
		move(search.breadthFirstSearch(me().getAreaPosition(), result));
	}

	/**
	 * @changedBy Ali Notification that a timestep has started.
	 * @param changes
	 *            The set of changes observed this timestep.
	 * @param heard
	 *            The set of communication messages this agent heard.
	 */
	protected abstract void think() throws SOSActionException;

	public int time() {
		return lastSense.getTime();
	}

	@Override
	protected void processSense(KASense sense) {
//		model().merge(sense.getChangeSet(),sense.getTime());
		sosLogger.logln("-------------------" + sense.getTime() + "--------------------");
		lastSense = sense;

		messageHandler.handleReceive(sense.getHearing());
		model().merge(sense.getChangeSet(), sense.getTime());//
		messageSystem.setChannels(); // Ali

		doThink();
	}

	private void doThink() {
		try {
			if (time() < config.getIntValue(KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
				throw new SOSActionException("Not Started");
			}
			think();
		} catch (SOSActionException sosAE) {
		} catch (Exception e) {
			sosLogger.error(e);
		}
	}

	@Override
	public E me() {
		return super.me();
	}

	protected void move(Path path) throws SOSActionException {
		Point2D xy = path.getDestination().second();
		send(new AKMove(getID(), time(), path.getIds(), (int) xy.getX(), (int) xy.getY()));
		throw new SOSActionException("MoveXY(" + path + ", " + xy + ")");
	}

	/**
	 * Send a rest command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @throws SOSActionException
	 */
	protected void sendRest() throws SOSActionException {
		send(new AKRest(getID(), time()));
		throw new SOSActionException("Rest ");

	}

	/**
	 * Send an extinguish command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target building.
	 * @param water
	 *            The amount of water to use.
	 * @throws SOSActionException
	 */
	protected void sendExtinguish(Building target, int water) throws SOSActionException {
		send(new AKExtinguish(getID(), time(), target.getID(), water));
		throw new SOSActionException("Extinguish " + target);
	}

	/**
	 * Send a clear command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target road.
	 * @throws SOSActionException
	 */
	protected void sendClear(Blockade target) throws SOSActionException {
		send(new AKClearArea(getID(), time(), target.getX(),target.getY()));
		throw new SOSActionException("Clear " + target);
	}

	/**
	 * Send a rescue command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target human.
	 * @throws SOSActionException
	 */
	protected void sendRescue(Human target) throws SOSActionException {
		send(new AKRescue(getID(), time(), target.getID()));
		throw new SOSActionException("Rescue " + target);
	}

	/**
	 * Send a load command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target human.
	 * @throws SOSActionException
	 */
	protected void sendLoad(Human target) throws SOSActionException {
		send(new AKLoad(getID(), time(), target.getID()));
		throw new SOSActionException("Load " + target);
	}

	/**
	 * Send an unload command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @throws SOSActionException
	 */
	protected void sendUnload() throws SOSActionException {
		send(new AKUnload(getID(), time()));
		throw new SOSActionException("Unload");
	}

	/**
	 * Send a speak command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param channel
	 *            The channel to speak on.
	 * @param data
	 *            The data to send.
	 */
	protected void sendSpeak(int time, int channel, byte[] data) {
		send(new AKSpeak(getID(), time, channel, data));
	}

	/**
	 * Send a subscribe command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param channels
	 *            The channels to subscribe to.
	 */
	protected void sendSubscribe(int time, int... channels) {
		send(new AKSubscribe(getID(), time, channels));
	}

	/**
	 * Send a say command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param data
	 *            The data to send.
	 */
	protected void sendSay(int time, byte[] data) {
		send(new AKSay(getID(), time, data));
	}

	/**
	 * Send a tell command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param data
	 *            The data to send.
	 */
	protected void sendTell(int time, byte[] data) {
		send(new AKTell(getID(), time, data));
	}

	@Override
	public Config getConfig() {
		return config;
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

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		updater.updateByMessage(header, data, dynamicBitArray, sender, channel);
	}

}
