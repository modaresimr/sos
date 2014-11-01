package sos.base.message.structure.blocks;

public class DataArrayList {
	private final String[] keys;
	private final int[] values;
	private int lenght = 0;

	public DataArrayList(int size) {
		keys = new String[size];
		values = new int[size];
	}

	public void fillList(int n) {
		for (int i = 0; i < values.length; i++) {
			values[i] = n;
		}
	}

	public void put(String name, int value) {
		if (value < 0)
			new Error("negative value put!!!").printStackTrace();
		keys[lenght] = name;
		values[lenght++] = value;
	}

	public int get(String key) {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals(key))
				return values[i];
		}
		throw new Error("Can't find " + key + " in datalist");
	}

	public int[] values() {
		return values;
	}

	public String[] keys() {
		return keys;
	}

	public int size() {
		return lenght;
	}

	public int getValue(int index) {
		return values[index];
	}

	public String getKey(int index) {
		return keys[index];
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i] + "=" + values[i] + ", ");
		}
		return sb.toString();
	}
}
