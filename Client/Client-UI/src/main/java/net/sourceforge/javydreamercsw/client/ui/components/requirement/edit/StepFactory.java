/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.client.ui.components.requirement.edit;

import com.validation.manager.core.db.Step;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepFactory extends ChildFactory<Step> implements Lookup.Provider {

    /**
     * The lookup for Lookup.Provider
     */
    private final Lookup lookup;
    /**
     * The InstanceContent that keeps this entity's abilities
     */
    private final InstanceContent instanceContent;
    private final ArrayList<Step> steps = new ArrayList<Step>();

    public StepFactory() {
        // Create an InstanceContent to hold abilities...
        instanceContent = new InstanceContent();
        // Create an AbstractLookup to expose InstanceContent contents...
        lookup = new AbstractLookup(instanceContent);
        // Add a "Reloadable" ability to this entity
        instanceContent.add(new Reloadable() {
            @Override
            public void reload() throws Exception {
//                for (Iterator<? extends Step> it = Lookup.getDefault().lookupAll(Step.class).iterator(); it.hasNext();) {
//                    ICardGame game = it.next();
//                    if (!games.contains(game)) {
//                        games.add(game);
//                    }
//                }
//                LOG.log(Level.INFO, "Games found: {0}", games.size());
//                Collections.sort(games, new Comparator<ICardGame>() {
//                    @Override
//                    public int compare(ICardGame o1, ICardGame o2) {
//                        return o1.getName().compareTo(o2.getName());
//                    }
//                });
            }
        });
    }
    
    public void refresh() {
        refresh(false);
    }

    @Override
    protected boolean createKeys(List<Step> toPopulate) {
        // The query node is reloadable, isn't it? Then just
        // get this ability from the lookup ...
        Reloadable r = getLookup().lookup(Reloadable.class);
        // ... and  use the ability
        if (r != null) {
            try {
                r.reload();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        toPopulate.addAll(steps);
        return true;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
