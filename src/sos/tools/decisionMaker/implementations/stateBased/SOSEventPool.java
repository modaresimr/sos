package sos.tools.decisionMaker.implementations.stateBased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;

/**
 * SOS Event pool object
 * @author Salim
 */
public class SOSEventPool {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<Class<? extends SOSIState>, List<SOSEvent>> events = new HashMap<Class<? extends SOSIState>, List<SOSEvent>>();

	public void addEvent(Class<? extends SOSIState> state, SOSEvent event) {
		List<SOSEvent> list = events.get(state);
		if (list == null) {
			list = new ArrayList<SOSEvent>();
			events.put(state, list);
		}
		list.add(event);
	}

	public List<SOSEvent> getEvents(Class<? extends SOSIState> state) {
		return events.get(state);
	}
	public List<SOSEvent> removeEvents(Class<? extends SOSIState> state) {
		return events.remove(state);
	} 
}
