package sos.tools.decisionMaker.implementations.stateBased.states;

import java.util.List;

import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.SOSInformationModel;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.definitions.feedback.SOSFeedback;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;

/**
 * SOS State interface which includes Abstract State specifications and requirements.
 * 
 * @author Salim
 */
public abstract class SOSIState<E extends SOSInformationModel> {
	protected E infoModel;

	public SOSIState(E infoModel) {
		this.infoModel = infoModel;
	}

	/**
	 * Is called to make decisions
	 * 
	 * @param eventPool
	 * @return
	 */
	public abstract SOSTask<?> decide(SOSEventPool eventPool) throws SOSActionException ;

	/**
	 * Feedbacks are used to inform state of the quality of its functionality.<br>
	 * For example, a feedback may inform the state that it is taken too much time.<br>
	 * Feedbacks are only given to the state which has made a decision
	 * 
	 * @param feedbacks
	 */
	public abstract void giveFeedbacks(List<SOSFeedback> feedbacks);

	/**
	 * This informs the state that it hasn't returned a task ro the task ahsn't been used
	 */
	public abstract void skipped();

	/**
	 * This informs the state that it wasn't used because an earlier state returned a task.
	 */
	public abstract void overTaken();

	/**
	 * Is called to see whether a state is valid or not.
	 * 
	 * @return
	 */
	/* public abstract boolean isValid(); */

	/**
	 * called when the task returned by this state is used
	 */
	public void taken() {

	}

	public void handleEvents(List<SOSEvent> events) {
		if (events == null)
			return;
		for (SOSEvent sosEvent : events) {
			handleEvent(sosEvent);
		}
	}

	protected abstract void handleEvent(SOSEvent sosEvent);

	public abstract void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel);
	
	public abstract String getName();


}
