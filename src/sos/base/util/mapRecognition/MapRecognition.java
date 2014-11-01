package sos.base.util.mapRecognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import sos.base.SOSWorldModel;

public class MapRecognition {
	public static final String mapsListAddress = "SOSFiles/MapRecongintion/maps.txt";
	public static final String mapsDir = "SOSFiles/MapRecongintion/maps/";
	ArrayList<AbstractMapInfo> maps;
	public enum MapName{
		VC(1263,1954,433100,442800),
		Paris(1618,3025,956653,996981),
		Kobe(736,1515,468520,343572),
		Berlin(1426,3385,2187484,1637291),
		Istanbul(1244,3337,1299638,979313),
		Mexico(1556,5108,2105764,2245930),
		MexicoSmall(1556,5108,631729,673779),
		Eindhoven(1308,5172,2078918,1756160),
		Small(0,0,700000,700000),
		Medium(0,0,1400000,1400000),
		Big(0,0,3000000,3000000),
		Unknown(0,0,0,0);


		private final int buildingsSize;
		private final int roadsSize;
		private final int mapWidth;
		private final int mapHeight;
		MapName(int buildingsSize,int roadsSize,int mapWidth,int mapHeight) {
			this.buildingsSize = buildingsSize;
			this.roadsSize = roadsSize;
			this.mapWidth = mapWidth;
			this.mapHeight = mapHeight;
		}

		public int getBuildingsSize() {
			return buildingsSize;
		}
		public int getRoadsSize() {
			return roadsSize;
		}
		public int getMapWidth() {
			return mapWidth;
		}
		public int getMapHeight() {
			return mapHeight;
		}


	}
	public MapRecognition() {

	}

	public void initialize(SOSWorldModel model) {
		createFiles();
		readMaps(model);
	}

	public void createFiles() {
		File f = new File(mapsDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(mapsListAddress);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.err.println("[MAP RECOGNIZATION]"+e.getMessage());
			}
		}
	}

	private void readMaps(SOSWorldModel model) {
		ArrayList<String[]> mapInfos = readMapListFile();
		if (mapInfos == null)
			return;
		maps = new ArrayList<AbstractMapInfo>(mapInfos.size());
		for (String[] strings : mapInfos) {
			maps.add(MapInfoFactory.createMapInfo(strings,model));
		}
	}

	private ArrayList<String[]> readMapListFile() {
		ArrayList<String[]> headers = new ArrayList<String[]>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(mapsListAddress)));
		} catch (FileNotFoundException e) {
			System.err.println("[Map Recognization]"+e.getMessage());
			return headers;
			//			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				headers.add(parseHeader(line));
			}
		} catch (IOException e) {
			System.err.println("[Map Recognization]"+e.getMessage());
		}
		return headers;
	}

	public MapInformation verifyMap(SOSWorldModel model) {
		for (AbstractMapInfo mapInfo : maps) {
			if (mapInfo.match(model)) {
				return new MapInformation(mapInfo.name, true, mapInfo.getRealMapName());
			}
		}
		ACLC map = (ACLC) MapInfoFactory.createMapInfo(MapInfoFactory.BCLC, getNewName(),getRealMapName(model), model);
		maps.add(map);
		writeMapsToFile();
		return new MapInformation(map.getMapName(), false, map.getRealMapName());
	}

	static  MapName getRealMapName(SOSWorldModel model) {
		for (MapName mapName : MapName.values()) {
			if(equalInRange(model.buildings().size(), mapName.getBuildingsSize(), 100)&&equalInRange(model.roads().size(), mapName.getRoadsSize(), 100)&&equalInRange((int)model.getBounds().getWidth(), mapName.getMapWidth(), 10000)&&equalInRange((int)model.getBounds().getHeight(), mapName.getMapHeight(), 10000))
				return mapName;
		}
		System.out.println(model.buildings().size()+"\t"+model.roads().size()+"\t"+model.getBounds().getWidth()+"\t"+ model.getBounds().getHeight());
 //		System.exit(0);
		double mapDimension = Math.hypot(model.getBounds().getWidth(),model.getBounds().getHeight());
		if(mapDimension<Math.hypot(MapName.Small.getMapWidth(),MapName.Small.getMapHeight()))
			return MapName.Small;
		if(mapDimension<Math.hypot(MapName.Medium.getMapWidth(),MapName.Medium.getMapHeight()))
			return MapName.Medium;
		return MapName.Big;
	}

	private static boolean equalInRange(int first,int second,int range){
		return Math.abs(first-second)<=range;
	}
	private String[] parseHeader(String header) {
		String[] res = new String[3];
		int first = header.indexOf("'");
		int end = header.indexOf('\'', first + 1);
		res[0] = header.substring(first + 1, end);
		first = header.indexOf('\'', end + 1);
		end = header.indexOf('\'', first + 1);
		res[1] = header.substring(first + 1, end);
		first = header.indexOf('\'', end + 1);
		end = header.indexOf('\'', first + 1);
		res[2] = header.substring(first + 1, end);
//		System.out.println(res[0] + "----" + res[1] + "----" + res[2]);
		return res;
	}

	public String getNewName() {
		return "MAP-" + maps.size();
	}

	public void writeMapsToFile() {
		String s = "";
		for (AbstractMapInfo mi : maps) {
			s += mi.getMapHeader() + "\n";
		}
		try {
			FileOutputStream fos = new FileOutputStream(new File(mapsListAddress));
			fos.write(s.getBytes());
		} catch (FileNotFoundException e) {
			System.err.println("[Map Recognization]"+e.getMessage());
		} catch (IOException e) {
			System.err.println("[Map Recognization]"+e.getMessage());
		}

	}
}
