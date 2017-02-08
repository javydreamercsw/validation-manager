package net.sourceforge.javydreamercsw.validation.manager.web;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VMDemoResetThread extends Thread {

    public static VMDemoResetThread instance = null;
    public long reset_period = 86400000; //Daily
    private static final Logger LOG
            = Logger.getLogger(VMDemoResetThread.class.getName());

    @Override
    public void run() {
        while (true) {
            reset_period = DataBaseManager.getDemoResetPeriod();
            if (reset_period > 0) {
                try {
                    try {
                        sleep(reset_period);
                    } catch (InterruptedException se) {
                        break;
                    }
                    //Check again this is a demo environment, just in case
                    if (DataBaseManager.isDemo()) {
                        LOG.warning("Dropping tables...");
                        DataBaseManager.nativeQuery("DROP ALL OBJECTS");
                        LOG.warning("Done!");
                    }
                    //Reload the database
                    LOG.warning("Reloading DB...");
                    DataBaseManager.reload(true);
                    LOG.warning("Done!");
                } catch (VMException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    public static VMDemoResetThread getInstance() {
        if (instance == null) {
            instance = new VMDemoResetThread();
        }
        return instance;
    }

    VMDemoResetThread() {
    }
}
