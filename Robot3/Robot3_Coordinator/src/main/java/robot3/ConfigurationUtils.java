package robot3;

import java.io.File;

public class ConfigurationUtils {

	//public static String W1_COAP_SERVER="coap://172.16.239.10:8563";
	public static String W1_COAP_SERVER; //="coap://localhost:8563";
	public static String CONFIGURATOR_SERVER;

	//UNIX Sockets
	public static final File Robot3CtrlrSocketFile = new File("/tmp/robot3ctrl.sock");
	public static final File AT6SocketFile = new File("/tmp/at6.sock");
	public static final File AT7SocketFile = new File("/tmp/at7.sock");
	public static final File AT8SocketFile = new File("/tmp/at8.sock");
	public static final File AT9SocketFile = new File("/tmp/at9.sock");

	public static final File Robot3CoordinatorSocketFile = new File("/tmp/robot3coordinator.sock");

}



