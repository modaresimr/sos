package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;

public class TemperatureTransmition extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {

	public TemperatureTransmition() {
		super(Building.class);
		setVisible(true);
	}

	@Override
	public int getZIndex() {
		return 8;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape sh = NamayangarUtils.transformShape(entity, transform);
		//		if(entity==selectedBuilding){
		double energy = entity.virtualData[0].getRadiationEnergy();
		double affect = 0;
		for (Entry<Short, Float> bv : entity.real_neighbors_BuildValue().entrySet()) {
			int fn = entity.model().buildings().get(bv.getKey()).virtualData[0].getFieryness();
			if (fn == 0 || fn > 3 && fn < 7)
				affect += energy * bv.getValue() / entity.model().buildings().get(bv.getKey()).virtualData[0].getCapacity();
		}
		g.setColor(Color.BLUE);
		g.drawString((int) (affect) + "", transform.xToScreen(entity.x()), transform.yToScreen(entity.y()));

		//		}

		return sh;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
		//		return model() instanceof FireDisasterSpace;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		ArrayList<Pair<String, String>> inspect = new ArrayList<Pair<String, String>>();
		return inspect;
	}

	//	private Building selectedBuilding;

	@Override
	public void objectSelected(SOSSelectedObj sso) {
		//		if (sso == null) {
		//			selectedBuilding = null;
		//		} else {
		//			if (sso.getObject() instanceof Building) {
		//				selectedBuilding = (Building) sso.getObject();
		//			} else
		//				selectedBuilding = null;
		//
		//		}
	}

	@Override
	public void preCompute() {
		getViewer().getViewerFrame().addSelectedObjectListener(this);
		super.preCompute();
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
