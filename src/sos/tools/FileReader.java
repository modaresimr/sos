package sos.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Salim
 * 
 */
public class FileReader {
	FileInputStream fi;
	java.io.FileReader fr;
	private int bufferSize;
	
	public FileReader(String path, String file, int bufferSize) {
		this.bufferSize = bufferSize;
		File f = new File(path.substring(0, path.length() - 1));
		f.mkdirs();
		f = new File(path + file);
		System.out.println(path + file);
		
		try {
			if (!f.exists())
				f.createNewFile();
			fi = new FileInputStream(f);
			fr = new java.io.FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Byte> readFile(int stringSize) {
		// ----------------------------------
		// ----------------------------------
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		int real = 0;
		byte[] b = new byte[bufferSize];
		// ----------------------------------
		try {
			while ((bytes.size() < stringSize) && fi.available() > 0) {
				real = fi.read(b);
				for (int i = 0; i < b.length && i < real; i++) {
					bytes.add(b[i]);
				}
			}
			// ----------------------------------
			fr.skip(bytes.size());
			// ----------------------------------
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ----------------------------------
		// ----------------------------------
		return bytes;
		
	}
	
	public String readFile() {
		// ----------------------------------
		char[] c = new char[bufferSize];
		String res = "";
		// ----------------------------------
		try {
			if (br == null)
				br = new BufferedReader(fr);
			int num = br.read(c);
			// ----------------------------------
			fi.skip(num);
			res = String.copyValueOf(c, 0, num);
			// ----------------------------------
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ----------------------------------
		return res;
	}
	
	BufferedReader br;
	
	public String readString(int size) {
		// ----------------------------------
		StringBuilder res = new StringBuilder();
		char[] c = new char[bufferSize];
		// ----------------------------------
		try {
			// ----------------------------------
			if (br == null)
				br = new BufferedReader(fr);
			// ----------------------------------
			while (res.length() < size && fi.available() > 0) {
				if (size < res.length() + bufferSize)
					c = new char[size - res.length()];
				int num = br.read(c);
				res.append(c, 0, num);
				c = new char[bufferSize];
				System.gc();
				fi.skip(num);
			}
			// ----------------------------------
			if (fi.available() == 0)
				fr.close();
			// ----------------------------------
		} catch (IOException e) {
			e.printStackTrace();
		}
		// --------------------------- clear everything
		c = null;
		System.gc();
		// ---------------------------
		return res.substring(0);
	}
	
	public int available() {
		int av = 0;
		try {
			av = fi.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return av;
	}
	
}
