package sos.base;

import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;

/**
 * 
 * @author Ali
 * 
 */
public class CenterActivity {
	
	protected final SOSAgent<?> sosAgent;
	
	public CenterActivity(SOSAgent<?> sosAgent) {
		this.sosAgent = sosAgent;
	}
	
	protected void preCompute() {
		
	}
	
	protected void prepareForThink() throws Exception {
		
	}
	
	protected void think() throws SOSActionException, Exception {
		
	}
	
	protected void finalizeThink() throws Exception {
		
	}
	
	protected void rest() throws SOSActionException {
		throw new SOSActionException("Rest");
	}
	
	// @Deprecated
	// protected void hear(ReceiveMessageBlock receiveMessageBlock, StandardEntity sender) {
	//
	// }

	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
