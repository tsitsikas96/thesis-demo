package robot3;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import robot3.fsm.Robot3Coordinator;
import robot3.lwm2m.Robot;
import robot3.lwm2m.RobotInstance;

@SpringBootApplication
public class Robot3CoordinatorApplication {

	public static Robot robot3;
	public static void main(String[] args) {
		if(args.length==2){
			ConfigurationUtils.W1_COAP_SERVER = args[0];
			ConfigurationUtils.CONFIGURATOR_SERVER = args[1];
			SpringApplication.run(Robot3CoordinatorApplication.class, args);
			Robot3Coordinator robot3controller = new Robot3Coordinator();

			robot3 = new Robot("Robot3", robot3controller, args);
			robot3.init();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Thread(RobotInstance.robot3Ctrl).start();
		}
		else{
			System.out.println("Please insert 2 parameter for w1server,configurator");
		}
	}
}
