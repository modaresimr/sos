package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class ConnectedBuildings extends SOSAbstractToolsLayer<List<Building>> {

	public ConnectedBuildings() {
		super(null);
		setVisible(true);
	}

	@Override
	public int getZIndex() {
		return 15;
	}

	private ArrayList<Building> getConnectedBuildings(Building b, boolean[] traveresedBuildings) {
		ArrayList<Building> result = new ArrayList<Building>();
		if (traveresedBuildings[b.getBuildingIndex()])
			return result;
		result.add(b);
		traveresedBuildings[b.getBuildingIndex()] = true;
		for (Area area : b.getNeighbours()) {
			if (area instanceof Building)
				result.addAll(getConnectedBuildings((Building) area, traveresedBuildings));
		}
		return result;
	}

	@Override
	protected void makeEntities() {

		boolean[] traveresedBuildings = new boolean[model().buildings().size()];
		ArrayList<List<Building>> blocks = new ArrayList<List<Building>>();
		int onlyOneBlock=0;
		for (Building b : model().buildings()) {
			if (traveresedBuildings[b.getBuildingIndex()])
				continue;
			
			ArrayList<Building> block = getConnectedBuildings(b, traveresedBuildings);
			if(block.size()==1)
				onlyOneBlock++;
			else
				blocks.add(block);
		}
		setEntities(blocks);
		System.out.println("blocks count="+blocks.size()+" building count="+model().buildings().size()+" only one blocks="+onlyOneBlock);
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	protected Shape render(List<Building> entity, Graphics2D g, ScreenTransform transform) {
		int c = entity.get(0).getBuildingIndex() + 1;
		
		Color color = new Color(Math.abs(c * 25) % 255, Math.abs(17 * c) % 255, Math.abs(34 * c) % 255);
		g.setColor(color);

		for (Building building : entity) {

			Shape shape = NamayangarUtils.transformShape(building, transform);
			g.fill(shape);
		}
		g.setColor(color.darker().darker());
		NamayangarUtils.drawString(entity.size()+"", g, transform, entity.get(0));
		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(List<Building> entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}
