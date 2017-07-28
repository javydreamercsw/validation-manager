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
package net.sourceforge.javydreamercsw.validation.manager.web.wizard.project;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestCaseTypeServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.teemu.wizards.WizardStep;

/**
 * GAMP specific configuration.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class GAMPStep implements WizardStep {

    private final ProjectCreationWizard wizard;
    private final ComboBox category
            = new ComboBox(TRANSLATOR.translate("general.category"));
    private final boolean version = true;
    private final boolean iq = true;
    private boolean urs = false;//User Requirement Spec
    private boolean fs = false;//Functional Spec
    private boolean cs = false;//Configuration Spec
    private boolean ds = false;//Design Specification
    private boolean ms = false;//Module Specification
    private boolean supplierAssessment = false;
    private static final Logger LOG
            = Logger.getLogger(GAMPStep.class.getSimpleName());

    public GAMPStep(ProjectCreationWizard wizard) {
        this.wizard = wizard;
        getCategory().setWidth(100, Unit.PERCENTAGE);
        getCategory().setTextInputAllowed(false);
        getCategory().addValueChangeListener(event -> {
            if (getCategory().getValue() != null) {
                urs = false;
                fs = false;
                ds = false;
                ms = false;
                supplierAssessment = false;
                cs = false;
                LOG.log(Level.INFO, "Type: {0}\nCategory: {1}\nTemplate: {2}",
                        new Object[]{wizard.getType(), getCategory().getValue(), "GAMP5"});
                String c = ((String) getCategory().getValue());
                int cat = Integer.parseInt(c.substring(c.length() - 1));
                if (wizard.getType().equals("general.software")) {
                    switch (cat) {
                        case 5:
                            urs = true;
                            fs = true;
                            ds = true;
                            ms = true;
                            supplierAssessment = true;
                            break;
                        case 4:
                            fs = true;
                            cs = true;
                            urs = true;
                            supplierAssessment = true;
                            break;
                        case 3:
                            urs = true;
                            supplierAssessment = true;
                            break;
                        default:
                        //Defaults
                    }
                } else {
                    //Hardware
                    switch (cat) {
                        case 2:
                            ds = true;
                            supplierAssessment = true;
                            break;
                        case 1:
                            //Defaults
                            break;
                        default:
                        //Defaults
                    }
                }
            }
        });
    }

    @Override
    public String getCaption() {
        return TRANSLATOR.translate("template.gamp5.settings");
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        Label text = new Label();
        text.setSizeFull();
        text.setEnabled(false);
        text.setValue(TRANSLATOR.translate("template.gamp5.disclaimer"));
        getCategory().removeAllItems();
        switch (wizard.getType()) {
            case "general.software":
                //Load SW options
                getCategory().addItem("template.gamp5.sw.cat1");
                getCategory().addItem("template.gamp5.sw.cat3");
                getCategory().addItem("template.gamp5.sw.cat4");
                getCategory().addItem("template.gamp5.sw.cat5");
                break;
            default:
                //Load HW options
                getCategory().addItem("template.gamp5.hw.cat1");
                getCategory().addItem("template.gamp5.hw.cat2");
        }
        wizard.translateSelect(getCategory());
        vl.addComponent(getCategory());
        vl.addComponent(text);
        vl.setSizeFull();
        return vl;
    }

    @Override
    public boolean onAdvance() {
        if (getCategory().getValue() != null) {
            wizard.setCategory((String) getCategory().getValue());
        }
        if (getCategory().getValue() != null) {
            wizard.setProcess(new ProjectTemplateManager() {
                @Override
                public void run() {
                    try {
                        ProjectServer ps = wizard.getProject();
                        ps.write2DB();
                        setProject(ps);
                        if (ps.getRequirementSpecList() == null) {
                            ps.setRequirementSpecList(new ArrayList<>());
                        }
                        TestProjectServer verification = new TestProjectServer(TRANSLATOR
                                .translate("general.verification"), true);
                        verification.getProjectList().add(ps.getEntity());
                        verification.write2DB();
                        TestPlanServer plan
                                = new TestPlanServer(verification.getEntity(), true, true);
                        plan.setName(TRANSLATOR.translate("general.verification"));
                        plan.write2DB();
                        if (isVersion()) {
                            TestCaseServer tc = new TestCaseServer(TRANSLATOR
                                    .translate("version.test"),
                                    new Date(),
                                    new TestCaseTypeServer(5).getEntity());
                            tc.write2DB();
                            plan.addTestCase(tc.getEntity());
                        }
                        if (isIq()) {
                            TestCaseServer tc = new TestCaseServer(TRANSLATOR
                                    .translate("installation.test"),
                                    new Date(),
                                    new TestCaseTypeServer(6).getEntity());
                            tc.write2DB();
                            plan.addTestCase(tc.getEntity());
                        }
                        if (isUrs()) {
                            try {
                                RequirementSpecServer rs
                                        = new RequirementSpecServer(TRANSLATOR
                                                .translate("user.specification.name"),
                                                "", ps.getId(), 2);
                                rs.write2DB();
                                TestCaseServer tc = new TestCaseServer(TRANSLATOR
                                        .translate("requirement.test"),
                                        new Date(),
                                        new TestCaseTypeServer(5).getEntity());
                                tc.write2DB();
                                plan.addTestCase(tc.getEntity());
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                        if (isFs()) {
                            try {
                                RequirementSpecServer rs
                                        = new RequirementSpecServer(TRANSLATOR
                                                .translate("functional.specification.name"),
                                                "", ps.getId(), 3);
                                rs.write2DB();
                                TestCaseServer tc = new TestCaseServer(TRANSLATOR
                                        .translate("functional.test"),
                                        new Date(),
                                        new TestCaseTypeServer(4).getEntity());
                                tc.write2DB();
                                plan.addTestCase(tc.getEntity());
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                        if (isDs()) {
                            try {
                                RequirementSpecServer rs = new RequirementSpecServer(TRANSLATOR
                                        .translate("design.specification.name"),
                                        "", ps.getId(), 4);
                                rs.write2DB();
                                TestCaseServer tc = new TestCaseServer(TRANSLATOR
                                        .translate("integration.test"),
                                        new Date(),
                                        new TestCaseTypeServer(2).getEntity());
                                tc.write2DB();
                                plan.addTestCase(tc.getEntity());
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                        if (isMs()) {
                            try {
                                RequirementSpecServer rs = new RequirementSpecServer(TRANSLATOR
                                        .translate("module.specification.name"),
                                        "", ps.getId(), 5);
                                rs.write2DB();
                                TestCaseServer tc = new TestCaseServer(TRANSLATOR
                                        .translate("module.test"),
                                        new Date(),
                                        new TestCaseTypeServer(1).getEntity());
                                tc.write2DB();
                                plan.addTestCase(tc.getEntity());
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                        if (isCs()) {
                            try {
                                RequirementSpecServer rs = new RequirementSpecServer(TRANSLATOR
                                        .translate("configuration.specification.name"),
                                        "", ps.getId(), 3);
                                rs.write2DB();
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                        plan.update();
                        if (isSupplierAssessment()) {
                            LOG.warning("Missing Risk Management implementation");
                        }
                        TestProjectServer validation = new TestProjectServer(TRANSLATOR
                                .translate("general.validation"), true);
                        validation.getProjectList().add(ps.getEntity());
                        validation.write2DB();
                        TestPlanServer valPlan
                                = new TestPlanServer(validation.getEntity(), true, true);
                        valPlan.setName(TRANSLATOR.translate("general.validation"));
                        valPlan.write2DB();
                        ps.update();
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (NonexistentEntityException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    updateComplete();
                }
            });
        }
        return getCategory().getValue() != null;
    }

    @Override
    public boolean onBack() {
        //Disabling going back since Wizards is not user friendly for dynamic content
        return false;
    }

    /**
     * @return the category
     */
    public ComboBox getCategory() {
        return category;
    }

    /**
     * @return the version
     */
    public boolean isVersion() {
        return version;
    }

    /**
     * @return the iq
     */
    public boolean isIq() {
        return iq;
    }

    /**
     * @return the urs
     */
    public boolean isUrs() {
        return urs;
    }

    /**
     * @return the fs
     */
    public boolean isFs() {
        return fs;
    }

    /**
     * @return the cs
     */
    public boolean isCs() {
        return cs;
    }

    /**
     * @return the ds
     */
    public boolean isDs() {
        return ds;
    }

    /**
     * @return the ms
     */
    public boolean isMs() {
        return ms;
    }

    /**
     * @return the supplierAssessment
     */
    public boolean isSupplierAssessment() {
        return supplierAssessment;
    }
}
