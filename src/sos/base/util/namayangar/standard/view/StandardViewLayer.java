package sos.base.util.namayangar.standard.view;

import java.awt.geom.Rectangle2D;

import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.WorldModel;
import sos.base.entities.StandardWorldModel;
import sos.base.util.namayangar.view.AbstractViewLayer;

public abstract class StandardViewLayer extends AbstractViewLayer {
	/**
	 * The StandardWorldModel to view.
	 */
	protected StandardWorldModel world;
	
	/**
	 * Construct a new StandardViewLayer.
	 */
	protected StandardViewLayer() {
	}
	
	@Override
	public Rectangle2D view(Object... objects) {
		processView(objects);
		if (world == null) {
			return null;
		}
		return world.getBounds();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void viewObject(Object o) {
		if (o instanceof WorldModel<?>) {
			world = StandardWorldModel.createStandardWorldModel((WorldModel<? extends Entity>) o);
		}
	}
	
}
