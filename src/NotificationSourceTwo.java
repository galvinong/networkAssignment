import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Galvin on 7/12/2015.
 */
public class NotificationSourceTwo extends UnicastRemoteObject implements Server, Runnable {
    //SERVER
    private static final long serialVersionUID = -464196277362659008L;
    String identity = "unknown";

    //List of clients
    List<Client> list = new ArrayList<>();

    //for run code
    boolean done = false;
    Random random = new Random();

    public NotificationSourceTwo() throws RemoteException {
        super();
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public boolean register(Client nSink) throws RemoteException {
        if (list.add(nSink)) {
            System.out.println("Registering client : " + nSink);
            for (int i = 0; i < list.size(); i++) {
                System.out.println("Current list of clients registered: " + list.get(i).getIdentity());
            }
            return true;
        } else {
            return false;
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

    public void run() {
        while (!done) {
            Notification msg = null;
            try {
                Thread.sleep(30 * 1000);

                msg = new normalNotification(2, "Weather temperature is " + random.nextInt((20 - 1 + 1) + 1) + "\u00b0c");
//                System.out.println(((normalNotification) msg).printInfo());
            } catch (Exception e) {
                e.printStackTrace();
                done = true; //Stop the code
            }
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Client currentClient = ((Client) iterator.next());
                try {
                    //Send the alert
                    currentClient.alert(msg);
                } catch (RemoteException e) {
                    System.out.println("Problem alerting Notification Sink, removing it from notifications");
                    iterator.remove();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            NotificationSourceTwo notificationSourceTwo = new NotificationSourceTwo();

            //Making the port different here, so that it doesn't clash on the same machine
            LocateRegistry.createRegistry(1100);
            notificationSourceTwo.start();

            System.out.println("Server Starting");
            Naming.rebind(NotificationSourceTwo.LOOKUP_NAMETWO, notificationSourceTwo);
            System.out.println("Server Ready");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
