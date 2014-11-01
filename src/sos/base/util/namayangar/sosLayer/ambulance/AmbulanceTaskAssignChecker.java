package sos.base.util.namayangar.sosLayer.ambulance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.fire_v2.base.worldmodel.FireWorldModel;
import sos.police_v2.base.worldModel.PoliceWorldModel;

public class AmbulanceTaskAssignChecker extends SOSAbstractToolsLayer<AmbulanceTeam>{

	public AmbulanceTaskAssignChecker() {
		super(AmbulanceTeam.class);
	}
	@Override
	public int getZIndex() {
		return 100;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().ambulanceTeams());
	}

	@Override
	protected Shape render(AmbulanceTeam entity, Graphics2D g, ScreenTransform transform) {
		Human target = entity.getWork().getTarget();
		if(target==null || !target.isPositionDefined())
			return null;
		g.setStroke(new BasicStroke(2));
		int c=entity.getAmbIndex();
		g.setColor(new Color(Math.abs(c * 25) % 255, Math.abs(17 * c) % 255, Math.abs(34 * c) % 255));
		
		NamayangarUtils.drawLine(entity, target, g, transform);
		Line2D shape = NamayangarUtils.transformLine(entity, target,  transform);
		NamayangarUtils.drawEntity(entity, g, transform);
		NamayangarUtils.drawEntity(target, g, transform);
		return shape;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return !(model() instanceof FireWorldModel||model() instanceof PoliceWorldModel);
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(AmbulanceTeam entity) {
		ArrayList<Pair<String, String>> in=new ArrayList<Pair<String,String>>();
		in.add(new Pair<String, String>("target", entity.getWork().getTarget()+""));
		return in;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Ambulance;
	}
}
