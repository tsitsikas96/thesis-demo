package handlers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import handlers.GeneralHandler;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import java.util.concurrent.BlockingQueue;

public class NodeHandler implements Runnable{

    private LeshanServer server;
    private Registration registration;
    private GeneralHandler handler;
    private int MODEL = 5000;
    private int INSTANCE;
    private int BLINK_DONE = 5700;
    private DockerClient dockerClient;
    private BlockingQueue<Boolean> msgQueue;

    public NodeHandler(LeshanServer server_src, Registration registration,int instance, DockerClient dockerClient,BlockingQueue<Boolean> msgQueue){
        this.server = server_src;
        this.registration = registration;
        this.handler = new GeneralHandler(server_src,registration);
        this.INSTANCE = instance;
        this.dockerClient = dockerClient;
        this.msgQueue = msgQueue;
    }

    @Override
    public void run() {
        this.handler.sendObserveRequest(MODEL, INSTANCE, BLINK_DONE);
        try {
            this.msgQueue.take();
            System.out.println("Node Passed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CreateContainerCmd new_cont = this.dockerClient.createContainerCmd("leshanclient:latest")
                .withName("node_"+ (this.INSTANCE+1))
                .withCmd("-inst",Integer.toString(this.INSTANCE),"-uri","192.168.99.104","-mon_uri","192.168.99.1","-endpt","Node "+(this.INSTANCE+1));
        CreateContainerResponse response = new_cont.exec();
        WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(response.getId()).exec(resultCallback);
        try {
            resultCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.dockerClient.startContainerCmd("node_"+(this.INSTANCE + 1)).exec();
    }
}