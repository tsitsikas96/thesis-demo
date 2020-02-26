package configurators;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.OutputStream;

public class UnixClient {

    private static final File socketFile = new File("/tmp/deployer.sock");

    public void communicateWithAT(String message){

        try {
            AFUNIXSocket sock = AFUNIXSocket.newInstance();
            sock.connect(new AFUNIXSocketAddress(socketFile));

            try(OutputStream os = sock.getOutputStream()) {
                System.out.println("Starting AT " + os);
                os.write(message.getBytes("UTF-8"));
                os.flush();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }

}
