package servers;

import listeners.CustomRegistrationListener;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ConfiguratorTest {

    private final static String[] modelPaths = new String[] {"R1_Objects.xml","R2_Objects.xml","R3_Objects.xml"};
    private static LeshanServer server;
    private static JettyServer httpserver;
    static final String DB_URL = "jdbc:mysql://localhost:50000/gocas_shop";
    static final String USER = "admin";
    static final String PASS = "qwe123";
    public static Connection conn = null;

    public static LeshanServer createAndStartServer(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");
        }
        catch(SQLException se){
            System.out.println("Please start database first");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

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
        httpserver = new JettyServer();
        new Thread(httpserver).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try{
                conn.close();
                httpserver.stop();
                server.stop();
            }catch(SQLException se){}
            System.out.println("\nGoodbye!");
        }));
    }

}