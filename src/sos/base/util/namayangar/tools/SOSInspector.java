package sos.base.util.namayangar.tools;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;

import rescuecore2.misc.Pair;

/**
 * @author Yoosef
 */
public class SOSInspector extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<Pair<String, String>> datas = new ArrayList<Pair<String, String>>();
//	private SOSSelectedObj entity;
	
	public void inspect(ArrayList<Pair<String, String>> data){
		datas=data;
		fireChange();
	}
	private ArrayList<JTextField> fields = new ArrayList<JTextField>();

	public void fireChange() {
		int j = 0;
		for (int i = 0; i < datas.size(); i++, j += 2) {
			if (j < fields.size()) {
				fields.get(j).setText(datas.get(i).first());
				fields.get(j + 1).setText(datas.get(i).second());
			} else {
				JTextField t1 = new JTextField(datas.get(i).first());
				JTextField t2 = new JTextField(datas.get(i).second());
				add(t1);
				add(t2);
				fields.add(t1);
				fields.add(t2);
			}

		}
		for (int i = 2 * datas.size(); i < fields.size(); i++) {
			fields.get(i).setText("");
		}
		setLayout(new GridLayout(fields.size() / 2, 2));

	}

}
