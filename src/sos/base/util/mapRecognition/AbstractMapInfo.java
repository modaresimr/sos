package sos.base.util.mapRecognition;

import sos.base.SOSWorldModel;
import sos.base.util.mapRecognition.MapRecognition.MapName;

/**
 * @author Salim
 */
public abstract class AbstractMapInfo {
	protected String name;
	protected MapName realMapName;
	public static final int ADDITIANL_NAME_IDNEX = 2;
	public static final int TYPE_NAME_IDNEX = 1;
	public static final int NAME_IDNEX = 0;

	public AbstractMapInfo(String mapName, MapName realMapName, SOSWorldModel model) {
		name = mapName;
		this.realMapName = realMapName;
		makeDataAndWriteToFile(model);
	}

	public AbstractMapInfo(String[] header, SOSWorldModel model) {
		parseHeader(header, model);
		readFromFile();
	}

	public AbstractMapInfo(String mapName, SOSWorldModel model) {
		name = mapName;
		realMapName = MapName.Unknown;
		makeDataAndWriteToFile(model);
	}

	public AbstractMapInfo(String name, MapName realMapName) {
		this.name = name;
		this.realMapName = realMapName;
		readFromFile();
	}

	public abstract boolean match(SOSWorldModel model);

	private void makeDataAndWriteToFile(SOSWorldModel model) {
		makeData(model);
		writeInfoToFIle();
	}

	protected abstract void makeData(SOSWorldModel model);

	private void parseHeader(String[] header, SOSWorldModel model) {
		name = header[NAME_IDNEX];

		try {
			realMapName = MapName.valueOf(header[ADDITIANL_NAME_IDNEX]);
		} catch (Exception e) {
		} finally {
			if (realMapName == null || realMapName == MapName.Big || realMapName == MapName.Medium || realMapName == MapName.Small || realMapName == MapName.Unknown)
				realMapName = MapRecognition.getRealMapName(model);
		}
		if (!getTypeName().equals(header[TYPE_NAME_IDNEX])) {
			try {
				throw new Exception("incorrect MapInfo was instantiated");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public abstract float getAccordance(SOSWorldModel model);

	public String getMapName() {
		return name;
	}

	public void setMapName(String name) {
		this.name = name;
	}

	public abstract String getTypeName();

	public abstract void writeInfoToFIle();

	public abstract void readFromFile();

	public abstract boolean keepDataInMemory();

	public MapName getRealMapName() {
		return realMapName;
	}

	public String getMapHeader() {
		return "<MAP MapName=\'" + getMapName() + "\' InfoType=\'" + getTypeName() + "\' AdditionalName='" + getRealMapName() + "' >";
	}
}
