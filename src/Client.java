import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Galvin on 7/12/2015.
 */
public interface Client extends Remote {
    //CLIENT

    //Alert the client whenever there's a update
    void alert(Notification msg) throws RemoteException;

    String getIdentity() throws RemoteException;

}
