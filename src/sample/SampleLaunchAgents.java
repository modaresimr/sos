package sample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
import sos.base.SOSConstant;
import sos.base.util.Splash;
import sos.base.util.TimeNamayangar;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * @author Ali
 *         Launcher for SOS agents. This will launch as many instances of each of the
 *         SOS agents as possible, all using one connection.
 */
public final class SampleLaunchAgents {
	private static final String DEFAULT_HOST = "sos-pc2";
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
	private static Config config = new Config();;
	public static final SOSLoggerSystem sosLogger = new SOSLoggerSystem(null, "Main", true, OutputType.Both, true);

	private SampleLaunchAgents() {
	}

	/**
	 * Launch 'em!
	 * 
	 * @param args
	 *            The following arguments are understood: -p <port>, -h
	 *            <hostname>, -fb <fire brigades>, -pf <police forces>, -at
	 *            <ambulance teams>
	 * @throws Exception
	 */
	public static void main(String[] args) {
		setConstantsFromConfig();
		boolean SHOW_SOS_STARTING_DIALOG = (!SOSConstant.IS_CHALLENGE_RUNNING) && SOSConstant.SHOW_SOS_STARTING_DIALOG;// because of warning
		try {
			if (!SOSConstant.IS_CHALLENGE_RUNNING && SOSConstant.SHOW_SOS_STARTING_SPLASH) {
				if (splash == null)
					splash = new Splash(10,"sample");
				splash.showSplash();
			}
			if (args.length > 0 || !(SHOW_SOS_STARTING_DIALOG)) {
				launch(args);
			} else {
				launch(showSOSDialog());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (splash != null)
				splash.exit();
			System.exit(-1);
		}
	}

	private static void setConstantsFromConfig() {
		try {
			Config config = new Config(new File("sos.config"));
			//if(config.getIntValue("sos.password")==SOSConstant.PASSWORD)
			if (!SOSConstant.IS_CHALLENGE_RUNNING)
				SOSConstant.IS_CHALLENGE_RUNNING = config.getBooleanValue("sos.IS_CHALLENGE_RUNNING", true);
			if (!SOSConstant.NO_ANY_LOG)
				SOSConstant.NO_ANY_LOG = config.getBooleanValue("sos.NO_ANY_LOG", true);
		} catch (ConfigException e1) {
			sosLogger.error(e1.getMessage());
		}
	}

	public static void launch(String targs[]) {
		ArrayList<String> s = new ArrayList<String>(Arrays.asList(targs));
		sosLogger.debug(s);
		if (s.contains("one")) {
			s.remove("one");
			justOneAgent = true;
		}

		String[] args = new String[s.size()];
		s.toArray(args);
		try {
			Registry.SYSTEM_REGISTRY.registerEntityFactory(StandardEntityFactory.INSTANCE);
			Registry.SYSTEM_REGISTRY.registerMessageFactory(StandardMessageFactory.INSTANCE);
			Registry.SYSTEM_REGISTRY.registerPropertyFactory(StandardPropertyFactory.INSTANCE);

			args = CommandLineOptions.processArgs(args, config);

			int port = config.getIntValue(Constants.KERNEL_PORT_NUMBER_KEY, Constants.DEFAULT_KERNEL_PORT_NUMBER);
			String host = config.getValue(Constants.KERNEL_HOST_NAME_KEY, Constants.DEFAULT_KERNEL_HOST_NAME);
			parsArgs(args);
			ComponentLauncher launcher = new TCPComponentLauncher(host, port, config);
			connect(launcher, fb, pf, at, config);
		} catch (IOException e) {
			sosLogger.fatal("Error connecting agents", e);
			System.exit(-1);
		} catch (ConfigException e) {
			sosLogger.fatal("Configuration error", e);
			System.exit(-1);
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


	private static void connect(ComponentLauncher launcher, int fb, int pf, int at, Config config) throws InterruptedException, ConnectionException, ComponentConnectionException {
		TimeNamayangar tm = new TimeNamayangar();
		tm.start();
		int connectedAgentCount = 0;
		while (fb-- != 0) {
			if (justOneAgent && connectedAgentCount == 1)
				break;
			print("\nConnecting fire brigade " + (connectedAgentCount) + "...");
			try {
				SOSSampleFireBrigade fire = new SOSSampleFireBrigade();
				launcher.connect(fire);
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
				SOSSampleAmbulanceTeam ambulance = new SOSSampleAmbulanceTeam();
				launcher.connect(ambulance);
				connectedAgentCount++;
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
				SOSSamplePoliceForce police = new SOSSamplePoliceForce();
				launcher.connect(police);
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
			splash = new Splash(10,"sample");
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
		center.setEnabledFalse();
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
			if (fire.getCount()!=0 || police.getCount()!=0 || ambulance.getCount()!=0 || center.getCount() !=0) {
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
	private JCheckBox check;

	public CountButton(String title, String prefix, final int max) {
		//		setBackground(Color.white);
		//		setBorder(new StrokeBorder(new BasicStroke(1)));
		this.prefix = prefix;
		JLabel l = new JLabel(title);
		add(l);
		int count = 0;

		boolean all = count == 30;
		spinner = new JSpinner(new SpinnerNumberModel(count, 0, max, 1));
		check = new JCheckBox("All");
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

	public void setEnabledFalse() {
		check.setEnabled(false);
		spinner.setEnabled(false);
		super.setEnabled(false);
	}

	public int getCount() {
		if(!isEnabled())
			return 0;
		if(spinner.isEnabled())
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