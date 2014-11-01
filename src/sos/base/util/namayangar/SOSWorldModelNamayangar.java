package sos.base.util.namayangar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import rescuecore2.config.Config;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.entities.StandardWorldModel;
import sos.base.util.namayangar.sosLayer.other.AllValueLayer;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.sosLayer.reachablity.ReachableEdgesLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSInspectorGui;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;
import sos.base.util.namayangar.view.RenderedObject;
import sos.base.util.namayangar.view.SOSViewListener;
import sos.base.util.namayangar.view.ViewComponent;
import sos.base.util.namayangar.view.ViewLayer;

public class SOSWorldModelNamayangar implements GUIComponent, SOSViewListener {
	private static final int SIZE = 700;
	static final int VIEWER_WIDTH = 1024;
	static final int VIEWER_HEIGHT = 600;
	public SOSAnimatedWorldModelViewer viewer;
	private JLabel timeLabel;
	//		StandardEntity selectedObject;
	//	private SOSInspector inspector;
	//	private JTextField field;
	private SOSWorldModel world;
	public ReachableEdgesLayer reachEdgesLayer;
	private NumberFormat format;
	///////
	private JTextField jtX = new JTextField(10);
	private JTextField jtY = new JTextField(10);

	///////
	private JFrame frame;
	private JPopupMenu popupMenu;
	private JPanel rightPanel;
	private JPanel statusPanel;
	private JSplitPane split;
	private SOSInspectorGui inspectorGui;

	private ArrayList<SelectedObjectListener> selectedObjectListeners = new ArrayList<SelectedObjectListener>();
	private JTabbedPane tabbedPane = new JTabbedPane();

	/**
	 * Construct a StandardWorldModelViewerComponent.
	 */
	public SOSWorldModelNamayangar(Config config, SOSWorldModel model, String title) {
		setToolkit(title);
		format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(0);
		world = model;
		tabbedPane.addTab("Information", addInfoPanel());
		viewer = new SOSAnimatedWorldModelViewer(this, model);
		viewer.initialise(config);

		frame = new JFrame(title);
		frame.add(getGUIComponent());
		frame.setSize(SIZE, SIZE);
		frame.setVisible(true);

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, viewer, rightPanel());
		split.setDividerLocation(300);
		frame.add(split, BorderLayout.CENTER);
		frame.add(statusPanel(), BorderLayout.NORTH);
		frame.add(bottomPanel(), BorderLayout.SOUTH);
		viewer.setPreferredSize(new Dimension(VIEWER_WIDTH, VIEWER_HEIGHT));

		frame.setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth(), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight() - 50);

		viewer.addViewListener(this);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setAlwaysOnTop(false);

		split.setDividerLocation((int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth() * .73d));

	}

	private void setToolkit(String title) {
		try {
			Toolkit xToolkit = Toolkit.getDefaultToolkit();
			java.lang.reflect.Field awtAppClassNameField;
			awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, title);
		} catch (Exception e1) {
			//				e1.printStackTrace();
		}
	}

	private JPanel statusPanel() {
		statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		statusPanel.setVisible(false);
		timeLabel = new JLabel("Time: Not started", JLabel.CENTER);
		timeLabel.setBackground(Color.WHITE);
		timeLabel.setOpaque(true);
		timeLabel.setFont(timeLabel.getFont().deriveFont(Font.PLAIN, 20));
		statusPanel.add(timeLabel);
		statusPanel.setBackground(Color.white);
		return statusPanel;
	}

	private Component bottomPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(controlPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel controlPanel() {
		jtX.setEditable(false);
		jtY.setEditable(false);
		jtX.setVisible(true);
		jtY.setVisible(true);
		jtX.setFont(new Font("Arial", Font.BOLD, 12));
		jtY.setFont(new Font("Arial", Font.BOLD, 12));
		JPanel jp = new JPanel(new GridLayout(2, 1));
		//		jp.setSize(new Dimension(30, 20));
		jp.add(jtX);
		jp.add(jtY);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(VIEWER_WIDTH, 10));
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// panel.add(jp);
		//		panel.add(m_cycleProgressBar);
		addIdTextField(panel);
		JPanel right1Panel = new JPanel();
		right1Panel.setLayout(new GridLayout());
		addZoomPanel(right1Panel);

		mainPanel.add(panel, BorderLayout.WEST);
		mainPanel.add(right1Panel, BorderLayout.EAST);
		return mainPanel;
	}

	private void addZoomPanel(JPanel panel) {
		final JTextField m_zoomTextField = new JTextField("100");
		m_zoomTextField.setSize(30, 20);
		m_zoomTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.getTransform().setZoomLevel(Integer.parseInt(m_zoomTextField.getText()) / 100d);
				m_zoomTextField.setText((int) (viewer.getTransform().getZoomLevel() * 100) + "");
				viewer.repaint();
			}
		});
		JButton m_increaseZoomButton = new JButton("+");
		m_increaseZoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.getTransform().zoomIn();
				m_zoomTextField.setText((int) (viewer.getTransform().getZoomLevel() * 100) + "");
				viewer.repaint();
			}
		});
		JButton m_decreaseZoomButton = new JButton("-");
		m_decreaseZoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.getTransform().zoomOut();
				m_zoomTextField.setText((int) (viewer.getTransform().getZoomLevel() * 100) + "");
				viewer.repaint();
			}
		});
		JButton m_resetZoomButton = new JButton("R");
		m_resetZoomButton.setBackground(Color.white);
		m_resetZoomButton.setToolTipText("Reset map changed");
		m_resetZoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.getTransform().resetZoom();
				m_zoomTextField.setText((int) (viewer.getTransform().getZoomLevel() * 100) + "");
				viewer.repaint();
			}
		});
		final JToggleButton m_enableDrag = new JToggleButton("D", true);
		m_enableDrag.setBackground(Color.white);
		m_enableDrag.setToolTipText("Diable/Enable draging");
		m_enableDrag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewer.setEnableDrag(m_enableDrag.isSelected());
			}
		});
		final JToggleButton showHideOtherPanel = new JToggleButton("Show", false);
		showHideOtherPanel.setBackground(Color.green.darker());
		showHideOtherPanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rightPanel.setVisible(showHideOtherPanel.isSelected());
				statusPanel.setVisible(showHideOtherPanel.isSelected());
				split.setDividerLocation(.8);
			}
		});
		panel.add(new JLabel(" Zoom:"));
		panel.add(m_decreaseZoomButton);
		panel.add(m_zoomTextField);
		panel.add(m_increaseZoomButton);
		panel.add(m_resetZoomButton);
		panel.add(m_enableDrag);
		panel.add(showHideOtherPanel);
	}

	private void addIdTextField(JPanel panel) {
		panel.add(new JLabel(" Select"));
		String[] listData = { "Id", "AgentIndex", "AmbIndex", "FireIndex", "PoliceIndex", "RoadIndex", "BuildingIndex", "AreaIndex" };
		final JComboBox list = new JComboBox(listData);

		panel.add(list);
		final JTextField idTextField = new JTextField(10);
		idTextField.setSize(100, 20);
		idTextField.setText(world.me().getID().toString());
		final ActionListener act = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int idIndex = Integer.parseInt(idTextField.getText());
					if (list.getSelectedItem().equals("Id")) {
						StandardEntity entity = world.getEntity(new EntityID(idIndex));
						if (entity == null)
							throw new NullPointerException();
						setOveralSelectedObject(entity);
					} else if (list.getSelectedItem().equals("AgentIndex")) {
						setOveralSelectedObject(world.agents().get(idIndex));
					} else if (list.getSelectedItem().equals("AmbIndex")) {
						setOveralSelectedObject(world.ambulanceTeams().get(idIndex));
					} else if (list.getSelectedItem().equals("FireIndex")) {
						setOveralSelectedObject(world.fireBrigades().get(idIndex));
					} else if (list.getSelectedItem().equals("PoliceIndex")) {
						setOveralSelectedObject(world.policeForces().get(idIndex));
					} else if (list.getSelectedItem().equals("RoadIndex")) {
						setOveralSelectedObject(world.roads().get(idIndex));
					} else if (list.getSelectedItem().equals("BuildingIndex")) {
						setOveralSelectedObject(world.buildings().get(idIndex));
					} else if (list.getSelectedItem().equals("AreaIndex")) {
						setOveralSelectedObject(world.areas().get(idIndex));
					} else {
						idTextField.setBackground(Color.red.brighter());
						return;
					}
					/*
					 * if (viewer.selectedLayer.getSelectedObject() instanceof StandardEntity) {
					 * StandardEntity ent = (StandardEntity) viewer.selectedLayer.getSelectedObject();
					 * int x,y,SIZE=(int) (world.getBounds().getWidth()/2);
					 * x=ent.getLocation().first();
					 * y=ent.getLocation().second();
					 * Rectangle2D.Double newView = new Rectangle2D.Double(Math.max(x-SIZE/2,0),Math.max(y-SIZE/2,0),SIZE,SIZE);
					 * if(!newView.contains(viewer.getTransform().getViewBounds()))
					 * viewer.getTransform().show(newView);
					 * }
					 */
					idTextField.setBackground(Color.green.brighter());

				} catch (Exception ex) {
					idTextField.setBackground(Color.red.brighter());
					setSelectedObject(null);
				}
			}

			private void setOveralSelectedObject(StandardEntity entity) {

				SOSSelectedObj selected = new SOSSelectedObj(entity);

				for (ViewLayer layer : viewer.getLayers()) {
					if (entity instanceof Building)
						if (layer.getName().equals("Building shapes")) {
							selected.addLayer(layer);
						}
					if (entity instanceof Road)
						if (layer.getName().equals("Roads")) {
							selected.addLayer(layer);
						}
					if (entity instanceof Blockade)
						if (layer.getName().equals("Blockades")) {
							selected.addLayer(layer);
						}
					if (entity instanceof Human)
						if (layer.getName().equals("Humans")) {
							selected.addLayer(layer);
						}

				}
				setSelectedObject(selected);
			}
		};
		idTextField.addActionListener(act);
		list.addActionListener(act);
		idTextField.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					try {
						String result = (String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
						idTextField.setText(result);
						act.actionPerformed(new ActionEvent(e.getSource(), 0, null));
					} catch (Exception ee) {
					}

				}
			}
		});
		panel.add(idTextField);
		list.setSelectedIndex(0);
	}

	public void setSelectedObject(SOSSelectedObj selectedObject) {

		for (SelectedObjectListener listener : selectedObjectListeners) {
			listener.objectSelected(selectedObject);
		}
		//		viewer.selectedLayer.setSelectedObject(selectedObject);
		//		inspector.inspect(selectedObject);
		//		addToSearchValueLayerList(selectedObject);//Salim
		viewer.repaint();
	}

	private AllValueLayer getAllValueLayer() {
		for (ViewLayer vl : viewer.getLayers()) {
			if (vl instanceof AllValueLayer) {
				return (AllValueLayer) vl;
			}
		}
		return new AllValueLayer();
	}

	/**
	 * @author Salim
	 *         This is for the search panels and Layers
	 * @param obj
	 */
	public void addToSearchValueLayerList(Object obj) {
		if (!(obj instanceof Area))
			return;
		//get the layer from the layer list

		// now check if it is visible add the selected object(if it is a Building) to yhe list of buildings
		if (getAllValueLayer().isVisible()) {
			getAllValueLayer().addArea((Area) obj);
		}
	}

	public void timestepCompleted(StandardWorldModel model, ChangeSet changeSet) {
		viewer.view(model, /* time.getCommands(), */changeSet);
		viewer.repaint();
		timeLabel.setText("Time: " + (world).time());
		inspectorGui.repaint();
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	@Override
	public JComponent getGUIComponent() {
		return viewer;
	}

	@Override
	public String getGUIComponentName() {
		return "SOS World view";
	}

	private Component rightPanel() {
		rightPanel = new JPanel();
		rightPanel.setVisible(false);
		rightPanel.setPreferredSize(new Dimension(300, 100));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		addOptionPanel();

		rightPanel.add(tabbedPane);

		return rightPanel;
	}

	private JPanel addAllValuePanel() {
		return getAllValueLayer().getPanel();
	}

	private int indexOf(LayerType[] layers, LayerType layer) {
		for (int i = 0; i < layers.length; i++) {
			LayerType l = layers[i];
			if (layer.equals(l))
				return i;
		}
		return -1;
	}

	private void addOptionPanel() {
		LayerType[] layerTypes = LayerType.values();
		 JPanel[] layerPanels = new JPanel[layerTypes.length];

		for (int i = 0; i < layerPanels.length; i++)
		{

			final JPanel panel = new JPanel() {
				@Override
				public Dimension getPreferredSize() {
					if (this.getComponentCount() == 0)
						return super.getPreferredSize();
					return new Dimension((int) super.getPreferredSize().getWidth(), (this.getComponents().length + 1) * 40);
				}
			};
			layerPanels[i]=panel;
			JButton jb=new JButton("Disable All");
			jb.setBackground(Color.red);
			layerPanels[i].add(jb);
			jb.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (Component cm : panel.getComponents()) {
						if (cm instanceof JToggleButton) {
							JToggleButton jtb = (JToggleButton) cm;
							if(jtb.isSelected())
//								jtb.setSelected(false);
								jtb.doClick();
						}
					}
				}
			});
			//			layerPanels[i].setLayout(new BoxLayout(layerPanels[i], BoxLayout.PAGE_AXIS));
		}

		for (final ViewLayer layer : viewer.getLayers()) {
			final JToggleButton jtb = new JToggleButton(layer.getName(), layer.isVisible());
			jtb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					layer.setVisible(jtb.isSelected());
					viewer.repaint();
				}
			});

			if (layer instanceof SOSAbstractToolsLayer) {
				LayerType lt = ((SOSAbstractToolsLayer<?>) layer).getLayerType();
				if(lt==null)
					lt=LayerType.None;
				int index = indexOf(layerTypes, lt);
				layerPanels[index].add(jtb);
			}else
				layerPanels[indexOf(layerTypes, LayerType.Entity)].add(jtb);
		}
		for (int i = 0; i < layerPanels.length; i++)
		{
			if (layerPanels[i] != null) {
				tabbedPane.addTab(layerTypes[i] + "", new JScrollPane(layerPanels[i], JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
				int d = 20 - layerPanels[i].getComponentCount();
				if (d < 0)
					d = 0;
				layerPanels[i].setLayout(new GridLayout(layerPanels[i].getComponentCount() + 2 + d, 1, 2, 2));

			}
		}
		//				while (jp.getComponentCount() < COMPONENT_COUNT)
		//					jp.add(new JLabel());
		//				while (basePanel.getComponentCount() < COMPONENT_COUNT)
		//					basePanel.add(new JLabel());

		//		jp.setLayout(new GridLayout(jp.getComponentCount() + 2, 1, 2, 2));
		//		basePanel.setLayout(new GridLayout(basePanel.getComponentCount() + 2, 1, 2, 2));
		//		optionPanel.add(jp);

		//
		//		tabbedPane.addTab("Agent Layer", new JScrollPane(optionPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		//		tabbedPane.addTab("Base Layers", new JScrollPane(basePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

//		tabbedPane.addTab("All Value", addAllValuePanel());
	}

	private JPanel addInfoPanel() {
		inspectorGui = new SOSInspectorGui(this);
		JScrollPane jsp = new JScrollPane(inspectorGui, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel totalPanel = new JPanel();
		totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));

		totalPanel.add(new JLabel("Properties"));
		totalPanel.add(jsp);
		return totalPanel;
	}

	@Override
	public void objectsClicked(ViewComponent view, List<RenderedObject> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void objectsRollover(ViewComponent view, Collection<SOSSelectedObj> objects, MouseEvent e) {
		jtX.setText("x: " + format.format(viewer.getTransform().screenToX(e.getX())));
		jtY.setText("y: " + format.format(viewer.getTransform().screenToY(e.getY())));
	}

	@Override
	public void objectsClicked(ViewComponent view, Collection<SOSSelectedObj> objects, MouseEvent e) {
		if (objects.size() > 0) {
			if ((e.getButton() == MouseEvent.BUTTON3))
				showMenuSelectObject(view, objects, e);
			else {

				for (SOSSelectedObj sso : objects) {
					if (sso.getObject() instanceof Human) {

						setSelectedObject(sso);
						break;
					}
					if (sso.getObject() instanceof Area) {
						setSelectedObject(sso);
						break;
					}
				}
			}

		} else {
			setSelectedObject(null);
		}

	}

	private synchronized void showMenuSelectObject(ViewComponent view, Collection<SOSSelectedObj> objects, MouseEvent e) {

		if (popupMenu != null) {
			popupMenu.setVisible(false);
		}
		popupMenu = new JPopupMenu();
		for (SOSSelectedObj renderObject : objects) {

			final SOSMenuItem menuItem = new SOSMenuItem(renderObject);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setSelectedObject(menuItem.getSOSSelectedObj());
				}
			});
			popupMenu.add(menuItem);
		}
		popupMenu.show(viewer, e.getX(), e.getY());// viewer, objs.get(0)., y)
		// if (popupMenu.isPopupTrigger(e))
		viewer.repaint();
	}

	class SOSMenuItem extends JMenuItem {
		private final SOSSelectedObj sosSelectedObj;

		public SOSMenuItem(SOSSelectedObj sosSelectedObj) {
			super(sosSelectedObj.getObject().toString());
			this.sosSelectedObj = sosSelectedObj;
		}

		public SOSSelectedObj getSOSSelectedObj() {
			return sosSelectedObj;
		}

		private static final long serialVersionUID = 1L;

	}

	public SOSInspectorGui getInspectorGui() {
		return inspectorGui;
	}

	@Override
	public void objectsRollover(ViewComponent view, List<RenderedObject> objects) {
		// TODO Auto-generated method stub

	}

	public void addSelectedObjectListener(SelectedObjectListener selectedObjectListener) {
		this.selectedObjectListeners.add(selectedObjectListener);
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
}
