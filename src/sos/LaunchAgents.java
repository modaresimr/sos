package sos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rescuecore2.CommandLineOptions;
import rescuecore2.Constants;
import rescuecore2.components.ComponentConnectionException;
import rescuecore2.components.ComponentLauncher;
import rescuecore2.components.TCPComponentLauncher;
import rescuecore2.config.Config;
import rescuecore2.config.ConfigException;
import rescuecore2.connection.ConnectionException;
import rescuecore2.registry.Registry;
import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardPropertyFactory;
import rescuecore2.standard.messages.StandardMessageFactory;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.CenterAgent;
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.SOSConstant.AgentType;
import sos.base.util.Splash;
import sos.base.util.TimeNamayangar;
import sos.base.util.namayangar.NamayangarsList;
import sos.base.util.sampler.SOSViewer;
import sos.base.util.sampler.SamplerException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.police_v2.PoliceForceAgent;

/**
 * @author Ali
 *         Launcher for SOS agents. This will launch as many instances of each of the
 *         SOS agents as possible, all using one connection.
 */
public final class LaunchAgents {
	//private static final String DEFAULT_HOST = "192.168.128.183";
	private static final String DEFAULT_HOST = "192.168.0.105";
	private static final String DEFAULT_PORT = "7000";

	private static final String FIRE_BRIGADE_FLAG = "-fb";
	private static final String POLICE_FORCE_FLAG = "-pf";
	private static final String AMBULANCE_TEAM_FLAG = "-at";
	private static final String AMBULANCE_CENTER_FLAG = "-ac";
	private static final String POLICE_OFFICE_FLAG = "-po";
	private static final String CENTER_FLAG = "-ce";
	private static final String FIRE_STATION_FLAG = "-fs";
	private static final String MASHIN_NUMBER_FLAG = "-mn";
	static int fb = 0, pf = 0, at = 0, fs = 0, po = 0, ac = 0, mashin_number = 1, center = 0;
	private static boolean justOneAgent = false;
	public static Splash splash;
	public static String host;

	public static final SOSLoggerSystem sosLogger = new SOSLoggerSystem(null, "Main", true, OutputType.Both, true);

	private LaunchAgents() {
	}

	/**
	 * Launch 'em!
	 * 
	 * @param args
	 *            The following arguments are understood: -p <port>, -h
	 *            <hostname>, -fb <fire brigades>, -pf <police forces>, -at
	 *            <ambulance teams>
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws ConfigException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, ConfigException {
		System.out.println("sos-version:build 14-415");
		//		args=new String[]{"-police"};

		setRegistry();
		setConstantsFromConfig(args);
		setGui();
		try {
			launch();
		} catch (Exception e) {
			e.printStackTrace();
			if (splash != null)
				splash.exit();
			System.exit(-1);
		}
	}

	private static void showSplash() {
		if (!SOSConstant.IS_CHALLENGE_RUNNING && SOSConstant.SHOW_SOS_STARTING_SPLASH) {
			if (splash == null)
				splash = new Splash(1);
			splash.showSplash();
		}
	}

	private static void setToolkit() {

		try {
			Toolkit xToolkit = Toolkit.getDefaultToolkit();
			Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, "Agent" + getType());

			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e1) {
			//				e1.printStackTrace();
		}
	}

	public static String getType() {
		return (fb != 0 ? "FB" : "") + (pf != 0 ? "PF" : "") + (at != 0 ? "AT" : "");
	}

	private static void setRegistry() {
		Registry.SYSTEM_REGISTRY.registerEntityFactory(StandardEntityFactory.INSTANCE);
		Registry.SYSTEM_REGISTRY.registerMessageFactory(StandardMessageFactory.INSTANCE);
		Registry.SYSTEM_REGISTRY.registerPropertyFactory(StandardPropertyFactory.INSTANCE);
	}

	public static void launch() {
		try {
			int port = SOSConstant.getConfig().getIntValue(Constants.KERNEL_PORT_NUMBER_KEY, Constants.DEFAULT_KERNEL_PORT_NUMBER);
			host = SOSConstant.getConfig().getValue(Constants.KERNEL_HOST_NAME_KEY, Constants.DEFAULT_KERNEL_HOST_NAME);
			ComponentLauncher launcher = new TCPComponentLauncher(host, port, SOSConstant.getConfig());
			if (true)
				connect(launcher, fb, pf, at);
			else {
				if (SOSConstant.IS_CHALLENGE_RUNNING || justOneAgent || fb > 0 || pf > 0 || at > 0 || center > 0)
					connect(launcher, fb, pf, at);
				else
					multiConnect(launcher, fb, pf, at);
			}
			connectViewer(launcher, SOSConstant.getConfig());
		} catch (ConnectionException e) {
			sosLogger.fatal("Error connecting agents", e);
			System.exit(-1);
		} catch (InterruptedException e) {
			sosLogger.fatal("Error connecting agents", e);
			System.exit(-1);
		} catch (ComponentConnectionException e) {
			if (e.getMessage() != null && e.getMessage().indexOf("No more agents") < 0)
				sosLogger.fatal(e.getMessage(), e);
			else {
				sosLogger.fatal("No more agents");
			}
			System.exit(-1);
		}
		if (splash != null)
			splash.exit();
		splash = null;
		System.out.println("============================================================");
	}

	public static SOSViewer viewer;

	private static void connectViewer(ComponentLauncher launcher, Config config2) throws InterruptedException, ConnectionException, ComponentConnectionException {
		if (SOSConstant.SAMPLING) {
			try {
				viewer = new SOSViewer();
				launcher.connect(viewer);
			} catch (SamplerException e) {
				e.printStackTrace();
			}
		}
	}

	private static void setGui() {
		setToolkit();
		showSplash();
		if (namayangarsList == null)
			namayangarsList = new NamayangarsList();
	}

	private static void setConstantsFromConfig(String[] args) throws IOException, ConfigException {
		try {
			SOSConstant.setConfig(new Config(new File("sos.config")));
		} catch (ConfigException e1) {
			sosLogger.error(e1.getMessage());
		}

		boolean SHOW_SOS_STARTING_DIALOG = (!SOSConstant.IS_CHALLENGE_RUNNING) && SOSConstant.SHOW_SOS_STARTING_DIALOG;// because of warning
		ArrayList<String> s = new ArrayList<String>(Arrays.asList(args));
		if (args.length == 0 && SHOW_SOS_STARTING_DIALOG) {
			String[] targs = showSOSDialog();
			s.addAll(Arrays.asList(targs));
		}
		sosLogger.debug(s);
		if (s.contains("one")) {
			s.remove("one");
			justOneAgent = true;
		}

		String[] targs = new String[s.size()];
		targs = s.toArray(targs);
		targs = CommandLineOptions.processArgs(targs, SOSConstant.getConfig());
		parsArgs(targs);
		//		System.out.println(Arrays.asList(targs));
		//		System.out.println(fb +" "+pf+" "+at);
		System.out.println(SOSConstant.getConfig());
		//		System.out.println(config.getValue(Constants.KERNEL_HOST_NAME_KEY));
	}

	static NamayangarsList namayangarsList = null;
	static int threadCount = 8;

	private static void multiConnect(ComponentLauncher launcher, int fb, int pf, int at) throws ComponentConnectionException {
		TimeNamayangar tm = new TimeNamayangar();
		tm.start();

		old = "";
		if (fb != 0)
			connectThreadic(launcher, AgentType.FireBrigade);
		old = "";
		if (at != 0)
			connectThreadic(launcher, AgentType.AmbulanceTeam);
		old = "";
		if (pf != 0)
			connectThreadic(launcher, AgentType.PoliceForce);
		old = "";
		if (center != 0)
			connectThreadic(launcher, AgentType.Center);
		old = "";

		System.out.println("");
		if (connectedAgentCount == 0)
			throw new ComponentConnectionException("No more agents");
		tm.finish();
		println("Connect Time:" + tm);
		System.gc();
	}

	static SOSAgent<?> getNewAgent(AgentType agentType) {
		switch (agentType) {
		case FireStation:
		case PoliceOffice:
		case AmbulanceCenter:
		case Center:
			return new CenterAgent();
		case AmbulanceTeam:
			return new AmbulanceTeamAgent();
		case FireBrigade:
			return new FireBrigadeAgent();
		case PoliceForce:
			return new PoliceForceAgent();
		default:
			throw new Error("Unknown Agent");
		}

	}

	static boolean canConnectMore = true;

	static void connectThreadic(final ComponentLauncher launcher, final AgentType type) {
		connectSOSAgent(getNewAgent(type), launcher);//for precompute;
		canConnectMore = true;
		Thread[] threads = new Thread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			threads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					while (canConnectMore) {
						if (!connectSOSAgent(getNewAgent(type), launcher))
							break;
					}
					canConnectMore = false;
				}
			});
			threads[i].start();
		}
		for (int i = 0; i < threadCount; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static Object syncObject = new Object();
	static int connectedAgentCount = 0;

	static boolean connectSOSAgent(SOSAgent<?> agent, ComponentLauncher launcher) {

		try {
			//				SOSConstant.IS_CHALLENGE_RUNNING=true;
			print("\nConnecting " + agent.type() + (connectedAgentCount) + "...");
			launcher.connect(agent);
			synchronized (syncObject) {
				connectedAgentCount++;
			}
			namayangarsList.addAgent(agent);
			println("success " + agent.me());
			return true;
		} catch (OutOfMemoryError e) {
			println("Unsuccess!Heap Space Error!!!!");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().indexOf("No more agents") < 0)
				println("failed: " + e.getMessage());
			print("\n No More " + agent.type() + "!");
			return false;
		}
	}

	private static void connect(ComponentLauncher launcher, int fb, int pf, int at) throws InterruptedException, ConnectionException, ComponentConnectionException {

		TimeNamayangar tm = new TimeNamayangar();
		tm.start();
		int connectedAgentCount = 0;
		while (fb-- != 0) {
			if (justOneAgent && connectedAgentCount == 1)
				break;
			print("\nConnecting fire brigade " + (connectedAgentCount) + "...");
			try {
				FireBrigadeAgent fire = new FireBrigadeAgent();
				launcher.connect(fire);
				namayangarsList.addAgent(fire);
				connectedAgentCount++;
				println("success " + fire.me());
			} catch (OutOfMemoryError e) {
				println("Unsuccess!Heap Space Error!!!!");
				e.printStackTrace();
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().indexOf("No more agents") < 0)
					println("failed: " + e.getMessage());
				break;
			}
		}
		old = "";
		while (at-- != 0) {
			if (justOneAgent && connectedAgentCount == 1)
				break;
			print("\nConnecting ambulance team " + (connectedAgentCount) + "...");
			try {
				//				SOSConstant.IS_CHALLENGE_RUNNING=true;
				AmbulanceTeamAgent ambulance = new AmbulanceTeamAgent();
				launcher.connect(ambulance);
				connectedAgentCount++;
				namayangarsList.addAgent(ambulance);
				println("success " + ambulance.me());

			} catch (OutOfMemoryError e) {
				println("Unsuccess!Heap Space Error!!!!");
				e.printStackTrace();
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().indexOf("No more agents") < 0)
					println("failed: " + e.getMessage());
				break;
			}
		}
		old = "";

		while (pf-- != 0) {
			if (justOneAgent && connectedAgentCount == 1)
				break;
			print("\nConnecting police force " + (connectedAgentCount) + "...");
			try {
				PoliceForceAgent police = new PoliceForceAgent();
				launcher.connect(police);
				namayangarsList.addAgent(police);
				connectedAgentCount++;
				println("success " + police.me());
			} catch (OutOfMemoryError e) {
				println("Unsuccess!Heap Space Error!!!!");
				e.printStackTrace();
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().indexOf("No more agents") < 0)
					println("failed: " + e.getMessage());
				break;
			}
		}
		old = "";
		while (center-- != 0) {
			if (justOneAgent && connectedAgentCount == 1)
				break;
			try {
				print("\nConnecting centre " + (connectedAgentCount) + "...");
				CenterAgent center = new CenterAgent();
				launcher.connect(center);
				namayangarsList.addAgent(center);
				connectedAgentCount++;
				println("success " + center.me());
			} catch (OutOfMemoryError e) {
				println("Unsuccess!Heap Space Error!!!!");
				e.printStackTrace();
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().indexOf("No more agents") < 0)
					println("failed: " + e.getMessage());
				break;
			}
		}
		System.out.println("");
		if (connectedAgentCount == 0)
			throw new ComponentConnectionException("No more agents");
		tm.finish();
		println("Connect Time:" + tm);
		System.gc();
	}

	static String old = "";

	public static void println(String s) {
		if (SOSConstant.NO_ANY_LOG)
			System.out.println(s);
		sosLogger.logln(s);
		if (splash != null)
			splash.setText(old + s);
		old = "";
	}

	public static void print(String s) {
		if (SOSConstant.NO_ANY_LOG)
			System.out.print(s);
		old += s;
		sosLogger.log(s);
		if (splash != null)
			splash.setText(old);
	}

	private static void parsArgs(String[] args) {
		if (args.length == 0) {
			fb = -1;
			pf = -1;
			at = -1;
			center = -1;
			return;
		}
		for (int i = 0; i < args.length; ++i)
			if (args[i].equalsIgnoreCase("-fire")) {
				fb = -1;
			} else if (args[i].equalsIgnoreCase("-police")) {
				pf = -1;
			} else if (args[i].equalsIgnoreCase("-ambulance")) {
				at = -1;
			} else if (args[i].equalsIgnoreCase("-center")) {
				center = -1;
			} else if (args[i].equalsIgnoreCase("-all")) {
				fb = -1;
				pf = -1;
				at = -1;
				center = -1;
			}

		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals(FIRE_BRIGADE_FLAG)) {
				fb = Integer.parseInt(args[++i]);
			} else if (args[i].equals(POLICE_FORCE_FLAG)) {
				pf = Integer.parseInt(args[++i]);
			} else if (args[i].equals(AMBULANCE_TEAM_FLAG)) {
				at = Integer.parseInt(args[++i]);
			} else if (args[i].equals(AMBULANCE_CENTER_FLAG)) {
				ac = Integer.parseInt(args[++i]);
				center = (center == -1) ? ac : center + ac;
			} else if (args[i].equals(FIRE_STATION_FLAG)) {
				fs = Integer.parseInt(args[++i]);
				center = (center == -1) ? fs : center + fs;
			} else if (args[i].equals(POLICE_OFFICE_FLAG)) {
				po = Integer.parseInt(args[++i]);
				center = (center == -1) ? po : center + po;
			} else if (args[i].equals(CENTER_FLAG)) {
				po = Integer.parseInt(args[++i]);
				center = (center == -1) ? po : center + po;
			} else if (args[i].equals(MASHIN_NUMBER_FLAG)) {
				mashin_number = Integer.parseInt(args[++i]);
			} else {
				sosLogger.warn("The " + i + "th argument is Unrecognised option: " + args[i]);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static String[] showSOSDialog() {
		if (splash == null)
			splash = new Splash(10);
		JPanel optionPanel = new JPanel(new GridLayout(4, 2));

		final JToggleButton all = new JToggleButton("All");
		//		final JToggleButton fire = new JToggleButton("Fire");
		final CountButton fire = new CountButton("Fire ", "-fb", 30);
		//		final JToggleButton police = new JToggleButton("Police");
		final CountButton police = new CountButton("Police ", "-pf", 30);
		//		final JToggleButton ambulance = new JToggleButton("Ambulance");
		final CountButton ambulance = new CountButton("Amb", "-at", 30);
		//		final JToggleButton center = new JToggleButton("Center");
		final CountButton center = new CountButton("Center", "-ce", 20);

		final JToggleButton one = new JToggleButton("one");
		one.setBackground(Color.white);
		all.setBackground(Color.white);
		final JCheckBox useDefaultConnectionSettings = new JCheckBox("Default Connection Settings", true);

		final JTextField host = new JTextField(DEFAULT_HOST, 8);
		final JTextField port = new JTextField(DEFAULT_PORT);
		final JPanel connectionPanel = new JPanel();
		connectionPanel.add(new Label("Host:"));
		connectionPanel.add(host);
		connectionPanel.add(new Label("Port:"));
		connectionPanel.add(port);
		connectionPanel.setVisible(false);
		useDefaultConnectionSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				connectionPanel.setVisible(!connectionPanel.isVisible());
			}
		});
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(splash.cloneContent(), BorderLayout.BEFORE_FIRST_LINE);
		optionPanel.add(all);
		optionPanel.add(one);
		optionPanel.add(fire);
		optionPanel.add(police);
		optionPanel.add(ambulance);
		optionPanel.add(center);
		optionPanel.add(useDefaultConnectionSettings);
		optionPanel.add(connectionPanel);

		mainPanel.add(optionPanel);
		JOptionPane pane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION, null, null, null);
		pane.setWantsInput(true);
		pane.setFocusable(true);
		JDialog dialog = pane.createDialog(null, "SOS Starting dialog");
		pane.selectInitialValue();
		dialog.setAlwaysOnTop(true);
		dialog.show();
		dialog.dispose();
		String value = (String) pane.getInputValue();

		if (value == JOptionPane.UNINITIALIZED_VALUE) {
			System.exit(0);
		}
		ArrayList<String> argsList = new ArrayList<String>();
		if (all.isSelected())
			argsList.add("-all");
		else {
			/*
			 * if (fire.isSelected())
			 * argsList.add("-fire");
			 * if (ambulance.isSelected()) {
			 * argsList.add("-ambulance");
			 * }
			 * if (police.isSelected())
			 * argsList.add("-police");
			 * if (center.isSelected())
			 * argsList.add("-center");
			 */
			if (one.isSelected())
				argsList.add("one");
			if (fire.getCount() != 0 || police.getCount() != 0 || ambulance.getCount() != 0 || center.getCount() != 0) {
				argsList.addAll(fire.toArguments());
				argsList.addAll(police.toArguments());
				argsList.addAll(ambulance.toArguments());
				argsList.addAll(center.toArguments());
			} else {
				argsList.add("-all");
			}

		}
		if (!useDefaultConnectionSettings.isSelected()) {
			argsList.add("-h");
			argsList.add(host.getText());
			argsList.add("-p");
			argsList.add(port.getText());
		}
		String[] otherArgs = value.split(" ");
		for (String string : otherArgs) {
			if (!(string.equalsIgnoreCase(" ") || string.trim().equalsIgnoreCase("")))
				argsList.add(string);
		}
		for (int i = 0; i < argsList.size(); i++) {
			if (argsList.get(i).equalsIgnoreCase(" ") || argsList.get(i).trim().equalsIgnoreCase(""))
				argsList.remove(i--);
		}
		final String myArgs[] = argsList.toArray(new String[0]);
		return myArgs;
	}
}

class CountButton extends JPanel {
	/**
		 *
		 */
	private static final long serialVersionUID = 1L;
	private final String prefix;
	private JSpinner spinner;

	public CountButton(String title, String prefix, final int max) {
		//		setBackground(Color.white);
		//		setBorder(new StrokeBorder(new BasicStroke(1)));
		this.prefix = prefix;
		JLabel l = new JLabel(title);
		add(l);
		int count = 0;

		boolean all = count == 30;
		spinner = new JSpinner(new SpinnerNumberModel(count, 0, max, 1));
		final JCheckBox check = new JCheckBox("All");
		check.setOpaque(false);
		check.setSelected(all);
		spinner.setEnabled(!all);
		spinner.setPreferredSize(new Dimension(40, 20));
		check.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				spinner.setEnabled(!check.isSelected());
				//				if (check.isSelected()) {
				//					spinner.setValue(max);
				//				}
			}
		});
		add(spinner);
		add(check);

	}

	public int getCount() {
		if (spinner.isEnabled())
			return (Integer) spinner.getValue();
		return -1;
	}

	public List<String> toArguments() {
		ArrayList<String> args = new ArrayList<String>();
		args.add(prefix);
		args.add(getCount() + "");
		return args;
	}
}