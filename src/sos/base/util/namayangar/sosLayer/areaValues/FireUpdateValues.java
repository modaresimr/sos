package sos.base.util.namayangar.sosLayer.areaValues;

import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;

public class FireUpdateValues extends AbstractAreaValue {

	@Override
	public int getValue(SOSAgent<?> agent, Area area) {
		return ((Building) area).priority();
	}

	@Override
	public String getValueName() {
		return "FUpdateP";
	}

	@Override
	public boolean isValidForArea(SOSAgent<?> agent, Area a) {
		if (!(a instanceof Building))
			return false;
		if (((Building) a).getFireBuilding() == null)
			return false;
		return true;
	}

	@Override
	public boolean shouldbeAddedToList() {
		return true;
	}

}
