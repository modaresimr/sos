package sos.base.util.namayangar.sosLayer.ambulance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.VirtualCivilian;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class VirtualCivilianLayer extends SOSAbstractToolsLayer<VirtualCivilian> {

	private static final int SIZE = 10;

	public VirtualCivilianLayer() {
		super(VirtualCivilian.class);
		setVisible(true);
	}

	@Override
	public int getZIndex() {
		return 30;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().getVirtualCivilians());
	}

	@Override
	protected Shape render(VirtualCivilian entity, Graphics2D g, ScreenTransform transform) {
		//		NamayangarUtils.drawString("b:"+entity.getBuridness()+" dt:"+entity.getDeathTime())+" t:"+sentity.getTimeInLowComValidCivilianCount(), g, transform, entity);
		int x = transform.xToScreen(entity.getPosition().getX() );
		int y = transform.yToScreen(entity.getPosition().getY() );
		Shape shape = new Ellipse2D.Double(x- SIZE / 2, y - SIZE / 2, SIZE, SIZE);
		g.setColor(Color.yellow);
		g.fill(shape);
		//		g.setColor(getColour(h));
		g.setStroke(new BasicStroke(3));
		if (!entity.isReallyReachable()) {
			g.setColor(Color.red);
			if (entity.getPosition().isReallyReachable(true))
				g.setColor(Color.yellow);
		} else {
			g.setColor(Color.green);
		}

		g.draw(shape);
		g.setStroke(new BasicStroke(1));
		return shape;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return model().sosAgent().messageSystem.type == Type.LowComunication;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(VirtualCivilian entity) {
		ArrayList<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
		result.add(new Pair<String, String>("DeathTime", entity.getDeathTime() + ""));
		result.add(new Pair<String, String>("BuriedNess", entity.getBuridness() + ""));
		result.add(new Pair<String, String>("IsReallyReachable", entity.isReallyReachable() + ""));
		result.add(new Pair<String, String>("Position", entity.getPosition() + ""));
		return result;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Ambulance;
	}
}
