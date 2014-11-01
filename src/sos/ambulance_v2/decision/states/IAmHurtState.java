package sos.ambulance_v2.decision.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sos.ambulance_v2.AmbulanceInformationModel;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.channel.Channel;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.base.util.information_stacker.act.AbstractAction;
import sos.base.util.information_stacker.act.RescueAction;
import sos.tools.Utils;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.targets.EmptyTarget;
import sos.tools.decisionMaker.implementations.tasks.RescueTask;
import sos.tools.decisionMaker.implementations.tasks.RestTask;
import sos.tools.decisionMaker.implementations.tasks.StandardMoveToListTask;

/* 
 * @author Salim,reyhaneh
 */

public class IAmHurtState extends SOSIState<AmbulanceInformationModel> {
	
	private static final int MIN_DEATH_TIME = 6;
	private static final int MIN_HP = 3000;
	private static final int MAX_DAMAGE = 100;
	private static final int MIN_TIME_TO_RESCUE = 3;
	private int deathTime;
	private AmbulanceTeamAgent ambulance =null;
	int timeToRefuge = 6;

	public IAmHurtState(AmbulanceInformationModel infoModel) {
		super(infoModel);
		ambulance = infoModel.getAmbulance();
	}

	/**
	 * @author Salim,reyhaneh
	 * @throws SOSActionException 
	 */
	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) throws SOSActionException {
		infoModel.getLog().info("$$$$$$$$$$$$$$$$$$$$$$ IAmHurtState $$$$$$$$$$$$$$$$$$$$$$$$$");
		
		
		if(!isValid()){
			infoModel.getLog().info("$$$$$ Skipped from IAmHurtState $$$$$");
			return null;
		}
		
		 if(((AmbulanceTeamAgent)infoModel.getAgent()).me().getBuriedness() > 0)
			 return new RestTask(new EmptyTarget(),infoModel.getTime(), "Agent has buriedness.");
		 
		 if(infoModel.getModel().refuges().isEmpty()){
			 if(infoModel.getATEntity().getWork() != null &&  infoModel.getATEntity().getWork().getTarget() != null){
				 Human target = infoModel.getATEntity().getWork().getTarget();
				 target.getRescueInfo().setIgnoredUntil(IgnoreReason.InvalidOldTarget, 999);
				 AmbulanceUtils.rejectTarget(target, infoModel.getATEntity(), ambulance);
			 }
			 return null;
		 }
			 
		 timeToRefuge = getTimeToNearestRefuge();
		AbstractAction act = infoModel.getInfoStacker().getInformations(1).getAct();
		if( deathTime < (timeToRefuge+infoModel.getTime()+3) )
			return iAmGoingToDieSoon(act);
		else
			return	iAmGoingToBeAlive(act);
			
	}

	public boolean isValid() {

		if(((AmbulanceTeamAgent)(infoModel.getAgent())).isLoadingInjured())
			return false;
		if( iWillDieInNextCycles()){
			infoModel.getLog().error("agent is going to die");
        	infoModel.getAgent().messageBlock=new MessageBlock(MessageXmlConstant.HEADER_DEAD_AGENT);
        	infoModel.getAgent().messageBlock.addData(MessageXmlConstant.DATA_AGENT_INDEX,((Human)infoModel.getAgent().me()).getAgentIndex());
        	infoModel.getAgent().messages.add(infoModel.getAgent().messageBlock);
			return true;
		}
		else if(((AmbulanceTeamAgent)infoModel.getAgent()).me().getBuriedness() > 0)
			return true;
		else if(((AmbulanceTeamAgent)infoModel.getAgent()).me().getDamage() ==0 )
			return false;
		else if ((deathTime -  infoModel.getTime()) < MIN_DEATH_TIME || (((AmbulanceTeamAgent)infoModel.getAgent()).me().getHP()/((AmbulanceTeamAgent)infoModel.getAgent()).me().getDamage() < MIN_HP/MAX_DAMAGE))
			return true;
		else if (deathTime -  infoModel.getTime() > 20 && ((AmbulanceTeamAgent)infoModel.getAgent()).me().getDamage() > 30)
			return true;
		else if ( deathTime - infoModel.getTime() < 20 )
			return true;
		else 
			return false;
	}
	
	private boolean iWillDieInNextCycles(){
		deathTime = ((Human)infoModel.getAgent().me()).getRescueInfo().getDeathTime();
		int aliveTime = deathTime-infoModel.getTime();
		int rescueTime = (infoModel.getModel().refuges().isEmpty())?999 : getTimeToNearestRefuge()+((AmbulanceTeamAgent)infoModel.getAgent()).me().getBuriedness();
		if(aliveTime < 4 && aliveTime < rescueTime)
			return true;
		return false;
	}
	
	private SOSTask iAmGoingToBeAlive(AbstractAction act) throws SOSActionException {
		if(infoModel.getATEntity().getWork() != null &&  infoModel.getATEntity().getWork().getTarget() != null){
			Human target = infoModel.getATEntity().getWork().getTarget();
			int nowWorkingOn = target.getRescueInfo().getNowWorkingOnMe().size();
			if( deathTime > (timeToRefuge+infoModel.getTime()+ (target.getBuriedness()/nowWorkingOn)) 
					&&  infoModel.getATEntity().getHP() >3500 
					&& (infoModel.getATEntity().getDamage()< 50 || infoModel.getTime() > 170))
					return null;
			
			if(target.getRescueInfo().getDeathTime() < (timeToRefuge+infoModel.getTime()+ (target.getBuriedness()/nowWorkingOn)+ 6)
					|| target.getBuriedness() > 10){
				target.getRescueInfo().setIgnoredUntil(IgnoreReason.InvalidOldTarget, 999);
				AmbulanceUtils.rejectTarget(target, infoModel.getATEntity(), ambulance);
			}
				
		}
		
			return goToRefuge();
		
	}

	private SOSTask goToRefuge() {
		
		return new StandardMoveToListTask(infoModel.getModel().refuges(), infoModel.getTime());
		
	}
	
	private SOSTask iAmGoingToDieSoon(AbstractAction act) {
			
		if( act instanceof RescueAction ){
			return new RescueTask(((RescueAction)act).getHuman(),infoModel.getTime());
		}
		//TODO continue last action
		
		ArrayList<Civilian> validCivilians =getValidCivilians();
		if( !validCivilians.isEmpty()){
			Civilian nearestCivilian = (Civilian) Utils.getNearestEntity(validCivilians, infoModel.getPositionPoint()); //
			if(nearestCivilian != null)
				return canIrescueNearestCivilian(nearestCivilian);
			}
		return goToNearestBuilding();
		
	}



	private  ArrayList<Civilian> getValidCivilians() {

		ArrayList<Civilian> validCivilians=new ArrayList<Civilian>();
		for ( Civilian civilian : infoModel.getModel().civilians()){
				
			if( !civilian.isBuriednessDefined() )
				continue;
			if( !civilian.isDamageDefined() )
				continue;
			if( !civilian.isHPDefined() )
				continue;
			if( !civilian.isPositionDefined())
				continue;
			if( !civilian.isReallyReachableSearch())
				continue;
			validCivilians.add(civilian);
			
			}
		
		return validCivilians;
	}
	private SOSTask canIrescueNearestCivilian(Civilian nearestCivilian) {
		
		sos.base.move.Path path = infoModel.getAgent().move.getPathTo(Arrays.asList(nearestCivilian.getAreaPosition()), StandardMove.class);
		int timeArrived = Utils.getSampleTimeToTarget(path);
		
		if( timeArrived < deathTime-MIN_TIME_TO_RESCUE)
			return new RescueTask(nearestCivilian,infoModel.getTime());
		else{
			infoModel.getLog().info("$$$$$ Skipped from IAmHurtState $$$$$");
			return null;
		}
	}

	private SOSTask goToNearestBuilding() {
		return new StandardMoveToListTask(infoModel.getModel().buildings(), infoModel.getTime());
	}

	@Override
	public void giveFeedbacks(List<SOSFeedback> feedbacks) {
	}

	@Override
	public void skipped() {

	}

	@Override
	public void overTaken() {

	}


	public int getTimeToNearestRefuge() {
		sos.base.move.Path path = infoModel.getAgent().move.getPathFromTo(java.util.Collections.singleton(infoModel.getEntity().getAreaPosition()), infoModel.getModel().refuges(), StandardMove.class);
		return Utils.getSampleTimeToTarget(path);
	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {
		
	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
	
	
}
