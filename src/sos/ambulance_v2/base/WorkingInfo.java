package sos.ambulance_v2.base;

import java.security.InvalidParameterException;

import rescuecore2.misc.Pair;
import sos.ambulance_v2.AmbulanceInformationModel;
import sos.ambulance_v2.base.AmbulanceConstants.ATstates;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Human;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

public class WorkingInfo {
	private Pair<Human, SOSIState<AmbulanceInformationModel>> target = null;
	private short nextFreeTime;
	private ATstates currentState = ATstates.SEARCH;
	private boolean needHelpInSay = false;
	private final AmbulanceTeam owner;

	public WorkingInfo(AmbulanceTeam owner) {
		this.owner = owner;
	}

	public void setNeedHelpInSay(boolean needHelpInSay) {
		this.needHelpInSay = needHelpInSay;
	}

	public boolean needHelpInSay() {
		return this.needHelpInSay;
	}

	public void setCurrentState(ATstates currentState) {

		this.currentState = currentState;
	}

	public ATstates getCurrentState() {
		return currentState;
	}

	public short getNextFreeTime() {
		return nextFreeTime;
	}

	public void setNextFreeTime(int nextFreeTime) {
		if (nextFreeTime < owner.getAgent().time())
			nextFreeTime = owner.getAgent().time();
		this.nextFreeTime = (short) nextFreeTime;
	}

	public Human getTarget() {
		if (target != null)
			return target.first();
		else
			return null;
	}

	public SOSIState<AmbulanceInformationModel> getState() {
		if (target != null)
			return target.second();
		else
			return null;
	}

	public void setTarget(Human newTarget, SOSIState<AmbulanceInformationModel> state) {
		if ((newTarget == null && state != null) /* || (state == null && newTarget != null) */) {
			setNextFreeTime(0);
			throw new InvalidParameterException("NewTarget: " + newTarget + "   State:" + state);
		}
		if (newTarget == null && state == null) {
			setNextFreeTime(0);
			clearTarget();
			return;
		}
		if (newTarget == null) {
			Exception e = new Exception();
			e.printStackTrace();

		}
		if (this.target != null && this.target.first() != null)
			this.target.first().getRescueInfo().removeAT(owner);

		newTarget.getRescueInfo().addAT(owner);

		this.target = new Pair<Human, SOSIState<AmbulanceInformationModel>>(newTarget, state);

	}

	private void clearTarget() {
		this.target = new Pair<Human, SOSIState<AmbulanceInformationModel>>(null, null);
	}

	@Override
	public String toString() {
		return "WorkInfo[target=" + target + " ft=" + nextFreeTime + " state=" + currentState + "]";
	}
}