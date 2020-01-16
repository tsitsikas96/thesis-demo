package servlets;

import handlers.GeneralHandler;
import org.eclipse.leshan.server.registration.Registration;
import servers.ConfiguratorTest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OrderHandler implements Runnable {

    public static boolean all_connected = false;
    public static BlockingQueue<Boolean> orderqueue = new ArrayBlockingQueue<>(1);
    private GeneralHandler handler;
    private Registration[] registrations;
    private int MODEL1 = 20000,MODEL2 = 20001, MODEL3 = 20002;
    private int INSTANCE = 0;
    private int NEW_ORDER = 5;

    public OrderHandler(GeneralHandler handler, Registration[] registrations){
        this.registrations = registrations;
        this.handler = handler;
    }

    @Override
    public void run() {
        String select = "SELECT head_cushion, quantity FROM orders ORDER BY time ASC LIMIT 1;";
        String delete = "DELETE FROM `orders` ORDER BY time ASC LIMIT 1;";
        while(all_connected){
            try {
                Statement stmt = ConfiguratorTest.conn.createStatement();
                ResultSet rs = stmt.executeQuery(select);
                if(rs.next() != false){
                    Boolean head_cushion = rs.getBoolean("head_cushion");
                    int quantity = rs.getInt("quantity");
                    System.out.println("Order received");
                    stmt = ConfiguratorTest.conn.createStatement();
                    stmt.executeUpdate(delete);
                    for(int i=0;i<quantity;i++){
                        this.handler.sendExecuteRequest(this.registrations[0],MODEL1,INSTANCE,NEW_ORDER);
                        this.handler.sendExecuteRequest(this.registrations[1],MODEL2,INSTANCE,NEW_ORDER);
                        this.handler.sendExecuteRequest(this.registrations[2],MODEL3,INSTANCE,NEW_ORDER);
                        orderqueue.take();
                    }
                }
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
