package sos.base.util.namayangar.sosLayer.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.entities.AmbulanceTeam;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class LastActDoneByThisAmbulance extends SOSAbstractToolsLayer<AmbulanceTeam> {

	public LastActDoneByThisAmbulance() {
		super(AmbulanceTeam.class);
	}
	@Override
	public int getZIndex() {
		return 100;
	}

	@Override
	protected void makeEntities() {
		if (model().me() instanceof AmbulanceTeam)
			setEntities(Arrays.asList((AmbulanceTeam) model().me()));
	}

	@Override
	protected Shape render(AmbulanceTeam entity, Graphics2D g, ScreenTransform transform) {
		AmbulanceTeamAgent agent = ((AmbulanceTeamAgent) entity.getAgent());
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
		return null;
	}

	@Override
	public boolean isValid() {
		return model().me() instanceof AmbulanceTeam;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(AmbulanceTeam entity) {
		if(model().time()<3)
			return null;
		ArrayList<Pair<String, String>> pair = new ArrayList<Pair<String, String>>();
		AmbulanceTeamAgent agent = ((AmbulanceTeamAgent) entity.getAgent());
		pair.add(new Pair<String, String>("LastState", agent.lastState));
		pair.add(new Pair<String, String>("LastAct", agent.informationStacker.getInformations(1).getAct()+""));
		pair.add(new Pair<String, String>("LastException", agent.lastException + ""));

		return pair;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Ambulance;
	}
}
