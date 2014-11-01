package sos.ambulance_v2.decision.saveHumanHandling;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.base.AmbulanceConstants.ATstates;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.ambulance_v2.tools.ParticleFilter;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.util.SOSActionException;
import sos.base.util.SOSGeometryTools;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: ara
 * To change this template use File | Settings | File Templates.
 */
public class SaveCivilianTaskHandler extends AmbulanceGeneralHandler {

	private SaveHumanTask state = null;
	private int firstHp0Time = 1000;

	public SaveCivilianTaskHandler(AmbulanceTeamAgent ownerAgent, Human target) {
		super(ownerAgent, target);
		this.target = target;
		this.state = SaveHumanTask.MOVING_TO_TARGET;

		if (ownerAgent.isLoadingInjured())
			this.state = SaveHumanTask.LOADING;
	}

	/********************************* moveToTarget *****************************************/
	private void moveToTarget() throws SOSActionException {

		if (isInMyArea(target) && isInMyseen()) {
			self.log().logln("****I am in my target place!");
			if (isTargetInLastCycleInMyPositionAndNeedHelp())
			{
				changeTarget();
				if (target.getBuriedness() > 0) {
					sendAckIfNotSent(0);
					state = SaveHumanTask.RESCUING;
					handle();
					return;
				}
				else {
					state = SaveHumanTask.LOADING;
					handle();
					return;
				}
			}
			else {
				isDone = true;
				ignoreTarget(IgnoreReason.TargetOutOfMyAction, self.time() + 10);
				sendTaskAckMsg(2);//rejected
				self.log().logln("****target out of my action");
				return;
			}

		}
		else {
			self.log().logln("****I am going to target " + target + "    " + place);
			if (!isReachableTarget(target) && !isReachablityBug() || isStockBug()) {
				self.log().logln("****NOT REACHABLE TARGET! from reachability");
				ignoreTarget(IgnoreReason.NotReachableToTarget, self.time() + 1);
				sendTaskAckMsg(2);//rejected
				target.getRescueInfo().removeAT(self.me());
				if (getWork().getTarget().equals(target))
					getWork().setTarget(null, null);
				getWork().setCurrentState(ATstates.SEARCH);
				isDone = true;
				return;
			}
			int busy = getMyBusyCycles(target, place, ATstates.MOVE_TO_TARGET);
			getWork().setCurrentState(ATstates.MOVE_TO_TARGET);
			getWork().setNextFreeTime((self.time() + busy));

			if (self.stuckUntil < self.time()) {
				sendAckIfNotSent(0);
				sendInfoMsgIfRequire(ATstates.MOVE_TO_TARGET, busy);
			}
			sendStatueMsgBySay(ATstates.MOVE_TO_TARGET, false);
			self.moveTo(target);
		}

	}

	/*****************************************************************************************/
	private void rescue() throws SOSActionException {
		self.log().logln("****I am in rescuing state");
		ArrayList<Civilian> viewedCivilian = AmbulanceUtils.getLastCycleInMyPositionNeedHelpCivilians();
		ArrayList<AmbulanceTeam> inPlaceAmbulanceTeams = AmbulanceUtils.getLastCycleInMyPositionAmbulanceTeams();

		if (viewedCivilian.contains(target)) { // I see the target now
			inPlaceAmbulanceTeams.remove(self.me());
			boolean loader = AmITheLoader2(inPlaceAmbulanceTeams);
			boolean rescueContinue = viewedCivilian.size() > 1 ;

			if (target.getBuriedness() > 0) {
				self.log().logln("*********buriedness=" + target.getBuriedness());
				if (loader || rescueContinue) {
					self.log().logln("*********primary and rescueContinue");
					int busy = getMyBusyCycles(target, place, ATstates.RESCUE);
					self.me().getWork().setCurrentState(ATstates.RESCUE);
					self.me().getWork().setNextFreeTime((self.time() + busy));

					sendInfoMsgIfRequire(ATstates.RESCUE, busy);
					sendRescueMsgBySay();

					self.rescue(target);
				} else {
					self.log().logln("*********my work is finished here!");
					isDone = true;
					ignoreTarget(IgnoreReason.TargetLoadedByAnotherAT, 1001);
				}
			} else {
				state = SaveHumanTask.LOADING;
				this.handle();
				return;
			}
		} else { //target out of my action
			if (self.lastException instanceof TimeoutException) {
				self.log().error("ambulance suddenly moved from server when it was in place of target " + target + "!!!!!moving to target again...");
				state = SaveHumanTask.MOVING_TO_TARGET;
				this.handle();
				return;
			}
			isDone = true;
			ignoreTarget(IgnoreReason.TargetOutOfMyAction, self.time() + 15);
			sendTaskAckMsg(2);//rejected
			return;
		}

	}

	/*****************************************************************************************/
	private void load() throws SOSActionException {
		if (self.isFull()) {
			self.log().logln("****I am full and gonna go to refuge!");
			state = SaveHumanTask.MOVING_TO_REFUGE;
			handle();
			return;
		}
		acknowledgeMsgSent = false;

		ArrayList<Civilian> viewedCivi = AmbulanceUtils.getLastCycleInMyPositionNeedHelpCivilians();
		ArrayList<AmbulanceTeam> ats = AmbulanceUtils.getLastCycleInMyPositionAmbulanceTeams();
		self.log().logln(" in position humanoids=" + viewedCivi);
		self.log().logln(" in position ATs=" + ats);
		ats.remove(self.me());
		boolean loader = AmbulanceUtils.AmITheLoader2(ats);
		if (viewedCivi.size() > 1 && (target.getRescueInfo().getATneedToBeRescued() == 1 || AmbulanceUtils.AmITheLoader1(target.getRescueInfo().getNowWorkingOnMe()))) {
			self.log().info("Number of AT Now Working On Me = " + AmbulanceUtils.AmITheLoader1(target.getRescueInfo().getNowWorkingOnMe()));
			loader = true;
		}
		if (loader && viewedCivi.contains(target)) {
			self.log().logln("************ loading " + target);
			int busy = getMyBusyCycles(target, place, ATstates.MOVE_TO_REFUGE);
			lastInfoSent = -1;
			sendInfoMsgIfRequire(ATstates.MOVE_TO_REFUGE, busy);
			sendStatueMsgBySay(ATstates.MOVE_TO_REFUGE, false);
			self.load(target);
		} else {
			if (loader && self.getVisibleEntities(Civilian.class).contains(target)) {
				self.log().error("ambulance suddenly moved from server when it was in place of target " + target + "!!!!!moving to target again...");
				state = SaveHumanTask.MOVING_TO_TARGET;
				handle();
				return;
			}

			ignoreTarget(IgnoreReason.ImNotLoader, self.time() + 10);
			self.log().logln("Target ignored beacause i am not loader of " + target);
			isDone = true;
			return;
		}

	}

	/*****************************************************************************************/
	private void moveToRoad() throws SOSActionException {
		self.log().logln("****Moving to road state");

		int busy = 1;
		self.me().getWork().setNextFreeTime((self.time() + busy));
		sendInfoMsgIfRequire(ATstates.MOVE_TO_REFUGE, busy);

		if (self.location() instanceof Road) {
			if (self.isFull()) {
				ignoreTarget(IgnoreReason.UnloadInRoad, self.time() + 10);
				if (self.model().refuges().isEmpty()) {
					target.getRescueInfo().setIgnoredUntil(IgnoreReason.NoRefuge, 1000);
				}
				state = SaveHumanTask.UNLOADING;
				handle();
				return;
			} else {
				self.log().error("Ambulance " + self.getID() + " is in Road but nothing to unload!");
			}
		} else {

			state = SaveHumanTask.MOVING_TO_REFUGE; //bekhatere inke bade aftershock ke AT to building gir mikone age kaC ro load karde bashe
			//hey mikhad bere road,chon har dafe state e ghabli ro negah midare ta dar baz mishe task unload mishe
			self.move.moveStandard(self.model().roads());
		}

	}

	/*****************************************************************************************/
	private void moveToRefuge() throws SOSActionException {

		self.log().logln("****Moving to refuge state!");
		if (self.location() instanceof Refuge) {
			self.log().logln("****I am in refuge!");
			if (self.isFull()) {
				ignoreTarget(IgnoreReason.InRefuge, self.time() + 5);
				state = SaveHumanTask.UNLOADING;
				handle();
				return;
			} else {
				self.log().error("Ambulance " + self.getID() + " is in refuge but nothing to unload!");
			}
		} else {
			boolean is_reachable_refuge = isReachableAnyRefuge();
			if ((!is_reachable_refuge && !isReachablityBug()) || self.model().refuges().isEmpty()) {
				self.log().logln("****NOT REACHABLE ANY REFUGE!");
				//				sendTaskAckMsg(2);//rejected
				//				target.getRescueInfo().removeAT(self.me());
				//				if (!(self.location() instanceof Building)) {
				//					ignoreTarget(IgnoreReason.NotReachableToRefuge, self.time() + 5);
				//					state = SaveHumanTask.UNLOADING;
				//					handle();
				//					return;
				//				}
				state = SaveHumanTask.MOVING_TO_ROAD;
				handle();
				return;

			}
			if (!target.isAlive() && ParticleFilter.HP_PRECISION / target.getDamage() < self.time() - firstHp0Time) {
				self.log().logln("****Civilian has been dead!");
				ignoreTarget(IgnoreReason.DeadHuman, 1000);
				state = SaveHumanTask.UNLOADING;
				handle();
				return;
			}
			if (self.isFull())
				sendAckIfNotSent(1);//finished

			int busy = getMyBusyCycles(target, place, ATstates.MOVE_TO_REFUGE);
			self.me().getWork().setCurrentState(ATstates.MOVE_TO_REFUGE);
			self.me().getWork().setNextFreeTime((short) (self.time() + busy));
			sendInfoMsgIfRequire(ATstates.MOVE_TO_REFUGE, busy);
			sendStatueMsgBySay(ATstates.MOVE_TO_REFUGE, false);

			self.moveToRefuges();

		}
	}

	/*****************************************************************************************/
	private void unload() throws SOSActionException {

		if (self.isLoadingInjured()) {
			self.log().info("in unload function : unload" + self.me().getWork().getTarget());
			state = SaveHumanTask.MOVING_TO_REFUGE;
			self.unload();
		}
		isDone = true;
		if (self.me().getWork().getTarget().equals(target)) {
			self.me().getWork().setTarget(null, null);
			self.me().getWork().setNextFreeTime(0);
		}
		self.me().getWork().setCurrentState(ATstates.SEARCH);

		self.log().logln("****Task completed successfully!****");

	}

	/*****************************************************************************************/
	//-------------------------------------------------------------------------------------	
	private void sendInfoMsgIfRequire(ATstates status, int busy) {
		self.log().trace("IsItReadyToSendInfoMSG?time=" + self.time() + " lastInfoSent=" + lastInfoSent + " statechanged?" + stateChanged);
		if (self.time() - lastInfoSent > 7 || stateChanged) {
			sendInfoMsg(target.getID().getValue(), status, busy);
			self.log().info("@@@@@ message send @@@@@@");
		}
	}

	//-------------------------------------------------------------------------------------	
	private void sendRescueMsgBySay() {
		boolean needHelp = false;
		if (target.getRescueInfo().getATneedToBeRescued() - target.getRescueInfo().getNowWorkingOnMe().size() > 0)
			needHelp = true;
		sendStatueMsgBySay(ATstates.RESCUE, needHelp);
	}

	//-------------------------------------------------------------------------------------	
	public void sendAckIfNotSent(int statusType) {
		if (!acknowledgeMsgSent) {
			acknowledgeMsgSent = true;
			sendTaskAckMsg(statusType);//accepted
		}
	}

	//-------------------------------------------------------------------------------------
	private boolean isReachableAnyRefuge() {
		if (self.model().refuges().isEmpty())
			return false;
		boolean isNotReachable = self.move.isReallyUnreachable(self.model().refuges());
		return !isNotReachable;
	}

	/*******************************************************************************************/
	/********************* isTargetInLastCycleInMyPositionAndNeedHelp ***************************/
	public boolean isTargetInLastCycleInMyPositionAndNeedHelp() {

		ArrayList<Human> viewedHumansInLastCycle = new ArrayList<Human>();
		ArrayList<Human> lastCycleSensedHumans = self.getVisibleEntities(Human.class);

		for (Human human : lastCycleSensedHumans) {
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

	/*******************************************************************************************/
	/********************************* canSeeTheCenterOfBuilding *******************************/

	public boolean canSeeTheCenterOfBuilding(Point2D p1, Area area) {
		return (SOSGeometryTools.getDistance(p1, area.getPositionPoint()) < self.VIEW_DISTANCE);
	}

	/*******************************************************************************************/
	/**************************************** Handle *******************************************/
	@Override
	public void handle() throws SOSActionException {
		if (target.getHP() == 0)
			firstHp0Time = Math.min(firstHp0Time, self.time());

		self.log().info("state in SaveCivilianTaskHandler class handler() :" + state);

		switch (state) {

		case MOVING_TO_TARGET:
			moveToTarget();
			break;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		case RESCUING:
			rescue();
			break;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		case LOADING:
			load();
			break;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		case MOVING_TO_ROAD:
			moveToRoad();
			break;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		case MOVING_TO_REFUGE:
			moveToRefuge();
			break;
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		case UNLOADING:
			unload();
		}
	}

	//-------------------------------------------------------------------------------------
	@Override
	public boolean finished() {
		self.log().info("check if Rescuing Civilian State finished...");
		if (isDone) {
			self.log().debug(this.getClass() + " is done");
			return true;
		}

		if (!target.isPositionDefined()) {
			self.log().debug("!target.isPositionDefined() return true");
			isDone = true;
			ignoreTarget(IgnoreReason.NoPosition, self.time() + 5);
			sendTaskAckMsg(1);//finished
		}
		if (state == SaveHumanTask.UNLOADING && self.location() instanceof Refuge && !self.isFull()) {
			self.log().debug("if (status == Rescue_Complete_status.UNLOADING && self.location() instanceof Refuge && !self.isFull()) return true");
			return true;
		}
		if (!self.isFull() && ((self.target == null || !self.target.isPositionDefined() || self.target.getRescueInfo().isIgnored()) && target == self.target)) {
			self.log().debug("if ((self.target == null || !self.target.isPositionDefined() || self.target.getRescueInfo().isIgnored()) && target == self.target) return true");
			return true;
		}
		if (target.isPositionDefined() && target.getPosition() instanceof Road && !isReachableAnyRefuge()) {
			self.log().logln("Not reachable any refuge! in finished()");
			ignoreTarget(IgnoreReason.NotReachableToRefuge, self.time() + 2);
			return true;
		}
		if (target.getPosition() instanceof Refuge) {
			ignoreTarget(IgnoreReason.InRefuge, 1000);
			return true;
		}
		if (!target.getPosition().equals(self.me()) && target.getPosition() instanceof AmbulanceTeam) {
			ignoreTarget(IgnoreReason.InAmbulance, self.time() + 5);
			return true;
		}

		//sinash 2013
		if (!self.isFull() && (target.getRescueInfo().getIgnoreReason() == IgnoreReason.IgnoredTargetMessageReceived) || (target.getRescueInfo().getIgnoreReason() == IgnoreReason.WillDie)) {
			self.log().debug("GONNA_DIE_IGNORED: target: " + target.getID().getValue() + " cycle " + self.time() + 5);
			return true;
		}

		return false;
	}

	//-------------------------------------------------------------------------------------
	@Override
	public void resetState() {
		if (state == SaveHumanTask.RESCUING || (state == SaveHumanTask.LOADING && !self.isFull()))
			state = SaveHumanTask.MOVING_TO_TARGET;
		if (self.getLoadingInjured() != null && self.getLoadingInjured().equals(target))
			state = SaveHumanTask.MOVING_TO_REFUGE;
		acknowledgeMsgSent = false;
		lastInfoSent = 2;
		stateChanged = false;
	}

}