package sos.tools.decisionMaker.definitions.commands;

import java.util.List;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;

/**
 * SOS abstract Task class which defines trivial specifications and functionalities.
 * 
 * @author Salim
 */
public abstract class SOSTask<E extends SOSITarget> {
	protected E target;
	private int creationTime = -1;
	private List<SOSTask> dependencies;
	private List<SOSTask> requiredFutureTasks;

	public SOSTask(E target, int creatinTime) {
		this.target = target;
		this.creationTime = creatinTime;
	}

	/**
	 * Returns target of the task from type E.
	 * 
	 * @return
	 */
	public abstract SOSITarget getTarget();

	/**
	 * Checks if the target is not null.
	 * 
	 * @return
	 */
	public boolean hasTarget() {
		return target != null;
	}

	/**
	 * Returns time of creation
	 * 
	 * @return
	 */
	public int creationTime() {
		return creationTime;
	}

	/**
	 * This function is used to execute a task
	 * 
	 * @throws SOSActionException
	 */
	public abstract void execute(SOSAgent<? extends Human> agent) throws SOSActionException;
}
