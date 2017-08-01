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
public class NotificationSource extends UnicastRemoteObject implements Server, Runnable {
    //SERVER
    private static final long serialVersionUID = -464196277362659008L;
    String identity = "unknown";

    //List of clients
    List<Client> list = new ArrayList<>();

    //for run code
    boolean done = false;
    Random random = new Random();

    public NotificationSource() throws RemoteException {
        super();
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public boolean register(Client nSink) throws RemoteException {
        //Register the notification sinks to the list
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

    //Basically randomly generates temperatures, depending on the temperature, urgent or normal notification will be sent out
    public void run() {
        while (!done) {
            Notification msg = null;
            try {
                Thread.sleep(10 * 1000);
                int temp = ((random.nextInt(35 - 1 + 1) + 1));
                if (temp > 28 || temp < 10) {
                    System.out.println(temp);
                    msg = new urgentNotification(1, "Server Room A, Temperature: " + temp + "\u00b0c ABNORMAL!");
                } else {
                    msg = new normalNotification(2, "Server Room A, Temperature: " + temp + "\u00b0c");
                    System.out.println(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Stop while loop
                done = true;
            }
            Iterator iterator = list.iterator();

            //For every sink registered with the source, send the alert message
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
            //Instantiate the source and start the server
            NotificationSource notificationSource = new NotificationSource();

            //Default Port for RMI is 1099
            LocateRegistry.createRegistry(1099);
            notificationSource.start();

            System.out.println("Server Starting");
            Naming.rebind(NotificationSource.LOOKUP_NAME, notificationSource);
            System.out.println("Server Ready");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
