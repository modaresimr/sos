package sos.base.reachablity.tools;


public class ReachablityTester {
/*	
	// Morteza2011*****************************************************************
	public static void checkExpandedArea(PoliceForceAgent agent) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (Road r : agent.model().roads()) {
			testList.add(r.getExpandedArea().getEdges());
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}
	
	// Morteza2011*****************************************************************
	public static void checkExpandedBlockades(PoliceForceAgent agent) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (Road r : agent.model().roads()) {
			for (SOSBlockade b : r.getMiddleBlockades()) {
				testList.add(b.getExpandedBlock().getEdges());
		}
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}
	
	// Morteza2011*****************************************************************
	public static void checkExpandedBlockade(PoliceForceAgent agent, EntityID road) {
		if (agent.model().getEntity(road) instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Blockade> blocks = ((Road) agent.model().getEntity(road)).getBlockades();
			if (blocks == null)
				return;
			for (Blockade b : blocks) {
				testList.add(b.getExpandedBlock().getEdges());
				if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
					agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
			}
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkMergedBlockades(PoliceForceAgent agent) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (Road r : agent.model().roads()) {
			for (SOSArea blockade : r.getMergedBlockades()) {
				testList.add(blockade.getEdges());
			}
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}
	
	// Morteza2011*****************************************************************
	public static void checkReachableParts(PoliceForceAgent agent) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (Road r : agent.model().roads()) {
			for (SOSArea rp : r.getReachableParts()) {
				testList.add(rp.getEdges());
			}
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}
	
	// Morteza2011*****************************************************************
	public static void checkReachableEdges(PoliceForceAgent agent) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (Road r : agent.model().roads()) {
			if (r.getBlockades() == null)
				continue;
			for (ArrayList<EdgeElement> list : r.getReachableEdges()) {
				List<Edge> edgeList = new ArrayList<Edge>();
				for (EdgeElement ee : list) {
					Edge e = new Edge(ee.getStart(), ee.getEnd());
					edgeList.add(e);
				}
				testList.add(edgeList);
			}
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}
	
	// Morteza2011*****************************************************************
	public static void checkMyLocationsReachableEdges(PoliceForceAgent agent) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		if (agent.location() instanceof Road) {
			Road r = (Road) agent.location();
			ArrayList<EdgeElement> list = r.getReachableEdges().get(0);
			List<Edge> edgeList = new ArrayList<Edge>();
			for (EdgeElement ee : list) {
				Edge e = new Edge(ee.getStart(), ee.getEnd());
				edgeList.add(e);
			}
			testList.add(edgeList);
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkPoliceReachablity_P2P(PoliceForceAgent agent) {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			Point2D start = new Point2D(((Road) agent.location()).getX() - 15000, ((Road) agent.location()).getY());
			Point2D end = new Point2D(((Road) agent.location()).getX() + 15000, ((Road) agent.location()).getY());
			test.add(new Edge(new Point2D(start.getX() - 1000, start.getY()), new Point2D(start.getX() + 1000, start.getY())));
			test.add(new Edge(new Point2D(start.getX(), start.getY() - 1000), new Point2D(start.getX(), start.getY() + 1000)));
			test.add(new Edge(new Point2D(end.getX() - 1000, end.getY() - 1000), new Point2D(end.getX() + 1000, end.getY() + 1000)));
			test.add(new Edge(new Point2D(end.getX() - 1000, end.getY() + 1000), new Point2D(end.getX() + 1000, end.getY() - 1000)));
			testList.add(test);
			for (Blockade b : PoliceReachablity.clearableBlockades((Road) agent.location(), start, end))
				testList.add(b.getEdges());
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkPoliceReachablity_E2E(PoliceForceAgent agent) {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			Edge start = new Edge(new Point2D(26807.0, 122150.0), new Point2D(33000.0, 117000.0));
			Edge start2 = new Edge(new Point2D(34000.0, 116998.0), new Point2D(37000.0, 116992.0));
			Edge end = new Edge(new Point2D(108690.0, 116840.0), new Point2D(111310.0, 123160.0));
			test.add(start);
			test.add(end);
			testList.add(test);
			for (Blockade b : PoliceReachablity.clearableBlockades((Road) agent.location(), start2, end))
				testList.add(b.getEdges());
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkPoliceReachablity_P2E(PoliceForceAgent agent) throws SOSActionException {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			Point2D start = new Point2D(((Road) agent.location()).getX(), ((Road) agent.location()).getY());
			Edge end = new Edge(new Point2D(126840.0, 46000.0), new Point2D(126840.0, 48000.0));
			test.add(new Edge(new Point2D(start.getX() - 1000, start.getY()), new Point2D(start.getX() + 1000, start.getY())));
			test.add(new Edge(new Point2D(start.getX(), start.getY() - 1000), new Point2D(start.getX(), start.getY() + 1000)));
			test.add(end);
			testList.add(test);
			ArrayList<Blockade> blocks = PoliceReachablity.clearableBlockades((Road) agent.location(), start, end);
			for (Blockade b : blocks)
				testList.add(b.getEdges());
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
			System.out.println(blocks);
			// if (blocks != null && blocks.size() > 0)
			// agent.clear(blocks.get(0));

		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkPoliceReachablity_E2P(PoliceForceAgent agent) {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			Edge start = new Edge(new Point2D(108690.0, 116840.0), new Point2D(111310.0, 123160.0));
			Point2D end = new Point2D(((Road) agent.location()).getX() - 15000, ((Road) agent.location()).getY());
			test.add(new Edge(new Point2D(end.getX() - 1000, end.getY()), new Point2D(end.getX() + 1000, end.getY())));
			test.add(new Edge(new Point2D(end.getX(), end.getY() - 1000), new Point2D(end.getX(), end.getY() + 1000)));
			test.add(start);
			testList.add(test);
			for (Blockade b : PoliceReachablity.clearableBlockades((Road) agent.location(), start, end))
				testList.add(b.getEdges());
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkIsReachable_E2E(PoliceForceAgent agent) {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			List<Edge> edges = ((Road) agent.location()).getEdges();
			Edge start = edges.get(8);
			Edge end = edges.get(0);
			test.add(start);
			test.add(end);
			if (Reachablity.isReachable((Road) agent.location(), start, end) == ReachablityState.Open) {
				test.add(new Edge(start.getMidPoint(), end.getMidPoint()));
			}
			testList.add(test);
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkIsReachable_P2E(PoliceForceAgent agent) {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			List<Edge> edges = ((Road) agent.location()).getEdges();
			Point2D start = new Point2D(((Road) agent.location()).getX() - 15000, ((Road) agent.location()).getY());
			Edge end = edges.get(8);
			test.add(new Edge(new Point2D(start.getX() - 1000, start.getY()), new Point2D(start.getX() + 1000, start.getY())));
			test.add(new Edge(new Point2D(start.getX(), start.getY() - 1000), new Point2D(start.getX(), start.getY() + 1000)));
			test.add(end);
			testList.add(test);
			if (Reachablity.isReachable((Road) agent.location(), start, end) == ReachablityState.Open) {
				test.add(new Edge(start, end.getMidPoint()));
			}
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
	
	// Morteza2011*****************************************************************
	public static void checkGetReachablePart(FireBrigadeAgent agent) {
		if (agent.location() instanceof Road) {
			ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
			List<Edge> test = new ArrayList<Edge>();
			Point2D p = new Point2D(128000, 44000);
			test.add(new Edge(new Point2D(p.getX() - 1000, p.getY()), new Point2D(p.getX() + 1000, p.getY())));
			test.add(new Edge(new Point2D(p.getX(), p.getY() - 1000), new Point2D(p.getX(), p.getY() + 1000)));
			int index=OtherReachablityTools.getReachablePart((Road) agent.location(), p);
			SOSArea a = ((Road)agent.location()).getReachableParts().get(index);
			if (a != null)
				test.addAll(a.getEdges());
			testList.add(test);
			if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
				agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
		}
	}
*/}
