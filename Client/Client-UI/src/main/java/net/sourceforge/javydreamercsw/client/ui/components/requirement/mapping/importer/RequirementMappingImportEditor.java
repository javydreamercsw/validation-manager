package net.sourceforge.javydreamercsw.client.ui.components.requirement.mapping.importer;

import net.sourceforge.javydreamercsw.client.ui.components.ImportCustomEditor;

public class RequirementMappingImportEditor extends ImportCustomEditor {
    private static final long serialVersionUID = 6981691671253477164L;

    @Override
    public void init() {
        for (RequirementMapMapping rmm : RequirementMapMapping.values()) {
            cb.addItem(rmm.getValue());
        }
    }
}
