package sos.tools.decisionMaker.implementations.targets;

import sos.base.entities.Human;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;

/**
 *@author Salim , reyhaneh
 */
public  class LastRescueTarget implements SOSITarget{

	private final Human human;

	public LastRescueTarget(Human human) {
		this.human = human;
	}

	public Human getHuman() {
		return human;
	}
	


	
}
