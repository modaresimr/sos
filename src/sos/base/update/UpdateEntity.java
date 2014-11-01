package sos.base.update;

import sos.base.entities.StandardEntity;

public class UpdateEntity<T extends StandardEntity> {
	private final T entity;
	private int id;
	private boolean isNew;
	
	
	public UpdateEntity(T createEntity, boolean isNew) {
		entity = createEntity;
		this.isNew = isNew;
	}

	public T getEntity() {
		return entity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
