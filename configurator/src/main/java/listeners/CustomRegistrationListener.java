package listeners;

import handlers.GeneralHandler;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import servlets.OrderHandler;
import java.util.Collection;

public class CustomRegistrationListener implements RegistrationListener {

    private LeshanServer server;
    private GeneralHandler handler;
    private int MODEL1 = 20000,MODEL2 = 20001, MODEL3 = 20002;
    private int INSTANCE = 0;
    private int ROBOT_FINISHED = 21;
    private int connections = 0;
    private Registration[] registrations = {null,null,null};

    public CustomRegistrationListener(LeshanServer server){
        this.server = server;
        this.handler = new GeneralHandler(this.server);
        this.server.getObservationService().addListener(new CustomObservationListener(this.handler));
    }

    @Override
    public void registered(Registration registration, Registration previousReg,
                           Collection<Observation> previousObservations) {
        System.out.println("New Device: " + registration.getEndpoint());
        this.connections++;
        switch (registration.getEndpoint()){
            case "Robot1":
                this.registrations[0] = registration;
                this.handler.sendObserveRequest(registration,MODEL1,INSTANCE,ROBOT_FINISHED);
                break;
            case "Robot2":
                this.registrations[1] = registration;
                this.handler.sendObserveRequest(registration,MODEL2,INSTANCE,ROBOT_FINISHED);
                break;
            case "Robot3":
                this.registrations[2] = registration;
                this.handler.sendObserveRequest(registration,MODEL3,INSTANCE,ROBOT_FINISHED);
                break;
        }
        if(this.connections == 3){
            OrderHandler.all_connected = true;
            new Thread(new OrderHandler(this.handler,this.registrations)).start();
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
        this.connections--;
        OrderHandler.all_connected = false;
    }
}
