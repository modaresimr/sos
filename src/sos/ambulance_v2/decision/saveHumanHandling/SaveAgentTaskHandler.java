package sos.ambulance_v2.decision.saveHumanHandling;

import java.util.ArrayList;

import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.base.AmbulanceConstants.ATstates;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;

public class SaveAgentTaskHandler extends AmbulanceGeneralHandler {

	private SaveHumanTask state = null;

	public SaveAgentTaskHandler(AmbulanceTeamAgent ownerAgent, Human target) {
		super(ownerAgent, target);
		this.target = target;
		this.state = SaveHumanTask.MOVING_TO_TARGET;

	}

	/*******************************************************************************************/
	/**************************************** Handle *******************************************/
	@Override
	public void handle() throws SOSActionException {

		switch (state) {
		case MOVING_TO_TARGET:
			moveToTarget();
			break;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		case RESCUING:
			rescue();
			break;
		default:
			break;
		}
	}

	/********************************************************************************************/
	private void rescue() throws SOSActionException {
		self.log().logln("****I am in rescuing state");
		ArrayList<AmbulanceTeam> inPlaceAmbulanceTeams = AmbulanceUtils.getLastCycleInMyPositionAmbulanceTeams();

		self.log().logln(" in position ATs=" + inPlaceAmbulanceTeams);

		if (isTargetInLastCycleInMyPositionAndNeedHelp()) {
			inPlaceAmbulanceTeams.remove(self.me());
			boolean rescueContinue = true;
			if (target.getBuriedness() > 0) {
				self.log().logln("*********buriedness=" + target.getBuriedness());
				if (rescueContinue) {
					self.log().logln("*********primary and rescueContinue");
					int busy = getMyBusyCycles(target, place, ATstates.RESCUE);
					self.me().getWork().setCurrentState(ATstates.RESCUE);
					self.me().getWork().setNextFreeTime(self.time() + busy);
					if (self.time() - lastInfoSent > 7 || stateChanged)
						sendInfoMsg(target.getID().getValue(), ATstates.RESCUE, busy);
					boolean needHelp = false;
					if (target.getRescueInfo().getATneedToBeRescued() - target.getRescueInfo().getNowWorkingOnMe().size() > 0)
						needHelp = true;
					sendStatueMsgBySay(ATstates.RESCUE, needHelp);
					self.rescue(target);
				} else {
					self.log().logln("*********my work is finished here!");
					isDone = true;
					ignoreTarget(IgnoreReason.FinishedWorkOnTarget, self.time() + 4);
				}
			} else {

				this.isDone = true;
				sendTaskAckMsg(1);//finished
				return;
			}
		} else { //target out of my action
			if (self.getVisibleEntities(Human.class).contains(target)) {
				self.log().error("ambulance suddenly moved from server when it was in place of target " + target + "!!!!!moving to target again...");
				state = SaveHumanTask.MOVING_TO_TARGET;
				handle();
				return;
			}
			isDone = true;
			ignoreTarget(IgnoreReason.TargetOutOfMyAction, 1000);
			sendTaskAckMsg(2);//rejected
			return;
		}
	}

	/********************************************************************************************/
	/********************* isTargetInLastCycleInMyPositionAndNeedHelp ***************************/
	public boolean isTargetInLastCycleInMyPositionAndNeedHelp() {
		ArrayList<Human> viewedHumansInLastCycle = new ArrayList<Human>();
		ArrayList<Human> humans = self.getVisibleEntities(Human.class);
		for (Human human : humans) {
			if (human instanceof Civilian) {
				Civilian civilian = (Civilian) human;
				if (civilian.isPositionDefined()
						&& !(civilian.getPosition() instanceof Building)
						&& civilian.getDamage() == 0)
					continue;

				if (civilian.getHP() != 0
						&& civilian.isPositionDefined()
						&& isInMyArea(civilian))
					viewedHumansInLastCycle.add(civilian);
			} else {
				if (human.getHP() != 0
						&& human.getBuriedness() != 0
						&& isInMyArea(human))
					viewedHumansInLastCycle.add(human);
			}
		}
		self.log().logln(" in position humanoids=" + viewedHumansInLastCycle);

		if (viewedHumansInLastCycle.contains(target))
			return true;
		else
			return false;

	}

	/********************************************************************************************/

	private void moveToTarget() throws SOSActionException {

		if (isInMyArea(target) && isInMyseen()) {
			self.log().logln("****I am in my target place!");

			if (isTargetInLastCycleInMyPositionAndNeedHelp()) {
				changeTarget();
				if (target.getBuriedness() > 0) {
					if (!acknowledgeMsgSent) {
						acknowledgeMsgSent = true;
						sendTaskAckMsg(0);//accept
					}
					state = SaveHumanTask.RESCUING;
					handle();
					return;
				} else {
					this.isDone = true;
					sendTaskAckMsg(1);//finished
					return;
				}
			} else { //target out of my action
				if (self.getVisibleEntities(Human.class).contains(target)) {
					self.log().error("ambulance suddenly moved from server when it was in place of target " + target + "!!!!!moving to target again...");
					if (target.getBuriedness() > 0)
						self.rescue(target);
					//    					state = SaveHumanTask.MOVING_TO_TARGET;
					//    					handle();
					return;
				}
				isDone = true;
				ignoreTarget(IgnoreReason.TargetOutOfMyAction, self.time() + 10);
				sendTaskAckMsg(2);//reject
				self.log().logln("****target out of my action");
				return;
			}
		} else {
			self.log().logln("****I am going to target " + target + "    " + place);
			if (!isReachableTarget(target) && !isReachablityBug() || isStockBug()) {
				self.log().logln("****NOT REACHABLE TARGET! from reachability");
				sendTaskAckMsg(2);//rejected msg
				target.getRescueInfo().removeAT(self.me());
				if (getWork().getTarget().equals(target))
					getWork().setTarget(null, null);
				ignoreTarget(IgnoreReason.NotReachableToTarget, self.time() + 2);
				isDone = true;
				return;
			}
			int busy = getMyBusyCycles(target, place, ATstates.MOVE_TO_TARGET);
			self.me().getWork().setCurrentState(ATstates.MOVE_TO_TARGET);
			self.me().getWork().setNextFreeTime(self.time() + busy);
			if (!acknowledgeMsgSent) {
				acknowledgeMsgSent = true;
				sendTaskAckMsg(0);//accepted msg
			}
			if (self.time() - lastInfoSent > 7 || stateChanged)
				sendInfoMsg(target.getID().getValue(), ATstates.MOVE_TO_TARGET, busy);
			sendStatueMsgBySay(ATstates.MOVE_TO_TARGET, false);
			self.moveTo(target);
		}
	}

	/********************************************************************************************/
	//-------------------------------------------------------------------------------------
	@Override
	public boolean finished() {
		if (isDone)
			return true;
		if (!target.isPositionDefined()) {
			isDone = true;
			ignoreTarget(IgnoreReason.NoPosition, self.time() + 5);
			sendTaskAckMsg(1);//finished
		}
		if (target.getBuriedness() == 0) {
			self.log().info(" in SaveAgentTaskHandler class in finished function IgnoreUntil set to 1000");
			ignoreTarget(IgnoreReason.FinishedWorkOnTarget, 1000);
			return true;
		}
		if ((self.target == null || !self.target.isPositionDefined() || self.target.getRescueInfo().isIgnored()) && target == self.target)
			return true;

		return false;
	}

	@Override
	public void resetState() {

		if (state == SaveHumanTask.RESCUING)
			state = SaveHumanTask.MOVING_TO_TARGET;
		acknowledgeMsgSent = false;
		lastInfoSent = 2;
		stateChanged = false;
	}

}
