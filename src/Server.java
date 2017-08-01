import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Galvin on 7/12/2015.
 */
public interface Server extends Remote {
    //ADD Server Lookup Names here
    String LOOKUP_NAME = "TEMPERATURE_SERVER";
    String LOOKUP_NAMETWO = "WEATHER_SERVER";

    boolean register(Client nSink) throws RemoteException;

    String getIdentity() throws RemoteException;
}
