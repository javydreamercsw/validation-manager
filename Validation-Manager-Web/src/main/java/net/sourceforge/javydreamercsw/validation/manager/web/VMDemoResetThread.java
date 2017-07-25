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
package net.sourceforge.javydreamercsw.validation.manager.web;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VMDemoResetThread extends Thread {

    private static final Logger LOG
            = Logger.getLogger(VMDemoResetThread.class.getName());

    @Override
    public void run() {
        try {
            //Check again this is a demo environment, just in case
            if (DataBaseManager.isDemo()) {
                LOG.warning("Dropping tables...");
                DataBaseManager.clean();
                LOG.warning("Done!");
            }
            //Reload the database
            LOG.warning("Reloading DB...");
            DataBaseManager.reload(true);
            LOG.warning("Done!");
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
