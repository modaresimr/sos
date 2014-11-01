package sos.base.util.mapRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sos.base.SOSWorldModel;
import sos.base.util.mapRecognition.MapRecognition.MapName;

/**
 * @author Salim
 */
public class BasicACLC extends AbstractMapInfo {
	private int xApexes[];
	private int yApexes[];
	private float accordance = -1;
	public static final float ACCORDANCE_THRESHLD = (float) (0.99);

	public BasicACLC(String mapName, MapName realMapName, SOSWorldModel model) {
		super(mapName, realMapName, model);
	}

	public BasicACLC(String[] header,SOSWorldModel model) {
		super(header,model);
	}

	public BasicACLC(String mapName, SOSWorldModel model) {
		super(mapName, model);
	}

	public BasicACLC(String name, MapName realMapName) {
		super(name, realMapName);
	}

	@Override
	public float getAccordance(SOSWorldModel model) {
		if (accordance == -1) {
			computeAccordance(model);
		}
		return accordance;
	}

	private void computeAccordance(SOSWorldModel model) {
		if (model.areas().size() != xApexes.length)
			return;
		for (int i = 0; i < model.areas().size(); i++) {
			if (model.areas().get(i).getX() == xApexes[i] && model.areas().get(i).getY() == yApexes[i]) {
				accordance++;
			}
		}
		accordance /= model.areas().size();

	}

	@Override
	public String getTypeName() {
		return MapInfoFactory.BCLC;
	}

	@Override
	public void writeInfoToFIle() {
		File f = new File(MapRecognition.mapsDir + name + ".txt");
		ObjectOutputStream oos;
		try {
			if (!f.exists())
				f.createNewFile();
			oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(xApexes);
			oos.writeObject(yApexes);
		} catch (FileNotFoundException e) {
			System.err.println("[MAP RECOGNIZATION]"+e.getMessage());
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
			System.err.println("[MAP RECOGNIZATION]"+e.getMessage());
		}
		if (!keepDataInMemory()) {
			xApexes = null;
			yApexes = null;
		}
	}

	/*public static int getByteArray(byte[] bytes) {
		int res = 0;
		for (int i = 0; i < 4; i++) {
			int offset = (3 - i) * 8;
			res += ((bytes[i] << offset));
		}
		return 0;
	}*/

	@Override
	public void readFromFile() {
		try {
			File f = new File(MapRecognition.mapsDir + name + ".txt");
			//			if (!f.exists()) {
			//				f.createNewFile();
			//			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			xApexes = (int[]) ois.readObject();
			yApexes = (int[]) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean keepDataInMemory() {
		return false;
	}

	@Override
	protected void makeData(SOSWorldModel model) {
		xApexes = new int[model.areas().size()];
		yApexes = new int[model.areas().size()];
		for (int i = 0; i < model.areas().size(); i++) {
			xApexes[i] = model.areas().get(i).getX();
			yApexes[i] = model.areas().get(i).getY();
		}
		
	}

	@Override
	public boolean match(SOSWorldModel model) {
		//		for (int i = 0; i < xApexes.length; i++) {
		//			System.out.println(xApexes[i] + "???" + yApexes[i]);
		//		}
		if (xApexes == null)
			throw new Error("Error dare file map ");
		if (getAccordance(model) > ACCORDANCE_THRESHLD)
			return true;
		return false;
	}

	//	public static void addToByteArray(byte[] bytes, int index, int value) {
	//		for (int i = 0; i < 4; i++) {
	//			int offset = (3 - i) * 8;
	//			bytes[index + i] = (byte) ((value >>> offset) & 0xFF);
	//		}
	//	}

}
