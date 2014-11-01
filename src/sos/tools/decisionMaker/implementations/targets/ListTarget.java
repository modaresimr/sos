package sos.tools.decisionMaker.implementations.targets;

import java.util.ArrayList;
import java.util.List;

import sos.tools.decisionMaker.definitions.commands.SOSITarget;

public class ListTarget<E> extends ArrayList<E> implements SOSITarget {
	/**
	 * @author Salim
	 */
	private static final long serialVersionUID = 1L;

	public ListTarget(List<? extends E> areas) {
		this.addAll(areas);
	}
}
