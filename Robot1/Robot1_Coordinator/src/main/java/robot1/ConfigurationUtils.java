package robot1;

import java.io.File;

public class ConfigurationUtils {

	public static String W1_COAP_SERVER;
	public static String W2_COAP_SERVER;
	public static String CONFIGURATOR_SERVER;

	//UNIX Sockets
	public static final File Robot1CtrlrSocketFile = new File("/tmp/robot1ctrl.sock");
	public static final File AT1SocketFile = new File("/tmp/at1.sock");
	public static final File AT4SocketFile = new File("/tmp/at4.sock");
	public static final File Robot1CoordinatorSocketFile = new File("/tmp/robot1coordinator.sock");

}
