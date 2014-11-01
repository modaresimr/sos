package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import rescuecore2.misc.Pair;
import sos.base.entities.FireBrigade;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class PositioningViewer extends SOSAbstractToolsLayer<FireBrigadeAgent> {

	public PositioningViewer() {
		super(FireBrigadeAgent.class);
	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	@Override
	protected void makeEntities() {
		if (model().me() instanceof FireBrigade) {
			setEntities(Arrays.asList(((FireWorldModel) model()).owner()));
		}
	}

	@Override
	protected Shape render(FireBrigadeAgent entity, Graphics2D g, ScreenTransform transform) {
		if (entity.FDK.getInfoModel().getLastSelectedBuilding() != null) {
			g.setColor(Color.red);
			Shape shape = NamayangarUtils.transformShape(entity.FDK.getInfoModel().getLastSelectedBuilding(), transform);
			g.draw(shape);

		}
		String lastState = "LAST SELECT --> " + " " + "       " + AbstractFireBrigadeAgent.maxDistance;
		g.setFont(new Font("arial", Font.PLAIN, 30));
		g.setColor(Color.yellow);
		g.drawString(lastState, 30, 150);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		return new JScrollPane(((FireWorldModel) model()).owner().positioning.datas, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(FireBrigadeAgent entity) {
		ArrayList<Pair<String, String>> pair = new ArrayList<Pair<String, String>>();

		return pair;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
