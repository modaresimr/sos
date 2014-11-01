package sos.ambulance_v2.decision.saveHumanHandling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.base.AmbulanceConstants;
import sos.ambulance_v2.base.AmbulanceConstants.ATstates;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.ambulance_v2.base.WorkingInfo;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.util.SOSActionException;
import sos.base.util.SOSGeometryTools;
import sos.base.util.information_stacker.CycleInformations;
import sos.base.util.information_stacker.act.MoveAction;
import sos.base.util.information_stacker.act.StockMoveAction;
import sos.search_v2.tools.cluster.ClusterData;

/**
 * Created by IntelliJ IDEA.
 * User: ara
 */
public abstract class AmbulanceGeneralHandler {

	protected  Human target;
	protected final Pair<? extends Area, Point2D> place;
	protected final AmbulanceTeamAgent self;

	private final short MIDDLE_OF_SIMULATION = 120;
	private final short MIN_HIGH_LEVEL_BURIEDNESS=30;
	public boolean isDone = false;
	protected boolean acknowledgeMsgSent = false;
	protected int lastInfoSent = 2;
	protected boolean stateChanged = true;
	private int startTime;

	public abstract boolean finished();

	public abstract void resetState();

	public abstract void handle() throws SOSActionException;

	public AmbulanceGeneralHandler(AmbulanceTeamAgent ownerAgent, Human target) {
		this.self = ownerAgent;
		if(target == null)
			self.log().error("in AmbulanceGeneralHandler target is null");
		this.place = target.getPositionPair();
		this.target = target;
		startTime = self.time();
	}

	protected void ignoreTarget(IgnoreReason reason, int till) {
		target.getRescueInfo().setIgnoredUntil(reason, till);
	}
	/*************************************** getWork *****************************************/	
	protected WorkingInfo getWork(){
		return self.me().getWork();
	}
	/*****************************************************************************************/
	/************************************* isInNearMe ****************************************/		
	protected boolean isInMyArea(Human human) {
		if ( self.me().getAreaPosition() == human.getAreaPosition() )
			return true;
		return false;
	}
	/********************************************************************************************/
	public int getMyBusyCycles(Human target, Pair<? extends Area, Point2D> place, ATstates currentState) {
		self.log().log("[Trace] calculating my busy time for " + target);
		int time = 0;
		switch (currentState) {
		case SEARCH:
			// for msg delay
			time++;
			self.log().log("\t" + currentState + " current time=" + time);
			break;
		case MOVE_TO_TARGET:
			if (target == null) {
				self.log().error("how target is null??");
				time++;
			} else {
				time += 10;//should calculate
				self.log().log("\t" + currentState + " time=" + time);
			}
		case RESCUE:
			if (target == null) {
				self.log().error("how target is null??");
				time++;
			} else {
				if (target.getBuriedness() != 0) {
					if (target.getRescueInfo().getNowWorkingOnMe().size() != 0) {
						time += target.getBuriedness() / target.getRescueInfo().getNowWorkingOnMe().size();
					} else {
						time += target.getBuriedness();
					}
					self.log().log("\t" + currentState + " time=" + time);
				}

				if (target instanceof Civilian) { // for load
					time++;
					self.log().log("\t" + currentState + "load time=" + time);
				}

			}

		case MOVE_TO_REFUGE:
			if (this.target == null) {
				self.log().error("how target is null??");
				time++;
			} else if (target instanceof Civilian) {
				boolean loader = AmITheLoader1(target.getRescueInfo().getNowWorkingOnMe());
				if (loader) {
					time += target.getRescueInfo().getTimeToRefuge();
					// for unload
					time++;
				}

				self.log().log("\t" + currentState + " time=" + time);
			}

		}
		return time;
	}
	/*******************************************************************************************/
	/*************************************AmITheLoader1*******************************************/
	public boolean AmITheLoader1(Collection<Integer> ids) {
		int age = 0;
		for (Integer at : ids) {
			if (at < self.getID().getValue())
				age++;
		}
		if (age == 0)
			return true;
		return false;
	}
	/*******************************************************************************************/
	/*************************************AmITheLoader2*******************************************/
	public boolean AmITheLoader2(Collection<AmbulanceTeam> ats) {
		int age = 0;
		for (Human at : ats) {
			if (at.getID().getValue() <self.getID().getValue())
				age++;
		}
		if (age == 0)
			return true;
		return false;
	}

	/*******************************************************************************************/
	/************************************** changeTarget ***************************************/
	protected void changeTarget() { 
		ArrayList<Human> viewedHumans = getLastCycleInMyPositionNeedHelpHumans();
		self.log().info("viewed Civilians = " + viewedHumans);
		if (viewedHumans.size() == 1)
			return;
		else if (viewedHumans.size() == 0)
			return;

		short ATNeeded = target.getRescueInfo().getATneedToBeRescued();
		short nowWorking = (short) target.getRescueInfo().getNowWorkingOnMe().size();
		self.log().info("my target = " + target + "  ATNeeded = " + ATNeeded + " nowWorking = " + target.getRescueInfo().getNowWorkingOnMe());
		if (ATNeeded >= nowWorking)
			return;

		self.log().info("target has extra AT");

//		if (!self.AmITheLoader1(target.getRescueInfo().getNowWorkingOnMe()))
//			return;

		self.log().info("I am extra AT");

		Human newTarget =chooseHuman(viewedHumans);
		AmbulanceUtils.updateATtarget(newTarget, self.me(), self.me().getWork().getState());
		this.target=newTarget;
	}
	/*******************************************************************************************/
	/************************************ chooseHuman *****************************************/
	protected Human chooseHuman(List<Human> humans) {
		Human bestTarget = null;
		int minDeathTime = Integer.MAX_VALUE;
		boolean bestTargetStillNeeds = false;

		for (Human hm : humans) {
			boolean currentNeeds = stillNeeds(hm);
			int dt = hm.getRescueInfo().getDeathTime();
			self.log().info("Human = "+ hm + "DeathTime = " + dt);
			if (bestTargetStillNeeds) {
				if (!currentNeeds)
					continue;
				if (dt < minDeathTime) {
					minDeathTime = dt;
					bestTarget = hm;
				}

			} else {
				if (currentNeeds) {
					minDeathTime = dt;
					bestTarget = hm;
					bestTargetStillNeeds = true;
				} else {
					if (dt < minDeathTime) {
						minDeathTime = dt;
						bestTarget = hm;
					}
				}

			}
		}
		self.log().info("bestTarget = "+ bestTarget);
		return bestTarget;
	}
	/*****************************************************************************************/
	/************************************ stillNeeds *****************************************/
	private boolean stillNeeds(Human hm) {
		short atNeeded = hm.getRescueInfo().getATneedToBeRescued();
		short nowWorking = (short) hm.getRescueInfo().getNowWorkingOnMe().size();
		return atNeeded > nowWorking;
	}

	/*****************************************************************************************/
	/******************* getLastCycleInMyPositionNeedHelpHumans ******************************/
	public ArrayList<Human> getLastCycleInMyPositionNeedHelpHumans() {
		self.log().info("getLastCycleInMyPositionNeedHelpCivilians");
		ArrayList<Human> humans = new ArrayList<Human>();
		ArrayList<Human> lastCycleSensedHumans = self.getVisibleEntities(Human.class);
		self.log().debug("lastCycleSensedHumans=" + lastCycleSensedHumans);
		for (Human hu : lastCycleSensedHumans) {
			if(hu.getBuriedness() ==0)
				continue;
			Human hum = hu;
			if (hum.isPositionDefined() && !(hum.getPosition() instanceof Building) && hum.getDamage() == 0)
				continue;
			if(!(hum instanceof Civilian) && MIDDLE_OF_SIMULATION < self.time()){
				if(self.model().getLastAfterShockTime() < 3 )
					continue;
				else if ( hum.getBuriedness()>MIN_HIGH_LEVEL_BURIEDNESS+3)
					continue;
			
			}

			if (hum.getHP() != 0
					&& hum.isPositionDefined()
					&& hum.getPosition().getID().getValue() == self.location().getID().getValue())
				humans.add(hum);
		}
		self.log().debug("getLastCycleInMyPositionNeedHelpCivilians=" + humans);
		return humans;
	}
	/*******************************************************************************************/
	/************************************** isInMyseen *****************************************/
	protected boolean isInMyseen() {

		if (SOSGeometryTools.distance(self.me().getPositionPoint(),place.second())<self.VIEW_DISTANCE )
			return true;
		
		return false;
	}
	
	/*******************************************************************************************/
	/******************************** isReachableTarget ****************************************/
	protected boolean isReachableTarget() {
		return !self.move.isReallyUnreachableXY(place.first(), (int) place.second().getX(), (int) place.second().getY());
	}
	/*******************************************************************************************/
	/********************************* isReachableTargetx ***************************************/
	protected boolean isReachableTarget(Human target) {
		return AmbulanceUtils.isReachableForAT(target,true);
	}
	/*******************************************************************************************/
	//-------------------------------------------------------------------------------------
	protected void sendStatueMsgBySay(ATstates state, boolean needHelp) {
		if ((self.messageSystem.type != Type.NoComunication) || (self.messageSystem.type != Type.LowComunication))
			return;
		int mlposIndex;
		int id;
		int st = state.getMessageIndex();

		int help = needHelp ? 1 : 0;
		if (state == ATstates.SEARCH) { //search mode
			id = 0;
		} else {
			id = this.target.getID().getValue();
		}
		mlposIndex = self.me().getPositionArea().getAreaIndex();
		self.messageBlock = new MessageBlock(MessageXmlConstant.HEADER_AMBULANCE_STATUS);
		self.messageBlock.addData(MessageXmlConstant.DATA_AMBULANCE_INDEX, self.me().getAmbIndex());
		self.messageBlock.addData(MessageXmlConstant.DATA_AT_STATE, st);
		self.messageBlock.addData(MessageXmlConstant.DATA_ID, id);
		self.messageBlock.addData(MessageXmlConstant.DATA_AREA_INDEX, mlposIndex);
		self.messageBlock.addData(MessageXmlConstant.DATA_NEED_HELP, help);
		self.messageBlock.addData(MessageXmlConstant.DATA_TIME, self.time());
		self.messageBlock.setResendOnNoise(false);
		self.sayMessages.add(self.messageBlock);
	}

	//-------------------------------------------------------------------------------------
	protected void sendTaskAckMsg(int type) {
		self.messageBlock = new MessageBlock(MessageXmlConstant.HEADER_AMBULANCE_TASK_ACK);
		self.messageBlock.addData(MessageXmlConstant.DATA_AMBULANCE_INDEX, self.me().getAmbIndex());
		self.messageBlock.addData(MessageXmlConstant.DATA_ID, target.getID().getValue());
		self.messageBlock.addData(MessageXmlConstant.DATA_ACK_TYPE, type);
		self.messageBlock.setResendOnNoise(false);
		self.messages.add(self.messageBlock);
		self.log().info("sending Task Ack Message" + target + "==>" + ((type == 0) ? "accepted" : (type == 1) ? "finished" : (type == 2) ? "rejected" : "unknown " + type));
	}

	//-------------------------------------------------------------------------------------
	protected void sendInfoMsg(int id, ATstates state, int finishTime) {
		int st = state.getMessageIndex();
		self.messageBlock = new MessageBlock(MessageXmlConstant.HEADER_AMBULANCE_INFO);
		self.messageBlock.addData(MessageXmlConstant.DATA_AMBULANCE_INDEX, self.me().getAmbIndex());
		self.messageBlock.addData(MessageXmlConstant.DATA_ID, id);
		self.messageBlock.addData(MessageXmlConstant.DATA_AT_STATE, st);
		self.messageBlock.addData(MessageXmlConstant.DATA_FINISH_TIME, self.time() + finishTime);
		self.messageBlock.setResendOnNoise(false);
		self.messages.add(self.messageBlock);
		self.sayMessages.add(self.messageBlock);

		lastInfoSent = self.time();
		stateChanged = false;
		self.log().info("sending info Message==>target=" + self.model().getEntity(id) + " state=" + state + " ft:" + finishTime);

	}

	//-------------------------------------------------------------------------------------
	protected boolean isReachablityBug() {
		Area myArea = self.me().getAreaPosition();
		if (!(self.me().getAreaPosition() instanceof Road))
			return false;
		if (self.me().getImReachableToEdges().isEmpty())
			return false;
		for (Edge edge : myArea.getPassableEdges()) {
			if (Reachablity.isReachable((Road) myArea, self.me().getPositionPoint(), edge) != ReachablityState.Close)
				return false;
		}
		self.log().error("Traffic Simulator bug-->reachablity should handle it...");
		return true;
	}

	public boolean isStockBug() {
		int longAgo = self.time() - startTime;
		int numberOfStock = 0;
		Point2D lastLocation = self.me().getPositionPoint();
		for (int i = 1; i <= longAgo; i++) {
			CycleInformations info = self.informationStacker.getInformations(i);

			if (info.getAct() instanceof StockMoveAction)
				if (lastLocation.distance(info.getPositionPair().second()) < AmbulanceConstants.STOCK_DISTANSE)
					numberOfStock++;
				else
					numberOfStock = 0;

			else if (!(info.getAct() instanceof MoveAction))
				numberOfStock = 0;
			lastLocation = info.getPositionPair().second();
			if(numberOfStock>2){
				self.log().warn("I'm in stock bug..:(");
				return true;
			}
		}
		return false;
	}


	public ArrayList<ClusterData> getNearestClusters(int number){
		ArrayList<ClusterData> nearestCluster=new ArrayList<ClusterData>();
		final ClusterData myCluster = self.model().searchWorldModel.getClusterData();

		ArrayList<ClusterData> cds = new ArrayList<ClusterData>(self.model().searchWorldModel.getAllClusters());
		Collections.sort(cds, new Comparator<ClusterData>() {

			@Override
			public int compare(ClusterData o1, ClusterData o2) {
				double o1s = SOSGeometryTools.distance(myCluster.getX(),myCluster.getY(),o1.getX(),o1.getY());
				double o2s = SOSGeometryTools.distance(myCluster.getX(),myCluster.getY(),o2.getX(),o2.getY());
				if (o1s>o2s )
					return 1;
				if ( o1s<o2s)
					return -1;
				return 0;
			}
		});
		int i = 0;

		for (ClusterData cd : cds) {
			if (i >= number)
				break;
			i++;
			nearestCluster.add(cd);
		}
		return nearestCluster;

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + target + "]";
	}
}