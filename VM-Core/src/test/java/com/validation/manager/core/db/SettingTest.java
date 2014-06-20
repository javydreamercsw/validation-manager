package com.validation.manager.core.db;

import com.validation.manager.core.server.core.VMSettingServer;
import static com.validation.manager.core.server.core.VMSettingServer.getSetting;
import static com.validation.manager.core.server.core.VMSettingServer.getSettings;
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
        ArrayList<VmSetting> settings = getSettings();
        assertFalse(settings.isEmpty());
        for (VmSetting setting : settings) {
            assertTrue(getSetting(setting.getSetting()) != null);
        }
    }
}
