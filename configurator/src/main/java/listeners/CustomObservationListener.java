package listeners;

import configurators.Robot1Configurator;
import handlers.GeneralHandler;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;

import java.util.concurrent.BlockingQueue;

public class CustomObservationListener implements ObservationListener {

    private GeneralHandler handler;

    public CustomObservationListener(GeneralHandler handler){
        this.handler = handler;
    }

    public CustomObservationListener(){}

    @Override
    public void newObservation(Observation observation, Registration registration) {
    }

    @Override
    public void cancelled(Observation observation) {}

    @Override
    public void onResponse(Observation observation, Registration registration, ObserveResponse observeResponse) {
        if (observeResponse.isSuccess()) {
            System.out.println(registration.getEndpoint() + " finished");
            switch (registration.getEndpoint()){
                case "Robot1":
                    new Thread(new Robot1Configurator(this.handler,registration)).start();
                    break;
            }
        }
        else {
            System.out.println("Failed to read: " + observeResponse.getCode() + " " + observeResponse.getErrorMessage());
        }
    }

    @Override
    public void onError(Observation observation, Registration registration, Exception e) {}


}
