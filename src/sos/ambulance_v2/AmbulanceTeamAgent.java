package sos.ambulance_v2;

import sos.ambulance_v2.base.AbstractAmbulanceTeamAgent;
import sos.ambulance_v2.decision.ATFeedbackFactory;
import sos.ambulance_v2.decision.AmbulanceCenterActivity;
import sos.ambulance_v2.decision.AmbulanceDecisionMaker;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.search_v2.agentSearch.AmbulanceSearch;
import sos.tools.decisionMaker.definitions.commands.SOSTask;

/**
 * SOS ambulance team agent.
 */
public class AmbulanceTeamAgent extends AbstractAmbulanceTeamAgent {

	private AmbulanceDecisionMaker ADK;
	/******* stuck *****/
	public int stuckUntil=-1;
	private boolean taskAssigner=false;
	private SOSTask<?> currentTask;
	public Human target=null;
	
	
	
	@Override
	protected void preCompute() {
		super.preCompute();// it's better to call first!
		InitializeVariables();
		chooseTheAssigner();
		if (taskAssigner && messageSystem.type != Type.CenteralMiddleMan) {
			addCenterActivity(new AmbulanceCenterActivity(this));
		}
		ADK = new AmbulanceDecisionMaker(this, new ATFeedbackFactory());

		newSearch = new AmbulanceSearch(this);
	}


	@Override
	protected void prepareForThink() {
		super.prepareForThink();
		try {
			currentTask = ADK.decidePreThink();
		} catch (SOSActionException e) {
			e.printStackTrace();
		}
	}


	protected void chooseTheAssigner() {
		if (messageSystem.type == Type.NoComunication || model().ambulanceTeams().size() == 1)
			return;
		if (messageSystem.type == Type.CenteralMiddleMan && messageSystem.getMine().isMiddleMan()) 
			this.taskAssigner = true;
		
		if ((messageSystem.type == Type.NoMiddleMan || messageSystem.type == Type.WithMiddleMan) && me().getMessageWeightForSending() > 1) 
			this.taskAssigner = true;
		
	}

	@Override
	protected void think() throws SOSActionException {
		super.think();

		// gets a task from decision maker
		if (currentTask == null)
			currentTask = ADK.decide();
		if (currentTask != null) // there had been a task
			currentTask.execute(this);
		log().info("######## search ########");
		finishTasksState();
	}

	@Override
	protected void finalizeThink() {
		super.finalizeThink();

	}

	// *************************************************************************************************
	public boolean amIGoingToDieSoon() {
		if (me().getDamage() == 0)
			return false;
		if (SimpleDeathTime.getEasyLifeTime(me().getHP(), me().getDamage(), time()) < 4)
			return true;
		return false;
	}

	// *************************************************************************************************
	public boolean isItCriticalTogoRefuge() {
		if (me().getDamage() == 0)
			return false;
		if (me().getHP() == 0)
			return false;
		if (me().getRescueInfo().getInjuryDeathTime() - time() > 20 && me().getDamage() > 50)
			return true;
		if (me().getRescueInfo().getInjuryDeathTime() - time() < 20 && me().getDamage() > 25)
			return true;
		return false;
	}

	//*************************************************************************************************
	/**
	 * @r@mik initializing humanoids properties
	 */
	private void InitializeVariables() {
		for (Human hm : model().humans()) 
			hm.getRescueInfo().updateProperties();
		
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {

	}

	// *************************************************************************************************
	/*
	 * Ali: Please keep it at the end!!!!(non-Javadoc)
	 */
	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray, sender, channel);
		ADK.hear(header, data, dynamicBitArray, sender, channel);//Added by Salim
	}
}