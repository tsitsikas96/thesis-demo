package listeners;

import handlers.GeneralHandler;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

import java.util.Collection;

public class CustomRegistrationListener implements RegistrationListener {

    private LeshanServer server;
    private int MODEL1 = 20000,MODEL2 = 20001, MODEL3 = 20002;
    private int INSTANCE = 0;
    private int ROBOT_FINISHED = 21;
    private GeneralHandler handler;

    public CustomRegistrationListener(LeshanServer server){
        this.server = server;
        this.handler = new GeneralHandler(this.server);
        this.server.getObservationService().addListener(new CustomObservationListener(this.handler));
    }

    @Override
    public void registered(Registration registration, Registration previousReg,
                           Collection<Observation> previousObservations) {
        System.out.println("New Device: " + registration.getEndpoint());
        switch (registration.getEndpoint()){
            case "Robot1":
                this.handler.sendObserveRequest(registration,MODEL1,INSTANCE,ROBOT_FINISHED);
                this.handler.sendExecuteRequest(registration,MODEL1,INSTANCE,5);
                break;
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
    }
}
