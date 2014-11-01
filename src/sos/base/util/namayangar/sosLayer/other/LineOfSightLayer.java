package sos.base.util.namayangar.sosLayer.other;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSLineOfSightPerception.Ray;
import sos.base.entities.StandardEntity;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.standard.view.StandardViewLayer;
import sos.base.util.namayangar.view.RenderedObject;

public class LineOfSightLayer {
	private transient Collection<Ray> rays;
	private transient Map<StandardEntity, Collection<Ray>> sources;
	private transient StandardEntity selected;
	public void clear() {
		synchronized (rays) {
			rays.clear();
			sources.clear();
		}
	}
	
	public void addRay(StandardEntity source, Ray ray) {
		synchronized (rays) {
			rays.add(ray);
			sources.get(source).add(ray);
		}
	}
	
	@SuppressWarnings("unused")
	private class RayLayer extends StandardViewLayer {
		@Override
		public Collection<RenderedObject> render(Graphics2D g, ScreenTransform transform, int width, int height) {
			Collection<Ray> toDraw = new HashSet<Ray>();
			synchronized (rays) {
				if (selected == null) {
					toDraw.addAll(rays);
				} else {
					toDraw.addAll(sources.get(selected));
				}
			}
			g.setColor(Color.CYAN);
			for (Ray next : toDraw) {
				Line2D line = next.getRay();
				Point2D origin = line.getOrigin();
				Point2D end = line.getPoint(next.getVisibleLength());
				int x1 = transform.xToScreen(origin.getX());
				int y1 = transform.yToScreen(origin.getY());
				int x2 = transform.xToScreen(end.getX());
				int y2 = transform.yToScreen(end.getY());
				g.drawLine(x1, y1, x2, y2);
			}
			return new ArrayList<RenderedObject>();
		}
		
		@Override
		public String getName() {
			return "Line of sight rays";
		}

		@Override
		public int getZIndex() {
			return 0;
		}

		@Override
		public ArrayList<Pair<String, String>> inspect(Object entity) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}

