package sos.base.util;

import java.util.EnumSet;

import rescuecore2.standard.entities.StandardEntityURN;
import sos.base.SOSAgent;
import sos.base.SOSConstant.AgentType;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;

/**
 * A no-op agent.
 */
public class DummyAgent extends SOSAgent<StandardEntity> {

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.FIRE_STATION, StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.AMBULANCE_CENTRE, StandardEntityURN.POLICE_FORCE, StandardEntityURN.POLICE_OFFICE);
	}

	@Override
	public AgentType type() {
		return null;
	}

	@Override
	protected void prepareForThink() {

	}

	@Override
	protected void think() throws SOSActionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void finalizeThink() {
		// TODO Auto-generated method stub

	}

	// @Override
	// @Deprecated
	// protected void hear(ReceiveMessageBlock receiveMessageBlock, StandardEntity sender) {
	// super.hear(receiveMessageBlock, sender);
	// }

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray, sender, channel);
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {
		// TODO Auto-generated method stub

	}

}