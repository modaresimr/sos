package sos.base.util.namayangar.standard.view;

import sos.base.util.namayangar.view.LayerViewComponent;

/**
   A viewer for StandardWorldModels.
 */
public class StandardWorldModelViewer extends LayerViewComponent {
	 private static final long serialVersionUID = 1L;

	 /**
       Construct a standard world model viewer.
     */
    public StandardWorldModelViewer() {
//        addDefaultLayers();//TODO WHY HAPPEND???
    }

    @Override
    public String getViewerName() {
        return "Standard world model viewer";
    }

    /**
       Add the default layer set, i.e. nodes, roads, buildings, humans and commands.
     */
    public void addDefaultLayers() {
        addLayer(new BuildingLayer());
        addLayer(new RoadLayer());
        addLayer(new AreaNeighboursLayer());
        addLayer(new RoadBlockageLayer());
        addLayer(new BuildingIconLayer());
        addLayer(new HumanLayer());
        addLayer(new CommandLayer());
        addLayer(new PositionHistoryLayer());
    }
}
