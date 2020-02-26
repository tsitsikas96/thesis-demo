package handlers;

import org.eclipse.leshan.core.request.ExecuteRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationService;

import java.sql.Timestamp;

public class GeneralHandler {

    private LeshanServer server;
    private RegistrationService service;

    public GeneralHandler(LeshanServer server_src){
        this.server = server_src;
        this.service = this.server.getRegistrationService();
    }

    public void sendObserveRequest(String endpoint, int objectId, int objectInstanceId, int resourceId){
        try {
            Registration reg = this.service.getByEndpoint(endpoint);
            ObserveResponse response = this.server.send(reg, new ObserveRequest(objectId,objectInstanceId,resourceId));
            if (response.isSuccess()) {
                System.out.println("Observing: " + objectId + "/"+objectInstanceId + "/" + resourceId + " at " + reg.getEndpoint());
            }else {
                System.out.println("Failed to observe: " + objectId + "/"+objectInstanceId + "/" + resourceId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sendExecuteRequest(String endpoint, int objectId, int objectInstanceId, int resourceId){
        try {
            Registration reg = this.service.getByEndpoint(endpoint);
            ExecuteResponse response = this.server.send(reg, new ExecuteRequest(objectId,objectInstanceId,resourceId,String.format("{ \"sender\": \"SERVER\", \"receiver\": \"ROBOT\", \"timestamp\": \"%s\"}", new Timestamp(System.currentTimeMillis()))));
            if (response.isSuccess()) {
                System.out.println("Executing: " + objectId + "/"+objectInstanceId + "/" + resourceId + " at " + reg.getEndpoint());
            }else {
                System.out.println("Failed to execute: " + objectId + "/"+objectInstanceId + "/" + resourceId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
