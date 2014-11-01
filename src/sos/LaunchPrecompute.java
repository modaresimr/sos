package sos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.UnsupportedLookAndFeelException;

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
import sos.base.SOSConstant;
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
public final class LaunchPrecompute {
	//private static final String DEFAULT_HOST = "192.168.128.183";
	public static String host;

	public static final SOSLoggerSystem sosLogger = new SOSLoggerSystem(null, "Main", true, OutputType.Both, true);

	private LaunchPrecompute() {
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
		System.out.println("sos-version:build 13-403");
		//		args=new String[]{"-police"};

		setRegistry();
		setConstantsFromConfig(args);
		try {
			launch();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
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
			connect(launcher, 1, 1, 1, SOSConstant.getConfig());
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

	private static void setConstantsFromConfig(String[] args) throws IOException, ConfigException {
		try {
			SOSConstant.setConfig(new Config(new File("sos.config")));
		} catch (ConfigException e1) {
			sosLogger.error(e1.getMessage());
		}

		ArrayList<String> s = new ArrayList<String>(Arrays.asList(args));
		sosLogger.debug(s);

		String[] targs = new String[s.size()];
		targs = s.toArray(targs);
		targs = CommandLineOptions.processArgs(targs, SOSConstant.getConfig());
		//		System.out.println(Arrays.asList(targs));
		//		System.out.println(fb +" "+pf+" "+at);
		System.out.println(SOSConstant.getConfig());
		//		System.out.println(config.getValue(Constants.KERNEL_HOST_NAME_KEY));
	}

	static NamayangarsList namayangarsList = null;

	private static void connect(ComponentLauncher launcher, int fb, int pf, int at, Config config) throws InterruptedException, ConnectionException, ComponentConnectionException {

		TimeNamayangar tm = new TimeNamayangar();
		tm.start();
		int connectedAgentCount = 0;
		while (fb-- != 0) {
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
		old = "";
	}

	public static void print(String s) {
		if (SOSConstant.NO_ANY_LOG)
			System.out.print(s);
		old += s;
		sosLogger.log(s);
	}

}