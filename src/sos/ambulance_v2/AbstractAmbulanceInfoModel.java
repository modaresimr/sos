package sos.ambulance_v2;

import sos.ambulance_v2.tools.MultiDitinctSourceCostInMM;
import sos.base.SOSAgent;
import sos.base.entities.StandardEntity;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.tools.decisionMaker.SOSPlatoonAgentInformationModel;

/**
 * @author Salim
 */
public abstract class AbstractAmbulanceInfoModel extends SOSPlatoonAgentInformationModel {
	private int lastCycleISetNumberOfATs;
	private int lastupdateTime;
	private MultiDitinctSourceCostInMM costTable;

	public AbstractAmbulanceInfoModel(SOSAgent<? extends StandardEntity> agent) {
		super(agent);
	}

	public void setlastTimeUpdated(int time) {
		lastupdateTime = time;
	}

	public int getLastTimeUpdated() {
		return lastupdateTime;
	}

	public abstract SOSLoggerSystem getADLogger();

	public abstract SOSLoggerSystem getHumanUpdateLogger();

	public MultiDitinctSourceCostInMM getCostTable() {
		return costTable;
	}

	public void setCostTable(MultiDitinctSourceCostInMM costTable) {
		this.costTable = costTable;
	}

	public int getLastCycleISetNumberOfATs() {
		return lastCycleISetNumberOfATs;
	}

	public void setLastCycleISetNumberOfATs(int lastCycleISetNumberOfATs) {
		this.lastCycleISetNumberOfATs = lastCycleISetNumberOfATs;
	}

}
