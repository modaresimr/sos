package sos.police_v2.clearableBlockadeToReachable;

import java.awt.geom.Path2D;
import java.util.ArrayList;

import rescuecore2.worldmodel.EntityID;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.move.Path;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceForceAgent;
import sos.tools.GraphEdge;
import sos.tools.geometry.SOSGeometryTools;


public class ReachablingWithEdge extends ClearableBlockadeToReachable{

	public ReachablingWithEdge(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public ArrayList<Blockade> getBlockingBlockadeOfPath(Path path){
		GraphEdge[] allMoveEdges = path.getEdges();
		log.info("making reachable to " + path+" using"+this.getClass().getSimpleName());
		if (allMoveEdges == null) {
			log.info(" I am on the right position and the path is null " + path);
			return new ArrayList<Blockade>();
		}
		Area blockArea = null;
		Area position = null;
		GraphEdge blockEdge = null;
		
		
		
		for (GraphEdge graphEdge : allMoveEdges) {
			if (graphEdge instanceof WorldGraphEdge) {
				position = agent.model().areas().get(((WorldGraphEdge) graphEdge).getInsideAreaIndex());
			}
			
			if (position == null)
				position = getAreaOfAGraphEdge(graphEdge);
			
			if (position == null) {
				for (EntityID id : path.getIds()) {
					for (short myEdge : agent.model().getEntity(id).getAreaPosition().getGraphEdges()) {
						if (myEdge == graphEdge.getIndex())
							position = (Area) agent.model().getEntity(id);
						break;
					}
					if (position != null)
						break;
				}
			}

			if (position != null) {
				if (SOSGeometryTools.distance(agent.location(), position) < PoliceConstants.moveDistance) {
					if (isRealyBlock(graphEdge, position)) {
						blockArea = position;
						blockEdge = graphEdge;
						break;
					}
				}
			} else
				log.debug(" in graph edge virtual graph edge nis... bara hamin area position nulle ");//TODO ghablan console debug bood
		}
		if (blockArea != null) {
			ArrayList<Blockade> myBlockades = new ArrayList<Blockade>();
			if (blockArea.isBlockadesDefined() && blockArea.getBlockades().size() > 0)
				for (Blockade blockade : blockArea.getBlockades()) {
					if (haveIntersect(blockEdge, blockade))
						myBlockades.add(blockade);
				}
			return myBlockades;
		} else
			log.debug("hich blockade E dar 200 metri peida nakarde");

		return new ArrayList<Blockade>();

	}
	public Path2D findGeoPathInMove(ArrayList<Area> position) {

		Path2D geopath = new Path2D.Double();
		geopath.moveTo(agent.me().getX(), agent.me().getY());
		Path path = agent.informationStacker.getLastMovePath();
		
		GraphEdge[] allMoveEdges = path.getEdges();
		position.add(path.getSource().first());
		if (allMoveEdges == null) {
			geopath.lineTo(path.getDestination().second().getX(), path.getDestination().second().getY());
		} else {
			int index = 2;
			for (int i = 0; i < allMoveEdges.length; i++) {

				GraphEdge edge = allMoveEdges[i];
				short tail;
				short head;
				if (isInEdge(edge.getHeadIndex())) {
					head = edge.getHeadIndex();
					tail = edge.getTailIndex();
				} else {
					tail = edge.getHeadIndex();
					head = edge.getTailIndex();
				}
				Node start = agent.model().nodes().get(head);
				Node end = agent.model().nodes().get(tail);

				geopath.lineTo(start.getPosition().getX(), start.getPosition().getY());
				geopath.lineTo(end.getPosition().getX(), end.getPosition().getY());

				if (edge instanceof WorldGraphEdge) {
					Area area = agent.model().areas().get(((WorldGraphEdge) edge).getInsideAreaIndex());
					if(area.getLastSenseTime()==agent.time())
						position.add(area);
					index--;
					if (index == 0)
						break;
					//					break;
				}
			}
			if (position.size() <= 2) {
				if(path.getDestination().first().getLastSenseTime()==agent.time())
				position.add(path.getDestination().first());
				geopath.lineTo(path.getDestination().second().getX(), path.getDestination().second().getY());
			}
		}
		return geopath;

	}

	private boolean isInEdge(short headIndex) {

		for (int i = 0; i < agent.me().getAreaPosition().getPassableEdges().length; i++) {
			Edge ed = agent.me().getAreaPosition().getPassableEdges()[i];
			if (headIndex == ed.getNodeIndex())
				return true;
		}
		return false;
	}

	private Area getAreaOfAGraphEdge(GraphEdge graphEdge) {
		Area position = null;
		for (Area area : agent.model().getObjectsInRange(agent.me(), 30000,Area.class)) {
			for (short myEdge : area.getGraphEdges()) {
				if (myEdge == graphEdge.getIndex())
					position = area;
				break;
			}
			if (position != null)
				break;
		}
		return position;
	}
	private boolean haveIntersect(GraphEdge graphEdge, Blockade blockade) {
		Path2D geoGraph = new Path2D.Double();
		geoGraph.moveTo(agent.model().nodes().get(graphEdge.getHeadIndex()).getPosition().getX(), agent.model().nodes().get(graphEdge.getHeadIndex()).getPosition().getY());
		geoGraph.lineTo(agent.model().nodes().get(graphEdge.getTailIndex()).getPosition().getX(), agent.model().nodes().get(graphEdge.getTailIndex()).getPosition().getY());
		geoGraph.closePath();
		if (SOSGeometryTools.haveIntersection(blockade.getExpandedBlock().getShape(), geoGraph))
			return true;
		return false;
	}

	private boolean isRealyBlock(GraphEdge graphEdge, Area position) {
		Path2D geoGraph = new Path2D.Double();
		geoGraph.moveTo(agent.model().nodes().get(graphEdge.getHeadIndex()).getPosition().getX(), agent.model().nodes().get(graphEdge.getHeadIndex()).getPosition().getY());
		geoGraph.lineTo(agent.model().nodes().get(graphEdge.getTailIndex()).getPosition().getX(), agent.model().nodes().get(graphEdge.getTailIndex()).getPosition().getY());
		geoGraph.closePath();
		if (graphEdge.getState() == GraphEdgeState.Block)
			return true;
		if (graphEdge.getState() == GraphEdgeState.Open) {
			if (position.isBlockadesDefined() && position.getBlockades().size() > 0)
				for (Blockade blockade : position.getBlockades()) {
					if (SOSGeometryTools.haveIntersection(blockade.getExpandedBlock().getShape(), geoGraph))
						return true;
				}
		}
		return false;
	}


}
