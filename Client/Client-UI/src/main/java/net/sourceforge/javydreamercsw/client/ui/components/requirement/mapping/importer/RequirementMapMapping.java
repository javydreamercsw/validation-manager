package net.sourceforge.javydreamercsw.client.ui.components.requirement.mapping.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sourceforge.javydreamercsw.client.ui.components.ImportMappingInterface;
import org.openide.util.NbBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public enum RequirementMapMapping implements ImportMappingInterface {

    PARENT("parent.map", true),
    CHILDREN("child.map", true),
    IGNORE("ignore.map", false, true);
    private final String key;
    private final boolean required;
    private final boolean ignore;

    @NbBundle.Messages({
        "parent.map=Parent",
        "child.map=Child(ren)",
        "ignore.map=Ignore"
    })

    RequirementMapMapping(String key, boolean required) {
        this.key = key;
        this.required = required;
        this.ignore = false;
    }

    RequirementMapMapping(String key, boolean required,
            boolean ignore) {
        this.key = key;
        this.required = required;
        this.ignore = ignore;
    }

    /**
     * @return the key
     */
    @Override
    public String getValue() {
        return NbBundle.getMessage(RequirementMapMapping.class, key); // NOI18N
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
