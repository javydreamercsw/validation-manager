package com.validation.manager.core.db;

import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.ArrayList;
import java.util.Iterator;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SettingTest extends AbstractVMTestCase {

    @Test
    @SuppressWarnings({"unchecked"})
    public void testSettings() {
        ArrayList<VmSetting> settings = VMSettingServer.getSettings();
        assertFalse(settings.isEmpty());
        for (Iterator<VmSetting> it = settings.iterator(); it.hasNext();) {
            VmSetting setting = it.next();
            assertTrue(VMSettingServer.getSetting(setting.getSetting()) != null);
        }
    }
}
