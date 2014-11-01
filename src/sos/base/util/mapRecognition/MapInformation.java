package sos.base.util.mapRecognition;

import sos.base.util.mapRecognition.MapRecognition.MapName;

public class MapInformation {
	private String mapName;
	private MapRecognitionType mrt;
	private MapName realMapName;

	public enum MapRecognitionType {
		KNOWN, IS_NEW;
		@Override
		public String toString() {
			if (equals(KNOWN))
				return "KNOWN";
			else
				return "IS_NEW";
		};
	}

	public MapInformation(String name, boolean knwon, MapName realMapName) {
		this.setRealMapName(realMapName);
		this.setMapName(name);
		if (knwon)
			mrt = MapRecognitionType.KNOWN;
		else
			mrt = MapRecognitionType.IS_NEW;
	}

	public MapRecognitionType getCondition() {
		return mrt;
	}

	public boolean isNewMap() {
		return mrt == MapRecognitionType.IS_NEW;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapName() {
		return mapName;
	}

	@Override
	public String toString() {
		return "<MapInformation Name: " + mapName + " type:" + mrt + " RealMapName:" + getRealMapName().name() + " >";
	}

	public void setRealMapName(MapName realMapName) {
		this.realMapName = realMapName;
	}

	public MapName getRealMapName() {
		return realMapName;
	}

	//Salim
	public boolean isBigMap() {
		switch (getRealMapName()) {

		case Berlin:
		case Big:
		case Eindhoven:
		case Mexico:
			return true;
		case Istanbul:
		case Paris:
		case Unknown:
		case Medium:
		case MexicoSmall:
		case Small:
		case Kobe:
		case VC:
			return false;
		default:
			System.err.println("this map has not recognized yet");
		}
		return false;
	}

	//Salim
	public boolean isMediumMap() {
		switch (getRealMapName()) {
		case Istanbul:
		case Paris:
		case Unknown:
		case Medium:
			return true;

		case Berlin:
		case Big:
		case Eindhoven:
		case Mexico:
		case MexicoSmall:
		case Small:
		case Kobe:
		case VC:
			return false;
		default:
			System.err.println("this map has not recognized yet");
		}
		return false;
	}

}
