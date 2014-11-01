package sos.tools.decisionMaker.implementations.stateBased;

import java.util.ArrayList;
import java.util.List;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.tools.decisionMaker.definitions.SOSAbstractDecisionMaker;
import sos.tools.decisionMaker.definitions.SOSInformationModel;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

/**
 * State Based decision maker is used in state based strategies.<br>
 * It provides functionalities such as feedback system for states
 * 
 * @author Salim
 */
public abstract class SOSStateBasedDecisionMaker<E extends SOSInformationModel> extends SOSAbstractDecisionMaker<E> {
	protected List<SOSIState<E>> thinkStates;
	private List<SOSIState<E>> preThinkStates;
	protected StateFeedbackFactory feedbackFactory;
	protected SOSLoggerSystem abstractStateLogger;
	public SOSStateBasedDecisionMaker(SOSAgent<? extends Human> agent, StateFeedbackFactory feedbackFactory, Class<? extends SOSInformationModel> infoModelClass) {
		super(agent, infoModelClass);
		abstractStateLogger=agent.abstractStateLogger;
		this.feedbackFactory = feedbackFactory;
		setThinkStates(new ArrayList<SOSIState<E>>());
		setPreThinkStates(new ArrayList<SOSIState<E>>());

		initiateStates();
	}

	public SOSTask<?> decidePreThink() throws SOSActionException {
		return runStates(preThinkStates);
	}

	@Override
	public SOSTask<?> decide() throws SOSActionException {
		return runStates(thinkStates);
	}

	public SOSTask<?> runStates(List<SOSIState<E>> states) throws SOSActionException {
		SOSEventPool eventPool = new SOSEventPool();
		SOSTask<?> result = null;
		for (SOSIState<E> state : states) {
			state.handleEvents(eventPool.removeEvents(state.getClass()));
			if (result != null) {
				state.overTaken();
				continue;
			}
			/*
			 * if (!state.isValid()) {
			 * state.skipped();
			 * } else {
			 */
			long t1 = System.currentTimeMillis();
			try {
				result = state.decide(eventPool);
			} catch (Exception e) {
				if (e instanceof SOSActionException){
					infoModel.getAgent().lastState=state+"";
					abstractStateLogger.logln(this.infoModel.getTime()+":"+state.getClass().getSimpleName()+"\t\t\t : action="+e.getMessage()+"\t\t\t"+(System.currentTimeMillis() - t1)+"ms");
					throw (SOSActionException) e;
				}
				infoModel.getLog().error(e);
			}
			long time = System.currentTimeMillis() - t1;
			state.giveFeedbacks(feedbackFactory.createFeedbacks(infoModel.getAgent(), state, result, time));
			if (result != null){
				state.taken();
				abstractStateLogger.logln(this.infoModel.getTime()+":"+state.getClass().getSimpleName()+"\t\t\t"+(System.currentTimeMillis() - t1)+"ms");
			}
			infoModel.getAgent().sosLogger.act.info(state + " : time " + time);
		}
		for (SOSIState<E> state : states) {
			state.handleEvents(eventPool.removeEvents(state.getClass()));
		}
		//		}
		return result;
	}

	public abstract void initiateStates();

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		for (SOSIState<E> state : getPreThinkStates()) {
			state.hear(header, data, dynamicBitArray, sender, channel);
		}
		for (SOSIState<E> state : getThinkStates()) {
			state.hear(header, data, dynamicBitArray, sender, channel);
		}
	}

	public List<SOSIState<E>> getPreThinkStates() {
		return preThinkStates;
	}

	public void setPreThinkStates(List<SOSIState<E>> states) {
		this.preThinkStates = states;
	}

	public List<SOSIState<E>> getThinkStates() {
		return thinkStates;
	}

	public void setThinkStates(List<SOSIState<E>> states) {
		this.thinkStates = states;
	}
}
