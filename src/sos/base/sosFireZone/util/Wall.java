package sos.base.sosFireZone.util;

import java.awt.Point;

import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.util.IntList;

import com.infomatiq.jsi.Rectangle;

//*******************************************************************************************************************************
public class Wall {

	public static final int MAX_SAMPLE_DISTANCE = 200000;//if not intersected change will be changed to 200000
	public static final int SAMPLE_DISTANCE = 5000;
	public int ownerIndex = -1; // for means of figuring out the walls of a Building
	public int rays;
	public int hits;
	//	public int selfHits;
	//	public int strange;
	public static float RAY_RATE = 0;//set in constructor ..... Building connection precision (smaller number,lesser precision)
	public double length;
	public Point a;
	public Point b;

	//
	// *********************************************************************************************Test:ok***********************
	/**
	 * gets 2 point coordinates and makes a wall object between two points and initializes it's primer properties
	 */
	public Wall(int xx1, int yy1, int xx2, int yy2, Building owner) {
		a = new Point(xx1, yy1);
		b = new Point(xx2, yy2);
		length = a.distance(b);
		rays = (int) Math.ceil(length * RAY_RATE);
		hits = 0;
		if (owner.getAgent().getMapInfo().isBigMap()) {
			RAY_RATE = 0.001f;
			//			SAMPLE_DISTANCE=10000;
		} else
			RAY_RATE = 0.0025f;
		this.ownerIndex = owner.getBuildingIndex();

	}

	// *********************************************************************************************Test:ok***********************
	/** checks if the both coordinates are not as the same */
	public boolean validate() {
		return !(a.x == b.x && a.y == b.y);
	}

	//	static ShapeDebugFrame debug = new ShapeDebugFrame();

	// *********************************************************************************************Test:ok***********************
	/**
	 * finds the hits for setting Building connection value
	 */
	public void findHits(SOSWorldModel world, byte[] connectedBuilding) {
		//		ShapeDebugFrame debug = world.buildings().get(ownerIndex).debug;
		//		int DEBUG_ID = 17066;
		//		ArrayList<Line2D> rayList = null;
		//		if (world.buildings().get(ownerIndex).getID().getValue() == DEBUG_ID)
		//			rayList = new ArrayList<Line2D>();
		//		else
		//			return;
		//		rayList.clear();
		//		tm1.start();

		//		if(owner.getID().getValue()!=53267)
		//			return;
		//		System.out.println("D");
		//		selfHits = 0;
		//		strange = 0;
		boolean first = true;
		for (int emitted = 0; emitted < rays; emitted++) {
			Point start = Utill.getRndPoint(a, b);
			if (start == null) {
				//				strange++;
				continue;
			}

			Point end = Utill.getRndPoint(start, MAX_SAMPLE_DISTANCE, world.buildings().get(ownerIndex));//return the point should not be in building
			//			if (world.buildings().get(ownerIndex).getID().getValue() == DEBUG_ID)
			//				rayList.add(new Line2D(start, end));

			//			Point pointNearStartRay = new Point();
			//			int pointNearStartRayx = start.x+(10*((end.x>start.x)?1:-1));
			//			int pointNearStartRayy = (int) (line1m*(pointNearStartRayx)+linr1c);
			//									//			boolean selfCross =world.buildings().get(ownerIndex).getShape().contains(pointNearStartRayx ,pointNearStartRayy);
			//			boolean selfCross = false;
			//			for (Wall w : owner.walls()) {
			//				if (this.equals(w))
			//					continue;
			//				Point cross = Utill.intersectLowProcess(start, end, w.a, w.b, line1m, linr1c);
			//				if (cross != null){
			//					selfCross = true;
			//					break;
			//				}
			//			}
			if (end == null) {
				////				selfHits++;
				//				emitted--;
				//
			} else {
				double line1m = (start.getY() - end.getY()) / (start.getX() - end.getX());
				double linr1c = line1m * (-start.getX()) + start.getY();

				Wall closest = null;

				//				if(true)
				//				continue;
				//				tm2.start();
				int num = ((MAX_SAMPLE_DISTANCE - 1) / SAMPLE_DISTANCE) + 1;
				int dx = (end.x - start.x) / num;
				int dy = (end.y - start.y) / num;
				//								Line2DShapeInfo line = new ShapeDebugFrame.Line2DShapeInfo(new Line2D(new Point2D(start.x, start.y), new Point2D(end.x, end.y)), "ray", Color.white, false, true);
				boolean[] bd = new boolean[world.buildings().size()];
				//				Arrays.fill(bd, false);
				//				bd[ownerIndex] = true;
				Point newstart = new Point(start.x, start.y);
				Point newend = new Point();
				double minDist = Double.MAX_VALUE;
				bd[ownerIndex] = true;
				for (int i = 0; i < num; i++) {
					//					if(i==num-1)
					//					else
					newend.move(newstart.x + dx, newstart.y + dy);
					//										debug.show("", line,new ShapeDebugFrame.Point2DShapeInfo(new Point2D(end.x, end.y), "end", Color.red, true),
					//												new ShapeDebugFrame.Point2DShapeInfo(new Point2D(start.x, start.y), "start", Color.blue, true));
					Rectangle rect = new Rectangle(newstart.x, newstart.y, newend.x, newend.y);
					//					java.awt.Rectangle rectawt = new java.awt.Rectangle(newstart.x, newstart.y, newend.x-newstart.x, newend.y-newstart.y);
					//										Collection<Building> jlist = world.getObjectsInRectangle(rect, Building.class);
					IntList indexlist = world.getBuildingIndexInRectangle(rect);

					//					jlist.remove(owner);
					//										ArrayList<Building> jlist=new ArrayList<Building>();
					//					for (Building jbuilding : jlist) {
					for (int ii = 0; ii < indexlist.size(); ii++) {
						int bindex = indexlist.get(ii);

						if (bd[bindex])
							continue;
						//						if (!owner.getNearBuildings().contains(jbuilding))
						//							continue;

						//						bd[bindex] = true;//commented az amd
						Building building = world.buildings().get(bindex);
						//												jlist.add(building);
						for (Wall other : building.walls()) {

							if (other == this)
								continue;
							Point cross = Utill.intersectLowProcess(start, newend, other.a, other.b, line1m, linr1c/* , jbuilding */);
							if (cross != null) {
								double d = cross.distanceSq(start);
								if (d < minDist) {
									minDist = d;
									closest = other;
								}
							}
						}
					}
					//					if (world.buildings().get(ownerIndex).getID().getValue() == DEBUG_ID) {
					//						world.buildings().get(ownerIndex).debug.setBackgroundEntities(Color.gray, world.buildings());
					//						ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
					//						shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new Line2D(new Point2D(newstart.x, newstart.y), new Point2D(newend.x, newend.y)), "selected", Color.white, true, false));
					//						shapes.add((closest != null) ? new ShapeDebugFrame.Line2DShapeInfo(new Line2D(closest.a, closest.b), "intersect", Color.black, true, true) : new ShapeDebugFrame.DetailInfo("noting"));
					//						shapes.add(new ShapeDebugFrame.AWTShapeInfo(world.buildings().get(ownerIndex).getShape(), world.buildings().get(ownerIndex) + "", Color.red, true));
					//						shapes.add(new ShapeDebugFrame.AWTShapeInfo(rectawt, "rect", Color.red, true));
					//						shapes.addAll(ShapeDebugFrame.convertToShapeList(Color.green, jlist));
					////						shapes.add(new ShapeDebugFrame.Line2DShapeInfo(rayList, "rays", Color.blue, false, true));
					//						shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new Line2D(start, newend),"ray",Color.blue,false,true));
					//						debug.show("s", shapes);
					//					}
					if (closest != null)
						break;
					newstart.move(newstart.x + dx, newstart.y + dy);

				}
				//				tm2.stop();
				if (closest == null) {
					continue;
				}
				if (closest.ownerIndex == this.ownerIndex) {
					//					selfHits++;
					emitted--;
				}
				//				d+=Math.sqrt(minDist);
				//				count++;
				if (closest != this && closest != null && closest.ownerIndex != this.ownerIndex) {
					hits++;
					//					Integer value = cb.get(closest.owner);
					//					int temp = 0;
					//					if (value != null) {
					//						temp = value.intValue();
					//					}
					//					temp++;
					//					cb.put(closest.owner, temp);
					if (first && (Utill.distance(start.x, start.y, closest.a.x, closest.a.y) < 10 || Utill.distance(start.x, start.y, closest.b.x, closest.b.y) < 10) && (Utill.distance(end.x, end.y, closest.a.x, closest.a.y) < 10 || Utill.distance(end.x, end.y, closest.b.x, closest.b.y) < 10)) {
						hits = rays;
						connectedBuilding[closest.ownerIndex] += rays;
						break;
					}
					connectedBuilding[closest.ownerIndex]++;
					first = false;
				}

			}
		}
		//		if (world.buildings().get(ownerIndex).getID().getValue() == DEBUG_ID)
		//		{
		//			world.buildings().get(ownerIndex).debug.setBackgroundEntities(Color.GRAY, world.buildings());
		//			ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		//			shapes.add(new ShapeDebugFrame.AWTShapeInfo(world.buildings().get(ownerIndex).getShape(), world.buildings().get(ownerIndex) + "", Color.red, true));
		//			for (int i = 0; i < connectedBuilding.length; i++) {
		//				if (connectedBuilding[i] != 0) {
		//					shapes.add(new ShapeDebugFrame.AWTShapeInfo(world.buildings().get(i).getShape(), world.buildings().get(i) + ":" + connectedBuilding[i], Color.yellow, false));
		//				}
		//			}
		//			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(rayList, "rays", Color.blue, false, true));
		//			world.buildings().get(ownerIndex).debug.show("s", shapes);
		//		}
	}

	// *********************************************************************************************Test:ok***********************
	@Override
	public String toString() {
		return "wall (" + a.x + "," + a.y + ")-(" + b.x + "," + b.y + "),length=" + length + "mm, rays=" + rays + " hits:" + hits;
	}

	// ***************************************************************************************************************************
}
