package sos.ambulance_v2.decision;

import sos.ambulance_v2.base.AbstractAmbulanceCenterActivity;
import sos.base.SOSAgent;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;

/**
 * SOS centre agent.
 * 
 * @author Hesam002 14-June-2010 (23-03-1389)
 */

public class AmbulanceCenterActivity extends AbstractAmbulanceCenterActivity implements MessageXmlConstant {

	public AmbulanceCenterActivity(SOSAgent<? extends StandardEntity> sosAgent) {
		super(sosAgent);
	}

	@Override
	protected void prepareForThink() throws Exception {
		super.prepareForThink();
	}

	@Override
	protected void think() throws SOSActionException, Exception {
		super.think();
	}

	@Override
	protected void finalizeThink() throws Exception {
		super.finalizeThink();
	}

	/*
	 * Ali: Please keep it at the end!!!!(non-Javadoc)
	 */

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray, sender, channel);
	}

}