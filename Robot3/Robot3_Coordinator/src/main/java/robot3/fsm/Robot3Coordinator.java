package robot3.fsm;

import robot3.ConfigurationUtils;
import robot3.Robot3CoordinatorApplication;
import robot3.fsm.signals.NewOrder;
import robot3.lwm2m.RobotInstance;
import robot3.unixsocket.UnixClient;
import robot3.unixsocket.UnixServer;
import uml4iot.GenericStateMachine.core.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import robot3.fsm.signals.SubAssR3Completed;
import robot3.fsm.signals.W1Pos3Available;


public class Robot3Coordinator extends StateMachine{

    public static Robot3CoordinatorState robot3State;
    public static Logger LOGGER;
    public static boolean pos3avail =false;
    public static BlockingQueue<SMReception> notificationQueue;
    public static BlockingQueue<String> atQueue = new LinkedBlockingDeque<>();
    State waiting4order,waiting4w1pos3,subAss3;
    private UnixClient unixClient = new UnixClient();
    UnixServer server = new UnixServer();
    public Robot3Coordinator(){
        super(null);
        notificationQueue = new ArrayBlockingQueue<SMReception>(10);
        waiting4order = new Waiting4Order();
        waiting4w1pos3 = new Waiting4W1Pos3();
        subAss3 = new SubAss3();

        new Waiting4Order_2_Waiting4W1Pos3(waiting4order,waiting4w1pos3);
        new waiting4W1Pos3_2_subAss3 (waiting4w1pos3,subAss3);
        new subAss3_2_waiting4order (subAss3,waiting4order);

        LOGGER = Logger.getLogger(Robot3Coordinator.class.getName() + " LOGGER");
        LOGGER.info("\n ");
        LOGGER.setLevel(Level.ALL); // Request that every detail gets logged.
        LOGGER.info("Robot3 starting");
//        setInitState(waiting4w1pos3);
        setInitState(waiting4order);
        server.start();
        LOGGER.info("R3: Assembly Coordinator State = " + robot3State +"\n");
    }

    public BlockingQueue<SMReception> getEventQueue() {
        return itsMsgQ;
    }

    //-----------------States----------------
    private class Waiting4Order extends State{

        @Override
        protected void entry() {
            robot3State = Robot3CoordinatorState.WAITING4ORDER;
            Robot3Coordinator.LOGGER.severe("R3: Assembly Coordinator State = " + robot3State + "\n");
            wait4Order();
        }

        @Override
        protected void doActivity() {

        }

        @Override
        protected void exit() {
            orderReceived();
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Waiting4W1Pos3 extends State {

        @Override
        protected void entry() {
            SendAcquire2W1Pos3();
            robot3State = Robot3CoordinatorState.WAITING4W1POS3;
            LOGGER.warning("R3: Assembly Coordinator State = " + robot3State +"\n");
        }

        @Override
        protected void doActivity() { }

        @Override
        protected void exit() { }
    }

    public class SubAss3 extends State {

        @Override
        protected void entry() {
            robot3State = Robot3CoordinatorState.SUBASS3;
            LOGGER.warning("R3: Assembly Coordinator State = " + robot3State +"\n");
            try {
                workOnW1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void doActivity() { }

        @Override
        protected void exit() {
            SendRelease2W1Pos3();
            try {
				Thread.sleep(750);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Robot3Coordinator.LOGGER.severe("R3: Notifying Configurator\n");
            Robot3CoordinatorApplication.robot3.robot3instance.fireResourcesChange(21);
        }
    }

    //---------------------Transitions-----------------------------------------

    class Waiting4Order_2_Waiting4W1Pos3 extends Transition {

        public Waiting4Order_2_Waiting4W1Pos3(State fromState, State toState) {
            super(fromState, toState);
        }

        @Override
        protected boolean trigger(SMReception smReception) {
            Robot3Coordinator.LOGGER.warning("R3: Event Reception = " + smReception +"\n");
            return (smReception instanceof NewOrder);
        }

        @Override
        protected void effect() { }
    }

    class waiting4W1Pos3_2_subAss3 extends Transition {

        public waiting4W1Pos3_2_subAss3(State fromState, State toState) {
            super(fromState, toState);
        }

        @Override
        protected boolean trigger(SMReception smReception) {
            Robot3Coordinator.LOGGER.warning("R3: Event Reception = " + smReception +"\n");
            return (smReception instanceof W1Pos3Available);
        }

        @Override
        protected void effect() { }
    }

    class subAss3_2_waiting4order extends Transition {

        public subAss3_2_waiting4order(State fromState, State toState) {
            super(fromState, toState);
        }

        @Override
        protected boolean trigger(SMReception smReception) {
            Robot3Coordinator.LOGGER.warning("R3: Event Reception = " + smReception +"\n");
            return (smReception instanceof SubAssR3Completed);
        }

        @Override
        protected void effect() { }
    }

    //---------------------Functions----------------

    private void wait4Order(){
        Robot3Coordinator.LOGGER.warning("Waiting for order...\n");
        try {
            RobotInstance.event =new JSONObject()
                    .put("event", "R3_WAIT_4_ORDER")
                    .put("value", "").toString();
            RobotInstance.orderQueue.take();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void orderReceived(){
        Robot3Coordinator.LOGGER.warning("Order Received. Moving on..."+"\n");
        try{
            RobotInstance.event2sim = new JSONObject()
                    .put("event2sim", "R3_ORDER_RECEIVED")
                    .put("value","").toString();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void SendAcquire2W1Pos3(){
        LOGGER.severe("Now Sending Acquire message to W1 for Pos3.."+"\n");
        try {
			RobotInstance.event = new JSONObject()
			        .put("event", "R3acquireW1")
			        .put("value", "").toString();
            Robot3CoordinatorApplication.robot3.robot3instance.fireResourcesChange(16);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    private void SendRelease2W1Pos3(){
        LOGGER.severe("Now Sending Release message to W1 for Pos3.."+"\n");
        try {
			RobotInstance.event = new JSONObject()
			        .put("event", "R3releaseW1")
			        .put("value", "").toString();
            Robot3CoordinatorApplication.robot3.robot3instance.fireResourcesChange(16);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    public void workOnW1() throws InterruptedException {
        LOGGER.severe("SubAss3 started.."+"\n");
        pos3avail =true;

        callAT6();

        outerloop:
        while(true){
            String value = atQueue.take();
            switch (value.toString()){
                case "AT6_FINISHED":
                    callAT7();
                    break;
                case "AT7_FINISHED":
                    callAT8();
                    break;
//                case "AT8_FINISHED":
//                    callAT9();
//                    break;
//                case "AT9_FINISHED":
//                    LOGGER.severe("SubAss3 completed.."+"\n");
//                    SignalDetector.msgQ.add(new SubAssR3Completed());
//                    break outerloop;
                case "AT8_FINISHED":
                    LOGGER.severe("SubAss3 completed.."+"\n");
                    SignalDetector.msgQ.add(new SubAssR3Completed());
                    break outerloop;
            }
        }
    }

    private void callAT9(){
        System.out.println(" \n \n \n AT9 \n \n \n ");
        String sendText = new JSONObject()
                .put("AT9", "START")
                .put("sender","coordinator")
                .toString();
        unixClient.communicateWithAT(ConfigurationUtils.AT9SocketFile,sendText);
    }

    private void callAT8(){
        System.out.println(" \n \n \n AT8 \n \n \n ");
        String sendText = new JSONObject()
                .put("AT8", "START")
                .put("sender","coordinator")
                .toString();
        unixClient.communicateWithAT(ConfigurationUtils.AT8SocketFile,sendText);
    }

    private void callAT7(){
        System.out.println(" \n \n \n AT7 \n \n \n ");
        String sendText = new JSONObject()
                .put("AT7", "START")
                .put("sender","coordinator")
                .toString();
        unixClient.communicateWithAT(ConfigurationUtils.AT7SocketFile,sendText);
    }

    private void callAT6(){
        System.out.println(" \n \n \n AT6 \n \n \n ");
        String sendText = new JSONObject()
                .put("AT6", "START")
                .put("sender","coordinator")
                .toString();
        unixClient.communicateWithAT(ConfigurationUtils.AT6SocketFile,sendText);
    }
}
