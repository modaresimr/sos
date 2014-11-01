package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class FireSelectBuildingScore extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {

	public FireSelectBuildingScore() {
		super(Building.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 20;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.green);
		if (entity.priority() != 0)
			g.drawString(entity.priority() + "", transform.xToScreen(entity.x()), transform.yToScreen(entity.y()));
		return null;
	}

	JTextArea textArea = new JTextArea();
	public JTable tabel;

	@Override
	public JComponent getGUIComponent() {
		selectedBuilding = model().buildings().get(0);
		tabel = new JTable(tbModel);
		return tabel;
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		ArrayList<Pair<String, String>> inspect = new ArrayList<Pair<String, String>>();
		return inspect;
	}

	private Building selectedBuilding;
	private EntityTableModel tbModel = new EntityTableModel();

	@Override
	public void objectSelected(SOSSelectedObj sso) {
		if (sso == null) {
			selectedBuilding = null;
		} else {
			if (sso.getObject() instanceof Building) {
				selectedBuilding = (Building) sso.getObject();
				textArea.setText("");
				tbModel.setEntity(selectedBuilding);
			} else
				selectedBuilding = null;

		}
	}

	@Override
	public void preCompute() {
		getViewer().getViewerFrame().addSelectedObjectListener(this);
		super.preCompute();
	}

	private class EntityTableModel extends AbstractTableModel {
		Building selected;

		private void updateProperty() {
			fireTableDataChanged();
		}

		public void setEntity(Building entity) {
			if (tabel != null && entity.scoreData.size()>0)
			{
				
				if (entity.scoreData.get(0).second() != entity.model().time())
					tabel.setBackground(Color.red);
				else
					tabel.setBackground(Color.white);
			}
			selected = entity;
			updateProperty();
			fireTableStructureChanged();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (selected == null)
				return 0;
			return selected.scoreData.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0)
				return selected.scoreData.get(row).first();
			else
				return selected.scoreData.get(row).second();
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
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
