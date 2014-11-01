package sos.base.message.structure;

public abstract class AbstractReceiveMessageBitArray implements ReceiveMessageBitArray {
	
	protected final byte[] data;
	
	public AbstractReceiveMessageBitArray(byte[] b) {
		data = b;
	}
	
	public abstract int get(int offset, int lenght);
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 2 << 6; j > 0; j >>>= 1) {
				sb.append((data[i] & j) == 0 ? "0" : "1");
			}
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public int length() {
		return data.length * 8;
	}

}
