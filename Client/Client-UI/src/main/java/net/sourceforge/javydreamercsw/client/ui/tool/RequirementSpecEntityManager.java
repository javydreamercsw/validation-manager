package net.sourceforge.javydreamercsw.client.ui.tool;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
public class RequirementSpecEntityManager implements
        VMEntityManager<RequirementSpec>, LookupListener {

    private Map<String, RequirementSpec> map = new TreeMap<>();
    private Lookup.Result<Project> result = null;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecEntityManager.class.getSimpleName());
    private Project current;

    public RequirementSpecEntityManager() {
        result = Utilities.actionsGlobalContext().lookupResult(Project.class);
        result.allItems();
        result.addLookupListener(RequirementSpecEntityManager.this);
    }

    @Override
    public boolean supportEntity(Class entity) {
        return entity.equals(RequirementSpec.class)
                || entity.isInstance(RequirementSpec.class);
    }

    @Override
    public void updateEntity(RequirementSpec entity) {
        if (map.containsKey(entity.getName())) {
            RequirementSpecServer rs = new RequirementSpecServer(entity);
            rs.update();
            map.put(entity.getName(), rs.getEntity());
        }
    }

    @Override
    public void removeEntity(RequirementSpec entity) {
        if (map.containsKey(entity.getName())) {
            map.remove(entity.getName());
        }
    }

    @Override
    public Collection<RequirementSpec> getEntities() {
        List<RequirementSpec> entities = new ArrayList<>();
        for (Map.Entry<String, RequirementSpec> entry : map.entrySet()) {
            entities.add(entry.getValue());
        }
        return entities;
    }

    @Override
    public void addEntity(RequirementSpec entity) {
        if (!map.containsKey(entity.getName())) {
            RequirementSpecServer rs = new RequirementSpecServer(entity);
            rs.update();
            map.put(entity.getName(), rs.getEntity());
        }
    }

    @Override
    public RequirementSpec getEntity(Object entity) {
        assert entity instanceof String : "Invalid parameter!";
        return map.get((String) entity);
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
                        new RequirementSpecPopulatorAction().actionPerformed(null);
                    }
                }
            }
        }
    }

    public class RequirementSpecPopulatorAction extends AbstractAction {

        private final RequestProcessor RP
                = new RequestProcessor("Requirement Spec Populator", 1, true);
        private RequestProcessor.Task theTask = null;
        private ProgressHandle ph;

        @Override
        public void actionPerformed(ActionEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ph = ProgressHandleFactory.createHandle("Requirement Spec Populator",
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
                                    "Populating Specs for project: {0}",
                                    current.getName());
                            map.clear();
                            ProjectServer ps = new ProjectServer(current);
                            List<RequirementSpec> specs = ps.getRequirementSpecList();
                            for(Project child:ps.getChildren()){
                                specs.addAll(child.getRequirementSpecList());
                            }
                            for (RequirementSpec spec : specs) {
                                map.put(spec.getName(), spec);
                            }
                        }
                    };
                    theTask = RP.create(runnable); //the task is not started yet

                    theTask.addTaskListener(new TaskListener() {
                        public void taskFinished(RequestProcessor.Task task) {
                            ph.finish();
                            LOG.log(Level.FINE,
                                    "Populating requirement specs for project: {0} done!",
                                    current.getName());
                        }

                        @Override
                        public void taskFinished(org.openide.util.Task task) {
                            ph.finish();
                            LOG.log(Level.FINE,
                                    "Populating requirement specs for project: {0} done!",
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
}
