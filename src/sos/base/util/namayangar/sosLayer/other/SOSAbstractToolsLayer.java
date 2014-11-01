package sos.base.util.namayangar.sosLayer.other;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.SOSAnimatedWorldModelViewer;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.standard.view.StandardViewLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSRenderObject;
import sos.base.util.namayangar.view.RenderedObject;

public abstract class SOSAbstractToolsLayer<T> extends StandardViewLayer {

	private final Class<T> clazz;

	public SOSAbstractToolsLayer(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public final String getName() {
		return this.getClass().getSimpleName();
	}

	private Collection<T> entities;

	@Override
	public Collection<RenderedObject> render(Graphics2D g, ScreenTransform transform, int width, int height) {
		makeEntities();
		Collection<RenderedObject> result = new ArrayList<RenderedObject>();
		if (getEntities() != null) {
			synchronized (getEntities()) {
				for (T next : getEntities()) {
					RenderedObject r = new SOSRenderObject(next, render(next, g, transform), this);
					result.add(r);

				}
			}
		}
		return result;
	}

	/**
	 * till the viewer don't connect success it can't do any thing
	 * so in precompute you can do something
	 */
	public void preCompute() {

	}

	public SOSWorldModel model() {
		return (SOSWorldModel) world;
	}

	public void setEntities(Collection<T> entities) {
		this.entities = entities;
	}

	public void setEntities(T... e) {
		setEntities(Arrays.asList(e));
	}

	protected Collection<T> getEntities() {
		return entities;
	}

	/**
	 * before each render objects should be maked
	 */
	protected abstract void makeEntities();

	/**
	 * you should transform a shape from map view to screen view
	 * 
	 * @see NamayangarUtils.transformShape
	 * @param entity
	 * @param g
	 * @param transform
	 * @return a shape that is valid in screen
	 */
	protected abstract Shape render(T entity, Graphics2D g, ScreenTransform transform);

	//	protected abstract void paintShape(T r, Shape shape, Graphics2D g);

	/**
	 * Get a JComponent that should be added to the GUI.
	 * 
	 * @return A JComponent you can return null if you want nothing to add to view .
	 */
	public abstract JComponent getGUIComponent();

	/**
	 * if you have some component for special usage you can return false where ever the Layer is invalid
	 * //Example
	 * if you are Police Force and add some Layer so they don't have any meaning for other agents so they must be invalid
	 * 
	 * @return
	 */
	public abstract boolean isValid();

	protected SOSAnimatedWorldModelViewer getViewer() {
		return (SOSAnimatedWorldModelViewer) component;
	}

	@Override
	public ArrayList<Pair<String, String>> inspect(Object entity) {
		T typedObject = getTypedObject(entity);
		if (typedObject == null)
			return null;
		return sosInspect(typedObject);
	}

	public abstract ArrayList<Pair<String, String>> sosInspect(T entity);

	public abstract LayerType getLayerType();

	private T getTypedObject(Object selectedObj) {
		try {
			if (clazz != null && clazz.isAssignableFrom(selectedObj.getClass())) {
				@SuppressWarnings("unchecked")
				T selected = (T) selectedObj;
				testType(selected);
				return selected;
			}
		} catch (ClassCastException e) {
			//			System.err.println("ALI!!!!TODO:::: it may a class cast exception ");
		}
		return null;
	}

	/**
	 * it is just for testing type!!! i couldn't found better way!!!
	 * 
	 * @param test
	 */
	private void testType(T test) {
		//it is just for testing type!!! i couldn't found better way!!!
	}

}
