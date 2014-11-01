package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.FireBrigade;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class LastActDoneByThisFire extends SOSAbstractToolsLayer<FireBrigade> {

	public LastActDoneByThisFire() {
		super(FireBrigade.class);
	}
	@Override
	public int getZIndex() {
		return 1000;
	}

	@Override
	protected void makeEntities() {
		if (model().me() instanceof FireBrigade)
			setEntities(Arrays.asList((FireBrigade) model().me()));
	}

	@Override
	protected Shape render(FireBrigade entity, Graphics2D g, ScreenTransform transform) {
		FireBrigadeAgent agent = ((FireBrigadeAgent) entity.getAgent());
		if(model().time()<3)
			return null;
		String lastState = "State= "+agent.FDK.lastState + "| LastAct= "+agent.FDK.lastAct +" ("+ agent.informationStacker.getInformations(1).getAct() + " | Last cycle Exception= " + entity.getAgent().lastException;
		
		g.setFont(new Font("arial", Font.PLAIN, 30));
		g.setColor(Color.yellow);
		g.drawString(lastState, 30, 30);
		g.setFont(new Font("arial", Font.PLAIN, 15));
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
		return model() instanceof FireWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(FireBrigade entity) {
		if(model().time()<3)
			return null;
		ArrayList<Pair<String, String>> pair = new ArrayList<Pair<String, String>>();
		FireBrigadeAgent agent = ((FireBrigadeAgent) entity.getAgent());
		pair.add(new Pair<String, String>("LastState", "UNDEFINE"));
		pair.add(new Pair<String, String>("LastAct", agent.informationStacker.getInformations(1).getAct()+""));
		pair.add(new Pair<String, String>("LastException", agent.lastException + ""));

		return pair;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
