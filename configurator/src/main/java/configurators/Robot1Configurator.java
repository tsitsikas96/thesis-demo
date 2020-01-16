package configurators;

import handlers.GeneralHandler;
import org.eclipse.leshan.server.registration.Registration;

public class Robot1Configurator implements Runnable{

    private Registration registration;
    private GeneralHandler handler;
    private static int MODEL = 20000;
    private static int INSTANCE = 0;
    private static int NEW_ORDER = 5;

    public Robot1Configurator(GeneralHandler handler, Registration registration){
        this.handler = handler;
        this.registration = registration;
    }

    @Override
    public void run() {
        System.out.println("Robot 1 Finished");
    }
}