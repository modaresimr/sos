package sos.base.util.namayangar.view;

import java.awt.event.MouseEvent;
import java.util.Collection;

import sos.base.util.namayangar.tools.SOSSelectedObj;


/**
   A listener for view events.
 */
public interface SOSViewListener extends ViewListener{
    /**
       Notification that a set of objects were clicked.
       @param view The ViewComponent that was clicked.
       @param objects The list of objects that were under the click point.
     */
	void objectsRollover(ViewComponent view, Collection<SOSSelectedObj> objects,MouseEvent e);

    /**
       Notification that a set of objects were rolled over.
       @param view The ViewComponent that was rolled over.
       @param objects The list of objects that were under the mouse point.
     */
	void objectsClicked(ViewComponent view, Collection<SOSSelectedObj> collection,MouseEvent e);
}
