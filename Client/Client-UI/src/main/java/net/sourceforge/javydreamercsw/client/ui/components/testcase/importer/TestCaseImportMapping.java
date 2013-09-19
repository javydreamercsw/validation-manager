package net.sourceforge.javydreamercsw.client.ui.components.testcase.importer;

import net.sourceforge.javydreamercsw.client.ui.components.ImportMappingInterface;
import org.openide.util.NbBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public enum TestCaseImportMapping implements ImportMappingInterface {

    REQUIREMENT("req.map", true),
    DESCRIPTION("desc.map", true),
    ACCEPTANCE_CRITERIA("ac.map", true),
    NOTES("notes.map", false),
    IGNORE("ignore.map", false);
    private final String key;
    private final boolean required;

    @NbBundle.Messages({
        "req.map=Requirement(s)",
        "desc.map=Description",
        "ac.map=Acceptance Criteria",
        "ignore.map=Ignore",
        "notes.map=Notes"
    })
    TestCaseImportMapping(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    /**
     * @return the key
     */
    @Override
    public String getValue() {
        return org.openide.util.NbBundle.getMessage(TestCaseImportMapping.class, key); // NOI18N
    }

    /**
     * @return the required
     */
    @Override
    public boolean isRequired() {
        return required;
    }
}
