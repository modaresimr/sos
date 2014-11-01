package sos.base.util.namayangar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import sos.LaunchAgents;
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.entities.StandardEntity;
import sos.base.util.namayangar.tools.ComponentMover;

public class NamayangarsList {
	public enum ActionType {
		ShowAll, ShowSelected, ShowIdInTextBox
	}

	private static final int WIDTH = 270;

	private static final int HEIGHT = 370;

	HashMap<Integer, SOSAgent<? extends StandardEntity>> agentNamayangarList = new HashMap<Integer, SOSAgent<? extends StandardEntity>>();
	Vector<String> agentList = new Vector<String>();
	JList jList = new JList();
	Window frame;

	public NamayangarsList() {
		if (SOSConstant.IS_CHALLENGE_RUNNING)
			return;

		//		frame = new JWindow();

		//		frame.setVisible(true);
		JFrame f = new JFrame("Namayangar " + LaunchAgents.getType());
		frame = f;
		f.setUndecorated(true);
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		Image image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphic = (Graphics2D) image.getGraphics();
		graphic.drawImage(new ImageIcon("namayangarList.png").getImage(), 0, 0, 32, 32, null);
		graphic.setColor(Color.BLACK);
		//		((Graphics2D)image.getGraphics()).setStroke(new BasicStroke(2));
		graphic.setFont(new Font("Times", Font.BOLD, 15));
		graphic.drawString(LaunchAgents.getType(), 5, 20);
		f.setIconImage(image);
		frame.setSize(WIDTH, HEIGHT);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - WIDTH) / 2;
		int y = (screen.height - HEIGHT) / 2;
		frame.setLocation(x, y);
		//		frame.setLocation(LaunchAgents.splash.getX(),LaunchAgents.splash.getY());
		Container p = frame;//.getContentPane();

		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JPanel idPanel = new JPanel();
		idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.LINE_AXIS));
		final JTextField idText = new JTextField();
		idText.addActionListener(new NamayangarAction(ActionType.ShowIdInTextBox));
		idPanel.add(new JLabel("ID:"));
		idPanel.add(idText);
		p.add(idPanel, BorderLayout.NORTH);
		JScrollPane pane = new JScrollPane(jList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		p.add(pane, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
		JButton button = new JButton("Show Namayangar");
		button.addActionListener(new NamayangarAction(ActionType.ShowSelected));
		buttonPanel.add(button);
		JButton allButton = new JButton("Show All Namayangar");
		allButton.addActionListener(new NamayangarAction(ActionType.ShowAll));
		buttonPanel.add(allButton);
		p.add(buttonPanel, BorderLayout.SOUTH);
		new ComponentMover(frame, frame, idPanel, buttonPanel, pane);
		f.setResizable(true);
		f.setVisible(true);
		//		f.setUndecorated(false);
	}

	public void addAgent(SOSAgent<? extends StandardEntity> agent) {
		if (SOSConstant.IS_CHALLENGE_RUNNING)
			return;

		agentNamayangarList.put(agent.getID().getValue(), agent);
		agentList.add(agent.getID().toString() + " " + agent.me().getClass().getSimpleName());
		Collections.sort(agentList);
		jList.setListData(agentList);
		//		jList.get
	}

	class NamayangarAction implements ActionListener {

		private ActionType type;

		public NamayangarAction(ActionType type) {
			this.type = type;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (type) {
			case ShowAll:
				for (SOSAgent<? extends StandardEntity> agent : agentNamayangarList.values()) {
					showWorldModelNamayangar(agent);
				}
				break;
			case ShowIdInTextBox:
				try {
					for (SOSAgent<? extends StandardEntity> agent : agentNamayangarList.values()) {
						if (agent.getID().toString().startsWith(((JTextField) e.getSource()).getText()))
							showWorldModelNamayangar(agent);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getClass() + "\nCause:" + ex.getCause() + "\nMessage:" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case ShowSelected:
				System.out.println(jList.getSelectedValues());
				for (Object agentObj : jList.getSelectedValues()) {
					for (SOSAgent<? extends StandardEntity> agent : agentNamayangarList.values()) {
						if (agent.getID().toString().startsWith(agentObj.toString().split(" ")[0]))
							showWorldModelNamayangar(agent);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	public void showWorldModelNamayangar(SOSAgent<?> agent) {
		agent.showNamayangar();
	}

	// public static void main(String[] args) {
	// NamayangarsList nl = new NamayangarsList();
	// for (int i = 0; i < 10; i++) {
	// nl.addAgent(new AmbulanceTeamAgent());
	// }
	// for (int i = 0; i < 10; i++) {
	// nl.addAgent(new PoliceForceAgent());
	// }
	// }

}
