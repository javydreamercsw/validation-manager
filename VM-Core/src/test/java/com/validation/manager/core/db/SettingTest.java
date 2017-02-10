package com.validation.manager.core.db;

import static com.validation.manager.core.server.core.VMSettingServer.getSetting;
import static com.validation.manager.core.server.core.VMSettingServer.getSettings;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.ArrayList;
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
        settings.forEach((setting) -> {
            assertTrue(getSetting(setting.getSetting()) != null);
        });
    }
}
