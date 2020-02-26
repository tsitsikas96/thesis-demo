package robot3.lwm2m;

import java.io.*;
import java.util.*;

import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.request.BindingMode;
import static org.eclipse.leshan.LwM2mId.*;
import static org.eclipse.leshan.client.object.Security.*;
import static robot3.fsm.Robot3Coordinator.LOGGER;

import org.json.JSONException;
import org.json.JSONObject;
import robot3.ConfigurationUtils;
import robot3.Robot3CoordinatorApplication;

public abstract class AbstractDevice {
    String endpoint, localAddress, secureLocalAddress, serverURIforW1, serverURIforW2,serverURIforConfigurator, serverURIforSimulator;
    int localPort, secureLocalPort;
    boolean needBootstrap;
    byte[] pskIdentity, pskKey;

    private static final String USAGE = "java -jar [filename] [OPTIONS]";

    public AbstractDevice(String endpoint, String[] args) {
       
        this.endpoint = endpoint;
        this.localPort = (new Random()).nextInt(60000 - 20000) + 20000;
        this.serverURIforW1 =  ConfigurationUtils.W1_COAP_SERVER;
        this.serverURIforConfigurator = ConfigurationUtils.CONFIGURATOR_SERVER;
       
    }

    public void init() {
        List<LwM2mObjectEnabler> enablersW1 = this.createObjectsW1();
        List<LwM2mObjectEnabler> enablersConfigurator = this.createObjectsConfigurator();

        // Create client
        LeshanClientBuilder builderW1 = new LeshanClientBuilder(endpoint);
         
        builderW1.setObjects(enablersW1);
        final LeshanClient client2W1 = builderW1.build();
        // Start the client
        client2W1.start();

        LeshanClientBuilder builderConfigurator = new LeshanClientBuilder(endpoint);
        builderConfigurator.setObjects(enablersConfigurator);
        final LeshanClient client2Configurator = builderConfigurator.build();
        client2Configurator.start();

        // De-register on shutdown and stop client.
        Runtime.getRuntime().addShutdownHook(new Thread() {
              @Override
              public void run() {
              client2W1.destroy(true); // send de-registration request before destroy
              client2Configurator.destroy(true);
        }});
    }

    protected List<LwM2mObjectEnabler>  createObjectsW1() {

        ObjectsInitializer initializer = getObjectInitializerW1();
        return getEnablers(initializer);
    }

    protected List<LwM2mObjectEnabler>  createObjectsConfigurator() {

        ObjectsInitializer initializer = getObjectInitializerConfigurator();
        return getEnablers(initializer);
    }

    protected List<LwM2mObjectEnabler> getEnablers(ObjectsInitializer initializer) {
        List<LwM2mObjectEnabler> enablers = initializer.create(SECURITY, SERVER);
        return enablers;
    }

    protected ObjectsInitializer getObjectInitializerW1(){
        // Initialize object list
        ObjectsInitializer initializer;
        LwM2mModel model = getLwM2mModel();
        if (model==null){
            initializer = new ObjectsInitializer();
        }
        else {
            initializer = new ObjectsInitializer(model);
        }

        if (needBootstrap) {
            if (pskIdentity == null)
                initializer.setInstancesForObject(SECURITY, noSecBootstap(serverURIforW1));
            else
                initializer.setInstancesForObject(SECURITY, pskBootstrap(serverURIforW1, pskIdentity, pskKey));
        } else {
            if (pskIdentity == null) {
                initializer.setInstancesForObject(SECURITY, noSec(serverURIforW1, 123));
                initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            } else {
                initializer.setInstancesForObject(SECURITY, psk(serverURIforW1, 123, pskIdentity, pskKey));
                initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            }
        }
        return initializer;
    }

    protected ObjectsInitializer getObjectInitializerConfigurator(){
        ObjectsInitializer initializer;
        LwM2mModel model = getLwM2mModel();
        if (model==null){
            initializer = new ObjectsInitializer();
        }
        else {
            initializer = new ObjectsInitializer(model);
        }

        if (needBootstrap) {
            if (pskIdentity == null)
                initializer.setInstancesForObject(SECURITY, noSecBootstap(serverURIforConfigurator));
            else
                initializer.setInstancesForObject(SECURITY, pskBootstrap(serverURIforConfigurator, pskIdentity, pskKey));
        } else {
            if (pskIdentity == null) {
                initializer.setInstancesForObject(SECURITY, noSec(serverURIforConfigurator, 123));
                initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            } else {
                initializer.setInstancesForObject(SECURITY, psk(serverURIforConfigurator, 123, pskIdentity, pskKey));
                initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            }
        }
        return initializer;
    }

    protected LwM2mModel getLwM2mModel() {
        InputStream defaultSpec = this.getClass().getResourceAsStream("/models/oma-objects-spec.json");
        InputStream r3Spec = this.getClass().getResourceAsStream("/models/R3_Objects.json");
        List<ObjectModel> models = ObjectLoader.loadJsonStream(defaultSpec);
        models.addAll(ObjectLoader.loadJsonStream(r3Spec));
        return new ObjModel(models);
    }

    private class ObjModel implements LwM2mModel{

        private final Map<Integer,ObjectModel> objects;

        public ObjModel(Collection<ObjectModel> models){
            if(models==null){
                objects = new HashMap<>();
            }
            else {
                Map<Integer,ObjectModel> map = new HashMap<>();
                for(ObjectModel model : models){
                    ObjectModel old = map.put(model.id,model);
                    if(old!=null){
                        System.out.println("Model already exists for object "+ model.id+". Overriding it.");
                    }
                }
                objects = Collections.unmodifiableMap(map);
            }
        }

        @Override
        public ResourceModel getResourceModel(int objectId, int resourceId) {
            ObjectModel object = objects.get(objectId);
            if (object != null) {
                return object.resources.get(resourceId);
            }
            return null;
        }

        @Override
        public ObjectModel getObjectModel(int objectId) {
            return objects.get(objectId);
        }

        @Override
        public Collection<ObjectModel> getObjectModels() {
            return objects.values();
        }
    }
}