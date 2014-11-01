package sos.base.util.namayangar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import sos.base.SOSWorldModel;
import sos.base.util.namayangar.sosLayer.other.AllValueLayer;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.sosLayer.other.SOSAnimatedHumanActualSizeLayer;
import sos.base.util.namayangar.sosLayer.other.SOSSelectedLayer;
import sos.base.util.namayangar.sosLayer.reachablity.ReachableEdgesLayer;
import sos.base.util.namayangar.standard.view.AnimatedHumanLayer;
import sos.base.util.namayangar.standard.view.BuildingLayer;
import sos.base.util.namayangar.standard.view.CommandLayer;
import sos.base.util.namayangar.standard.view.RoadBlockageLayer;
import sos.base.util.namayangar.standard.view.RoadLayer;
import sos.base.util.namayangar.standard.view.StandardWorldModelViewer;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * A viewer for StandardWorldModels.
 */
public class SOSAnimatedWorldModelViewer extends StandardWorldModelViewer {
	SOSLoggerSystem log=new SOSLoggerSystem(null, "Namayangar", false, OutputType.Console);
	private static final long serialVersionUID = 1L;
	private static final int FRAME_COUNT = 5;
	private static final int ANIMATION_TIME = 750;
	private static final int FRAME_DELAY = ANIMATION_TIME / FRAME_COUNT;
	
	private AnimatedHumanLayer humans;
	private SOSAnimatedHumanActualSizeLayer humansActualSize;
	private Timer timer;
	private final Object lock = new Object();
	private boolean done;
	public ReachableEdgesLayer reachEdgesLayer;
	public AllValueLayer allValue;
	private final SOSWorldModelNamayangar viewerFrame;
	private final SOSWorldModel model;

	/**
	 * Construct an animated world model viewer.
	 * @param viewerFrame 
	 * @param model 
	 */
	public SOSAnimatedWorldModelViewer(SOSWorldModelNamayangar viewerFrame, SOSWorldModel model) {
		this.model = model;
		this.viewerFrame = viewerFrame;
		addDefaultLayers();
		timer = new Timer(FRAME_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (lock) {
					if (done) {
						return;
					}
					done = true;
					if (humans.nextFrame()) {
						done = false;
						repaint();
					}
					if (humansActualSize.nextFrame()) {
						done = false;
						repaint();
					}
				}
			}
		});
		timer.setRepeats(true);
		timer.start();
	}
	
	@Override
	public String getViewerName() {
		return "Animated world model viewer";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addDefaultLayers() {
		addLayer(new BuildingLayer());
		addLayer(new RoadLayer());
		addLayer(new RoadBlockageLayer());
		addLayer(humans = new AnimatedHumanLayer());
		addLayer(humansActualSize = new SOSAnimatedHumanActualSizeLayer());
		addLayer(new CommandLayer());
		
//		addLayer(selectedLayer=new SelectedLayer());
		ArrayList<Class<? extends SOSAbstractToolsLayer>> soslayers = NamayangarUtils.getClasses("sos.base.util.namayangar.sosLayer", SOSAbstractToolsLayer.class);
		//System.err.println(soslayers+"-----  IsEmpty: "+soslayers.isEmpty());
		if(soslayers.isEmpty())
			log.error("No SOS Layer Found... Please cheack Your Project path to not have space or don't use jar file to view ");
		JTabbedPane jp = new JTabbedPane();
		
		viewerFrame.getTabbedPane().add("Layer Options",jp);
		for (Class<? extends SOSAbstractToolsLayer> class1 : soslayers) {
			SOSAbstractToolsLayer<?> newClass;
			try {
				newClass = class1.newInstance();
				newClass.view(model);
				if(newClass.isValid()){
					addLayer(newClass);
					newClass.preCompute();
					JComponent gui = newClass.getGUIComponent();
					if(gui!=null){
						if(newClass.getClass().equals(SOSSelectedLayer.class))
							viewerFrame.getTabbedPane().add(newClass.getName(),gui);
						else{
							jp.add(newClass.getName(),gui);
						}
					}
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sortLayers();
//		addLayer(new AllValueLayer());
//		
//		addLayer(new MiddleBlockadeLayer());
//		addLayer(new GraphEdgeLayer());
//		addLayer(new PositionHistoryLayer());
//		addLayer(new PoliceMSTTaskAssignLayer());
//		addLayer(new PolicePrecomputeStateMoveHistory());
//		addLayer(new FireZoneLayer());
//		addLayer (new ReachableEdgesLayer());
	}
	
	@Override
	public void view(Object... objects) {
		super.view(objects);
		synchronized (lock) {
			done = false;
			humans.computeAnimation(FRAME_COUNT);
			humansActualSize.computeAnimation(FRAME_COUNT);
		}
	}

	public SOSWorldModelNamayangar getViewerFrame() {
		return viewerFrame;
	}
}
