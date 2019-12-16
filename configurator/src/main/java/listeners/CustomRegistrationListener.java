package listeners;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.sun.org.apache.xpath.internal.operations.Bool;
import handlers.NodeHandler;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CustomRegistrationListener implements RegistrationListener {

    private LeshanServer server;
    private CustomObservationListener obsListener;
//    private final BlockingQueue<Boolean> messageQueue1;
//    private final BlockingQueue<Boolean> messageQueue2;
//    private final DockerClient dockerClient1;
//    private final DockerClient dockerClient2;

    public CustomRegistrationListener(LeshanServer server){
//        this.server = server;
//        this.dockerClient1 = createDockerClient("192.168.99.100","node1");
//        this.dockerClient2 = createDockerClient("192.168.99.103","node2");
//        this.messageQueue1 = new ArrayBlockingQueue<Boolean>(1);
//        this.messageQueue2 = new ArrayBlockingQueue<Boolean>(1);
//        this.obsListener = new CustomObservationListener(this.dockerClient1,this.dockerClient2,this.messageQueue1,this.messageQueue2);
//        server.getObservationService().addListener(obsListener);
    }

//    private DockerClient createDockerClient(String ip,String machineName){
//        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost("tcp://"+ ip +":2376")
//                .withDockerTlsVerify(true)
//                .withDockerCertPath("/home/ilias/.docker/machine/machines/"+machineName)
//                .build();
//
//// using jaxrs/jersey implementation here (netty impl is also available)
//        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
//                .withReadTimeout(5000)
//                .withConnectTimeout(1000)
//                .withMaxTotalConnections(100)
//                .withMaxPerRouteConnections(10);
//
//        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
//                .withDockerCmdExecFactory(dockerCmdExecFactory)
//                .build();
//        return dockerClient;
//    }

    @Override
    public void registered(Registration registration, Registration previousReg,
                           Collection<Observation> previousObsersations) {
        System.out.println("New Device: " + registration.getEndpoint());
//        switch (registration.getEndpoint()){
//            case "Node 1":
//                new Thread(new NodeHandler(this.server,registration,0,this.dockerClient1,this.messageQueue1)).start();
//                break;
//            case "Node 2":
//                new Thread(new NodeHandler(this.server,registration,1,this.dockerClient2,this.messageQueue2)).start();
//                break;
//        }

    }
    @Override
    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
        System.out.println("Device is still here: " + updatedReg.getEndpoint());
    }
    @Override
    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                             Registration newReg) {
        System.out.println("Device left: " + registration.getEndpoint());
    }
}
