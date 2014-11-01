package sos.ambulance_v2;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

import sos.ambulance_v2.base.AmbulanceConstants;
import sos.ambulance_v2.base.AmbulanceConstants.ATstates;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.VirtualCivilian;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.move.Path;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.base.util.SOSGeometryTools;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.WorldGraphEdge;
import sos.tools.GraphEdge;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

public class AmbulanceUtils {

	public static boolean isBurning(Human hm) {
		if (!hm.isPositionDefined())
			return false;
		if (!(hm.getPositionArea() instanceof Building))
			return false;
		Building b = (Building) hm.getPositionArea();
		if (b.isOnFire())
			return true;
		else
			return false;
	}

	public static ATstates convertStateIndexToState(int stateIndex) {
		switch (stateIndex) {
		case 0:
			return ATstates.MOVE_TO_TARGET;
		case 1:
			return ATstates.RESCUE;
		case 2:
			return ATstates.MOVE_TO_REFUGE;
		case 3:
			return ATstates.SEARCH;
		default:
			return null;
		}
	}

	/**
	 * using in refugeLess strategy
	 * 
	 * @param hm
	 * @return boolean
	 */
	public static boolean isHumaninFireBuilding(Human hm) {
		if (hm.getPositionArea() instanceof Building && isBuildingOnFire((Building) hm.getPositionArea()))
			return true;

		return false;
	}

	public static boolean isVirtualCivilianInFireBuilding(VirtualCivilian vc) {
		if (vc.getPosition() instanceof Building && isBuildingOnFire((Building) vc.getPosition()))
			return true;

		return false;
	}

	private static boolean isBuildingOnFire(Building building) {
		if (building.isEitherFieryOrBurnt())
			return true;
		return false;
	}

	public static boolean isValidToDecide(Human hm, SOSLoggerSystem log, AmbulanceTeamAgent agent) {

		if (!hm.isPositionDefined()) {
			log.trace(hm + " Position has not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isBuriednessDefined()) {
			log.trace(hm + " Buriedness is not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isHPDefined()) {
			log.trace(hm + " HP is not Defined==> it is invalid to decide");
			return false;
		}
		if (hm.getHP() == 0) {
			log.trace(hm + " id dead:( ==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof AmbulanceTeam) {
			log.trace(hm + " is in Ambulance==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof Refuge) {
			log.trace(hm + " is in Refuge==> it is invalid to decide");
			return false;
		}

		if (hm.getRescueInfo().isIgnored() && hm.getRescueInfo().getIgnoredUntil() > hm.model().time()) { //sinash bug fixed, changed "<" to ">" in the comparison
			log.trace(hm + "is ignored till " + hm.getRescueInfo().getIgnoredUntil() + " because " + hm.getRescueInfo().getIgnoreReason() + " ==> it is invalid to decide");
			return false;
		}
		if (hm instanceof Civilian) {

			if ((hm.getAreaPosition() instanceof Road) && hm.model().refuges().isEmpty()) {
				log.trace(hm + " is in road and there is no refuge in map ==> it is invalid to decide");
				return false;
			}

			if (!(hm.getPosition() instanceof Building) && (hm.getDamage() == 0 || hm.getHP() == 10000)) {//todo should better
				log.trace(hm + " is not in Building and damage==0 or hp==10000 ==> it is invalid to decide");
				return false;
			}

			if (hm.getBuriedness() == 0 && hm.getDamage() == 0 && hm.getHP() == 10000) {//TODO if near fire??????
				log.trace(hm + "has no buriedness and damge and it is healthy==> it is invalid to decide");
				return false;
			}
			
			/*
			 * if (hm.getDamage() == 0 || hm.getHP() == 10000) {//TODO should solve!!!why???
			 * return false;
			 * }
			 */

		} else { // Human is an agent
			//			if (!(hm.getPosition() instanceof Building))
			//				return false;//Comment by Ali because some time an agent may have buriedness but it was in road!!!!!
			if (hm.getBuriedness() == 0) {
				log.trace(hm + "has no buriedness ==> it is invalid to decide");
				return false;
			}

		}

		if(hm.getAreaPosition() instanceof Building && ((Building)hm.getAreaPosition()).isBurning() && hm.getBuriedness() > 10)
			return false;
		if (!(isReachableForAT(hm, true))) {
			log.trace(hm + "is not reachable ==> it is invalid to decide");
			return false;
		}

		log.trace(hm + " is valid to decide");
		//infos += xmlLog.addTag("Validity", "valid");
		//xmlLog.Info(infos);
		return true;
	}

	public static boolean isValidToDecide(VirtualCivilian vc, Human hm, SOSLoggerSystem log, AmbulanceTeamAgent ambulance) {

		if (!hm.isPositionDefined()) {
			log.trace(hm + " Position has not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isBuriednessDefined()) {
			log.trace(hm + " Buriedness is not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isHPDefined()) {
			log.trace(hm + " HP is not Defined==> it is invalid to decide");
			return false;
		}
		if (hm.getHP() == 0) {
			log.trace(hm + " id dead:( ==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof AmbulanceTeam) {
			log.trace(hm + " is in Ambulance==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof Refuge) {
			log.trace(hm + " is in Refuge==> it is invalid to decide");
			return false;
		}

		if (hm.getRescueInfo().isIgnored() && hm.getRescueInfo().getIgnoredUntil() > hm.model().time()) { //sinash bug fixed, changed "<" to ">" in the comparison
			log.trace(hm + "is ignored till " + hm.getRescueInfo().getIgnoredUntil() + " because " + hm.getRescueInfo().getIgnoreReason() + " ==> it is invalid to decide");
			return false;
		}
		if (hm instanceof Civilian) {

			if ((hm.getAreaPosition() instanceof Road) && hm.model().refuges().isEmpty()) {
				log.trace(hm + " is in road and there is no refuge in map ==> it is invalid to decide");
				return false;
			}

			if (!(hm.getPosition() instanceof Building) && (hm.getDamage() == 0 || hm.getHP() == 10000)) {//todo should better
				log.trace(hm + " is not in Building and damage==0 or hp==10000 ==> it is invalid to decide");
				return false;
			}

			if (hm.getBuriedness() == 0 && hm.getDamage() == 0 && hm.getHP() == 10000) {//TODO if near fire??????
				log.trace(hm + "has no buriedness and damge and it is healthy==> it is invalid to decide");
				return false;
			}
			/*
			 * if (hm.getDamage() == 0 || hm.getHP() == 10000) {//TODO should solve!!!why???
			 * return false;
			 * }
			 */

		} else { // Human is an agent
			//			if (!(hm.getPosition() instanceof Building))
			//				return false;//Comment by Ali because some time an agent may have buriedness but it was in road!!!!!
			if (hm.getBuriedness() == 0) {
				log.trace(hm + "has no buriedness ==> it is invalid to decide");
				return false;
			}

		}
		
		if(hm.getAreaPosition() instanceof Building && ((Building)hm.getAreaPosition()).isBurning() && hm.getBuriedness() > 10)
			return false;

		if (!(isReachableForAT(hm, true))) {
			log.trace(hm + "is not reachable ==> it is invalid to decide");
			return false;
		}

		log.trace(hm + " is valid to decide");
		return true;
	}

	public static boolean isValidToDecide(VirtualCivilian virtualcivilian, AmbulanceTeam AT, SOSLoggerSystem log, AmbulanceTeamAgent agent) {

		if (virtualcivilian.isIgnored()) {
			log.trace(virtualcivilian + " is ignored");
			return false;
		}
		if (virtualcivilian.getDeathTime() + 30 <= AT.model().time()) {
			log.trace(virtualcivilian + " is dead");
			return false;
		}

		if (!virtualcivilian.isPositionDefined()) {
			log.trace(virtualcivilian + " Position has not Defined==> it is invalid to decide");
			return false;
		}
		if (virtualcivilian.getPosition() instanceof Refuge) {
			log.trace(virtualcivilian + " is in Refuge==> it is invalid to decide");
			return false;
		}

		if ((virtualcivilian.getPosition() instanceof Road) && AT.model().refuges().isEmpty()) {
			log.trace(virtualcivilian + " is in road and there is no refuge in map ==> it is invalid to decide");
			return false;
		}

		if (virtualcivilian.getBuridness() == 0) {
			log.trace(virtualcivilian + "has no buriedness ==> it is invalid to decide");
			return false;
		}

		if (!(virtualcivilian.isReallyReachable())) {
			log.trace(virtualcivilian + "is not reachable ==> it is invalid to decide");
			return false;
		}
		
		if(virtualcivilian.getPosition() instanceof Building && ((Building)virtualcivilian.getPosition()).isBurning() && virtualcivilian.getBuridness() > 10)
			return false;

		log.trace(virtualcivilian + " is valid to decide");
		return true;
	}

	public static boolean isValidToRescue(Human hum, SOSLoggerSystem log) {
		if (hum == null) {
			log.heavyTrace("Human " + hum + " is invalid because it is null");
			return false;
		}
		if (!hum.isPositionDefined()) {
			log.heavyTrace("Human " + hum + " is invalid because it is position is undifined!");
			return false;
		}
		if (hum.getPosition() instanceof Refuge) {
			log.heavyTrace("Human " + hum + " is invalid because it is in refuge!!");
			return false;
		}
		if (hum.getPosition() instanceof AmbulanceTeam) {
			log.heavyTrace("Human " + hum + " is invalid because it is in AmbulanceTeam!!");
			return false;
		}
		if (!hum.isAlive()) {
			log.heavyTrace("Human " + hum + " is invalid because it is dead!!");
			return false;
		}

		if ((hum.getPosition() instanceof Building) && ((Building) (hum.getAreaPosition())).isEitherFieryOrBurnt()) {
			log.trace(hum + " is in fieryOrBurnt Building ==> it is invalid to decide");
			return false;
		}

		if (hum instanceof Civilian) {

			if ((hum.getAreaPosition() instanceof Road) && hum.model().refuges().isEmpty()) {
				log.trace(hum + " is in road and there is no refuge in map ==> it is invalid to decide");
				return false;
			}

			if (!(hum.getPosition() instanceof Building) && (hum.getDamage() == 0 || hum.getHP() == 10000)) {//todo should better
				log.trace(hum + " is not in Building and damage==0 or hp==10000 ==> it is invalid to decide");
				return false;
			}

			if (hum.getBuriedness() == 0 && hum.getDamage() == 0 /* && hum.getHP() == 10000 */) {//TODO if near fire??????
				log.trace(hum + "has no buriedness and damge and it is healthy==> it is invalid to decide");
				return false;
			}
			/*
			 * if (hm.getDamage() == 0 || hm.getHP() == 10000) {//TODO should solve!!!why???
			 * return false;
			 * }
			 */

		} else { // Human is an agent
			//			if (!(hm.getPosition() instanceof Building))
			//				return false;//Comment by Ali because some time an agent may have buriedness but it was in road!!!!!
			if (hum.getBuriedness() == 0) {
				log.trace(hum + "has no buriedness ==> it is invalid to decide");
				return false;
			}

		}
		
		if(hum.getAreaPosition() instanceof Building && ((Building)hum.getAreaPosition()).isBurning() && hum.getBuriedness() > 10)
			return false;
		
		if (hum.getRescueInfo().isIgnored()) {
			log.heavyTrace("Human " + hum + " is invalid because it is Ignored for " + hum.getRescueInfo().getIgnoreReason() + " until:" + hum.getRescueInfo().getIgnoredUntil() + "!!");
			return false;
		}
		if (!(isReachableForAT(hum, true))) {
			log.info(" Human " + hum + "is not valid because it is unreachable ");
			return false;
		}

		return true;
	}

	/*
	 * it returns the expireTime for task assigning(The max sycle that we can start the task)
	 */
	public static int taskAssigningExpireTime(Human hm, int deathTime) {
		int expire = 0;
		expire += Math.ceil(hm.getBuriedness() / (float) hm.getRescueInfo().getATneedToBeRescued());
		expire += (hm.getRescueInfo().getATneedToBeRescued() - 1);
		expire++; //for loading
		expire += hm.getRescueInfo().getTimeToRefuge();
		expire += AmbulanceConstants.AVERAGE_MOVE_TO_TARGET;
		expire += getCommunicationDelay(hm);
		expire++;//for unloading
		return deathTime - expire;
	}

	public static int taskAssigningExpireTime(VirtualCivilian virtualCivilian, int deathTime) {
		int expire = 0;
		expire += Math.ceil(virtualCivilian.getBuridness() / (float) virtualCivilian.getATneedToBeRescued());
		expire += (virtualCivilian.getATneedToBeRescued() - 1);
		expire++; //for loading
		expire += virtualCivilian.getTimeToRefuge();
		expire += AmbulanceConstants.AVERAGE_MOVE_TO_TARGET;
		expire += 1; //getCommunicationDelay(AT);
		expire++;//for unloading
		return deathTime - expire;
	}

	/*
	 * it returns the expireTime for task assigning(The max sycle that we can start the task)
	 */
	public static int taskAssigningExpireTime(Human hm, int deathTime, SOSLoggerSystem log) {
		int expire = 0;
		log.info("********* taskAssigningExpireTime  ************\ntarget = " + hm + " buriedness:" + hm.getBuriedness() + " damage:" + hm.getDamage() + " hp:" + hm.getHP() + " death:" + hm.getRescueInfo().getDeathTime());
		expire += Math.ceil(hm.getBuriedness() / (float) hm.getRescueInfo().getATneedToBeRescued());
		log.info("Buriedness:" + " expire =" + expire);
		expire += (hm.getRescueInfo().getATneedToBeRescued() - 1);
		log.info("ATneedToBeRescue: expire =" + expire);
		expire++; //for loading
		expire += hm.getRescueInfo().getTimeToRefuge();
		log.info("TimeToRefuge : expire =" + expire);
		expire += AmbulanceConstants.AVERAGE_MOVE_TO_TARGET;
		log.info("AVERAGE_MOVE_TO_TARGET : expire =" + expire);
		expire += getCommunicationDelay(hm);
		expire++;//for unloading
		log.info(" CommunicationDelay : expire =" + expire);
		log.info("expireTime = " + (deathTime - expire));
		return deathTime - expire;
	}

	public static int getCommunicationDelay(Human hm) {
		int cycle = 0;
		//			if (agent instanceof CenterAgent) {
		cycle += hm.model().sosAgent().messageSystem.getNormalMessageDelay() > 0 ? hm.model().sosAgent().messageSystem.getNormalMessageDelay() : 0;
		//			}
		return cycle;
	}

	public static boolean isValidToDecideForCenter(Human hm, SOSLoggerSystem log) {
		if (!hm.isPositionDefined()) {
			log.trace(hm + " Position has not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isBuriednessDefined()) {
			log.trace(hm + " Buriedness is not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isHPDefined()) {
			log.trace(hm + " HP is not Defined==> it is invalid to decide");
			return false;
		}
		if (hm.getHP() == 0) {
			log.trace(hm + " id dead:( ==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof AmbulanceTeam) {
			log.trace(hm + " is in Ambulance==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof Refuge) {
			log.trace(hm + " is in Refuge==> it is invalid to decide");
			return false;
		}

		if ((hm.getPosition() instanceof Building) && ((Building) (hm.getAreaPosition())).isEitherFieryOrBurnt()) {
			log.trace(hm + " is in fieryOrBurnt Building ==> it is invalid to decide");
			return false;
		}
		if (hm instanceof Civilian) {

			if ((hm.getAreaPosition() instanceof Road) && hm.model().refuges().isEmpty()) {
				log.trace(hm + " is in road and there is no refuge in map ==> it is invalid to decide");
				return false;
			}

			if (!(hm.getPosition() instanceof Building) && (hm.getDamage() == 0 || hm.getHP() == 10000)) {//todo should better
				log.trace(hm + " is not in Building and damage==0 or hp==10000 ==> it is invalid to decide");
				return false;
			}

			if (hm.getRescueInfo().isIgnored()) {
				switch (hm.getRescueInfo().getIgnoreReason()) {
				case FinishedWorkOnTarget:
				case IgnoredTargetMessageReceived:
				case InRefuge:
				case NoRefuge:
				case NoPosition:
				case NotReachableToRefuge:
				case Unreachable:
				case WillDie: //2013
				case FinishMessageReceived:
					log.trace(hm + "is ignored till " + hm.getRescueInfo().getIgnoredUntil() + " because " + hm.getRescueInfo().getIgnoreReason() + " ==> it is invalid to decide");
					return false;
				case CantLoad:
				case CenterAssignMoreImportantTarget:
				case HaveEnoughAT:
				case ImNotLoader:
				case IsNotMyMissionTarget:
				case NoComunicationAndFinished:
				case NotIgnored:
				case NotReachableToTarget:
				case ShouldCheck_Unknown:
				case TargetOutOfMyAction:
				case UnloadInRoad:
				default:
					log.trace(hm + "is ignored but it is not important for center IgnoreReason:" + hm.getRescueInfo().getIgnoreReason() + " ==> it is VALID to decide");
					break;
				}

			}
			if (hm.getBuriedness() == 0 && hm.getDamage() == 0) {//TODO if near fire??????
				log.trace(hm + "has no buriedness and damge and it is healthy==> it is invalid to decide");
				return false;
			}
			/*
			 * if (hm.getDamage() == 0 || hm.getHP() == 10000) {//TODO should solve!!!why???
			 * return false;
			 * }
			 */

		} else { // Human is an agent
			//			if (!(hm.getPosition() instanceof Building))
			//				return false;//Comment by Ali because some time an agent may have buriedness but it was in road!!!!!
			if (hm.getBuriedness() == 0) {
				log.trace(hm + "has no buriedness ==> it is invalid to decide");
				return false;
			}

		}

		log.trace(hm + " is valid to decide");
		return true;
	}

	public static boolean isValidToDecideIfSearchHaveNoTask(Human hm, AmbulanceTeamAgent agent) {
		SOSLoggerSystem log = agent.log();
		if (!hm.isPositionDefined()) {
			log.trace(hm + " Position has not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isBuriednessDefined()) {
			log.trace(hm + " Buriedness is not Defined==> it is invalid to decide");
			return false;
		}
		if (!hm.isHPDefined()) {
			log.trace(hm + " HP is not Defined==> it is invalid to decide");
			return false;
		}
		if (hm.getHP() == 0) {
			log.trace(hm + " id dead:( ==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof AmbulanceTeam) {
			log.trace(hm + " is in Ambulance==> it is invalid to decide");
			return false;
		}
		if (hm.getPosition() instanceof Refuge) {
			log.trace(hm + " is in Refuge==> it is invalid to decide");
			return false;
		}

		if (hm.getRescueInfo().isIgnored() && hm.getRescueInfo().getIgnoredUntil() > hm.model().time()) {
			log.trace(hm + "is ignored till " + hm.getRescueInfo().getIgnoredUntil() + " because " + hm.getRescueInfo().getIgnoreReason() + " ==> it is invalid to decide");
			if (hm.getRescueInfo().getIgnoreReason() != IgnoreReason.IsNotMyMissionTarget
					&& hm.getRescueInfo().getIgnoreReason() != IgnoreReason.HaveEnoughAT
					&& hm.getRescueInfo().getIgnoreReason() != IgnoreReason.IwasExtraAT)

				return false;
		}
		if (hm instanceof Civilian) {

			if (!(hm.getPosition() instanceof Building) && (hm.getDamage() == 0 || hm.getHP() == 10000)) {//todo should better
				log.trace(hm + " is not in Building and damage==0 or hp==10000 ==> it is invalid to decide");
				return false;
			}

			if (hm.getBuriedness() == 0 && hm.getDamage() == 0 && hm.getHP() == 10000) {//TODO if near fire??????
				log.trace(hm + "has no buriedness and damge and it is healthy==> it is invalid to decide");
				return false;
			}
			/*
			 * if (hm.getDamage() == 0 || hm.getHP() == 10000) {//TODO should solve!!!why???
			 * return false;
			 * }
			 */

		} else { // Human is an agent
			//			if (!(hm.getPosition() instanceof Building))
			//				return false;//Comment by Ali because some time an agent may have buriedness but it was in road!!!!!
			if (hm.getBuriedness() == 0) {
				log.trace(hm + "has no buriedness ==> it is invalid to decide");
				return false;
			}

		}

		if (agent.move.isReallyUnreachable(hm.getAreaPosition())) {
			log.trace(hm + "is really unreachable ==> it is invalid to decide");
			return false;
		}

		if (!(isReachableForAT(hm, true))) {
			log.info(" Human " + hm + "is not valid because it is unreachable ");
			return false;
		}

		log.trace(hm + " is valid to decide");
		return true;
	}

	public static Human getBestCivilianWhenSearchHaveNoTask(final AmbulanceTeamAgent self) {
		PriorityQueue<Human> civs = new PriorityQueue<Human>(10, new Comparator<Human>() {
			@Override
			public int compare(Human o1, Human o2) {
				int o1d = SOSGeometryTools.distance(o1.getAreaPosition().getPositionPoint(), self.me().getPositionPoint());
				int o2d = SOSGeometryTools.distance(o2.getAreaPosition().getPositionPoint(), self.me().getPositionPoint());
				return o1d - o2d;
			}
		});
		for (Human hum : self.model().humans()) {
			if (!(hum instanceof Civilian) && self.time() > 120)
				continue;
			if (AmbulanceUtils.isValidToDecideIfSearchHaveNoTask(hum, self))
				civs.add(hum);
		}
		return civs.peek();
	}

	public static Area getRoadNeighbour(Area positionArea) {
		if (positionArea instanceof Road)
			return positionArea;
		for (Area r : positionArea.getNeighbours()) {
			return getRoadNeighbour(r);
		}
		return null;
	}

	//-------------------------------------------------------------------------------------
	public static void updateATtarget(Human target, AmbulanceTeam agent, SOSIState<AmbulanceInformationModel> state) {
		if (target == null || state == null)
			throw new InvalidParameterException("Target: " + target + " State: " + state);
		if (agent.getWork().getTarget() != null)
			agent.getWork().getTarget().getRescueInfo().removeAT(agent);

		agent.getWork().setTarget(target, state);
	}

	//-------------------------------------------------------------------------------------
	public static void sendRejectMessage(Human target, AmbulanceTeamAgent ambulance) {
		ambulance.messageBlock = new MessageBlock(MessageXmlConstant.HEADER_AMBULANCE_TASK_ACK);
		ambulance.messageBlock.addData(MessageXmlConstant.DATA_AMBULANCE_INDEX, ambulance.me().getAmbIndex());
		ambulance.messageBlock.addData(MessageXmlConstant.DATA_ID, target.getID().getValue());
		ambulance.messageBlock.addData(MessageXmlConstant.DATA_ACK_TYPE, 2); // 2 == rejected
		ambulance.messageBlock.setResendOnNoise(false);
		ambulance.messages.add(ambulance.messageBlock);
		ambulance.log().info("sending Task Ack Message" + target + "==> rejected");
	}

	//-------------------------------------------------------------------------------------
	public static void rejectTarget(Human target, AmbulanceTeam me, AmbulanceTeamAgent ambulance) throws SOSActionException {

		AmbulanceUtils.sendRejectMessage(target, ambulance);
		target.getRescueInfo().removeAT(me);
		if (me.getWork().getTarget().equals(target))
			me.getWork().setTarget(null, null);

		if (ambulance.isLoadingInjured())
			ambulance.unload();
	}

	public static boolean isReachableForAT(Human target, boolean check) {

		if (target.isReallyReachable(check))
			return true;
		if (target.getAgent().move.isReallyUnreachableXY(target.getAreaPosition(), target.getX(), target.getY()))
			return false;

		Path path = target.getAgent().move.getPathTo(Arrays.asList(target.getAreaPosition()), StandardMove.class);
		int cost = 0;

		for (GraphEdge ge : path.getEdges())
			if (ge instanceof WorldGraphEdge)
				switch (ge.getState()) {
				case FoggyOpen:
					cost++;
					break;
				case FoggyBlock:
					cost += 3;
					break;
				default:
					break;
				}
		if (cost > 20)
			return false;

		return true;

	}

	public static void rejectTargetInPosition(AmbulanceTeamAgent ambulance, Area target) {
		for (Human hu : ambulance.model().humans()) {
			if (hu == null)
				continue;

			if (!hu.isPositionDefined())
				continue;

			if (hu.getAreaPosition().getID().getValue() != target.getID().getValue())
				continue;

			hu.getRescueInfo().setIgnoredUntil(IgnoreReason.StuckInUnreachableMode, ambulance.time() + 10);
		}
	}

	public static boolean LockInBlockade(AmbulanceTeamAgent ambulance) {
		if (!ambulance.me().getAreaPosition().isBlockadesDefined())
			return false;
		for (Blockade blockade : ambulance.me().getAreaPosition().getBlockades()) {
			if (blockade.getShape().contains(ambulance.me().getX(), ambulance.me().getY()))
				return true;
		}
		return false;
	}

	public static ArrayList<AmbulanceTeam> getLastCycleInMyPositionAmbulanceTeams() { // add self
		ArrayList<AmbulanceTeam> humans = new ArrayList<AmbulanceTeam>();
		AmbulanceTeamAgent me = AmbulanceTeamAgent.currentAgent();
		ArrayList<Human> lastCycleSensedHumans = me.getVisibleEntities(Human.class);
		for (Human hu : lastCycleSensedHumans) {
			if (hu instanceof AmbulanceTeam && hu.getBuriedness() == 0 && hu.getHP() != 0) {
				if (hu.getPosition().getID().getValue() == me.location().getID().getValue()) {
					humans.add((AmbulanceTeam) hu);
				}
			}
		}
		return humans;
	}

	public ArrayList<Human> getLastCycleInMyPositionNeedHelpHumanoids() {// farz mikonim ke civiliane load shode need help nemibashad
		ArrayList<Human> humans = new ArrayList<Human>();
		AmbulanceTeamAgent me = AmbulanceTeamAgent.currentAgent();
		ArrayList<Human> lastCycleSensedHumans = me.getVisibleEntities(Human.class);
		for (Human hu : lastCycleSensedHumans) {
			if (hu instanceof Civilian) {
				Civilian civ = (Civilian) hu;
				if (civ.getHP() != 0 /* && civ.damage()>0 */&& civ.isPositionDefined() && civ.getPosition().getID().getValue() == me.location().getID().getValue())
					humans.add(civ);
				if (civ.isPositionDefined() && !(civ.getPosition() instanceof Building) && civ.getDamage() == 0)
					humans.remove(civ);
			} else { //if it is agent
				if (hu.getHP() != 0 && hu.getBuriedness() != 0 && hu.getPosition().getID().getValue() == me.location().getID().getValue())
					humans.add(hu);
			}
		}
		return humans;
	}

	public static ArrayList<Civilian> getLastCycleInMyPositionNeedHelpCivilians() {//farz mikonim ke civiliane load shode need help nemibashad
		ArrayList<Civilian> humans = new ArrayList<Civilian>();
		AmbulanceTeamAgent me = AmbulanceTeamAgent.currentAgent();
		ArrayList<Civilian> lastCycleSensedHumans = me.getVisibleEntities(Civilian.class);
		for (Civilian hu : lastCycleSensedHumans) {
			Civilian civ = hu;
			if (civ.getHP() != 0
					&& civ.isPositionDefined()
					&& civ.getPosition().getID().getValue() == me.location().getID().getValue())
				humans.add(civ);
			if (civ.isPositionDefined() && !(civ.getPosition() instanceof Building) && civ.getDamage() == 0)
				humans.remove(civ);
		}
		return humans;
	}

	public static boolean AmITheLoader1(Collection<Integer> ids) {
		int age = 0;
		for (Integer at : ids) {
			if (at < me().getID().getValue())
				age++;
		}
		if (age == 0)
			return true;
		return false;
	}

	private static AmbulanceTeam me() {
		AmbulanceTeamAgent me = AmbulanceTeamAgent.currentAgent();
		return me.me();
	}

	public static boolean AmITheLoader2(Collection<AmbulanceTeam> ats) {
		int age = 0;
		for (Human at : ats) {
			if (at.getID().getValue() < me().getID().getValue())
				age++;
		}
		if (age == 0)
			return true;
		return false;
	}


}