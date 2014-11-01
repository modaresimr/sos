package sos.base.util.namayangar.tools;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.AbstractTableModel;

import rescuecore2.misc.Pair;
import sos.base.util.namayangar.SOSWorldModelNamayangar;
import sos.base.util.namayangar.view.ViewLayer;

/**
 * Yoosef
 * A component for inspecting Entities.
 */
public class SOSInspectorGui extends JTable implements MouseListener {
	/**
	  * 
	  */
	private static final long serialVersionUID = 1L;

/*	private static final Comparator<Pair<String, String>> PROPERTY_NAME_COMPARATOR = new Comparator<Pair<String, String>>() {
		@Override
		public int compare(Pair<String, String> o1, Pair<String, String> o2) {
			return o1.first().compareToIgnoreCase(o2.first());
		}
	};
*/	private EntityTableModel model;


	public SOSInspectorGui(SOSWorldModelNamayangar sosWorldModelNamayangar) {
		model = new EntityTableModel(sosWorldModelNamayangar);
		setModel(model);
		addMouseListener(this);
	}

	public void inspect(SOSSelectedObj e) {
		model.setEntity(e);
	}

	private static class EntityTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private SOSSelectedObj e;
		private ArrayList<Pair<String, String>> datas = new ArrayList<Pair<String, String>>();
		private final SOSWorldModelNamayangar sosWorldModelNamayangar;

		public EntityTableModel(SOSWorldModelNamayangar sosWorldModelNamayangar) {
			this.sosWorldModelNamayangar = sosWorldModelNamayangar;
			e = null;
		}

		private void updateProperty() {
			datas.clear();
			for (ViewLayer layer : sosWorldModelNamayangar.viewer.getLayers()) {
				ArrayList<Pair<String, String>> a = layer.inspect(e.getObject());
				if (a != null)
					datas.addAll(a);
			}
			fireTableDataChanged();
//			Collections.sort(datas, PROPERTY_NAME_COMPARATOR);
		}

		public void setEntity(SOSSelectedObj entity) {
			e = entity;
			updateProperty();
			fireTableStructureChanged();
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return datas.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0)
				return datas.get(row).first();
			else
				return datas.get(row).second();
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
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			JTextPane t = new JTextPane();
			t.setText(getValueAt(getSelectedRow(), 0).toString() + "   =====   " + getValueAt(getSelectedRow(), 1).toString());
			t.setEditable(false);
			JScrollPane jsp = new JScrollPane(t);
			jsp.setPreferredSize(new Dimension(600, 100));

			JOptionPane.showMessageDialog(new JTextArea(), jsp);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
