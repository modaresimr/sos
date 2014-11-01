package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import sos.base.SOSWorldModel;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public abstract class SOSAbstractSelectedComponent<T> {

	private boolean isVisible = false;
	private SOSWorldModel model;
	private final Class<T> clazz;

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public SOSAbstractSelectedComponent(Class<T> clazz) {
		this.clazz = clazz;

	}

	public void setModel(SOSWorldModel model) {
		this.model = model;

	}

	/*
	 * public JComponent getGui(){
	 * return null;
	 * }
	 */

	public final void paintSelected(Object selectedObj, Graphics2D g, ScreenTransform transform) {
		T selected = getTypedObject(selectedObj);
		if (selected != null)
			paint(selected, g, transform);
	}

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

	protected abstract void paint(T selectedObj, Graphics2D g, ScreenTransform transform);

	public abstract boolean isValid();

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public SOSWorldModel model() {
		return model;
	}

	public JComponent getGui() {
		return null;
	}
	
}
