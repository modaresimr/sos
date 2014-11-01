package sos.base.util.namayangar.sosLayer.fire;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSInspector;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;

public class VirtualData2 extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {
	private static final Color HEATING = new Color(176, 176, 56, 128);
	private static final Color BURNING = new Color(204, 122, 50, 128);
	private static final Color INFERNO = new Color(160, 52, 52, 128);
	private static final Color WATER_DAMAGE = new Color(50, 120, 130, 128);
	private static final Color MINOR_DAMAGE = new Color(100, 140, 210, 128);
	private static final Color MODERATE_DAMAGE = new Color(100, 70, 190, 128);
	private static final Color SEVERE_DAMAGE = new Color(80, 60, 140, 128);
	private static final Color BURNT_OUT = new Color(0, 0, 0, 255);

	public VirtualData2() {
		super(Building.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 50;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape shape = NamayangarUtils.transformShape(entity, transform);
		if (entity.virtualData[1] == null) {
			g.setColor(Color.black);
			g.fill(shape);
			return shape;
		}
		switch (entity.virtualData[1].getFieryness()) {
		case 0:
			g.setColor(Color.black);
			return null;
		case 1:
			g.setColor(HEATING);
			break;
		case 2:
			g.setColor(BURNING);
			break;
		case 3:
			g.setColor(INFERNO);
			break;
		case 4:
			g.setColor(WATER_DAMAGE);
			break;
		case 5:
			g.setColor(MINOR_DAMAGE);
			break;
		case 6:
			g.setColor(MODERATE_DAMAGE);
			break;
		case 7:
			g.setColor(SEVERE_DAMAGE);
			break;
		case 8:
			g.setColor(BURNT_OUT);
			break;
		}
		g.fill(shape);
		return shape;
	}

	SOSInspector myInspector = new SOSInspector();

	@Override
	public JComponent getGUIComponent() {
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(myInspector);
		return jp;
	}

	@Override
	public boolean isValid() {
		return true;
		//		return model() instanceof FireDisasterSpace;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		ArrayList<Pair<String, String>> inspect = new ArrayList<Pair<String, String>>();
		return inspect;
	}

	private Building selectedBuilding;

	@Override
	public void objectSelected(SOSSelectedObj sso) {
		if (sso == null) {
			selectedBuilding = null;
		} else {
			if (sso.getObject() instanceof Building) {
				selectedBuilding = (Building) sso.getObject();
				myInspector.inspect(selectedBuilding.virtualData[0].getData());
			} else
				selectedBuilding = null;

		}
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
