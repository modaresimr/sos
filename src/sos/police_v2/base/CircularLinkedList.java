package sos.police_v2.base;

import java.util.ArrayList;

public class CircularLinkedList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;
	private int pointerIndex=0;

	public void moveToPointer(int pointerIndex){
		this.pointerIndex = pointerIndex;
	}
	public E getNext(){
		if(size()==0)
			return null;
		return get(pointerIndex++%size());
	}

}
