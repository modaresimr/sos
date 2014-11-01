package sos.base.message.structure;


/**
 * 
 * @author Ali
 * 
 */
public class MessageBitArray extends AbstractReceiveMessageBitArray {
	/**
	 * @param bitSize
	 * @throws IllegalArgumentException
	 */


	public MessageBitArray(byte[] b) {
		super(b);
	}

	

	@Override
	public int get(int offset, int lenght) {
		lenght--;
		int index = offset >> 3;// divide by 8
		int posInFirstIndex = (offset << (29)) >>> (29);// %8
		int tmp = data[index] & 0x0000ff;
		int endIndex = (offset + lenght) >>> 3;// divide by 8
		int endPosInEndIndex = ((offset + lenght) << 29) >>> 29;// %8
		tmp = ((tmp << (24 + posInFirstIndex))) >>> (24 + posInFirstIndex);
		if (index == endIndex)
			return tmp >> (7 - endPosInEndIndex);// %8
		for (int i = index + 1; i < endIndex; i++) {
			tmp = tmp << 8 | (data[i] & 0x0000ff);
		}
		tmp = (tmp << (endPosInEndIndex + 1)) | ((data[endIndex] & 0x0000ff) >>> (7 - endPosInEndIndex));
		return tmp;
		// int result = 0;
		// // for (int i = start; i < lenght + start; i++) {
		// // result = (result << 1) | (get(i) ? 1 : 0);
		// // }
		// return result;
	}

	
	// public byte[] toByteArray() {
	// return data;
	// }
	
	}
