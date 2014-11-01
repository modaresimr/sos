package sos.tools.decisionMaker.implementations.tasks;

import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.ambulance_v2.decision.saveHumanHandling.AmbulanceGeneralHandler;
import sos.ambulance_v2.decision.saveHumanHandling.SaveAgentTaskHandler;
import sos.ambulance_v2.decision.saveHumanHandling.SaveCivilianTaskHandler;
import sos.base.SOSAgent;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.HumanTarget;

/**
 *@author Salim , reyhaneh
 */
public class SaveHumanTask extends SOSTask<HumanTarget>{

	AmbulanceGeneralHandler saveHumanHandler;
	public SaveHumanTask(Human human, int creatinTime) {
		super(new HumanTarget(human), creatinTime);
	}
	

	@Override
	public SOSITarget getTarget() {
		return target;
	}

	
	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {

		Human etarget = target.getHuman();
		
		if(etarget == null)
			((AmbulanceTeamAgent)agent).log().error("in saveHumanTask target is null");
		

		((AmbulanceTeamAgent)agent).log().info("######## SAVEHUMAN TASK ########");
		
	if(saveHumanHandler == null){
		
		if(etarget instanceof Civilian){
				saveHumanHandler = new SaveCivilianTaskHandler((AmbulanceTeamAgent) agent,etarget);
		}
		else{
			saveHumanHandler = new SaveAgentTaskHandler((AmbulanceTeamAgent)agent,etarget);
		}
	}
		saveHumanHandler.handle();
	}

}