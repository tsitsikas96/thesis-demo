package configurators;

import handlers.GeneralHandler;
import org.eclipse.leshan.server.registration.Registration;

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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.handler.sendExecuteRequest(registration,MODEL,INSTANCE,NEW_ORDER);
    }
}