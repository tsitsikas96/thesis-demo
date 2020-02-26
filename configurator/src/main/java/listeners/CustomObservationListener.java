package listeners;

import configurators.*;
import handlers.GeneralHandler;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;

public class CustomObservationListener implements ObservationListener {

    private GeneralHandler handler;
    public static long stop;

    public CustomObservationListener(GeneralHandler handler){
        this.handler = handler;
    }

    @Override
    public void newObservation(Observation observation, Registration registration) {
    }

    @Override
    public void cancelled(Observation observation) {}

    @Override
    public void onResponse(Observation observation, Registration registration, ObserveResponse observeResponse) {
        if (observeResponse.isSuccess()) {
            switch (registration.getEndpoint()){
                case "Robot1":
                    System.out.println("Robot 1 Finished chair: " + OrderUtils.robot1_chairs_made + " from order: " + Robot1Configurator.orders_made);
                    try {
                        Robot1Configurator.robot1_chairs_queue.put(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Robot2":
                  System.out.println("Robot 2 Finished chair: " + OrderUtils.robot2_chairs_made + " from order: " + Robot2Configurator.orders_made);
                    try {
                        Robot2Configurator.robot2_chairs_queue.put(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Robot3":
                    System.out.println("Robot 3 Finished chair: " + OrderUtils.robot3_chairs_made + " from order: " + Robot3Configurator.orders_made);
                    try {
                        Robot3Configurator.robot3_chairs_queue.put(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }
        else {
            System.out.println("Failed to read: " + observeResponse.getCode() + " " + observeResponse.getErrorMessage());
        }
    }

    @Override
    public void onError(Observation observation, Registration registration, Exception e) {}

}
