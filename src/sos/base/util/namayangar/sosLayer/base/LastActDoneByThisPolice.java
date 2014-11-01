package sos.base.util.namayangar.sosLayer.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.PoliceForce;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.base.worldModel.PoliceWorldModel;

public class LastActDoneByThisPolice extends SOSAbstractToolsLayer<PoliceForce> {

	public LastActDoneByThisPolice() {
		super(PoliceForce.class);
	}
	@Override
	public int getZIndex() {
		return 100;
	}

	@Override
	protected void makeEntities() {
		if (model().me() instanceof PoliceForce)
			setEntities(Arrays.asList((PoliceForce) model().me()));
	}

	@Override
	protected Shape render(PoliceForce entity, Graphics2D g, ScreenTransform transform) {
		PoliceForceAgent agent = ((PoliceForceAgent) entity.getAgent());
		if(model().time()<3)
			return null;
		String lastState = agent.lastState+ " | LastAct= " + agent.informationStacker.getInformations(1).getAct() + " | Last cycle Exception= " + entity.getAgent().lastException;
		g.setFont(new Font("arial", Font.PLAIN, 30));
		g.setColor(Color.yellow);
		g.drawString(lastState, 30, 30);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return model() instanceof PoliceWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(PoliceForce entity) {
		if(model().time()<3)
			return null;
		ArrayList<Pair<String, String>> pair = new ArrayList<Pair<String, String>>();
		PoliceForceAgent agent = ((PoliceForceAgent) entity.getAgent());
		pair.add(new Pair<String, String>("LastState", agent.lastState));
		pair.add(new Pair<String, String>("LastAct", agent.informationStacker.getInformations(1).getAct()+""));
		pair.add(new Pair<String, String>("LastException", agent.lastException + ""));

		return pair;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Police;
	}
}
