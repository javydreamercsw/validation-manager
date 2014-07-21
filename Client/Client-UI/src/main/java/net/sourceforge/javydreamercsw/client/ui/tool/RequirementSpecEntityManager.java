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

    private Map<Integer, RequirementSpec> map = new TreeMap<>();
    private Lookup.Result<Project> result = null;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecEntityManager.class.getSimpleName());
    private Project current;
    private boolean initialized = false;

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
        if (map.containsKey(entity.getRequirementSpecPK().getId())) {
            RequirementSpecServer rs = new RequirementSpecServer(entity);
            rs.update();
            map.put(entity.getRequirementSpecPK().getId(), rs.getEntity());
        }
    }

    @Override
    public void removeEntity(RequirementSpec entity) {
        if (map.containsKey(entity.getRequirementSpecPK().getId())) {
            map.remove(entity.getRequirementSpecPK().getId());
        }
    }

    @Override
    public Collection<RequirementSpec> getEntities() {
        List<RequirementSpec> entities = new ArrayList<>();
        map.entrySet().stream().forEach((entry) -> {
            entities.add(entry.getValue());
        });
        return entities;
    }

    @Override
    public void addEntity(RequirementSpec entity) {
        if (!map.containsKey(entity.getRequirementSpecPK().getId())) {
            RequirementSpecServer rs = new RequirementSpecServer(entity);
            rs.update();
            map.put(entity.getRequirementSpecPK().getId(), rs.getEntity());
        }
    }

    @Override
    public RequirementSpec getEntity(Object entity) {
        assert entity instanceof Integer : "Invalid parameter!";
        return map.get((Integer) entity);
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
                    //Only change when the root project changes.
                    if (p != current && p.getParentProjectId() == null) {
                        current = p;
                        new RequirementSpecPopulatorAction().actionPerformed(null);
                    }
                }
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    public class RequirementSpecPopulatorAction extends AbstractAction {

        private final RequestProcessor RP
                = new RequestProcessor("Requirement Spec Populator", 1, true);
        private RequestProcessor.Task theTask = null;
        private ProgressHandle ph;

        @Override
        public void actionPerformed(ActionEvent e) {
            java.awt.EventQueue.invokeLater(() -> {
                ph = ProgressHandleFactory.createHandle(
                        "Requirement Spec Populator", () -> handleCancel());
                Runnable runnable = () -> {
                    initialized = false;
                    LOG.log(Level.FINE,
                            "Populating Specs for project: {0}",
                            current.getName());
                    map.clear();
                    ProjectServer ps = new ProjectServer(current);
                    List<RequirementSpec> specs = ps.getRequirementSpecList();
                    ps.getChildren().stream().forEach((child) -> {
                        specs.addAll(child.getRequirementSpecList());
                    });
                    specs.stream().forEach((spec) -> {
                        map.put(spec.getRequirementSpecPK().getId(), spec);
                    });
                    initialized = true;
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
