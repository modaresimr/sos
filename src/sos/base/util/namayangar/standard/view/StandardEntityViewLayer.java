package sos.base.util.namayangar.standard.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rescuecore2.misc.Pair;
import rescuecore2.misc.collections.ArrayTools;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.Property;
import rescuecore2.worldmodel.WorldModel;
import sos.base.entities.StandardEntity;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.tools.SOSRenderObject;
import sos.base.util.namayangar.view.RenderedObject;

/**
 * An abstract base class for StandardWorldModel view layers that render standard entities.
 *
 * @param <T>
 *            The subclass of StandardEntity that this layer knows how to render.
 */
public abstract class StandardEntityViewLayer<T extends StandardEntity> extends StandardViewLayer {
	/**
	 * The entities this layer should render.
	 */
	protected List<T> entities;

	private Class<T> clazz;

	/**
	 * Construct a new StandardViewLayer.
	 *
	 * @param clazz
	 *            The class of entity that this layer can render.
	 */
	protected StandardEntityViewLayer(Class<T> clazz) {
		this.clazz = clazz;
		entities = new ArrayList<T>();
	}

	@Override
	public Rectangle2D view(Object... objects) {
		synchronized (entities) {
			entities.clear();
			preView();
			Rectangle2D result = super.view(objects);
			postView();
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void viewObject(Object o) {
		super.viewObject(o);
		if (clazz.isAssignableFrom(o.getClass())) {
			entities.add(clazz.cast(o));
		}
		if (o instanceof WorldModel<?>) {
			WorldModel<? extends Entity> wm = (WorldModel<? extends Entity>) o;
			for (Entity next : wm) {
				viewObject(next);
			}
		}
	}

	@Override
	public Collection<RenderedObject> render(Graphics2D g, ScreenTransform transform, int width, int height) {
		synchronized (entities) {
			Collection<RenderedObject> result = new ArrayList<RenderedObject>();
			for (T next : entities) {
				RenderedObject r = new SOSRenderObject(next, render(next, g, transform), this);
				result.add(r);
				// if (r.getObject() instanceof StandardEntity && ((StandardEntity) r.getObject()).isSelected())
				// paintSelectedShape(r, g);
				// FIXME
			}
			return result;
		}
	}

	/**
	 * Render an entity and return the shape. This shape is used for resolving mouse-clicks so should represent a hit-box for the entity.
	 *
	 * @param entity
	 *            The entity to render.
	 * @param graphics
	 *            The graphics to render on.
	 * @param transform
	 *            A helpful coordinate transformer.
	 * @return A Shape that represents the hit-box of the rendered entity.
	 */
	public abstract Shape render(T entity, Graphics2D graphics, ScreenTransform transform);

	/**
	 * Perform any pre-processing required before {@link #view} has been called.
	 */
	protected void preView() {
	}

	/**
	 * Perform any post-processing required after {@link #view} has been called.
	 */
	protected void postView() {
	}

	protected void paintSelectedShape(RenderedObject r, Graphics2D g) {
		if (r.getShape() != null) {
			g.setColor(Color.gray);
			g.draw(r.getShape());
			g.setColor(Color.yellow);
			g.fill(r.getShape());
		}
	}

	@Override
	public ArrayList<Pair<String, String>> inspect(Object entity) {
		T typedObject= getTypedObject(entity);
		if(typedObject==null)
			return null;
		ArrayList<Pair<String, String>> temp = sosInspect(typedObject);
		if(temp==null)
			temp=new ArrayList<Pair<String,String>>();
		return temp;
	}


	private T getTypedObject(Object selectedObj) {
		try{
			if (clazz.isAssignableFrom(selectedObj.getClass())) {
				@SuppressWarnings("unchecked")
				T selected = (T)selectedObj;
				testType(selected);
				return selected;
			}
		}catch (ClassCastException e) {
//			System.err.println("ALI!!!!TODO:::: it may a class cast exception ");
		}
		return null;
	}
	/**
	 * it is just for testing type!!! i couldn't found better way!!!
	 * @param test
	 */
	private void testType(T test){
	}

	/**
	 * to inspect entity property
	 *
	 * @param entity
	 * @return if return null nothing will be shown
	 */
	public ArrayList<Pair<String, String>> sosInspect(T entity) {
		ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

		list.add(new Pair<String, String>("Id", entity.getID().toString()));
		String[] tmp = entity.getURN().split(":");
		String val = tmp[tmp.length - 1].substring(0, 1).toUpperCase() + tmp[tmp.length - 1].substring(1);
		list.add(new Pair<String, String>("Type", val));
		ArrayList<Property> props = new ArrayList<Property>();
		if (entity != null) {
			props.addAll(entity.getProperties());
			Collections.sort(props, PROPERTY_NAME_COMPARATOR);
		}
		for (Property prop : props) {
			tmp = prop.getURN().split(":");
			String nameTmp = tmp[tmp.length - 1].substring(0, 1).toUpperCase() + tmp[tmp.length - 1].substring(1);
			val = "";
			if (prop.isDefined()) {
				Object value = prop.getValue();
				val = value.toString();
				if (value.getClass().isArray()) {
					val = ArrayTools.convertArrayObjectToString(value);
				}

			} else {
				val = "Undefined";
			}
			list.add(new Pair<String, String>(nameTmp, val));
		}

		list.add(new Pair<String, String>("Update Time", entity.updatedtime()+""));
		list.add(new Pair<String, String>("MsgTime", entity.getLastMsgTime()+""));
		list.add(new Pair<String, String>("sense Time", entity.getLastSenseTime()+""));
		list.add(new Pair<String, String>("reachable Time", entity.getLastReachableTime()+""));
		return list;
	}

	public static final Comparator<Property> PROPERTY_NAME_COMPARATOR = new Comparator<Property>() {
		@Override
		public int compare(Property p1, Property p2) {
			return p1.getURN().compareTo(p2.getURN());
		}
	};
}
