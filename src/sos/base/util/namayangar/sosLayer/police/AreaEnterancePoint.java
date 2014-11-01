package sos.base.util.namayangar.sosLayer.police;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;

import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.move.Path;
import sos.base.move.types.PoliceMove;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.police_v2.base.worldModel.PoliceWorldModel;

public class AreaEnterancePoint extends SOSAbstractToolsLayer<Building> {

	public AreaEnterancePoint() {
		super(Building.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 900;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.GREEN);
		Pair<Area, Point2D> select = getEntrancePoint(entity);
		if (select != null)
			NamayangarUtils.paintPoint2D(select.second(), transform, g);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return model() instanceof PoliceWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return LayerType.Police;
	}

	public Pair<Area, Point2D> getEntrancePoint(Area dest) {
		if (dest == null) {
			return null;
		}
		Path path = world.me().getAgent().move.getPathTo(Collections.singleton(dest), PoliceMove.class);
		ArrayList<EntityID> pathArray = path.getIds();
		if (pathArray.isEmpty()) {
			return null;
		}

		Edge inComingEdge;
		if (pathArray.size() == 1) {
			inComingEdge = dest.getEdgeTo(world.me().getAgent().location().getID());
		} else {
			inComingEdge = dest.getEdgeTo(pathArray.get(pathArray.size() - 2));
		}
		if (inComingEdge == null)
			return new Pair<Area, Point2D>(dest, dest.getPositionPoint());
		Line2D wallLine = inComingEdge.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());

		Vector2D offset;
		if (AliGeometryTools.havecorrectDirection(dest)) {
			offset = wallLine.getDirection().getNormal().normalised().scale(10);
		} else {
			offset = wallLine.getDirection().getNormal().normalised().scale(-10);
		}
		Point2D destXY = inComingEdge.getMidPoint().plus(offset);
		return new Pair<Area, Point2D>(dest, destXY);
	}
}
