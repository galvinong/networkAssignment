import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Galvin on 7/12/2015.
 */
public class Notification implements Serializable {

    //Notification object contains current timeStamp, and info included
    //Split into two different categories, URGENT and NORMAL
    //Timestamp is created when notification object is created

    static final int URGENT = 1, NORMAL = 2;
    private int type;
    String timeStamp;

    public Notification(int type) {
        this.type = type;
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public int getType() {
        return type;
    }
}

class urgentNotification extends Notification {
    String info = "";

    public urgentNotification(int type, String info) {
        super(type);
        this.info = info;
    }

    //Adds a urgent line here
    public synchronized String printInfo() {
        return "***URGENT*** Time: " + timeStamp + ", Info: " + info;
    }
}

class normalNotification extends Notification {
    String info = "";

    public normalNotification(int type, String info) {
        super(type);
        this.info = info;
    }

    public synchronized String printInfo() {
        return "Time: " + timeStamp + ", Info: " + info;
    }
}


