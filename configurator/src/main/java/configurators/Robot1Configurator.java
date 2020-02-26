package configurators;

import handlers.GeneralHandler;
import org.eclipse.leshan.server.registration.Registration;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Robot1Configurator implements Runnable{

    protected static volatile LinkedBlockingQueue order_received = new LinkedBlockingQueue();
    public static BlockingQueue robot1_chairs_queue = new ArrayBlockingQueue(1);
    private GeneralHandler handler;
    private int MODEL1 = 20000;
    private int INSTANCE = 0;
    private int NEW_ORDER = 5;
    private int chairs_to_make;
    public static int orders_made = 1;
    private LinkedList<Integer> chairs_in_orders_robot1;
    public static volatile boolean robot1_working;
    public static boolean R1_Connected = false;

    public Robot1Configurator(GeneralHandler handler, LinkedList<Integer> chairs_in_orders_robot1){
        this.handler = handler;
        this.chairs_in_orders_robot1 = chairs_in_orders_robot1;
    }

    @Override
    public void run() {
        while(true){
            try {
                order_received.take();
                robot1_working = true;
                this.chairs_to_make = this.chairs_in_orders_robot1.peekFirst();
                for(int i=0;i<this.chairs_to_make;i++){
                    OrderUtils.robot1_chairs_made = i+1;
                    this.handler.sendExecuteRequest("Robot1",MODEL1,INSTANCE,NEW_ORDER);
                    robot1_chairs_queue.take();
                }
                this.chairs_in_orders_robot1.removeFirst();
                orders_made++;
                robot1_working = false;
                OrderHandler.nextorder.put(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
