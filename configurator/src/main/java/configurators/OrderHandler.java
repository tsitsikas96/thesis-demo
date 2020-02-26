package servlets;

import configurators.UnixClient;
import handlers.GeneralHandler;
import listeners.CustomObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import servers.ConfiguratorTest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OrderHandler implements Runnable {

    public static boolean all_connected = false;
    public static int ROBOT1_CHAIRS, ROBOT2_CHAIRS, ROBOT3_CHAIRS;
    private static boolean previous_order = false,broken_loop = false;
    public static BlockingQueue<Boolean> orderqueue = new ArrayBlockingQueue<>(1);
    private GeneralHandler handler;
    private Registration[] registrations;
    private UnixClient unix_client;
    private int MODEL1 = 20000,MODEL2 = 20001, MODEL3 = 20002;
    private int INSTANCE = 0;
    private int NEW_ORDER = 5;

    public OrderHandler(GeneralHandler handler, Registration[] registrations, UnixClient unix_client){
        this.registrations = registrations;
        this.handler = handler;
        this.unix_client = unix_client;
    }

    @Override
    public void run() {
        String select = "SELECT head_cushion, quantity FROM orders ORDER BY time ASC LIMIT 1;";
        String delete = "DELETE FROM orders WHERE id in (SELECT id from orders ORDER BY time ASC LIMIT 1);";
        while(all_connected){
            try {
                Statement stmt = ConfiguratorTest.conn.createStatement();
                ResultSet rs = stmt.executeQuery(select);
                if(rs.next() != false){
                    boolean head_cushion = rs.getBoolean("head_cushion");
                    int quantity = rs.getInt("quantity");
                    ROBOT1_CHAIRS = quantity;
                    ROBOT2_CHAIRS = quantity;
                    ROBOT3_CHAIRS = quantity;
                    System.out.println("Order received");
                    if(!broken_loop){
                        this.handler.sendExecuteRequest(this.registrations[0],MODEL1,INSTANCE,NEW_ORDER);
                        this.handler.sendExecuteRequest(this.registrations[1],MODEL2,INSTANCE,NEW_ORDER);
                    }
                    if(head_cushion != previous_order){
                        this.unix_client.communicateWithAT(head_cushion ? "with_cushion":"no_cushion");
                        previous_order = head_cushion;
                        broken_loop = true;
                        break;
                    }
                    this.handler.sendExecuteRequest(this.registrations[2],MODEL3,INSTANCE,NEW_ORDER);
                    orderqueue.take();
                    CustomObservationListener.robot1_chairs_made = 1;
                    CustomObservationListener.robot2_chairs_made = 1;
                    CustomObservationListener.robot3_chairs_made = 1;
//                    for(int i=1;i<quantity;i++){
//                        this.handler.sendExecuteRequest(this.registrations[0],MODEL1,INSTANCE,NEW_ORDER);
//                        this.handler.sendExecuteRequest(this.registrations[1],MODEL2,INSTANCE,NEW_ORDER);
//                        this.handler.sendExecuteRequest(this.registrations[2],MODEL3,INSTANCE,NEW_ORDER);
//                        orderqueue.take();
//                    }
                    stmt = ConfiguratorTest.conn.createStatement();
                    stmt.executeUpdate(delete);
                    previous_order = head_cushion;
                    broken_loop = false;
                }
                else{
                    ConfiguratorTest.DBNotifacation.take();
                }
                stmt.close();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
