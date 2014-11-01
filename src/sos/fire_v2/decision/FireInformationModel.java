package sos.fire_v2.decision;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.target.SOSFireZoneSelector;
import sos.tools.decisionMaker.definitions.SOSInformationModel;

public class FireInformationModel extends SOSInformationModel {

	private SOSEstimatedFireZone lastSelectedFireZone;
	private Building lastSelectedBuilding;
	private SOSFireZoneSelector fireZoneSelector;

	private int lastSelectTime;

	private int  lastVisitCenter=0;
	
	private int timeInCenter = 0;

	
	public SOSLoggerSystem fireSearcher;
	public SOSLoggerSystem extinguishFire;

	public FireInformationModel(SOSAgent<? extends Human> agent) {
		super(agent);
		fireSearcher = new SOSLoggerSystem(agent.me(), "SOSUnEXSearcher/", true, OutputType.File, true);
		extinguishFire = new SOSLoggerSystem(agent.me(), "FireTarget/", true, OutputType.File, true);
		agent.sosLogger.addToAllLogType(extinguishFire);
		agent.sosLogger.addToAllLogType(fireSearcher);
		setFireZoneSelector(new SOSFireZoneSelector(agent));

	}

	public FireBrigadeAgent self() {
		return (FireBrigadeAgent) getAgent();
	}

	public SOSEstimatedFireZone getLastSelectedFireZone() {
		return lastSelectedFireZone;
	}

	public void setLastSelectedFireZone(SOSEstimatedFireZone lastSelectedFireZone) {
		this.lastSelectedFireZone = lastSelectedFireZone;
	}

	public void setSelectTime(int time) {
		this.setLastSelectTime(time);

	}

	public int getLastSelectTime() {
		return lastSelectTime;
	}

	public void setLastSelectTime(int lastSelectTime) {
		this.lastSelectTime = lastSelectTime;
	}

	public Building getLastSelectedBuilding() {
		return lastSelectedBuilding;
	}

	public void setLastSelectedBuilding(Building lastSelectedBuilding) {
		this.lastSelectedBuilding = lastSelectedBuilding;
	}

	public SOSFireZoneSelector getFireZoneSelector() {
		return fireZoneSelector;
	}

	public void setFireZoneSelector(SOSFireZoneSelector fireZoneSelector) {
		this.fireZoneSelector = fireZoneSelector;
	}

	public int getLastVisitCenter() {
		return lastVisitCenter;
	}

	public void setLastVisitCenter(int lastVisitCenter) {
		this.lastVisitCenter = lastVisitCenter;
	}

	public int getTimeInCenter() {
		return timeInCenter;
	}

	public void setTimeInCenter(int timeInCenter) {
		this.timeInCenter = timeInCenter;
	}

}
