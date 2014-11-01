
package sos.base.util.namayangar.sosLayer.police;
//
//import java.awt.BasicStroke;
//
public class Highways {}
//public class Highways extends SOSAbstractToolsLayer<Area> {
//
//
//	@Override
//	public int getZIndex() {
//		return 50;
//	}
//
//	@Override
//	protected void makeEntities() {
//
//		////		HashMap<PoliceForce, ArrayList<Task>> tasks = ((PoliceForceAgent) model().sosAgent()).getState(OpenHighwayesState.class).tasks;
//		long t = System.currentTimeMillis();
//		ArrayList<Area> vertecesAreas = new ArrayList<Area>();
//		//		ArrayList<Road> roads = ((PoliceForceAgent) model().sosAgent()).getState(OpenHighwayesState.class).Highways;
//		//		for (Road road : roads) {
//		//			areas.add(road);
//		//		}
//		////		for (ArrayList<Task> task : tasks.values()) {
//		////			for (Task task2 : task) {
//		////				areas.add(task2.getAreaPosition());
//		////			}
//		////		}
//		//		
//		FireDisasterSpace model = (FireDisasterSpace) model();
////		ArrayList<ConvexHull_arr_New> convexes = new ArrayList<ConvexHull_arr_New>();
//		ArrayList<ConvexHullNew> convexes = new ArrayList<ConvexHullNew>();
//
//		for (BuildingBlock bblock : model.islands()) {
//			ArrayList<Point> ps = new ArrayList<Point>();
// 			for (Building standardEntity : bblock.buildings()) {
//				//				int[] apexes = standardEntity.getApexList();
//				//				for (int i = 0; i < apexes.length; i += 2) {
//				//					ps.add(new Point(apexes[i], apexes[i + 1]));
//				//				}
//				ps.add(new Point(standardEntity.getX(), standardEntity.getY()));
////				buildings.add(standardEntity);
//			}
//			convexes.add(new ConvexHullNew(ps));
////			convexes.add(new ConvexHull_arr_New(buildings));
//		}
//		HashSet<Area> areahash = new HashSet<Area>();
//		FOR: for (Road road : model().roads()) {
//			for (ConvexHullNew convexHullNew : convexes) {
//
////				if(convexHullNew.isInConvex(road.getApexList()))
//				if (convexHullNew.isInConvex(new Point(road.getX(), road.getY())))
////				if (convexHullNew.contains(road.getX(), road.getY()))
//					continue FOR;
//			}
//			areahash.add(road);
//		}
//		ArrayList<Area> removeList = new ArrayList<Area>();
//		for (Area area : areahash) {
//			int i = 0;
//			for (Area neigh : area.getNeighbours()) {
//				if (areahash.contains(neigh))
//					i++;
//			}
//			if (i < 2)
//				removeList.add(area);
//
//		}
//		areahash.removeAll(removeList);
//
//		FOR:for (Area road : areahash) {
//			if (road.getNeighbours().size() >= 4) {
//				int validCount = 0;
//				ArrayList<Area> tmp = new ArrayList<Area>();
//				tmp.add(road);
//				tmp.addAll(road.getNeighbours());
//				for (Area area : road.getNeighbours()) {
//
//					if (getLenghtOfRoad(areahash, tmp, area, LENGHT) >= LENGHT)
//						validCount++;
//				}
//				if (validCount > VALIDCOUNT){
//					for (Area area : road.getNeighbours()) {
//						if(vertecesAreas.contains(area))
//							continue FOR;
//					}
//					vertecesAreas.add(road);
//				}
//			}
//		}
//
//		//		setEntities(((PoliceForceAgent) model().sosAgent()).getState(OpenHighwayesState.class).Highways);
//		//		setEntities(areahash);
//		System.out.println(System.currentTimeMillis() - t);
//		//		int[][] graph=new int[areas.size()][areas.size()];
//		//		for (int i = 0; i < graph.length; i++) {
//		//			for (int j = 0; j < graph.length; j++) {
//		//				for (int k = 0; k < graph.length; k++) {
//		//					
//		//				}
//		//			}
//		//		}
//		//		ArrayList<ShapeDebugFrame.AWTShapeInfo> back=new ArrayList<ShapeDebugFrame.AWTShapeInfo>();
//		//		for (Area area : model.areas()) {
//		//			ShapeDebugFrame.AWTShapeInfo awt=new AWTShapeInfo(area.getShape(), area.toString(),areas.contains(area)?Color.green: areahash.contains(area)? Color.BLACK:Color.gray, areas.contains(area));
//		//			back.add(awt);
//		//		}
//
////		ArrayList<Point>ps=new ArrayList<Point>();
//		ArrayList<Area> aList=new ArrayList<Area>();
//		for (Area a : vertecesAreas) {
//			for (Area area2: a.getNeighbours()) {
//				aList.add(area2);
//				//			int[] apexes = area2.getApexList();
//				//			for (int i = 0; i < apexes.length; i += 2) {
//				//				ps.add(new Point(apexes[i], apexes[i + 1]));
//				//			}
//			}
//		
//		}
////		ConvexHullNew chn=new ConvexHullNew(ps);
//		ConvexHull_arr_New chn=new ConvexHull_arr_New(aList);
//		HashSet<Area> newAreaHash= new HashSet<Area>();
//		for (Area area2 : areahash) {
////			if(chn.isInConvex(new Point(area2.getX(), area2.getY())))
////				newAreaHash.add(area2);
//			if(chn.contains(area2.getX(), area2.getY()))
//				newAreaHash.add(area2);
//		}
//		/*
//		HashMap<Area, ArrayList<Area>> yal = new HashMap<Area, ArrayList<Area>>();
//		for (Area area : vertecesAreas) {
//			HashSet<Area> tmp = new HashSet<Area>();
//			
////			tmp.add(area);
//			tmp.addAll(area.getNeighbours());
//			yal.put(area, new ArrayList<Area>());
//			for (Area a : area.getNeighbours()) {
//				Area neighbor = getHighwaysNeighbour(newAreaHash, vertecesAreas, new HashSet<Area>(tmp), a,area);
//				if (neighbor != null && !yal.get(area).contains(neighbor))
//					yal.get(area).add(neighbor);
//			}
////			break;
//		}
//		System.out.println(yal);
//*/
////		setEntities(vertecesAreas);
//				setEntities(newAreaHash);
//	}
//	//	ShapeDebugFrame debug=new ShapeDebugFrame();
//	Area getHighwaysNeighbour(HashSet<Area> areahash, ArrayList<Area> targets, HashSet<Area> OldPath, Area area, Area startTarget) {
//		System.out.println(OldPath);
//		//		debug.show("max="+max, new ShapeDebugFrame.AWTShapeInfo(area.getShape(), area.toString(), Color.blue, true));
//		if (targets.contains(area)) {
//			return area;
//		}
//		int mindist = Integer.MAX_VALUE;
//		Area result = null;
////		HashSet<Area> newPath = new HashSet<Area>(OldPath);
////		newPath.addAll(area.getNeighbours());
//		
//		for (Area area2 : area.getNeighbours()) {
//			if (OldPath.contains(area2))
//				continue;
//			if(area2.equals(startTarget))
//				continue;
//			if (!areahash.contains(area2))
//				continue;
//			HashSet<Area> newPath = OldPath;
//			newPath.add(area2);
//			Area a = getHighwaysNeighbour(areahash, targets, newPath, area2,startTarget);
//			if (a != null) {
//				int min = PoliceUtils.getDistance(area, a);
//				if (mindist > min) {
//					result = a;
//					mindist = min;
//				}
//			}
//		}
//
//		return result;
//	}
//
//	int getLenghtOfRoad(HashSet<Area> areahash, ArrayList<Area> OldPath, Area area, int max) {
//		if (max == 0)
//			return 0;
//		int tmpmax = 0;
//
//		for (Area area2 : area.getNeighbours()) {
//			if (area2 instanceof Building)
//				continue;
//			if (!areahash.contains(area2))
//				continue;
//			if (OldPath.contains(area2))
//				continue;
//			//			ArrayList<Area> newPath = new ArrayList<Area>(OldPath);
//			ArrayList<Area> newPath = OldPath;
//			newPath.add(area2);
//			tmpmax = Math.max(tmpmax, getLenghtOfRoad(areahash, newPath, area2, max - 1));
//		}
//		return tmpmax + 1;
//
//		////		HashMap<PoliceForce, ArrayList<Task>> tasks = ((PoliceForceAgent) model().sosAgent()).getState(OpenHighwayesState.class).tasks;
//		//		ArrayList<Area> areas = new ArrayList<Area>();
//		//		ArrayList<Road> roads = ((PoliceForceAgent) model().sosAgent()).getState(OpenHighwayesState.class).Highways;
//		//		for (Road road : roads) {
//		//			areas.add(road);
//		//		}
//		////		for (ArrayList<Task> task : tasks.values()) {
//		////			for (Task task2 : task) {
//		////				areas.add(task2.getAreaPosition());
//		////			}
//		////		}
//		//		
//		//		FireDisasterSpace model = (FireDisasterSpace) model();
//		//		setEntities(((PoliceForceAgent) model().sosAgent()).getState(OpenHighwayesState.class).Highways);
//		/*
//		 * FOR: for (Road road : model.roads()) {
//		 * if (road.getGraphEdges().length > 6) {
//		 * for (short geindex : road.getGraphEdges()) {
//		 * GraphEdge ge = model.graphEdges().get(geindex);
//		 * if (ge.getState() == GraphEdgeState.FoggyBlock)
//		 * continue FOR;
//		 * }
//		 * for (Area area : road.getNeighbours()) {
//		 * if(area instanceof Building)
//		 * continue FOR;
//		 * }
//		 * areas.add(road);
//		 * }
//		 * }
//		 */
//		//				for (BuildingBlock island : model.buildingBlocks()) {
//		//
//		//					ArrayList<java.awt.Point> points = new ArrayList<java.awt.Point>();
//		//					for (int i = 0; i < island.buildings().size(); i++) {
//		//						points.add(new java.awt.Point(island.buildings().get(i).getX(), island.buildings().get(i).getY()));
//		//					}
//		//					ConvexHull convexHull = new ConvexHull();
//		//					points =convexHull.makeQuickHull(points);
//		//					
//		//					break;
//		//				}
//		//		setEntities(areas);
//
//	}
//
//	@Override
//	protected Shape render(Area entity, Graphics2D g, ScreenTransform transform) {
//		g.setColor(Color.black);
//		g.setStroke(new BasicStroke(2));
//
//		Shape transformShape = NamayangarUtils.transformShape(entity, transform);
//		g.fill(transformShape);
//		g.setColor(Color.yellow);
////		g.drawString(entity.getID().toString(), transform.xToScreen(entity.getX()), transform.yToScreen(entity.getY()));
//		g.setStroke(new BasicStroke(1));
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	int LENGHT = 5;
//	int VALIDCOUNT = 3;
//
//	@Override
//	public JComponent getGUIComponent() {
//		JPanel p = new JPanel();
//		final JTextField jtf = new JTextField(10);
//		jtf.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					LENGHT = Integer.parseInt(jtf.getText());
//					makeEntities();
//					component.repaint();
//				} catch (Exception ex) {
//				}
//			}
//		});
//		p.add(jtf);
//		final JTextField jtf2 = new JTextField(10);
//		jtf2.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					VALIDCOUNT = Integer.parseInt(jtf2.getText());
//					makeEntities();
//					component.repaint();
//				} catch (Exception ex) {
//				}
//			}
//		});
//		p.add(jtf2);
//		return p;
//	}
//
//	@Override
//	public boolean isValid() {
//		// TODO Auto-generated method stub
//		return false;//model() instanceof FireDisasterSpace;
//	}
//
//	@Override
//	public ArrayList<Pair<String, String>> sosInspect(Area entity) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
