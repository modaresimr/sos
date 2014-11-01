package sos.base.util.namayangar.sosLayer.other;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.areaValues.AbstractAreaValue;
import sos.base.util.namayangar.sosLayer.areaValues.FireUpdateValues;
import sos.base.util.namayangar.sosLayer.areaValues.SearchValues;
import sos.base.util.namayangar.sosLayer.areaValues.VerticalBagLayout;
import sos.base.util.namayangar.tools.LayerType;

/**
 * @author Salim
 */
public class AllValueLayer extends SOSAbstractToolsLayer<Entry> {
	private ArrayList<Entry> selectedAreas = new ArrayList<Entry>();
	private ArrayList<AbstractAreaValue> checkers = new ArrayList<AbstractAreaValue>();
	private AllValuePanel panel;

	public AllValueLayer() {
		super(Entry.class);
		makeCheckers();
		setPanel(new AllValuePanel(this));
		
	}


	private void makeCheckers() {
		checkers.add(new SearchValues());
		checkers.add(new FireUpdateValues());
		//		Class<?>[] classes = AbstractAreaValue.class.getClasses();
		//		Class<?> c = SearchValues.class;
		//		Object ob = JavaTools.instantiateFactory("SearchValues", c);
		//		System.out.println(ob);
		//		for (Class<?> class1 : classes) {
		//			Object obj = JavaTools.instantiate(class1.getName(), class1);
		//			if (obj instanceof AbstractAreaValue) {
		//				if (((AbstractAreaValue) obj).shouldbeAddedToList())
		//					getCheckers().add((AbstractAreaValue) obj);
		//			}
		//		}
	}

	/**
	 * @author Salim
	 * @param a
	 */
	public void addArea(Area a) {
		if (containsArea(a.getID()))
			return;
		Entry e = new Entry(this, a, false, getCheckers().size());
		selectedAreas.add(e);
		panel.addArea(e);
	}

	public void removeArea(int index) {
		selectedAreas.remove(index);
		getPanel().removeArea(index);

	}

	public void removeArea(EntityID id) {
		for (int i = selectedAreas.size() - 1; i > -1; i--) {
			if (selectedAreas.get(i).area.getID().getValue() == id.getValue()) {
				selectedAreas.remove(i);
				getPanel().remove(i);
			}
		}
	}

	public boolean containsArea(EntityID id) {
		for (int i = selectedAreas.size() - 1; i > -1; i--) {
			if (selectedAreas.get(i).area.getID().getValue() == id.getValue())
				return true;
		}
		return false;
	}

	@Override
	protected Shape render(Entry entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.yellow);
		return entity.paint(g, transform);
	}

	public void setCheckers(ArrayList<AbstractAreaValue> checkers) {
		this.checkers = checkers;
	}

	public ArrayList<AbstractAreaValue> getCheckers() {
		return checkers;
	}

	public int indexOf(String name) {
		int index = -1;
		for (AbstractAreaValue c : checkers) {
			index++;
			if (c.getValueName().equals(name))
				return index;
		}
		return -1;
	}

	public void setPanel(AllValuePanel panel) {
		this.panel = panel;
	}

	public AllValuePanel getPanel() {
		return panel;
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void makeEntities() {
		setEntities(selectedAreas);		
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Entry entity) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public LayerType getLayerType() {
		return LayerType.None;
	}
}

class Entry {
	public final Area area;
	public final boolean checks[];
	private final AllValueLayer layer;

	public Entry(AllValueLayer layer, Area a, boolean all_none, int size) {
		this.layer = layer;
		this.area = a;
		checks = new boolean[size];
		for (int i = 0; i < checks.length; i++) {
			if (all_none)
				checks[i] = true;
			else
				checks[i] = false;
		}
	}

	public Shape paint(Graphics g, ScreenTransform transform) {
		int numberOfChecks = countChecks();
		if (numberOfChecks == 0)
			return null;
		if (numberOfChecks == 1) {
			return drawSingle(g, transform);
		} else {
			return drawMulti(g, transform);
		}
	}

	private Shape drawMulti(Graphics g, ScreenTransform transform) {
		System.out.println(":D :D :D");
		return null;
	}

	private Shape drawSingle(Graphics g, ScreenTransform transform) {
		System.out.println(":D ");
		int index = -1;
		for (int i = 0; i < checks.length; i++) {
			if (checks[i]) {
				index = i;
				break;
			}
		}
		g.drawString(getLayer().getCheckers().get(index).getValue(layer.model().sosAgent(), area) + "", transform.xToScreen(area.getX()), transform.yToScreen(area.getY()));
		return area.getShape();
	}

	public int countChecks() {
		int numberOfChecks = 0;
		for (int i = 0; i < checks.length; i++) {
			if (checks[i]) {
				numberOfChecks++;
			}
		}
		return numberOfChecks;
	}

	public void setCheck(int index, boolean b) {
		checks[index] = b;
	}

	public AllValueLayer getLayer() {
		return layer;
	}

}


class AllValuePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private JScrollPane pane;
	private AreaListPanel areaList;
	private CheckMarksPanel checkMarks;
	@SuppressWarnings("unused")
	private final AllValueLayer layer;

	public AllValuePanel(AllValueLayer layer) {
		this.layer = layer;
		areaList = new AreaListPanel(layer);
		pane = new JScrollPane(areaList);
		checkMarks = new CheckMarksPanel(layer.getCheckers(), layer, this);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, checkMarks, areaList);
		add(split);
	}

	public void selectAllFor(int index, boolean selected) {
		areaList.selectAllFor(index, selected);
	}

	public void removeArea(int index) {
		areaList.removeArea(index);
		repaint();
	}

	public void addArea(Entry e) {
		areaList.addArea(e);
		repaint();
	}
}

class CheckMarksPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AllValueLayer layer;
	private final AllValuePanel panel;
	@SuppressWarnings("unused")
	private final ArrayList<AbstractAreaValue> checks;

	public CheckMarksPanel(ArrayList<AbstractAreaValue> checks, AllValueLayer layer, AllValuePanel panel) {
		this.checks = checks;
		this.layer = layer;
		this.panel = panel;
		for (AbstractAreaValue c : checks) {
			if (c.shouldbeAddedToList()) {
				add(new CheckPanel(c.getValueName(), this));
			}
		}
	}

	public class CheckPanel extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String name;
		private JCheckBox checkBox;

		public CheckPanel(String name, CheckMarksPanel panel) {
			this.name = name;
			checkBox = new JCheckBox();
			checkBox.addActionListener(this);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JTextArea a = new JTextArea();
			a.setText(name);
			a.setEditable(false);
			JScrollPane p = new JScrollPane(a);
			add(p);
			add(checkBox);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == checkBox) {
				panel.selectAllFor(layer.indexOf(name), checkBox.isSelected());
				layer.getViewer().view();
			}
		}
	}
}

class AreaListPanel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AllValueLayer layer;
	ArrayList<EntryField> fields = new ArrayList<AreaListPanel.EntryField>();

	public AreaListPanel(AllValueLayer layer) {

		this.layer = layer;
		setLayout(new VerticalBagLayout());
	}

	public void selectAllFor(int index, boolean selected) {
		for (EntryField ef : fields) {
			System.out.println("asdsad");
			if (layer.getCheckers().get(index).isValidForArea(layer.model().sosAgent(), ef.getEntry().area)) {
				ef.getEntry().checks[index] = selected;
				ef.checkBoxes[index].setSelected(selected);
			}
		}
		layer.getViewer().view();
		repaint();
	}

	public void addArea(Entry e) {
		EntryField ef = new EntryField(this, e.area.toString(), e.checks, e);
		this.add(ef);
		fields.add(ef);
		repaint();
	}

	public class EntryField extends JPanel implements ActionListener, MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final String name;
		public final JCheckBox[] checkBoxes;
		private final Entry entry;
		public final AreaListPanel panel;

		public EntryField(AreaListPanel panel, String name, boolean[] checks, Entry e) {
			this.panel = panel;
			this.name = name;
			entry = e;
			checkBoxes = new JCheckBox[checks.length];
			JTextArea nameArea = new JTextArea();
			nameArea.setText(name);
			nameArea.addMouseListener(this);
			JPanel boxesPanel = new JPanel();
			boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.X_AXIS));
			JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nameArea, boxesPanel);
			panel.addMouseListener(this);
			split.addMouseListener(this);
			this.setBorder(BorderFactory.createRaisedBevelBorder());
			//======
			this.add(split);
			for (int i = 0; i < checks.length; i++) {
				checkBoxes[i] = new JCheckBox();
				if (entry.getLayer().getCheckers().get(i).isValidForArea(layer.model().sosAgent(), entry.area)) {
				} else {
					checkBoxes[i].setEnabled(false);
				}
				checkBoxes[i].addActionListener(this);
				boxesPanel.add(checkBoxes[i]);
			}
			setVisible(true);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = -1;
			for (JCheckBox cb : checkBoxes) {
				index++;
				if (cb == e.getSource()) {
					getEntry().setCheck(index, cb.isSelected());
					layer.getViewer().view();
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				PopUpMenu menu = new PopUpMenu(panel, name);
				System.out.println(this.getParent().getParent());
				menu.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		public Entry getEntry() {
			return entry;
		}
	}

	public class PopUpMenu extends JPopupMenu {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unused")
		private final AreaListPanel panel;

		public PopUpMenu(AreaListPanel panel, String name) {
			super();
			this.panel = panel;
			//-------
			JMenuItem item = new JMenuItem("Remove " + name);
			item.addActionListener(panel);
			add(item);
		}

		public void showMe(JPanel panel, int x, int y) {
			panel.add(this);
			show(panel, x, y);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().indexOf("Remove") != -1) {
			removeArea(e.getActionCommand().split(" ")[1]);
		}
		layer.getViewer().view();
	}

	public void removeArea(String name) {

		int index = -1;
		for (EntryField ef : fields) {
			index++;
			if (ef.getName().equals(name)) {
				//				layer.removeArea(ef.getEntry().area.getID());
				removeArea(index);
				break;
			}
		}
		repaint();
	}

	public void removeArea(int index) {
		remove(fields.get(index));
		fields.remove(index);
		repaint();
	}

}
