package net.sourceforge.javydreamercsw.client.ui.components.testcase.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    IGNORE("ignore.map", false, true);
    private final String key;
    private final boolean required;
    private final boolean ignore;

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
        this.ignore = false;
    }

    TestCaseImportMapping(String key, boolean required,
            boolean ignored) {
        this.key = key;
        this.required = required;
        this.ignore = ignored;
    }

    /**
     * @return the key
     */
    @Override
    public String getValue() {
        return NbBundle.getMessage(TestCaseImportMapping.class, key); // NOI18N
    }

    /**
     * @return the required
     */
    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isIgnored() {
        return ignore;
    }

    @Override
    public ImportMappingInterface getMappingValue(String key) {
        ImportMappingInterface result = null;
        for (ImportMappingInterface imi : getValues()) {
            if (imi.getValue().equals(key)) {
                result = imi;
                break;
            }
        }
        return result;
    }

    @Override
    public List<ImportMappingInterface> getValues() {
        ArrayList<ImportMappingInterface> values = new ArrayList<>();
        values.addAll(Arrays.asList(values()));
        return values;
    }
}
