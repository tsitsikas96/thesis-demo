package handlers;

import org.eclipse.leshan.core.request.ExecuteRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;

public class GeneralHandler {

    private LeshanServer server;
    private Registration registration;

    public GeneralHandler(LeshanServer server_src, Registration registration){
        this.server = server_src;
        this.registration = registration;
    }

    public void sendObserveRequest(int objectId,int objectInstanceId, int resourceId){
        try {
            ObserveResponse response = this.server.send(this.registration, new ObserveRequest(objectId,objectInstanceId,resourceId));
            if (response.isSuccess()) {
                System.out.println("Observing: " + objectId + "/"+objectInstanceId + "/" + resourceId + " at " + this.registration.getEndpoint());
            }else {
                System.out.println("Failed to observe: " + objectId + "/"+objectInstanceId + "/" + resourceId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
