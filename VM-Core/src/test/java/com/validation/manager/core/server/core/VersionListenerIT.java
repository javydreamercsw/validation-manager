package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import static junit.framework.TestCase.*;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VersionListenerIT extends AbstractVMTestCase {

    /**
     * Test of onChange method, of class VersionListener.
     */
    @Test
    public void testOnChange() {
        System.out.println("onChange");
        DataBaseManager.setVersioningEnabled(true);
        int count = 0;
        int max = 10;
        Requirement entity = new Requirement("SRS0001", "Description "
                + (++count));
        RequirementJpaController controller
                = new RequirementJpaController(DataBaseManager
                        .getEntityManagerFactory());
        assertEquals(0, controller.findRequirementEntities().size());
        controller.create(entity);
        assertEquals(count, controller.findRequirementEntities().size());
        for (int i = 0; i < max; i++) {
            try {
                System.out.println("Modification: " + (i + 1));
                entity.setDescription("Description " + (++count));
                controller.edit(entity);
            }
            catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
                fail();
            }
            catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                fail();
            }
        }
        assertEquals(max + 1, controller.getRequirementCount());
        Requirement prev = null;
        int x = 0;
//        for (Requirement r : controller.findRequirementEntities()) {
//            assertNotNull(r.getModificationTime());
//            assertNotNull(r.getDescription());
//            assertEquals(x == 0, r.isDirty());
//            if (x > 0) {
//                //First one is the oldest one (version 0.0.max+1)
//                if (prev != null) {
//                    assertTrue(r.compareTo(prev) > 0);
//                }
//                prev = r;
//            }
//            x++;
//        }
    }
}
