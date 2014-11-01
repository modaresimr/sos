package sos.base.util.mapRecognition;

import sos.base.SOSWorldModel;
import sos.base.util.mapRecognition.MapRecognition.MapName;

public class MapInfoFactory {
	public static final String BCLC = "BUILDING_CENTER_LIST_CHECKER";

	public static AbstractMapInfo createMapInfo(String typeName, String mapName, MapName realMapName, SOSWorldModel model) {
		if (typeName.equals(BCLC)) {
			return new ACLC(mapName, realMapName, model);
		} else
			return null;
	}

	public static AbstractMapInfo createMapInfo(String header[],SOSWorldModel model){
		if (header[AbstractMapInfo.TYPE_NAME_IDNEX].equals(BCLC)) {
			return new ACLC(header,model);
		} else
			return null;
	}

	public static AbstractMapInfo createMapInfo(String typeName, String mapName, SOSWorldModel model) {
		if (typeName.equals(BCLC)) {
			return new ACLC(mapName, model);
		} else
			return null;
	}

	public static AbstractMapInfo createMapInfo(String typeName, String name, MapName realMapName) {
		if (typeName.equals(BCLC)) {
			return new ACLC(name, realMapName);
		} else
			return null;
	}

}
