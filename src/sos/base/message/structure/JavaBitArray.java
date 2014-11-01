package sos.base.message.structure;

import java.util.Arrays;

public class JavaBitArray {
	
	private byte[] repn;
	private int length;
	
	private static final int BITS_PER_UNIT = 8;
	
	private static int subscript(int idx) {
		return idx / BITS_PER_UNIT;
	}
	
	private static int position(int idx) { // bits big-endian in each unit
		return 1 << (BITS_PER_UNIT - 1 - (idx % BITS_PER_UNIT));
	}
	
	/**
	 * Creates a JavaBitArray of the specified size, initialized to zeros.
	 */
	public JavaBitArray(int length) throws IllegalArgumentException {
		if (length < 0) {
			throw new IllegalArgumentException("Negative length for JavaBitArray");
		}

		this.length = length;
		
		repn = new byte[(length + BITS_PER_UNIT - 1) / BITS_PER_UNIT];
	}
	
	/**
	 * Creates a JavaBitArray of the specified size, initialized from the
	 * specified byte array. The most significant bit of a[0] gets
	 * index zero in the JavaBitArray. The array a must be large enough
	 * to specify a value for every bit in the JavaBitArray. In other words,
	 * 8*a.length <= length.
	 */
	public JavaBitArray(int length, byte[] a) throws IllegalArgumentException {
		
		if (length < 0) {
			throw new IllegalArgumentException("Negative length for JavaBitArray");
		}
		if (a.length * BITS_PER_UNIT < length) {
			throw new IllegalArgumentException("Byte array too short to represent " +
																"bit array of given length");
		}

		this.length = length;
		
		int repLength = ((length + BITS_PER_UNIT - 1) / BITS_PER_UNIT);
		int unusedBits = repLength * BITS_PER_UNIT - length;
		byte bitMask = (byte) (0xFF << unusedBits);
		
		/*
		 * normalize the representation:
		 * 1. discard extra bytes
		 * 2. zero out extra bits in the last byte
		 */
		repn = new byte[repLength];
		System.arraycopy(a, 0, repn, 0, repLength);
		if (repLength > 0) {
			repn[repLength - 1] &= bitMask;
		}
	}
	
	/**
	 * Create a JavaBitArray whose bits are those of the given array
	 * of Booleans.
	 */
	public JavaBitArray(boolean[] bits) {
		length = bits.length;
		repn = new byte[(length + 7) / 8];
		
		for (int i = 0; i < length; i++) {
			set(i, bits[i]);
		}
	}
	
	/**
	 * Copy constructor (for cloning).
	 */
	private JavaBitArray(JavaBitArray ba) {
		length = ba.length;
		repn = ba.repn.clone();
	}
	
	/**
	 * Returns the indexed bit in this JavaBitArray.
	 */
	public boolean get(int index) {
		if(index>=length)
			throw new RuntimeException("index bigger than length!");
		return (repn[index >> 3] & 1 << (7 - (index & 7))) != 0;
	}
	
	public boolean oldGet(int index) {
		return (repn[subscript(index)] & position(index)) != 0;
	}
	
	/**
	 * Sets the indexed bit in this JavaBitArray.
	 */
	public void set(int index, boolean value) {
		if(index>=length)
			throw new RuntimeException("index bigger than length!");
		int idx = subscript(index);
		int bit = position(index);
		
		if (value) {
			repn[idx] |= bit;
		} else {
			repn[idx] &= ~bit;
		}
	}
	
	/**
	 * Returns the length of this JavaBitArray.
	 */
	public int length() {
		return length;
	}
	
	/**
	 * Returns a Byte array containing the contents of this JavaBitArray.
	 * The bit stored at index zero in this JavaBitArray will be copied
	 * into the most significant bit of the zeroth element of the
	 * returned byte array. The last byte of the returned byte array
	 * will be contain zeros in any bits that do not have corresponding
	 * bits in the JavaBitArray. (This matters only if the JavaBitArray's size
	 * is not a multiple of 8.)
	 */
	public byte[] toByteArray() {
		return repn.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof JavaBitArray))
			return false;
		
		JavaBitArray ba = (JavaBitArray) obj;
		
		if (ba.length != length)
			return false;
		
		for (int i = 0; i < repn.length; i += 1) {
			if (repn[i] != ba.repn[i])
				return false;
		}
		return true;
	}
	
	/**
	 * Return a boolean array with the same bit values a this JavaBitArray.
	 */
	public boolean[] toBooleanArray() {
		boolean[] bits = new boolean[length];
		
		for (int i = 0; i < length; i++) {
			bits[i] = get(i);
		}
		return bits;
	}
	
	/**
	 * Returns a hash code value for this bit array.
	 * 
	 * @return a hash code value for this bit array.
	 */
	@Override
	public int hashCode() {
		int hashCode = 0;
		
		for (int i = 0; i < repn.length; i++)
			hashCode = 31 * hashCode + repn[i];
		
		return hashCode ^ length;
	}
	
	@Override
	public Object clone() {
		return new JavaBitArray(this);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length(); i++) {
			sb.append(get(i) ? "1" : "0");
			if (i % 4 == 3)
				sb.append(" ");
		}
		return sb.toString();
		
	}
	public JavaBitArray truncate() {
		for (int i = length - 1; i >= 0; i--) {
			if (get(i)) {
				return new JavaBitArray(i + 1, Arrays.copyOf(repn, (i + BITS_PER_UNIT) / BITS_PER_UNIT));
			}
		}
		return new JavaBitArray(1);
	}

//	public static void main(String[] args) {
//		JavaBitArray jba = new SOSBitArray(new byte[] { 5, 6 });
//		System.out.println(jba);
//		System.out.println(jba.get(7));
//	}
}
