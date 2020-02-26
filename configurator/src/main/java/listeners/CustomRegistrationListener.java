package listeners;

import configurators.*;
import handlers.GeneralHandler;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomRegistrationListener implements RegistrationListener {

    private LeshanServer server;
    private GeneralHandler handler;
    private int MODEL1 = 20000,MODEL2 = 20001, MODEL3 = 20002;
    private int INSTANCE = 0;
    private int ROBOT_FINISHED = 21;
    private int connections = 0;
    private UnixClient unixClient;
    private boolean order_handler_up = false;
    private volatile LinkedList<Boolean> orders_in_progress_robot3 = new LinkedList();
    private volatile LinkedList<Integer> chairs_in_orders_robot1 = new LinkedList();
    private volatile LinkedList<Integer> chairs_in_orders_robot2 = new LinkedList();
    private volatile LinkedList<Integer> chairs_in_orders_robot3 = new LinkedList();
    private static boolean Thread3_started = false;

    public CustomRegistrationListener(LeshanServer server){
        this.server = server;
        this.handler = new GeneralHandler(this.server);
        this.server.getObservationService().addListener(new CustomObservationListener(this.handler));
        this.unixClient = new UnixClient();
    }

    @Override
    public void registered(Registration registration, Registration previousReg,
                           Collection<Observation> previousObservations) {
        System.out.println("New Device: " + registration.getEndpoint());
        this.connections++;
        switch (registration.getEndpoint()){
            case "Robot1":
                Robot1Configurator.R1_Connected = true;
                this.handler.sendObserveRequest("Robot1",MODEL1,INSTANCE,ROBOT_FINISHED);
                new Thread(new Robot1Configurator(this.handler,this.chairs_in_orders_robot1)).start();
                break;
            case "Robot2":
                Robot2Configurator.R2_Connected = true;
                this.handler.sendObserveRequest("Robot2",MODEL2,INSTANCE,ROBOT_FINISHED);
                new Thread(new Robot2Configurator(this.handler,this.chairs_in_orders_robot2)).start();
                break;
            case "Robot3":
                this.handler.sendObserveRequest("Robot3",MODEL3,INSTANCE,ROBOT_FINISHED);
                if(!Thread3_started){
                    Thread3_started = true;
                    new Thread(new Robot3Configurator(this.handler,this.unixClient,this.chairs_in_orders_robot3,
                            this.orders_in_progress_robot3)).start();
                }
                if(Robot3Configurator.robot3_left){
                    try {
                        Robot3Configurator.robot3_disconnect_queue.put(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Robot3Configurator.robot3_left = false;
                }
                break;
        }
        if(this.connections==3 & !order_handler_up){
            OrderHandler.all_connected = true;
            new Thread(new OrderHandler(this.chairs_in_orders_robot1,this.chairs_in_orders_robot2,
                    this.chairs_in_orders_robot3,this.orders_in_progress_robot3)).start();
            order_handler_up = true;
        }
    }
    @Override
    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
        System.out.println("Device is still here: " + updatedReg.getEndpoint());
    }
    @Override
    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                             Registration newReg) {
        System.out.println("Device left: " + registration.getEndpoint());
        if(registration.getEndpoint().matches("Robot3")){
            Robot3Configurator.robot3_left = true;
        }
        this.connections--;

    }
}
