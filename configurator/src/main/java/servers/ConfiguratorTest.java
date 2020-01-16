import listeners.CustomRegistrationListener;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConfiguratorTest {

    private final static String[] modelPaths = new String[] {"R1_Objects.xml","R2_Objects.xml","R3_Objects.xml"};
    private static LeshanServer server;
    public static BlockingQueue<Boolean> queue1 = new ArrayBlockingQueue<>(1);
    public static LeshanServer createAndStartServer(){

        LeshanServerBuilder builder = new LeshanServerBuilder();

        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
        LwM2mModelProvider modelProvider = new StaticModelProvider(models);
        builder.setObjectModelProvider(modelProvider);

        final LeshanServer server = builder.build();

        server.start();
        return server;
    }

    private static void setRegistrationListener(){
        CustomRegistrationListener regListener = new CustomRegistrationListener(server);
        server.getRegistrationService().addListener(regListener);
    }

    public static void main(String[] args) throws Exception {
        server = createAndStartServer();
        setRegistrationListener();
        new Thread(new JettyServer()).start();
    }

}