package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import sos.base.entities.Building;
import sos.base.sosFireEstimator.SOSFireEstimatorWorldModel;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class CellViewer extends SOSAbstractSelectedComponent<Building>{

	public CellViewer() {
		super(Building.class);
	}
	@Override
	protected void paint(Building selectedObj, Graphics2D g, ScreenTransform transform) {
		Rectangle bound = selectedObj.getShape().getBounds();
		Rectangle2D w=model().getBounds(); 
			int mini = (int) ((bound.getMinX() - w.getMinX()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE);
			int minj = (int) ((bound.getMinY() - w.getMinY()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE);
			int maxi = (int) ((bound.getMaxX() - w.getMinX()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE)+1;
			int maxj = (int) ((bound.getMaxY() - w.getMinY()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE)+1;
			
			mini= (int) (mini*(SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinX()));
			minj= (int) (minj*(SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinY()));
			maxi= (int) (maxi*(SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinX()));
			maxj= (int) (maxj*(SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinY()));
			Rectangle r = new Rectangle(mini, minj, maxi-mini, maxj-minj);
			NamayangarUtils.drawShape(r, g, transform);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
