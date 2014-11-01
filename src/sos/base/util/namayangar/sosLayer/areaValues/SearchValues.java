package sos.base.util.namayangar.sosLayer.areaValues;

import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;

public class SearchValues extends AbstractAreaValue {

	public SearchValues() {
	}

	@Override
	public int getValue(SOSAgent<?> agent,Area area) {
		return 0;
	}

	@Override
	public boolean isValidForArea(SOSAgent<?> agent,Area a) {
		return a instanceof Building;
	}

	@Override
	public boolean shouldbeAddedToList() {
		return true;
	}

	@Override
	public String getValueName() {
		return "Search";
	}

}
