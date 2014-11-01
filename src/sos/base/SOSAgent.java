package sos.base;

import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.precompute.PreCompute;
import sos.base.sosFireEstimator.SOSFireEstimator;
import sos.base.sosFireZone.SOSFireZoneManager;
import sos.base.util.SOSActionException;
import sos.base.util.mapRecognition.MapInformation;
import sos.base.util.sampler.FireProSampler;
import sos.base.util.sampler.SOSSampler;
import sos.fire_v2.base.tools.SOSFireProbability;

public abstract class SOSAgent<E extends StandardEntity> extends AbstractSOSAgent<E> {
	private MapInformation mapInfo;
	public SOSFireEstimator fireEstimator;//Yousef
	public SOSFireZoneManager fireSiteManager;//Yousef
	public SOSFireProbability fireProbabilityChecker;//Yousef

	public SOSSampler sampler;
	


	@Override
	protected void preCompute() {
		sosLogger.info("ID=" + getID()); // Ali
		model().precompute();
		new PreCompute<E>(this).init(); // Ali
		fireProbabilityChecker = new SOSFireProbability(this);

		if (SOSConstant.SAMPLING)
		{
			try {
				sampler = new FireProSampler(model());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void prepareForThink() {
		if (time() > 2)
			move.cycle();// Aramik
	}

	@Override
	public void preSuperThink() {
		if (!isTimeToActFinished()) {
			long start = System.currentTimeMillis();
			if (messageSystem.type == Type.LowComunication)
				messageSystem.sendMessage(lowCommunicationMessages); // Ali
			else
				messageSystem.sendMessage(messages); // Ali
			sosLogger.act.info("     sending messages in speak message buffer got:" + (System.currentTimeMillis() - start) + "ms");

			start = System.currentTimeMillis();
			sendSayMessage();
			sosLogger.act.info("     sending messages in say message buffer got:" + (System.currentTimeMillis() - start) + "ms");
		} else
			sosLogger.act.warn("     [Timeout] can't send messages after " + getPassedTimeInThisCycle() + " ms cycle started...sending next cycle");

		long start = System.currentTimeMillis();
		waitAMiliSecond();//because send message is so important we yield time to another thread(agent)
		sosLogger.act.info("     wait a mili second got:" + (System.currentTimeMillis() - start) + "ms");
	}

	@Override
	protected void think() throws SOSActionException {

	}

	protected void sendSayMessage() {
		if (this instanceof CenterAgent)
			return;
//		if (sayMessages.getMessages().size() > 0 || messageSystem.type == Type.NoComunication || messageSystem.type == Type.LowComunication) {
// commented by ali	Because currently doesn't really attention to civilian probablity
			noCommunicationMessageSelector.chooseAllMessages();
//		}
		if (sayMessages.getMessages().size() > 0) {
			messageSystem.sayMessage(sayMessages);// Ali
		}
		sayMessages.clear();
	}

	@Override
	protected void finalizeThink() {
	}

	/**
	 * @author Ali
	 */
	@Override
	protected SOSWorldModel createWorldModel() {
		return new SOSWorldModel(this);
	}

	/**
	 * @author Ali
	 *         This method provide a global access to SOSAgent
	 * @param agentClass
	 *            is type of expected Agent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SOSAgent<? extends StandardEntity> currentAgent() {
		return currentAgent(SOSAgent.class);
	}

	// /**
	// * @author Ali
	// * OLD METHOD
	// * because of time usage, i removed this method and also ReceiveMessageBlock
	// */
	// @Deprecated
	// @Override
	// protected void hear(ReceiveMessageBlock receiveMessageBlock, StandardEntity sender) {
	// // updater.updateByMessage(receiveMessageBlock, sender);
	// }

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		updater.updateByMessage(header, data, dynamicBitArray, sender, channel);
		for (CenterActivity center : centerActivities) {
			center.hear(header, data, dynamicBitArray, sender, channel);
		}
	}

	public void setMapInfo(MapInformation mapInfo) {
		this.mapInfo = mapInfo;
	}

	public MapInformation getMapInfo() {
		return mapInfo;
	}

}
