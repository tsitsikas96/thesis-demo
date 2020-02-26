package configurators;

import servers.Configurator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OrderHandler implements Runnable {

    public static boolean all_connected = false;
    public static BlockingQueue<Boolean> nextorder = new ArrayBlockingQueue<>(1);
    private volatile LinkedList<Integer> chairs_in_orders_robot1;
    private volatile LinkedList<Integer> chairs_in_orders_robot2;
    private volatile LinkedList<Integer> chairs_in_orders_robot3;
    private volatile LinkedList<Boolean> orders_in_progress_robot3;

    public OrderHandler(LinkedList<Integer> chairs_in_orders_robot1, LinkedList<Integer> chairs_in_orders_robot2,
                        LinkedList<Integer> chairs_in_orders_robot3, LinkedList<Boolean> orders_in_progress_robot3){
        this.chairs_in_orders_robot1 = chairs_in_orders_robot1;
        this.chairs_in_orders_robot2 = chairs_in_orders_robot2;
        this.chairs_in_orders_robot3 = chairs_in_orders_robot3;
        this.orders_in_progress_robot3 = orders_in_progress_robot3;
    }

    @Override
    public void run() {
        String select = "SELECT head_cushion, quantity FROM orders ORDER BY time ASC LIMIT 1;";
        String delete = "DELETE FROM orders WHERE id in (SELECT id from orders ORDER BY time ASC LIMIT 1);";
        while(true){
            try {
                Statement stmt = Configurator.conn.createStatement();
                ResultSet rs = stmt.executeQuery(select);
                if(rs.next()){
                    boolean cushion = rs.getBoolean("head_cushion");
                    int quantity = rs.getInt("quantity");
                    this.chairs_in_orders_robot1.addLast(quantity);
                    this.chairs_in_orders_robot2.addLast(quantity);
                    this.orders_in_progress_robot3.addLast(cushion);
                    this.chairs_in_orders_robot3.addLast(quantity);
                    Robot3Configurator.order_received.put(true);
                    Robot2Configurator.order_received.put(true);
                    Robot1Configurator.order_received.put(true);
                    System.out.println("Order received");
                    nextorder.take();
                    stmt = Configurator.conn.createStatement();
                    stmt.executeUpdate(delete);
                }
                else{
                    Configurator.DBNotifacation.take();
                }
                stmt.close();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
