package sos.tools.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.LogLevel;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.tools.FileReader;
import sos.tools.FileWriter;
import sos.tools.Utils;
import sos.tools.geometry.SOSGeometryTools;

/** @author Salim */
public class MapVerifier {
	public final SOSWorldModel model;
	private boolean logInformation;
	SOSLoggerSystem logger;
	private String fileName = "MapVerifier-";
	private MapTable mapTable;
	public String root = "SOSFiles/MapVerifier/";
	// ----------------------------------------------------------------
	public static int NEIGHBOR_SET = -1;
	public static int NEIGHBOR_WAS_CORRECT = -2;
	public static int NEIGHBOR_SAME_EDGE_WAS_NOT_FOUND = -3;

	// ----------------------------------------------------------------

	public MapVerifier(SOSAgent<? extends StandardEntity> agent) {
		this.model = agent.model();
		logInformation = true;
		initiateMapTable();
		if (logInformation) {
			logger = new SOSLoggerSystem(null, "mapVerfier/" + fileName + agent.getID(), true, OutputType.File);
			logger.addLoggingLevel(LogLevel.Warn);
			logger.addLoggingLevel(LogLevel.Error);
		}
		logNormal("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logNormal("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Start %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logNormal("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		MapInformation map = checkFile();
		if (map == null) {
			if (logInformation)
				mapTable.makeNewMap();
			correctEdgesAndCheckNeighborsOfBuildings();
			correctEdgesAndCheckNeighborsOfRoads();
			if (logInformation) {
				mapTable.closeMapFile();
				mapTable.saveMapList();
			}
		} else {
			logNormal("--------- Reading map data from file ---------");
			mapTable.readMap(map);
		}
	}

	public void correctEdgesAndCheckNeighborsOfBuildings() {
		logNormal("********************************************************************************************");
		logNormal("***********************************Checking all Buildings***********************************");
		logNormal("********************************************************************************************");
		for (Building b : model.buildings()) {
			logNormal("---------- Checking Building: " + b.getID() + " ----------");
			// ---------------------------------- checks and corrects edges if needed
			List<Edge> newEdges = new ArrayList<Edge>();
			boolean changed = correctApexes(b, newEdges);
			// --------------------------------------
			if (newEdges.size() != b.getEdges().size() || changed) {// if changes have been made
				if (logInformation) {
					mapTable.addArea(b);
					logNormal("List of new Edges:");
					for (Edge object : newEdges) {
						logNormal(object.toString());
					}
				}
				b.setNewEdges(newEdges);
			}
			// -------------------------------------- checks if the area is polygon
			if (logInformation) {// FIXME make faster by writing the polygon algorithm
				java.awt.geom.Area a = new java.awt.geom.Area(SOSGeometryTools.getShape(b));
				if (!a.isPolygonal())
					logError("Building: " + b.getID() + " IS NOT POLYGON");
			}
		}
	}

	public void correctEdgesAndCheckNeighborsOfRoads() {
		logNormal("********************************************************************************************");
		logNormal("*************************************Checking all Roads*************************************");
		logNormal("********************************************************************************************");
		for (Road r : model.roads()) {
			logNormal("---------- Checking Road: " + r.getID() + " ----------");
			// ---------------------------------- checks and corrects edges if needed
			List<Edge> newEdges = new ArrayList<Edge>();
			boolean changed = correctApexes(r, newEdges);
			// --------------------------------------
			if (newEdges.size() != r.getEdges().size() || changed) {// if changes have been made
				logCheck("Edges were changed to:" + newEdges.size() + " from:" + r.getEdges().size());
				if (logInformation) {
					mapTable.addArea(r);
					logNormal("List of new Edges:");
					for (Edge object : newEdges) {
						logNormal(object.toString());
					}
				}
				r.setNewEdges(newEdges);
			}
			// -------------------------------------- checks if the area is polygon
			if (logInformation) {// FIXME make faster by writing the polygon algorithm
				java.awt.geom.Area a = new java.awt.geom.Area(SOSGeometryTools.getShape(r));
				if (!a.isPolygonal())
					logError("Road: " + r.getID() + " IS NOT POLYGON");
			}
		}
	}

	public boolean correctApexes(Area a, List<Edge> newEdges) {
		// --------------------------------------------------------------LIST AND MAPS
		boolean changed = false;
		List<Edge> edges = a.getEdges();
		HashMap<EntityID, Boolean> map = new HashMap<EntityID, Boolean>();
		HashMap<Pair<Double, Double>, Boolean> apexMap = new HashMap<Pair<Double, Double>, Boolean>();
		ArrayList<EntityID> lostNeighbors = new ArrayList<EntityID>();
		// -------------------------------------primitives
		int numberOfEntrances = a.getNeighbours().size();
		Edge current = edges.get(edges.size() - 1);
		Edge preEdge = null;
		Edge nextEdge = null;
		// -------------------------------------main loop
		for (int i = 0; i < edges.size(); i++) {
			preEdge = current;// initial value= last edge in list
			current = edges.get(i);// initiaal value= first edge in list
			if (i != edges.size() - 1)
				nextEdge = edges.get(i + 1);
			else
				nextEdge = edges.get(0);
			if (current.isPassable()) {// checks if the neighbor connected through this edge has the area as its neighbor too
				Area na = (Area) model.getEntity(current.getNeighbour());
				if (Utils.isInNeighbours(na, a) == -1) {// NA does not have this area in its neighbors
					logError(na.getID() + "--- does not have " + a.getID() + " while " + a.getID() + " has " + na.getID());
					int resCondition = na.addNeighbor(current, a.getID());// corrects the neighbors
					if (resCondition == NEIGHBOR_SAME_EDGE_WAS_NOT_FOUND) {
						logError(na.getID() + " Did not find the same edge to add to " + current);
					} else if (resCondition == NEIGHBOR_SET) {
						changed = true;
						logError(na.getID() + "Neighbor " + na.getID() + " was set succesfuly to " + current);
					} else if (resCondition == NEIGHBOR_WAS_CORRECT) {
						logWarning(na.getID() + "Neighbor was aleardy set");
					}
				}
			}
			// check ZERO length edge
			if (current.getStart().equals(current.getEnd())) {
				changed = true;
				logWarning("Zero Edge found: " + current);
				if (!preEdge.getEnd().equals(nextEdge.getStart())) {
					if (preEdge.getEnd().equals(nextEdge.getEnd()))
						nextEdge.switchHeadAndTail();
					else {
						Edge ee = new Edge(preEdge.getEnd(), preEdge.getEnd(), current.getNeighbour());
						logWarning("One edge is added in order to connect pre and next while 1 ZeroEdge is deleted:" + ee);
						newEdges.add(ee);
					}
				} else if (logInformation) {
					if (current.isPassable()) {
						lostNeighbors.add(current.getNeighbour());
					}
				}
				current = preEdge;
				continue;// we dont add a pair be cause it is repeated
			}
			// checking if an edge is repeated
			boolean repeated = false;
			if (current.getStart().equals(preEdge.getStart()) && current.getEnd().equals(preEdge.getEnd()))
				repeated = true;
			if (current.getEnd().equals(preEdge.getStart()) && current.getStart().equals(preEdge.getEnd()))
				repeated = true;
			if (repeated) {// correcting repeated edge
				changed = true;
				logWarning("TurningEdge found: " + current);
				if (preEdge.isPassable() && current.isPassable()) {
					if (!preEdge.getNeighbour().equals(current.getNeighbour()))
						logError("Two repeating edges have different Neighbors: " + preEdge.getNeighbour() + " & " + current.getNeighbour());
				} else if (!preEdge.isPassable() && current.isPassable()) {
					preEdge.setNeighbour(current.getNeighbour());
					logNormal("Neighbor " + current.getNeighbour() + "of " + current + " is added to: " + preEdge);
				}
				current = preEdge;
				continue;
			}
			if (logInformation) {
				// ------------------------keeps the saved neighbor relations in a map so later it can check if anything is lost or not
				if (current.isPassable())
					map.put(current.getNeighbour(), true);
				// ----------------------- checks if the Start point f the current edge is repeated or not
			}
			// -------------------------- adds the edge to the list of new edges
			if (!preEdge.getEnd().equals(current.getStart())) {
				if (preEdge.getEnd().equals(current.getEnd()) && !(current.getEnd().equals(nextEdge.getStart()))) {
					changed = true;
					current.switchHeadAndTail();
				} else if (preEdge.getEnd().equals(current.getEnd()) && (current.getEnd().equals(nextEdge.getStart()))) {
					while (nextEdge.getStart().equals(nextEdge.getEnd())) {
						changed = true;
						if (edges.size() > i + 1)
							edges.remove(i + 1);
						else {
							nextEdge = edges.get(0);
							break;
						}
						if (i != edges.size() - 1)
							nextEdge = edges.get(i);
						else
							nextEdge = edges.get(0);
					}
					if (!nextEdge.getStart().equals(nextEdge.getEnd())) {
						changed = true;
						current.switchHeadAndTail();
						nextEdge.setStart(current.getEnd());
					}
				} else {
					changed = true;
					Edge ee = new Edge(preEdge.getEnd(), current.getStart());
					newEdges.add(ee);
					logWarning("One edge is added in order to connect pre and next:" + ee);
				}
			}
			if (preEdge.getStart().equals(current.getStart())) {
				changed = true;
				current.setStart(preEdge.getEnd());
			}
			if (apexMap.get(new Pair<Double, Double>(current.getStart().getX(), current.getStart().getY())) == null) {
				apexMap.put(new Pair<Double, Double>(current.getStart().getX(), current.getStart().getY()), true);
			} else {// it means a repeated apex is going to be added
				logError("* This Area has repeated Apex");
				a.setCorrectApexList(false);
				// return false;
			}
			newEdges.add(current);
			// -------------------------
		}
		if (logInformation) {
			logCheck(a.getID() + " had " + numberOfEntrances + " passable Edges");
			int numberOfLostEntrances = 0;
			for (int i = 0; i < lostNeighbors.size(); i++) {
				if (map.get(lostNeighbors.get(i)) == null) {
					logError("An Neighbor properties of " + a.getID() + " to" + lostNeighbors.get(i) + " is lost because of a ZERO edge");
					numberOfLostEntrances++;
				}
			}
			if (numberOfLostEntrances > 0) {
				logError(a.getID() + " had " + numberOfLostEntrances + "Lost Entraces" + "!!!!");
			}
		}
		return changed;
	}

	public long getLong(int x, int y) {
		long l_1 = (long) (x) << (Integer.SIZE);
		long l_2 = l_1 + y;
		return l_2;
	}

	public boolean correctApexes(List<Edge> edges, List<Edge> newEdges) {
		HashMap<EntityID, Boolean> map = new HashMap<EntityID, Boolean>();
		HashMap<Long, Boolean> apexMap = new HashMap<Long, Boolean>();
		ArrayList<EntityID> lostNeighbors = new ArrayList<EntityID>();
		// -------------------------------------primitives
		Edge current = edges.get(edges.size() - 1);
		Edge preEdge = null;
		// -------------------------------------main loop
		for (int i = 0; i < edges.size(); i++) {
			preEdge = current;// initial value= last edge in list
			current = edges.get(i);// initiaal value= first edge in list
			// check ZERO length edge
			if (current.getStart().equals(current.getEnd())) {// in this condition neighbor is no more important //FIXME
				if (logInformation) {
					if (current.isPassable())
						lostNeighbors.add(current.getNeighbour());
				}
				current = preEdge;
				continue;// we dont add a pair be cause it is repeated
			}
			// checking if an edge is repeated
			boolean repeated = false;
			if (current.getStart().equals(preEdge.getStart()) && current.getEnd().equals(preEdge.getEnd()))
				repeated = true;
			if (current.getEnd().equals(preEdge.getStart()) && current.getStart().equals(preEdge.getEnd()))
				repeated = true;
			if (repeated) {// correcting repeated edge
				if (preEdge.isPassable() && current.isPassable()) {
					if (!preEdge.getNeighbour().equals(current.getNeighbour()))
						logError("Two repeating edges have different Neighbors: " + preEdge.getNeighbour() + " & " + current.getNeighbour());
				} else if (!preEdge.isPassable() && current.isPassable()) {
					preEdge.setNeighbour(current.getNeighbour());
				}
				current = preEdge;
				continue;
			}
			if (apexMap.get(current.getStart()) == null)
				apexMap.put(getLong((int) current.getStart().getX(), (int) current.getStart().getY()), true);
			else {// it means a repeated apex is going to be added
				return false;
			}
			if (logInformation) {
				// ------------------------keeps the saved neighbor relations in a map so later it can check if anything is lost or not
				if (current.isPassable())
					map.put(current.getNeighbour(), true);
				// ----------------------- checks if the Start point f the current edge is repeated or not
			}
			// -------------------------- adds the edge to the list of new edges
			newEdges.add(current);
			// -------------------------
		}
		if (logInformation) {
			@SuppressWarnings("unused")//TODO
			int numberOfLostEntrances = 0;
			for (int i = 0; i < lostNeighbors.size(); i++) {
				if (map.get(lostNeighbors.get(i)) == null) {
					numberOfLostEntrances++;
				}
			}
		}
		return true;
	}

	public void logError(String error) {
		if (logInformation)
			logger.error(error);
	}

	public void logWarning(String error) {
		if (logInformation)
			logger.warn(error);
	}

	public void logCheck(String error) {
		if (logInformation)
			logger.logln("[Checked] " + error);
	}

	public void logNormal(String text) {
		if (logInformation)
			logger.logln(text);
	}

	public void initiateMapTable() {
		FileReader fr = new FileReader(root, "maps.txt", 300000);
		String file = fr.readString(Integer.MAX_VALUE);
		mapTable = new MapTable(file, this);
	}

	public MapInformation checkFile() {
		logNormal("--------- checking in saved maps ---------");
		MapInformation map = mapTable.getMap(model.roads().size(), model.buildings().size(), model.getWorldBounds(), model.areas().get(0).getLocation());
		return map;
	}

}

class MapInformation {
	public String mapName;
	public int buildings;
	public int roads;
	public Pair<Integer, Integer> firstArea = null;
	public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> mapSize = null;

	public MapInformation(int roads, int buildings, String mapName, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> mapSize, Pair<Integer, Integer> firsArea) {
		this.roads = roads;
		this.buildings = buildings;
		this.mapSize = mapSize;
		this.firstArea = firsArea;
	}

	public MapInformation(String data, int num) {
		StringTokenizer st = new StringTokenizer(data.trim(), ",");
		mapName = "Map" + num;
		if (st.countTokens() == 8) {
			this.roads = new Integer(st.nextToken());
			this.buildings = new Integer(st.nextToken());
			this.mapSize = new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(new Integer(st.nextToken()), new Integer(st.nextToken())), new Pair<Integer, Integer>(new Integer(st.nextToken()), new Integer(st.nextToken())));
			this.firstArea = new Pair<Integer, Integer>(new Integer(st.nextToken()), new Integer(st.nextToken()));
		} else {
			try {
				throw new Error("Bad map data file Format");
			} catch (Exception e) {
			}
		}
	}

	public boolean equals(MapInformation mi) {
		if (mi.mapName.equals(mapName))
			return true;
		return false;
	}

	public String getDiscriber() {
		return roads + "," + buildings + "," + mapSize.first().first() + "," + mapSize.first().second() + "," + mapSize.second().first() + "," + mapSize.second().second() + "," + firstArea.first() + "," + firstArea.second();
	}

	@Override
	public String toString() {
		return mapName + getDiscriber();
	}

	public boolean equals(int roads, int buildings, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> mapSize, Pair<Integer, Integer> firstArea) {
		if (this.roads == roads && this.buildings == buildings && this.mapSize.first().first().equals(mapSize.first().first()) && this.mapSize.first().second().equals(mapSize.first().second()) && this.mapSize.second().equals(mapSize.second()) && this.mapSize.second().first().equals(mapSize.second().first()) && this.mapSize.second().second().equals(mapSize.second().second()) && this.firstArea.first().equals(firstArea.first()) && this.firstArea.second().equals(firstArea.second()))
			return true;
		return false;

	}
}

class MapTable {
	public int number = 0;
	public ArrayList<MapInformation> maps = new ArrayList<MapInformation>();
	private ObjectOutputStream oos;
	private final MapVerifier mv;

	public MapTable(String mapsData, MapVerifier mv) {
		this.mv = mv;
		if (mapsData.indexOf(';') > -1) {
			StringTokenizer st = new StringTokenizer(mapsData.trim(), ";");
			while (st.hasMoreTokens()) {
				number++;
				maps.add(new MapInformation(st.nextToken(), number));
			}
		}
	}

	public void saveMapList() {
		FileWriter fw = new FileWriter(mv.root, "maps.txt");
		for (MapInformation map : maps) {
			fw.writeln(map.getDiscriber() + ";");
		}

	}

	public void readMap(MapInformation map) {
		try {
			FileInputStream fis = new FileInputStream(new File(mv.root + map.mapName));
			ObjectInputStream ois = new ObjectInputStream(fis);
			int num = (Integer) ois.readObject();
			for (int i = 0; i < num; i++) {
				int id = (Integer) ois.readObject();
				List<Edge> edges = new ArrayList<Edge>(20);
				int[][] ar = (int[][]) ois.readObject();
				Boolean b = (Boolean) ois.readObject();
				for (int[] el : ar) {
					edges.add(new Edge(new Point2D(el[0], el[1]), new Point2D(el[2], el[3]), new EntityID(el[4])));
				}
				Area aa = ((Area) mv.model.getEntity(new EntityID(id)));
				aa.setNewEdges(edges);
				((Area) mv.model.getEntity(new EntityID(id))).setCorrectApexList(b);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public MapInformation getMap(int roads, int buildings, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pair, Pair<Integer, Integer> firstArea) {
		for (MapInformation map : maps) {
			if (map.equals(roads, buildings, pair, firstArea))
				return map;
		}
		return null;
	}

	public int getNewMapNumber() {
		number++;
		return number;
	}

	public void makeNewMap() {
		String mapName = "Map" + getNewMapNumber();
		File f = new File(mv.root, mapName);
		maps.add(new MapInformation(mv.model.roads().size(), mv.model.buildings().size(), mapName, mv.model.getWorldBounds(), mv.model.areas().get(0).getLocation()));
		try {
			oos = new ObjectOutputStream(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addArea(Area a) {
		int[][] ar = new int[a.getEdges().size()][6];
		for (int i = 0; i < a.getEdges().size(); i++) {
			Edge e = a.getEdges().get(i);
			ar[i][0] = e.getStartX();
			ar[i][1] = e.getStartY();
			ar[i][2] = e.getEndX();
			ar[i][3] = e.getEndY();
			if (e.isPassable())
				ar[i][4] = e.getNeighbour().getValue();
		}
		buffer.add(new AreaData(ar, a.getID().getValue(), a.hasCorrectApexList()));
	}

	ArrayList<AreaData> buffer = new ArrayList<AreaData>();

	public void flush() {

		try {
			for (AreaData ad : buffer) {
				oos.writeObject(ad.id);
				oos.writeObject(ad.ar);
				oos.writeObject(ad.correctData);
			}
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void closeMapFile() {
		try {
			oos.writeObject(buffer.size());
			flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class AreaData {
	public int[][] ar;
	public boolean correctData;
	int id;

	public AreaData(int[][] ar, int id, boolean correct) {
		this.ar = ar;
		this.id = id;
		correctData = correct;
	}
}
// List<Edge> edges = new ArrayList<Edge>();
// edges.add(new Edge(new Point2D(0, 0), new Point2D(1, 1)));
// edges.add(new Edge(new Point2D(1, 1), new Point2D(2, 2)));
// edges.add(new Edge(new Point2D(1, 1), new Point2D(2, 2)));
// edges.add(new Edge(new Point2D(2, 2), new Point2D(1, 1)));
// edges.add(new Edge(new Point2D(3, 3), new Point2D(2, 2)));
// edges.add(new Edge(new Point2D(3, 3), new Point2D(4, 4)));
// edges.add(new Edge(new Point2D(4, 4), new Point2D(5, 5)));
// edges.add(new Edge(new Point2D(5, 5), new Point2D(6, 6)));
// edges.add(new Edge(new Point2D(6, 6), new Point2D(5, 5)));
// edges.add(new Edge(new Point2D(5, 5), new Point2D(6, 6)));
// edges.add(new Edge(new Point2D(5, 5), new Point2D(7, 7)));
// edges.add(new Edge(new Point2D(7, 8), new Point2D(8, 8)));
// edges.add(new Edge(new Point2D(8, 8), new Point2D(9, 9)));
// edges.add(new Edge(new Point2D(8, 8), new Point2D(10, 10)));
// edges.add(new Edge(new Point2D(10, 10), new Point2D(1, 1)));
// edges.add(new Edge(new Point2D(10, 10), new Point2D(0, 0)));