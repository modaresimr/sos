package sos.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * 
 * @author salim
 */
public class FileWriter {
	PrintStream ps;
	FileOutputStream fo;
	private final String fileName;
	
	public FileWriter(String path, String filName) {
		fileName = filName;
		makeDirectory(path);
		File file = new File(path + fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fo = new FileOutputStream(file);
			ps = new PrintStream(fo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void makeDirectory(String path) {
		File dir = new File(path);
		dir.mkdirs();
	}
	
	public void write(ArrayList<String> inputs) {
		for (String string : inputs) {
			ps.print(string);
		}
	}
	
	public void writeln(ArrayList<String> inputs) {
		for (String string : inputs) {
			ps.print(string);
		}
		ps.println();
	}
	
	public void write(String string) {
		ps.print(string);
	}
	
	public void writeln(String string) {
		ps.println(string);
	}
	
	public void writeDictionary(ArrayList<String> inputs) {
		for (String string : inputs) {
			ps.println(string);
		}
		close();
	}
	
	public void close() {
		ps.close();
		
	}
	
	public void append(String s) {
		try {
			ps = new PrintStream(new AppendFileStream(fileName));
			ps.println(s);
			ps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

class AppendFileStream extends OutputStream {
	RandomAccessFile fd;
	
	public AppendFileStream(String file) throws IOException {
		fd = new RandomAccessFile(file, "rw");
		fd.seek(fd.length());
	}
	
	@Override
	public void close() throws IOException {
		fd.close();
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		fd.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		fd.write(b, off, len);
	}
	
	@Override
	public void write(int b) throws IOException {
		fd.write(b);
	}

}
