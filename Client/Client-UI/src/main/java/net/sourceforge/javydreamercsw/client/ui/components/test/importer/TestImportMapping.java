package net.sourceforge.javydreamercsw.client.ui.components.test.importer;

import org.openide.util.NbBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public enum TestImportMapping {

    REQUIREMENT("req.map"),
    DESCRIPTION("desc.map"),
    ACCEPTANCE_CRITERIA("ac.map"),
    IGNORE("ignore.map");
    private final String key;

    @NbBundle.Messages({
        "req.map=Requirement(s)",
        "desc.map=Description",
        "ac.map=Acceptance Criteria",
        "ignore.map=Ignore"
    })
    TestImportMapping(String key) {
        this.key = key;
    }

    /**
     * @return the key
     */
    public String getValue() {
        return org.openide.util.NbBundle.getMessage(TestImportMapping.class, key); // NOI18N
    }
}
