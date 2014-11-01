package sos.base.message.structure;

/**
 * 
 * @author Ali
 * 
 */
public class SOSBitArray extends JavaBitArray {
	/**
	 * @param bitSize
	 * @throws IllegalArgumentException
	 */
	public SOSBitArray(int bitSize) {
		super(bitSize);
	}

	public SOSBitArray(byte[] b) {
		super(b.length * Byte.SIZE, b);
	}

	public SOSBitArray(int size, byte[] b) {
		super(size, b);
	}

	public static SOSBitArray makeBit(int data, int size) {
		int j = size;
		SOSBitArray temp = new SOSBitArray(size);
		for (int i = 1; j > 0; i = i << 1) {
			j--;
			temp.set(j, (data & i) != 0);
		}
		return temp;
	}

	public void set(int index, SOSBitArray bitArray) {
		for (int i = 0; i < bitArray.length(); i++)
			super.set(index + i, bitArray.get(i));
	}

	public SOSBitArray getBit(int start, int lenght) {
		SOSBitArray result = new SOSBitArray(lenght);
		for (int i = 0; i < lenght; i++) {
			result.set(i, get(i + start));
		}
		return result;
	}

	public int get(int start, int lenght) {
		int result = 0;
		for (int i = start; i < lenght + start; i++) {
			result = (result << 1) | (get(i) ? 1 : 0);
		}
		return result;
	}

	@Override
	public byte[] toByteArray() {
		return super.toByteArray();
	}

	@Override
	 public String toString() {
		 if(length()<=0)
			  return "This array bit is empty!";
		  return super.toString();
	 }
}
