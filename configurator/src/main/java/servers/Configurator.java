package servers;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import configurators.UnixClient;
import listeners.CustomRegistrationListener;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Configurator {

    private final static String[] modelPaths = new String[] {"R1_Objects.xml","R2_Objects.xml","R3_Objects.xml","W1_Config.xml"};
    private static LeshanServer server;
    private static JettyServer httpserver;
    static final String DB_URL = "jdbc:pgsql://localhost:50000/gocas_shop";
    static final String USER = "admin";
    static final String PASS = "qwe123";
    public static PGConnection conn = null;
    private static PGNotificationListener listener;
    private static PGDataSource dataSource;
    public static BlockingQueue DBNotifacation = new ArrayBlockingQueue(1);

    public static LeshanServer createAndStartServer(){

        try {
            listener = new PGNotificationListener() {
                @Override
                public void notification(int processId, String channelName, String payload) {
                    try{
                        DBNotifacation.add(true);
                    }
                    catch (IllegalStateException e){}
                    
                }
            };
            dataSource = new PGDataSource();
            dataSource.setDatabaseUrl(DB_URL);
            dataSource.setUser(USER);
            dataSource.setPassword(PASS);
            System.out.println("Connecting to a selected database...");
            conn = (PGConnection) dataSource.getConnection();
            System.out.println("Connected database successfully...");
            conn.addNotificationListener(listener);
            Statement statement = conn.createStatement();
            statement.execute("LISTEN q_event");
            statement.close();
        }
        catch(SQLException se){
            System.out.println("Please start database first");
        }
        catch(Exception e) {
//            e.printStackTrace();
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