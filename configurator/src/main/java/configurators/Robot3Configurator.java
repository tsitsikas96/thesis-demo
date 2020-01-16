package configurators;

import handlers.GeneralHandler;
import org.eclipse.leshan.server.registration.Registration;
import servlets.OrderHandler;

public class Robot3Configurator implements Runnable{

    private Registration registration;
    private GeneralHandler handler;
    private int MODEL = 20002;
    private int INSTANCE = 0;
    private int NEW_ORDER = 5;

    public Robot3Configurator(GeneralHandler handler, Registration registration){
        this.handler = handler;
        this.registration = registration;
    }

    @Override
    public void run() {
        System.out.println("Robot 3 Finished");
        try {
            OrderHandler.orderqueue.put(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}