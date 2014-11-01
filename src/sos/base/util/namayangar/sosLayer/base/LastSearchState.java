package sos.base.util.namayangar.sosLayer.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.PlatoonAgent;
import sos.base.entities.Human;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class LastSearchState extends SOSAbstractToolsLayer<Human> {

	public LastSearchState() {
		super(Human.class);
		setVisible(true);
	}

	@Override
	public int getZIndex() {
		return 89;
	}

	@Override
	protected void makeEntities() {
		setEntities(Arrays.asList((Human) model().me()));
	}

	@Override
	protected Shape render(Human entity, Graphics2D g, ScreenTransform transform) {
		PlatoonAgent agent = ((PlatoonAgent) entity.getAgent());
		String lastState = "Last Search State= " + agent.newSearch.searchType.name();
		g.setFont(new Font("arial", Font.PLAIN, 30));
		g.setColor(Color.yellow);
		g.drawString(lastState, 30, 80);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Human entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}
