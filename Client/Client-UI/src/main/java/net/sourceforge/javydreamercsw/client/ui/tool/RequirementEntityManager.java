package net.sourceforge.javydreamercsw.client.ui.tool;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = VMEntityManager.class)
public class RequirementEntityManager implements VMEntityManager<Requirement>,
        LookupListener {

    private Map<String, Requirement> map = new TreeMap<>();
    private Lookup.Result<Project> result = null;
    private Project current = null;
    private static final Logger LOG
            = Logger.getLogger(RequirementEntityManager.class.getSimpleName());

    public RequirementEntityManager() {
        result = Utilities.actionsGlobalContext().lookupResult(Project.class);
        result.allItems();
        result.addLookupListener(RequirementEntityManager.this);
    }

    @Override
    public boolean supportEntity(Class entity) {
        return entity.equals(Requirement.class)
                || entity.isInstance(Requirement.class);
    }

    @Override
    public void updateEntity(Requirement entity) {
        if (map.containsKey(entity.getUniqueId())) {
            RequirementServer rs = new RequirementServer(entity);
            rs.update();
            map.put(entity.getUniqueId(), Collections.max(rs.getVersions()));
        }
    }

    @Override
    public void removeEntity(Requirement entity) {
        if (map.containsKey(entity.getUniqueId())) {
            map.remove(entity.getUniqueId());
        }
    }

    @Override
    public Collection<Requirement> getEntities() {
        return map.values();
    }

    @Override
    public void addEntity(Requirement entity) {
        if (!map.containsKey(entity.getUniqueId())) {
            RequirementServer rs = new RequirementServer(entity);
            rs.update();
            map.put(entity.getUniqueId(), Collections.max(rs.getVersions()));
        }
    }

    @Override
    public Requirement getEntity(Object entity) {
        assert entity instanceof String : "Invalid parameter!";
        return map.get((String) entity);
    }

    public class RequirementPopulatorAction extends AbstractAction {

        private final RequestProcessor RP
                = new RequestProcessor("Requirement Populator", 1, true);
        private RequestProcessor.Task theTask = null;
        private ProgressHandle ph;

        @Override
        public void actionPerformed(ActionEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ph = ProgressHandleFactory.createHandle("Requirement Populator",
                            new Cancellable() {

                                @Override
                                public boolean cancel() {
                                    return handleCancel();
                                }
                            });
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            LOG.log(Level.FINE,
                                    "Populating requirements for project: {0}",
                                    current.getName());
                            map.clear();
                            addProjectRequirements(current);
                        }
                    };
                    theTask = RP.create(runnable); //the task is not started yet

                    theTask.addTaskListener(new TaskListener() {
                        public void taskFinished(RequestProcessor.Task task) {
                            ph.finish();
                            LOG.log(Level.FINE,
                                    "Populating requirements for project: {0} done!",
                                    current.getName());
                        }

                        @Override
                        public void taskFinished(org.openide.util.Task task) {
                            ph.finish();
                            LOG.log(Level.FINE,
                                    "Populating requirements for project: {0} done!",
                                    current.getName());
                        }
                    });
                    //start the progresshandle the progress UI will show 500s after
                    ph.start();

                    //this actually start the task
                    theTask.schedule(0);
                }
            });
        }

        private boolean handleCancel() {
            LOG.info("handleCancel");
            if (null == theTask) {
                return false;
            }
            return theTask.cancel();
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Lookup.Result res = (Lookup.Result) le.getSource();
        Collection instances = res.allInstances();

        if (!instances.isEmpty()) {
            Iterator it = instances.iterator();
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof Project) {
                    Project p = (Project) item;
                    if (p != current) {
                        current = p;
                        new RequirementPopulatorAction().actionPerformed(null);
                    }
                }
            }
        }
    }

    private void addProjectRequirements(Project p) {
        ProjectServer ps = new ProjectServer(p);
        for (RequirementSpec rs : ps.getRequirementSpecList()) {
            for (RequirementSpecNode rsn : rs.getRequirementSpecNodeList()) {
                for (Requirement r : rsn.getRequirementList()) {
                    addEntity(r);
                }
            }
        }
        for (Project child : ps.getChildren()) {
            addProjectRequirements(child);
        }
    }
}
