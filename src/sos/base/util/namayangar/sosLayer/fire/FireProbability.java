package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;

public class FireProbability extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {

	public FireProbability() {
		super(Building.class);
		setVisible(true);
	}

	@Override
	public void preCompute() {
		super.preCompute();
		getViewer().getViewerFrame().addSelectedObjectListener(this);

	}

	@Override
	public int getZIndex() {
		return 6;
	}

	@Override
	protected void makeEntities() {
		setEntities((model()).sosAgent().fireProbabilityChecker.getProbabilisticFieryBuilding());
	}

	JTextArea tes = new JTextArea();

	@Override
	public JComponent getGUIComponent() {
		return tes;
	}

	@Override
	public boolean isValid() {
		return model().me() instanceof Human;
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.green);
		if (entity.getValuSpecialForFire() > 0)
			g.setColor(Color.red);
		Shape shape = NamayangarUtils.transformShape(entity, transform);
		g.fill(shape);
		g.setFont(new Font("serif", Font.ITALIC, 10));
		g.setColor(Color.black);
		NamayangarUtils.drawString(entity.getValuSpecialForFire() + "", g, transform, entity);
		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}

	private Building selectedBuilding;

	@Override
	public void objectSelected(SOSSelectedObj sso) {
		if (sso == null) {
			selectedBuilding = null;
		} else {
			if (sso.getObject() instanceof Building) {
				selectedBuilding = (Building) sso.getObject();
				tes.setText(selectedBuilding.ss);
			} else
				selectedBuilding = null;

		}
	}

}
