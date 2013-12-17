package net.sourceforge.javydreamercsw.client.ui.components.testcase.importer;

import net.sourceforge.javydreamercsw.client.ui.components.ImportCustomEditor;

public class TestCaseImportEditor extends ImportCustomEditor {

    @Override
    public void init() {
        for (TestCaseImportMapping tim : TestCaseImportMapping.values()) {
            cb.addItem(tim.getValue());
        }
    }
}
