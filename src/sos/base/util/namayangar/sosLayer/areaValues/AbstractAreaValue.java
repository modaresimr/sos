package sos.base.util.namayangar.sosLayer.areaValues;

import sos.base.SOSAgent;
import sos.base.entities.Area;

public abstract class AbstractAreaValue {

	protected  SOSAgent<?> agent;

	public AbstractAreaValue() {
	}

	public abstract int getValue(SOSAgent<?> agent, Area area);

	public abstract boolean isValidForArea(SOSAgent<?> agent, Area a);

	public abstract boolean shouldbeAddedToList();

	public abstract String getValueName();

}
