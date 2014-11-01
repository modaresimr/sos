package sos.base;

import java.util.EnumSet;

import rescuecore2.standard.entities.StandardEntityURN;
import sos.base.SOSConstant.AgentType;
import sos.base.entities.Center;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * @author Ali
 *         SOS centre agent.
 */
public class CenterAgent extends SOSAgent<Center> {

	public SOSLoggerSystem abstractlog;
	@Override
	protected void preCompute() {
		super.preCompute();
		abstractlog = new SOSLoggerSystem(me(), "AmbulanceAbstract", true, OutputType.File);
		abstractlog.setFullLoggingLevel();
		sosLogger.addToAllLogType(abstractlog);
	}

	@Override
	public String toString() {
		return me().toString();
	}

	@Override
	protected void prepareForThink() {
		super.prepareForThink();
	}

	@Override
	protected void think() throws SOSActionException {
		super.think();
//		model().updateSOSFireZones(); // Hesam 002
		rest();
	}

	@Override
	protected void finalizeThink() {
		super.finalizeThink();
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_STATION,
									StandardEntityURN.AMBULANCE_CENTRE,
									StandardEntityURN.POLICE_OFFICE);
	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray,sender, channel);
	}

	/**
	 * @author Ali
	 * This method provide a global access to SOSAgent
	 * @param agentClass is type of expected Agent
	 * @return
	 */
	public static CenterAgent currentAgent(){
		return currentAgent(CenterAgent.class);
	}
	@Override
	public AgentType type() {
		return AgentType.Center;
	}
	public SOSLoggerSystem log(){//aramik
		return sosLogger.agent;
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {
		// TODO Auto-generated method stub

	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}
}