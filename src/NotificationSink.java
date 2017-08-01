import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Galvin on 7/12/2015.
 */
public class NotificationSink extends UnicastRemoteObject implements Client {
    String identity = "unknown";

    protected NotificationSink() throws RemoteException {
    }

    @Override
    public void alert(Notification msg) throws RemoteException {

        //Handles the notifications received here, dependent on the type of notification
        switch (msg.getType()) {
            case Notification.URGENT:
                System.out.println(((urgentNotification) msg).printInfo());
                break;
            case Notification.NORMAL:
                System.out.println(((normalNotification) msg).printInfo());
                break;
        }
    }

    @Override
    public String getIdentity() throws RemoteException {
        try {
            identity = InetAddress.getLocalHost().getHostName() + " " + InetAddress.getLocalHost().getHostAddress();

        } catch (Exception e) {
            System.out.println(e);
        }
        return identity;
    }

    //CHANGE the HOST IP address for a different server
    protected final static String host = "localhost";
    protected final static String hostTwo = "192.168.33.4";

    //MAIN START CODE
    private void start() throws IOException, NotBoundException {
        System.out.println("Notification Sink Start");

        //ADD MORE NOTIFICATION SOURCES HERE
        Server tempServer = (Server) Naming.lookup("rmi://" + host + "/" + Server.LOOKUP_NAME);
        regServer(tempServer, Server.LOOKUP_NAME);


        Server weatherServer = (Server) Naming.lookup("rmi://" + host + "/" + Server.LOOKUP_NAMETWO);
        regServer(weatherServer, Server.LOOKUP_NAMETWO);
    }

    //Removes the repetition of code in the start code
    private void regServer(Server server, String LOOKUP_NAME) throws IOException, NotBoundException {
        System.out.println("Looking up server : " + server.getIdentity());

        if (server.register(this)) {
            System.out.println("Notification Sink registered with " + server.getIdentity() + " " + LOOKUP_NAME);
        } else {
            System.out.println("Error registering Notification Sink: " + LOOKUP_NAME);
        }
    }

    public static void main(String[] args) throws IOException, NotBoundException {
        //If using different machines, same port can be used
        LocateRegistry.getRegistry(host, 1099);
        LocateRegistry.getRegistry(hostTwo, 1100);

        //Start the sink code
        new NotificationSink().start();
    }

}
