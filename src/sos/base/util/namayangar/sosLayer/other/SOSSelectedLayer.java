package sos.base.util.namayangar.sosLayer.other;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Human;
import sos.base.entities.ShapeableObject;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.selectedLayer.SOSAbstractSelectedComponent;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;
import sos.base.worldGraph.Node;
import sos.tools.GraphEdge;

public class SOSSelectedLayer extends SOSAbstractToolsLayer<SOSSelectedObj> implements SelectedObjectListener {
	private static final Color SELECTED_COLOUR = Color.yellow.brighter();
	private static final int HUMAN_SIZE = 12;
	private static final int COUNT_OF_BUTTONS = 25;

	private JToggleButton multipleSelection = new JToggleButton("multipleSelection", false);
	private JToggleButton fillSelection = new JToggleButton("fillSelection", false);
	private JToggleButton selectSelection = new JToggleButton("selectSelection", true);
	ArrayList<SOSAbstractSelectedComponent<?>> allComponents = new ArrayList<SOSAbstractSelectedComponent<?>>();

	public SOSSelectedLayer() {
		super(SOSSelectedObj.class);
	}

	@Override
	public void preCompute() {
		super.preCompute();
		setEntities(new ArrayList<SOSSelectedObj>());
		getViewer().getViewerFrame().addSelectedObjectListener(this);
	}

	@Override
	public int getZIndex() {
		return 1000000;
	}

	@Override
	protected void makeEntities() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public JComponent getGUIComponent() {

		ArrayList<Class<? extends SOSAbstractSelectedComponent>> soslayers = NamayangarUtils.getClasses("sos.base.util.namayangar.sosLayer", SOSAbstractSelectedComponent.class);
		for (Class<? extends SOSAbstractSelectedComponent> class1 : soslayers) {
			try {
				SOSAbstractSelectedComponent newClass = class1.newInstance();
				newClass.setModel(model());
				if (newClass.isValid()) {
					allComponents.add(newClass);
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JPanel selectedPanel = new JPanel();
		selectedPanel.setLayout(new GridLayout(COUNT_OF_BUTTONS, 1, 2, 2));
		selectedPanel.setBorder(new TitledBorder(new LineBorder(Color.white, 2, true), "SOS Selected Tools"));
		selectedPanel.add(multipleSelection);
		multipleSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!multipleSelection.isSelected()) {
					getEntities().clear();
				}
			}
		});
		selectedPanel.add(fillSelection);
		selectedPanel.add(selectSelection);
		for (final SOSAbstractSelectedComponent sosAbstractSelectedComponent : allComponents) {
			final JToggleButton jtb = new JToggleButton(sosAbstractSelectedComponent.getName(), sosAbstractSelectedComponent.isVisible());
			jtb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sosAbstractSelectedComponent.setVisible(jtb.isSelected());
					getViewer().repaint();

				}
			});
			selectedPanel.add(jtb);
			JComponent gui=sosAbstractSelectedComponent.getGui();
			if(gui!=null)
				selectedPanel.add(gui);

		}
		for (int i = 0; i < COUNT_OF_BUTTONS-(selectedPanel.getComponentCount()); i++) {
			selectedPanel.add(new JLabel());
		}
		
		return new JScrollPane(selectedPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public ArrayList<SOSSelectedObj> getEntities() {
		return (ArrayList<SOSSelectedObj>) super.getEntities();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private void doInspect() {
		if(getEntities()!=null && getEntities().size()>0)
			 getViewer().getViewerFrame().getInspectorGui().inspect(getEntities().get(getEntities().size()-1));
	}

	@Override
	public Rectangle2D view(Object... objects) {
		Rectangle2D r = super.view(objects);
		doInspect();
		return r;
	}
	
	@Override
	protected Shape render(SOSSelectedObj selectedObj, Graphics2D g, ScreenTransform transform) {
		Shape shape = renderObject(selectedObj.getObject(), g, transform);
		if (shape != null) {

			g.setColor(SELECTED_COLOUR);
			if (fillSelection.isSelected()) {
				g.fill(shape);
			} else if (selectSelection.isSelected()) {
				g.setStroke(new BasicStroke(3));
				g.draw(shape);
				g.setStroke(new BasicStroke(1));
			}
			for (SOSAbstractSelectedComponent<?> next : allComponents) {
				if (next.isVisible())
					next.paintSelected(selectedObj.getObject(), g, transform);
			}
		}
		return null;
	}

	private Shape renderObject(Object selectedObject, Graphics2D g, ScreenTransform transform) {
		if (selectedObject instanceof ShapeableObject)
			return NamayangarUtils.transformShape(((ShapeableObject) selectedObject).getShape(), transform);
		if (selectedObject instanceof Human) {
			return renderHuman((Human) selectedObject, g, transform);
		}
		if (selectedObject instanceof GraphEdge)
			return renderGraphEdge((GraphEdge) selectedObject, g, transform);

		return null;
	}

	private Shape renderGraphEdge(GraphEdge e, Graphics2D g, ScreenTransform t) {
		Node start = ((SOSWorldModel) world).nodes().get(e.getHeadIndex());
		Node end = ((SOSWorldModel) world).nodes().get(e.getTailIndex());
		Shape line = new Line2D.Float(t.xToScreen(start.getPosition().getX()), t.yToScreen(start.getPosition().getY()), t.xToScreen(end.getPosition().getX()), t.yToScreen(end.getPosition().getY()));
		return line;
	}

	private Shape renderHuman(Human human, Graphics2D g, ScreenTransform t) {
		Point2D location = human.getPositionPoint();
		if (location == null) {
			return null;
		}
		int x = t.xToScreen(location.getX());
		int y = t.yToScreen(location.getY());
		Shape shape = new Ellipse2D.Double(x - HUMAN_SIZE / 2, y - HUMAN_SIZE / 2, HUMAN_SIZE, HUMAN_SIZE);
		return shape;

	}

	@Override
	public void objectSelected(SOSSelectedObj sso) {
		if (sso == null) {
			getEntities().clear();

		} else {
			if (multipleSelection.isSelected()){
				if(!getEntities().contains(sso))
					getEntities().add(sso);
				
			}else {
				getEntities().clear();
				getEntities().add(sso);
			}
		}
		doInspect();
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(SOSSelectedObj entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.None;
	}
}
