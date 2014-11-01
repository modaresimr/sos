package sos.base.util.namayangar.view;

import static rescuecore2.misc.collections.ArrayTools.convertArrayObjectToString;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.AbstractTableModel;

import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.Property;
import sos.base.entities.StandardEntity;

/**
 * @editor: Ali
 * A component for inspecting Entities.
 */
public class EntityInspector1 extends JTable implements MouseListener {
	 /**
	  * 
	  */
	 private static final long serialVersionUID = 1L;

	 private static final Comparator<Property> PROPERTY_NAME_COMPARATOR = new Comparator<Property>() {
		  @Override
		  public int compare(Property p1, Property p2) {
				return p1.getURN().compareTo(p2.getURN());
		  }
	 };

	 private EntityTableModel model;

	 /**
	  * Create a new EntityInspector.
	  */
	 public EntityInspector1() {
		  model = new EntityTableModel();
		  setModel(model);
		  addMouseListener(this);
	 }

	 /**
	  * Inspect an entity.
	  * 
	  * @param e
	  *            The entity to inspect.
	  */
	 public void inspect(Entity e) {
		  model.setEntity(e);
	 }

	 private static class EntityTableModel extends AbstractTableModel {
		  /**
		   * 
		   */
		  private static final long serialVersionUID = 1L;
		  private Entity e;
		  private List<Property> props;

		  public EntityTableModel() {
				e = null;
				props = new ArrayList<Property>();
		  }

		  public void setEntity(Entity entity) {
				e = entity;
				props.clear();
				if (e != null) {
					 props.addAll(e.getProperties());
					 Collections.sort(props, PROPERTY_NAME_COMPARATOR);
				}
				fireTableStructureChanged();
				fireTableDataChanged();
		  }

		  @Override
		  public int getRowCount() {
				return props.size() + 2;
		  }

		  @Override
		  public int getColumnCount() {
				return 2;
		  }

		  @Override
		  public Object getValueAt(int row, int col) {
				switch (col) {
				case 0:
					 if (row == 0) {
						  return "ID";
					 } else if (row == 1) {
						  return "Type";
					 } else {
						  String[] tmp = props.get(row - 2).getURN().split(":");
						  return tmp[tmp.length - 1].substring(0, 1).toUpperCase()+tmp[tmp.length - 1].substring(1);
					 }
				case 1:
					 if (row == 0) {
						  return e == null ? "" : e.getID();
					 } else if (row == 1) {
						  if (e == null)
								return "";
						  String[] tmp = e.getURN().split(":");
						  return tmp[tmp.length - 1].substring(0, 1).toUpperCase()+tmp[tmp.length - 1].substring(1);
					 } else {
						  Property prop = props.get(row - 2);
						  if (prop.isDefined()) {
								Object value = prop.getValue();
								if (value.getClass().isArray()) {
									 return convertArrayObjectToString(value);
								}
								return value;
						  } else {
								return "Undefined";
						  }
					 }
				default:
					 throw new IllegalArgumentException("Invalid column: " + col);
				}
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
		  // TODO Auto-generated method stub
		  if(e.getClickCount()>1){
				JTextPane t = new JTextPane();
				t.setText(getValueAt(getSelectedRow(), getSelectedColumn()).toString());
				t.setEditable(false);
				JScrollPane jsp=new JScrollPane(t);
				jsp.setPreferredSize(new Dimension(600, 100));
				
		  JOptionPane.showMessageDialog(new JTextArea(), jsp );
		  }
	 }

	 @Override	
	 public void mouseEntered(MouseEvent arg0) {
		  // TODO Auto-generated method stub
		  
	 }

	 @Override
	 public void mouseExited(MouseEvent arg0) {
		  // TODO Auto-generated method stub
		  
	 }

	 @Override
	 public void mousePressed(MouseEvent arg0) {
		  // TODO Auto-generated method stub
		  
	 }

	 @Override
	 public void mouseReleased(MouseEvent arg0) {
		  // TODO Auto-generated method stub
		  
	 }

	public void inspect(Object selectedObject) {
		if(selectedObject instanceof StandardEntity)
			inspect((StandardEntity)selectedObject);
		else
			inspect(null);
	}
}
