package sos.base.util.namayangar.sosLayer.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.move.types.PoliceReachablityMove;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class MoveCost extends SOSAbstractToolsLayer<Area>{

	public MoveCost() {
		super(Area.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 100;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().areas());
	}

	@Override
	protected Shape render(Area entity, Graphics2D g, ScreenTransform transform) {
		if(model().sosAgent().move.isReallyUnreachableXYPolice(entity.getPositionPair()))
			g.setColor(Color.red);
		else
			g.setColor(Color.green);
		
		NamayangarUtils.drawString(model().sosAgent().move.getWeightTo(entity, PoliceReachablityMove.class)+"", g, transform, entity);
		g.setStroke(new BasicStroke(2));
		NamayangarUtils.drawEntity(entity, g, transform);
		g.setStroke(new BasicStroke(1));
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Area entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}
}
