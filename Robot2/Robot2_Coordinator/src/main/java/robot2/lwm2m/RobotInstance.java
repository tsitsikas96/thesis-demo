package robot2.lwm2m;

import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import robot2.fsm.Robot2Coordinator;
import robot2.fsm.SignalDetector;
import robot2.fsm.signals.NewOrder;
import robot2.fsm.signals.W1Pos2Available;
import robot2.fsm.signals.W2Available;
import uml4iot.GenericStateMachine.core.BaseSignal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RobotInstance extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(RobotInstance.class);
    public static int modelId = 20001;
    public static String event;
    public static String event2sim;
    public static Robot2Coordinator robot2Coordinator;
    public static SignalDetector signaldetect;
    public static BlockingQueue<Boolean> orderQueue = new ArrayBlockingQueue<Boolean>(1),
            r3_disconnected = new ArrayBlockingQueue<>(1);
    public static boolean r3_dc = false;
    public RobotInstance(final Robot2Coordinator robot2Coordinator, String endpoint) {
    	this.robot2Coordinator = robot2Coordinator;
    	signaldetect = new SignalDetector(robot2Coordinator.itsMsgQ);
    	event="";
    	event2sim="";
    }

    @Override
    public ReadResponse read(ServerIdentity identity, int resourceid) {
        LOG.info("Read on Device Resource " + resourceid);
        switch (resourceid) {
        case 0:     //get Status of Robot2Coordinator
            return ReadResponse.success(resourceid, getStatus());
        case 16:
            return ReadResponse.success(resourceid,event);
        case 20:
            return ReadResponse.success(resourceid,event2sim);
        case 21:
            return ReadResponse.success(resourceid,"Robot2 Done");
        default:
            return super.read(identity,resourceid);
        }
    }

	@Override
    public ExecuteResponse execute(ServerIdentity identity,int resourceid, String params) {
		LOG.info("Execute on Device Resource " + resourceid + params);
        switch (resourceid) {
        case 3:    //setW1Pos2Available
            return addSignal(params, W1Pos2Available.class);
        case 4:    //setw2Available
            return addSignal(params, W2Available.class);
        case 5:
            try {
                orderQueue.put(true);
                return addSignal(params, NewOrder.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        case 6:
            r3_dc = true;
            return ExecuteResponse.success();
        case 7:
            try {
                r3_dc = false;
                r3_disconnected.put(true);
                return ExecuteResponse.success();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        default:
            return execute(identity,resourceid, params);
        }
    }

    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {
		return null;
    }

    private String getStatus() {
        return Robot2Coordinator.robot2State.toString();
    }
    
    private <T extends BaseSignal> ExecuteResponse addSignal(String args, Class<T> clazz){
        if (args == null){
            return ExecuteResponse.badRequest("Arguments not correct");
        }
        System.out.println(clazz.getSimpleName());

        switch(clazz.getSimpleName()){
            case "W1Pos2Available":    //setPos1Available
                signaldetect.msgQ.add( new W1Pos2Available());
                return ExecuteResponse.success();
            case "W2Available":    //setW2Available
                signaldetect.msgQ.add(new W2Available());
                return ExecuteResponse.success();
            case "NewOrder":
                signaldetect.msgQ.add(new NewOrder());
                return ExecuteResponse.success();
            default:
                return ExecuteResponse.badRequest(args) ;
        }
    }
}
