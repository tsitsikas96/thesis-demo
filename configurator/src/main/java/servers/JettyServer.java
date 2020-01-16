package servers;

import handlers.GeneralHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.OrderServlet;

public class JettyServer implements Runnable{

    private Server server;

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
