/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.history;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.DemoBuilder;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.HistoryJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import static junit.framework.TestCase.assertEquals;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VersionableTest extends AbstractVMTestCase {

    /**
     * Test of increaseMajorVersion method, of class Versionable.
     */
    @Test
    public void testIncreaseMajorVersion() {
        System.out.println("increaseMajorVersion");
        Versionable instance = new VersionableImpl();
        assertEquals(0, (int) instance.getMajorVersion());
        assertEquals(0, (int) instance.getMidVersion());
        assertEquals(1, (int) instance.getMinorVersion());
        instance.increaseMajorVersion();
        assertEquals(1, (int) instance.getMajorVersion());
        assertEquals(0, (int) instance.getMidVersion());
        assertEquals(0, (int) instance.getMinorVersion());
        assertEquals(2, instance.getHistoryList().size());
        checkHistory(instance);
    }

    /**
     * Test of increaseMidVersion method, of class Versionable.
     */
    @Test
    public void testIncreaseMidVersion() {
        System.out.println("increaseMidVersion");
        Versionable instance = new VersionableImpl();
        assertEquals(0, (int) instance.getMajorVersion());
        assertEquals(0, (int) instance.getMidVersion());
        assertEquals(1, (int) instance.getMinorVersion());
        instance.increaseMidVersion();
        assertEquals(0, (int) instance.getMajorVersion());
        assertEquals(1, (int) instance.getMidVersion());
        assertEquals(0, (int) instance.getMinorVersion());
        assertEquals(2, instance.getHistoryList().size());
        checkHistory(instance);
    }

    /**
     * Test of toString method, of class Versionable.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Versionable instance = new VersionableImpl();
        String r = instance.toString();
        assertEquals("Version: " + instance.getMajorVersion()
                + "." + instance.getMidVersion() + "."
                + instance.getMinorVersion(), instance.toString());
        instance.increaseMajorVersion();
        assertEquals("Version: " + instance.getMajorVersion()
                + "." + instance.getMidVersion() + "."
                + instance.getMinorVersion(), instance.toString());
        instance.increaseMidVersion();
        assertEquals("Version: " + instance.getMajorVersion()
                + "." + instance.getMidVersion() + "."
                + instance.getMinorVersion(), instance.toString());
    }

    /**
     * Test of compareTo method, of class Versionable.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Versionable instance = new VersionableImpl();
        assertEquals(0, instance.compareTo(new VersionableImpl()));
        instance.increaseMidVersion();
        assertEquals(1, instance.compareTo(new VersionableImpl()));
        Versionable instance2 = new VersionableImpl();
        instance2.increaseMajorVersion();
        assertEquals(-1, instance.compareTo(instance2));
    }

    /**
     * Test of auditable method, of class Versionable.
     */
    @Test
    public void testAuditable() {
        try {
            System.out.println("auditable");
            VersionableImpl v = new VersionableImpl();
            assertEquals(true, Versionable.auditable(v));
            v.updateHistory();
            assertEquals(1, v.getHistoryList().size());
            assertEquals(false, Versionable.auditable(v));
            v.setField1(2);
            v.updateHistory();
            assertEquals(1, v.getHistoryList().size());
            assertEquals(false, Versionable.auditable(v));
            v.setField2("new value");
            assertEquals(true, Versionable.auditable(v));
            v.updateHistory();
            assertEquals(2, v.getHistoryList().size());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    @Test
    public void testVersioning() {
        try {
            DemoBuilder.buildDemoProject();
            EntityManager em = DataBaseManager.getEntityManager();
            //Populate with demo classes.
            for (EntityType<?> entity : em.getMetamodel().getEntities()) {
                if (Versionable.class.isAssignableFrom(entity.getJavaType())) {
                    final String className = entity.getName();
                    System.out.println("Testing class: " + className);
                    //Make sure they have at least one auditable field.
                    List<Field> fields = FieldUtils
                            .getFieldsListWithAnnotation(entity.getJavaType(),
                                    Auditable.class);
                    assertEquals(false, fields.isEmpty());
                    //Get one from the demo data
                    Versionable v = (Versionable) DataBaseManager
                            .createdQuery("Select a from "
                                    + entity.getJavaType().getSimpleName()
                            + " a").get(0);
                    assertNotNull(v);
                    v.updateHistory();
                    int count = v.getHistoryList().size();
                    for (Field f : fields) {
                        //Now pick one of the Auditable fields
                        assertEquals(count, v.getHistoryList().size());
                        History history = v.getHistoryList().get(v
                                .getHistoryList().size() - 1);
                        assertEquals(count == 1 ? "audit.general.creation"
                                : "audit.general.modified", history.getReason());
                        assertEquals(0, history.getMajorVersion());
                        assertEquals(0, history.getMidVersion());
                        assertEquals(count++, history.getMinorVersion());
                        assertEquals(1, (int) history.getModifierId().getId());
                        assertNotNull(history.getModificationTime());
                        assertTrue(checkHistory(v));
                        assertFalse(Versionable.auditable(v));
                        System.out.println("Changing field: "
                                + f.getName() + " Type: "
                                + f.getType().getSimpleName());
                        f.setAccessible(true);
                        if (f.getType() == Integer.class) {
                            Integer current = (Integer) f.get(v);
                            Integer newValue = current + 1;
                            showChange(current, newValue);
                            f.set(v, newValue);
                        } else if (f.getType() == String.class) {
                            String current = (String) f.get(v);
                            String newValue = current + 1;
                            showChange(current, newValue);
                            f.set(v, newValue);
                        } else if (f.getType() == byte[].class) {
                            byte[] current = (byte[]) f.get(v);
                            byte[] append = "1".getBytes();
                            byte[] newValue = new byte[current.length
                                    + append.length];
                            showChange(current, newValue);
                            f.set(v, newValue);
                        } else if (f.getType() == Boolean.class) {
                            Boolean current = (Boolean) f.get(v);
                            Boolean newValue = !current;
                            showChange(current, newValue);
                            f.set(v, newValue);
                        } else {
                            fail("Unexpected field type: "
                                    + f.getType().getSimpleName());
                        }
                        assertTrue(Versionable.auditable(v));
                        v.updateHistory();
                        assertEquals(count, v.getHistoryList().size());
                        history
                                = v.getHistoryList().get(v.getHistoryList().size() - 1);
                        assertEquals(0, history.getMajorVersion());
                        assertEquals(0, history.getMidVersion());
                        assertEquals(count, history.getMinorVersion());
                        assertEquals(1, (int) history.getModifierId().getId());
                        assertEquals("audit.general.modified", history.getReason());
                        assertNotNull(history.getModificationTime());
                        assertTrue(checkHistory(v));
                        assertFalse(Versionable.auditable(v));
                        int total = new HistoryJpaController(DataBaseManager
                                .getEntityManagerFactory()).getHistoryCount();
                        //Test for issue #25 https://github.com/javydreamercsw/validation-manager/issues/25
                        v = (Versionable) DataBaseManager.getEntityManager()
                                .find(entity.getJavaType(),
                                        DataBaseManager.getEntityManagerFactory()
                                        .getPersistenceUnitUtil().getIdentifier(v));
                        assertTrue(checkHistory(v));
                        assertEquals(total, new HistoryJpaController(DataBaseManager
                                .getEntityManagerFactory()).getHistoryCount());
                        assertEquals(count, v.getHistoryList().size());
                    }
                }
            }
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    private void showChange(Object current, Object newValue) {
        System.out.println("Changing int value from: "
                + current + " to: "
                + newValue);
    }

    public class VersionableImpl extends Versionable {

        private List<History> history = new ArrayList<>();

        private int field1;

        @Auditable
        private String field2;

        @Override
        public List<History> getHistoryList() {
            return history;
        }

        @Override
        public void setHistoryList(List<History> historyList) {
            this.history = historyList;
        }

        /**
         * @return the field1
         */
        public int getField1() {
            return field1;
        }

        /**
         * @param field1 the field1 to set
         */
        public void setField1(int field1) {
            this.field1 = field1;
        }

        /**
         * @return the field2
         */
        public String getField2() {
            return field2;
        }

        /**
         * @param field2 the field2 to set
         */
        public void setField2(String field2) {
            this.field2 = field2;
        }
    }
}
