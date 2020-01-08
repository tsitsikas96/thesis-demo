package robot3.lwm2m;

import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import robot3.fsm.Robot3Coordinator;
import robot3.fsm.SignalDetector;
import robot3.fsm.signals.NewOrder;
import robot3.fsm.signals.W1Pos3Available;
import uml4iot.GenericStateMachine.core.BaseSignal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RobotInstance extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(RobotInstance.class);
    public static int modelId = 20002;
    public static String event;
    public static String event2sim;
    public static Robot3Coordinator robot3Ctrl;
    public static SignalDetector signaldetect;
    public static BlockingQueue<Boolean> orderQueue = new ArrayBlockingQueue<Boolean>(1);
    public RobotInstance(final Robot3Coordinator robot3Ctrl, String endpoint) {
    	this.robot3Ctrl = robot3Ctrl;
    	signaldetect = new SignalDetector(robot3Ctrl.itsMsgQ);
    	event="";
    	event2sim="";
    }

    @Override
    public ReadResponse read(ServerIdentity identity,int resourceid) {
        LOG.info("Read on Device Resource " + resourceid);
        switch (resourceid) {
        case 0:     //get Status of Robot3Coordinator
            return ReadResponse.success(resourceid, getStatus());
        case 16:
            return ReadResponse.success(resourceid,event);
        case 20:
            return ReadResponse.success(resourceid,event2sim);
        case 21:
            return ReadResponse.success(resourceid,"Robot3 Done");
        default:
            return super.read(identity,resourceid);
        }
    }

	@Override
    public ExecuteResponse execute(ServerIdentity identity,int resourceid, String params) {
		LOG.info("Execute on Device Resource " + resourceid + params);
        switch (resourceid) {
        case 3:    //setPos3Available
        	 return addSignal(params, W1Pos3Available.class);
        case 5:
            try {
                orderQueue.put(true);
                return addSignal(params, NewOrder.class);
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
        return Robot3Coordinator.robot3State.toString();
    }

    private Boolean getpos3available() {
        return Robot3Coordinator.pos3avail;
    }

    private <T extends BaseSignal> ExecuteResponse addSignal(String args, Class<T> clazz){
        if (args == null){
            return ExecuteResponse.badRequest("Arguments not correct");
        }
       switch (clazz.getSimpleName()){
           case "W1Pos3Available":
               signaldetect.msgQ.add(new W1Pos3Available());
               return ExecuteResponse.success();
           case "NewOrder":
               signaldetect.msgQ.add(new NewOrder());
               return ExecuteResponse.success();
           default:
               return ExecuteResponse.badRequest(args) ;
       }
    }
}
