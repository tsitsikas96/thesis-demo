package listeners;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.github.dockerjava.jaxrs.StopContainerCmdExec;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;

import java.util.concurrent.BlockingQueue;

public class CustomObservationListener implements ObservationListener {

    private DockerClient dockerClient1;
    private DockerClient dockerClient2;
    private BlockingQueue<Boolean> msgQueue1;
    private BlockingQueue<Boolean> msgQueue2;

    public CustomObservationListener(DockerClient dockerClient1, DockerClient dockerClient2, BlockingQueue<Boolean> msgQueue1, BlockingQueue<Boolean> msgQueue2){
        this.dockerClient1 = dockerClient1;
        this.dockerClient2 = dockerClient2;
        this.msgQueue1 = msgQueue1;
        this.msgQueue2 = msgQueue2;
    }

    @Override
    public void newObservation(Observation observation, Registration registration) {
    }

    @Override
    public void cancelled(Observation observation) {}

    @Override
    public void onResponse(Observation observation, Registration registration, ObserveResponse observeResponse) {
        if (observeResponse.isSuccess()) {
            System.out.println("Blink Complete "+ registration.getEndpoint() + ": " + ((LwM2mResource)observeResponse.getContent()).getValue());
            switch (registration.getEndpoint()){
                case "Node 1":
                    this.dockerClient1.stopContainerCmd("node_1").exec();
                    this.dockerClient1.removeContainerCmd("node_1").exec();
                    try {
                        this.msgQueue1.put(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("DOULEPSE NODE 1");
                    break;
                case "Node 2":
                    this.dockerClient2.stopContainerCmd("node_2").exec();
                    this.dockerClient2.removeContainerCmd("node_2").exec();
                    try {
                        this.msgQueue2.put(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("DOULEPSE NODE 2");
                    break;
            }
        }
        else {
            System.out.println("Failed to read: " + observeResponse.getCode() + " " + observeResponse.getErrorMessage());
        }
    }

    @Override
    public void onError(Observation observation, Registration registration, Exception e) {}


}
