package rescuecore2.worldmodel;

import java.io.IOException;
import java.io.Serializable;

import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * A type-safe ID class for entities. IDs are really just integers.
 */
public final class EntityID implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int id;
	
	/**
	 * Construct a new EntityID object.
	 * 
	 * @param id The numeric ID to use.
	 */
	public EntityID(int id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	/**
	 * Get the numeric ID for this object.
	 * 
	 * @return The numeric ID.
	 */
	public int getValue() {
		return id;
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}
	
	/**
	 * @author Ali-edited salim
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			// new SOSLoggerSystem(null, "error", true, OutputType.Both).error("You can't check equality of an EntityID and null", new Error("You can't check equality of an EntityID and null"));
			return false;
		}
		
		if (!(o instanceof EntityID)) {
			new SOSLoggerSystem(null, "error", true, OutputType.Both).error("You can't check equality of an EntityID and " + o.getClass(), new Error("You can't check equality of an EntityID and " + o.getClass()));
			return false;
		}
		
		if (o instanceof EntityID) {
			return this.id == ((EntityID) o).id;
		}
		return false;
	}
	
	/**
	 * @author Salim
	 */
	public boolean equals(int id) {
		return this.id == id;
	}
	
	
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		// s.writeInt(id);
	}
	
	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		// id = s.readInt();
		s.defaultReadObject();
	}

}