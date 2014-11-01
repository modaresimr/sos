package sos.base.util.namayangar.sosLayer.search;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Road;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class SearchTarget extends SOSAbstractToolsLayer<Road>{
	public SearchTarget() {
		super(Road.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 20;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().roads());
	}

	@Override
	protected Shape render(Road entity, Graphics2D g, ScreenTransform transform) {
		Shape shape = NamayangarUtils.transformShape(entity, transform);
//		SearchRoad sr=mySearch.getWorld().getSearchRoadByRealRoad(entity);
//		g.setColor(Color.yellow);
//		if(sr.isInEndOfPath()){
//			g.fill(shape);
//			g.setColor(Color.black);
//			g.drawString("val:" + sr.getTargetValue(), transform.xToScreen(entity.getX()), transform.yToScreen(entity.getY()));
//		}
		return shape;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return true;//model().me() instanceof Human;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Road entity) {
		ArrayList<Pair<String, String>> inspect = new ArrayList<Pair<String, String>>();
		return inspect;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}