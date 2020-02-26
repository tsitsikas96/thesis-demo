package configurators;

import handlers.GeneralHandler;
import org.eclipse.leshan.server.registration.Registration;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Robot2Configurator implements Runnable{
    protected static volatile LinkedBlockingQueue order_received = new LinkedBlockingQueue();
//    protected static BlockingQueue robot1_2_order_received = new ArrayBlockingQueue(1);
    public static BlockingQueue robot2_chairs_queue = new ArrayBlockingQueue(1);
    private Registration registration;
    private GeneralHandler handler;
    private int MODEL2 = 20001;
    private int INSTANCE = 0;
    private int NEW_ORDER = 5;
    private int chairs_to_make;
    public static int orders_made = 1;
    public static boolean R2_Connected = false;
    public static volatile boolean robot2_working;
    private LinkedList<Integer> chairs_in_orders_robot2;

    public Robot2Configurator(GeneralHandler handler, LinkedList<Integer> chairs_in_orders_robot2){
        this.handler = handler;
        this.chairs_in_orders_robot2 = chairs_in_orders_robot2;
    }

    @Override
    public void run() {
        while(true){
            try {
                order_received.take();
                robot2_working = true;
                this.chairs_to_make = this.chairs_in_orders_robot2.peekFirst();
                for(int i=0;i<this.chairs_to_make;i++){
                    OrderUtils.robot2_chairs_made = i+1;
                    this.handler.sendExecuteRequest("Robot2",MODEL2,INSTANCE,NEW_ORDER);
                    robot2_chairs_queue.take();
                }
                this.chairs_in_orders_robot2.removeFirst();
                orders_made++;
                robot2_working = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}