package configurators;

import handlers.GeneralHandler;
import listeners.CustomRegistrationListener;
import org.eclipse.leshan.server.registration.Registration;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Robot3Configurator implements Runnable{

    protected static volatile LinkedBlockingQueue order_received = new LinkedBlockingQueue();
    public static volatile BlockingQueue robot3_chairs_queue = new ArrayBlockingQueue(1);
    public static volatile BlockingQueue robot3_disconnect_queue = new ArrayBlockingQueue(1);
    private GeneralHandler handler;
    private int  MODEL1 = 20000, MODEL2 = 20001, MODEL3 = 20002;
    private int INSTANCE = 0;
    private int NEW_ORDER = 5, R3_DISCONNECTED = 6, R3_RECONNECTED = 7;
    private int chairs_to_make;
    private static volatile Boolean previous_order = false, current_order;
    public static volatile boolean robot3_left = false;
    private UnixClient unix_client;
    public static int orders_made = 1;
    private volatile LinkedList<Integer> chairs_in_orders_robot3;
    private volatile LinkedList<Boolean> orders_in_progress_robot3;

    public Robot3Configurator(GeneralHandler handler,UnixClient unix_client,
                              LinkedList<Integer> chairs_in_orders_robot3, LinkedList<Boolean> orders_in_progress_robot3){
        this.handler = handler;
        this.unix_client = unix_client;
        this.chairs_in_orders_robot3 = chairs_in_orders_robot3;
        this.orders_in_progress_robot3 = orders_in_progress_robot3;
    }

    @Override
    public void run() {
        while (true){
            try {
                order_received.take();
                current_order = this.orders_in_progress_robot3.removeFirst();
                if(current_order != previous_order){
                    this.unix_client.communicateWithAT(current_order ? "with_cushion":"no_cushion");
//                    if(Robot2Configurator.robot2_working){
//                        this.handler.sendExecuteRequest("Robot2",MODEL2,INSTANCE,R3_DISCONNECTED);
//                    }
//                        this.handler.sendExecuteRequest(CustomRegistrationListener.r2_reg,MODEL2,INSTANCE,R3_DISCONNECTED);
                    robot3_disconnect_queue.take();
//                    if(Robot2Configurator.robot2_working){
//                        this.handler.sendExecuteRequest("Robot2",MODEL2,INSTANCE,R3_RECONNECTED);
//                    }
                }

                previous_order = current_order;

                this.chairs_to_make = this.chairs_in_orders_robot3.removeFirst();
                for(int i=0;i<this.chairs_to_make;i++){
                    OrderUtils.robot3_chairs_made = i+1;
                    this.handler.sendExecuteRequest("Robot3",MODEL3,INSTANCE,NEW_ORDER);
                    robot3_chairs_queue.take();
                }
                orders_made++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (NullPointerException n){}
        }
    }
}