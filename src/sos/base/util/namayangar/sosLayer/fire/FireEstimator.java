package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;

public class FireEstimator extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {
	private static final Color HEATING = new Color(176, 176, 56, 128);
	private static final Color BURNING = new Color(204, 122, 50, 128);
	private static final Color INFERNO = new Color(160, 52, 52, 128);
	private static final Color WATER_DAMAGE = new Color(50, 120, 130, 128);
	private static final Color MINOR_DAMAGE = new Color(100, 140, 210, 128);
	private static final Color MODERATE_DAMAGE = new Color(100, 70, 190, 128);
	private static final Color SEVERE_DAMAGE = new Color(80, 60, 140, 128);
	private static final Color BURNT_OUT = new Color(0, 0, 0, 255);

	public FireEstimator() {
		super(Building.class);
		setVisible(true);
	}

	@Override
	public int getZIndex() {
		return 5;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape shape = NamayangarUtils.transformShape(entity, transform);
		//		if(entity.virtualData.secondData==null)
		//			return null;

		if (entity.virtualData[0] == null) {
			g.setColor(Color.red);
			g.fill(shape);
			return null;
		}
		if (entity.virtualData[0].cells == null) {
			g.setColor(Color.black);
			g.fill(shape);
			System.out.println("There is a cell with null value!!!!!!!");
			return null;
		}

		switch (entity.virtualData[0].getFieryness()) {
		case 0:
			g.setColor(Color.blue);
			//			g.drawString(entity.virtualData[0].getTemperature() + "", transform.xToScreen(entity.x()), transform.yToScreen(entity.y()));
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
		g.setColor(Color.blue);
		//		g.drawString(entity.virtualData[0].getTemperature() + "", transform.xToScreen(entity.x()), transform.yToScreen(entity.y()));

		return shape;
	}

	@Override
	public JComponent getGUIComponent() {
		selectedBuilding = model().buildings().get(0);
		return new JTable(tbModel);

	}

	@Override
	public boolean isValid() {
		return true;
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
				tbModel.setEntity(selectedBuilding);
			} else
				selectedBuilding = null;

		}
	}
	private EntityTableModel tbModel = new EntityTableModel();

	private static class EntityTableModel extends AbstractTableModel {
		Building selected;
		private NumberFormat format;
		public EntityTableModel() {
			format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(3);

		}
		private void updateProperty() {
			fireTableDataChanged();
		}

		public void setEntity(Building entity) {
			selected = entity;
			updateProperty();
			fireTableStructureChanged();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (selected == null)
				return 0;
			return selected.virtualData[0].getData().size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0)
				return selected.virtualData[0].getData().get(row).first();
			else
				return selected.virtualData[0].getData().get(row).second();
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Property";
			case 1:
				return "Value";
			default:
				throw new IllegalArgumentException("Invalid column: " + col);
			}
		}
	}

	@Override
	public void preCompute() {
		getViewer().getViewerFrame().addSelectedObjectListener(this);
		super.preCompute();
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}
}
