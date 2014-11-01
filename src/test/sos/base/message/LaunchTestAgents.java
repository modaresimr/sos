package test.sos.base.message;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

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
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.util.namayangar.NamayangarsList;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * @author Ali
 *         Launcher for SOS agents. This will launch as many instances of each of the
 *         SOS agents as possible, all using one connection.
 */
public final class LaunchTestAgents {
	//private static final String DEFAULT_HOST = "192.168.128.183";
	private static Config config ;
	public static final SOSLoggerSystem sosLogger = new SOSLoggerSystem(null, "Main", true, OutputType.Both, true);


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
	 * @throws IOException 
	 * @throws Exception
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		setConstantsFromConfig(args);
		setGui();
		setRegistery();
		launch();
	}

	private static void setGui() {
		setToolkit();
		if (namayangarsList == null)
			namayangarsList = new NamayangarsList();

	}

	private static void setRegistery() {
		Registry.SYSTEM_REGISTRY.registerEntityFactory(StandardEntityFactory.INSTANCE);
		Registry.SYSTEM_REGISTRY.registerMessageFactory(StandardMessageFactory.INSTANCE);
		Registry.SYSTEM_REGISTRY.registerPropertyFactory(StandardPropertyFactory.INSTANCE);
	}

	private static void setToolkit() {
		try {
			Toolkit xToolkit = Toolkit.getDefaultToolkit();
			java.lang.reflect.Field awtAppClassNameField;
			awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, "Agent");
		} catch (Exception e1) {
			//				e1.printStackTrace();
		}
	}

	private static void setConstantsFromConfig(String[] args) throws IOException {
		
		try {
			config = new Config(new File("sos.config"));
			CommandLineOptions.processArgs(args, config);
			//if(config.getIntValue("sos.password")==SOSConstant.PASSWORD)
			if (!SOSConstant.IS_CHALLENGE_RUNNING)
				SOSConstant.IS_CHALLENGE_RUNNING = config.getBooleanValue("sos.IS_CHALLENGE_RUNNING", true);
			if (!SOSConstant.NO_ANY_LOG)
				SOSConstant.NO_ANY_LOG = config.getBooleanValue("sos.NO_ANY_LOG", true);
		} catch (ConfigException e1) {
			sosLogger.error(e1.getMessage());
		}
	}

	public static void launch() {
		println("Launching sos test agents");
		try {
		
			int port = config.getIntValue(Constants.KERNEL_PORT_NUMBER_KEY, Constants.DEFAULT_KERNEL_PORT_NUMBER);
			String host = config.getValue(Constants.KERNEL_HOST_NAME_KEY, Constants.DEFAULT_KERNEL_HOST_NAME);
			
			ComponentLauncher launcher = new TCPComponentLauncher(host, port, config);
			connect(launcher, new MessageTestAgent(), config);
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

	private static void connect(ComponentLauncher launcher, SOSAgent<?> agent, Config config) throws InterruptedException, ConnectionException, ComponentConnectionException {
		launcher.connect(agent);
		namayangarsList.addAgent(agent);
		println("success " + agent);		
	}

	static NamayangarsList namayangarsList = null;

	public static void println(String s) {
		if (SOSConstant.NO_ANY_LOG)
			System.out.println(s);
		sosLogger.logln(s);
	}

	public static void print(String s) {
		if (SOSConstant.NO_ANY_LOG)
			System.out.print(s);
		sosLogger.log(s);
	}

}