package sos.fire_v2;

import sos.base.SOSAgent;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.base.AbstractFireStationActivity;
import sos.fire_v2.base.worldmodel.FireWorldModel.SystemType;

public class FireStationActivity extends AbstractFireStationActivity {

	SOSLoggerSystem centerLogger = null;

	public FireStationActivity(SOSAgent<?> sosAgent) {
		super(sosAgent);
		centerLogger = new SOSLoggerSystem(this.sosAgent.me(), "Agent/FireStation/" + SystemType.Agent.toString(), true, OutputType.File);
		cl("Center Activity By: " + this.sosAgent);
		cl("____________________________________________________________________________");
	}

	@Override
	protected void preCompute() {
		super.preCompute();
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

	public void cl(String s) {
		centerLogger.logln(s);
	}

	/*
	 * Ali: Please keep it at the end!!!!(non-Javadoc)
	 */
	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray,sender, channel);
	}
}