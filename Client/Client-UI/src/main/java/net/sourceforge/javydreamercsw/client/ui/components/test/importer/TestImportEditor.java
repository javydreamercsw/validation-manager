package net.sourceforge.javydreamercsw.client.ui.components.test.importer;

import net.sourceforge.javydreamercsw.client.ui.components.ImportCustomEditor;
import net.sourceforge.javydreamercsw.client.ui.components.testcase.importer.TestCaseImportMapping;

public class TestImportEditor extends ImportCustomEditor {

    @Override
    public void init() {
        for (TestCaseImportMapping tim : TestCaseImportMapping.values()) {
            cb.addItem(tim.getValue());
        }
    }
}
