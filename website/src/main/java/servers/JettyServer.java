package servers;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.OrderServlet;

import java.sql.SQLException;
import java.sql.Statement;

public class JettyServer implements Runnable {

    private Server server;
    static final String DB_URL = "jdbc:pgsql://localhost:50000/gocas_shop";
    static final String USER = "admin";
    static final String PASS = "qwe123";
    public static PGConnection conn = null;
    private static PGDataSource dataSource;

    public static void main(String[] args) {
        JettyServer httpserver = new JettyServer();
        new Thread(httpserver).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try{
                conn.close();
            }catch(SQLException se){}
            System.out.println("\nGoodbye!");
        }));
    }

    private void start() throws Exception {
        this.server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setWelcomeFiles(new String[]{"index.htm"});
        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("resourceBase","./src/main/java/frontend/");
        holderPwd.setInitParameter("dirAllowed","true");
        context.addServlet(holderPwd,"/");
        this.server.setHandler(context);
        context.addServlet(new ServletHolder(new OrderServlet()),"/order");
        this.server.start();
        try {
            dataSource = new PGDataSource();
            dataSource.setDatabaseUrl(DB_URL);
            dataSource.setUser(USER);
            dataSource.setPassword(PASS);
            System.out.println("Connecting to a selected database...");
            conn = (PGConnection) dataSource.getConnection();
            System.out.println("Connected database successfully...");
        }
        catch(SQLException se){
            System.out.println("Please start database first");
        }
    }

    public void stop(){
        try {
            this.server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
