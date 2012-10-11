/*
 * VMWeb.java
 *
 * Created on May 22, 2012, 1:19 PM
 */
package com.validation.manager.web.ui;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.CustomCheckBox;
import com.validation.manager.core.tool.requirement.importer.RequirementImportException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 * @version 1.0
 */
public class VMWeb extends Application implements HttpServletRequestListener {

    private static final long serialVersionUID = 1L;
    //Components
    private com.vaadin.ui.MenuBar menuBar = new com.vaadin.ui.MenuBar();
    private Embedded icon;
    private Embedded logo;
    //Panels
    private static ThreadLocal<VMWeb> threadLocal = new ThreadLocal<VMWeb>();
    private ResourceBundle vmrb =
            ResourceBundle.getBundle("com.validation.manager.resources.VMMessages", Locale.getDefault());
    private VMUserServer loggedUser;
    private ProjectServer currentProject;
    //Dynamic Menu
    private static TreeMap<Integer, VMMenuItem> items =
            new TreeMap<Integer, VMMenuItem>();
    private static TreeMap<String, MenuItem> groups = new TreeMap<String, MenuItem>();
    //Icons
    private ThemeResource smallIcon = new ThemeResource("icons/VMSmall.png");
    private Label currentProjectLabel = new Label();
    private Component rightComponent, leftComponent;
    private String fileName;
    private FileFilter reqImportFilter;
    private static final Logger LOG = Logger.getLogger(VMWeb.class.getName());
    private final String requirementPrefix = "REQ:";
    private final String folderPrefix = "SEC:";
    private final String specPrefix = "SPEC:";
    private final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();

    @Override
    public void init() {
        setInstance(this);
        reqImportFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx");
            }
        };
        try {
            //Switch to Validation Manager theme
            setTheme("vm");
            logo = new Embedded("", new ThemeResource("icons/vm_logo.png"));
            logo.setType(Embedded.TYPE_IMAGE);
            icon = new Embedded("Validation Manager", new ThemeResource("icons/VMSmall.png"));
            icon.setType(Embedded.TYPE_IMAGE);
            setMainWindow(new com.vaadin.ui.Window(
                    "Validation Manager " + DataBaseManager.getVersion()));
            DataBaseManager.get();
            showLanguageSelection();
            initMenuItems();
            //Default left component
            setDefaultWindow();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void showRequirementForm(Requirement r) {
        //Show edit by default
        showRequirementForm(r, true);
    }

    private void showRequirementForm() {
        //Show creation by default
        showRequirementForm(new Requirement("", "", ""), false);
    }

    private Form getRequirementForm(Requirement requirement, boolean edit) {
        return getRequirementForm(requirement, edit, null);
    }

    private void showCreateRequirementSpecWindow() {
        final Window createWindow = new Window();
        // Create a form and use FormLayout as its layout.
        final Form form = new Form();
        final RequirementSpec rs = new RequirementSpec();
        // Set form caption and description texts
        form.setCaption(getInstance().getResource().getString("menu.requirement.create.spec"));
        // Set the form to act immediately on user input. This is
        // necessary for the validation of the fields to occur immediately
        // when the input focus changes and not just on commit.
        form.setImmediate(true);
        final com.vaadin.ui.TextField name =
                new com.vaadin.ui.TextField(getInstance().getResource().getString("general.name") + ":");
        form.addField("name", name);
        // Create a bean item that is bound to the bean.
        BeanItem item = new BeanItem(rs);
        // Bind the bean item as the data source for the form.
        form.setItemDataSource(item);
        //Set the order of fields to display as well as which fields to hide
        ArrayList<String> fields = new ArrayList<String>();
        //Required fields
        fields.add("name");
        fields.add("product");
        fields.add("specLevel");
        fields.add("version");
        for (String field : fields) {
            form.getField(field).setRequired(true);
            form.getField(field).setRequiredError(
                    getInstance().getResource().getString(
                    "message.required.field.missing").replaceAll("%f", field));
        }
        //Non required fields
        fields.add("description");
        //Set field factory to create the fields of the form
        form.setFormFieldFactory(new FormFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                // Identify the fields by their Property ID.
                String pid = (String) propertyId;
                if ("name".equals(pid)) {
                    TextField textField =
                            new TextField(getInstance().getResource()
                            .getString("general.name") + ":");
                    textField.addValidator(new Validator() {
                        @Override
                        public void validate(Object value)
                                throws InvalidValueException {
                            if (!isValid(value)) {
                                throw new InvalidValueException(
                                        "Invalid value: " + value);
                            }
                        }

                        @Override
                        public boolean isValid(Object value) {
                            for (Iterator<RequirementSpec> it =
                                    currentProject.getRequirementSpecList()
                                    .iterator(); it.hasNext();) {
                                RequirementSpec reqSpec = it.next();
                                if (reqSpec.getName().equals(
                                        form.getField("name").getValue()
                                        .toString())) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    });
                    textField.setEnabled(true);
                    return textField;
                } else if ("product".equals(pid)) {
                    // Wrap them in a container for binding to a Select
                    final BeanItemContainer<Project> productContainer =
                            new BeanItemContainer<Project>(Project.class);
                    for (Iterator<Object> it =
                            DataBaseManager.namedQuery("Project.findAll")
                            .iterator(); it.hasNext();) {
                        productContainer.addBean((Project) it.next());
                    }
                    Select select = new Select(
                            getInstance().getResource()
                            .getString("general.project") + ":",
                            productContainer);
                    // Show the Project names in the select list
                    select.setItemCaptionPropertyId("name");
                    //Allow to create new ones
                    select.setNewItemsAllowed(true);
                    //Make sure something is selected
                    select.setNullSelectionAllowed(false);
                    select.addValidator(new Validator() {
                        @Override
                        public void validate(Object value)
                                throws InvalidValueException {
                            if (!isValid(value)) {
                                throw new InvalidValueException(
                                        "Invalid value: " + value);
                            }
                        }

                        @Override
                        public boolean isValid(Object value) {
                            return value != null
                                    && !value.toString().trim().isEmpty();
                        }
                    });
                    if (rs.getProject() != null) {
                        select.setValue(rs.getProject());
                    } else {
                        select.setValue(currentProject);
                    }
                    return select;
                } else if ("specLevel".equals(pid)) {
                    // Wrap them in a container for binding to a Select
                    final BeanItemContainer<SpecLevel> specLevelContainer =
                            new BeanItemContainer<SpecLevel>(SpecLevel.class);
                    for (Iterator<Object> it =
                            DataBaseManager.namedQuery("SpecLevel.findAll")
                            .iterator(); it.hasNext();) {
                        specLevelContainer.addBean((SpecLevel) it.next());
                    }
                    Select select = new Select(
                            getInstance().getResource()
                            .getString("general.requirement.level") + ":",
                            specLevelContainer);
                    // Show the Project names in the select list
                    select.setItemCaptionPropertyId("name");
                    //Allow to create new ones
                    select.setNewItemsAllowed(true);
                    //Make sure something is selected
                    select.setNullSelectionAllowed(false);
                    select.addValidator(new Validator() {
                        @Override
                        public void validate(Object value)
                                throws InvalidValueException {
                            if (!isValid(value)) {
                                throw new InvalidValueException(
                                        "Invalid value: " + value);
                            }
                        }

                        @Override
                        public boolean isValid(Object value) {
                            return value != null
                                    && !value.toString().trim().isEmpty();
                        }
                    });
                    if (rs.getSpecLevel() != null) {
                        select.setValue(rs.getSpecLevel());
                    }
                    return select;
                } else if ("description".equals(pid)) {
                    TextArea textArea =
                            new TextArea(getInstance().getResource()
                            .getString("general.description") + ":");
                    textArea.setEnabled(true);
                    return textArea;
                } else if ("version".equals(pid)) {
                    TextField textField =
                            new TextField(getInstance().getResource()
                            .getString("general.version") + ":");
                    textField.setEnabled(false);
                    return textField;
                } else {
                    return null;//Invalid field
                }
            }
        });
        form.setVisibleItemProperties(fields);
        form.setFooter(new HorizontalLayout());
        // The Commit button calls form.commit().
        Button commit = new Button(
                getInstance().getResource().getString("general.create"),
                form, "commit");
        commit.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    new RequirementSpecJpaController(
                            DataBaseManager.getEntityManagerFactory()).create(rs);
                } catch (PreexistingEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } finally {
                    if (createWindow != null) {
                        getMainWindow().removeWindow(createWindow);
                    }
                }
            }
        });
        form.getFooter().addComponent(commit);
        Button cancel = new Button(
                getInstance().getResource().getString("general.cancel"));
        cancel.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (createWindow != null) {
                    getMainWindow().removeWindow(createWindow);
                }
            }
        });
        form.getFooter().addComponent(cancel);
        createWindow.center();
        createWindow.setModal(true);
        createWindow.addComponent(form);
        createWindow.setWidth(25, Sizeable.UNITS_PERCENTAGE);
        getMainWindow().addWindow(createWindow);
    }

    private void showCreateRequirementSectionWindow() {
        final Window createWindow = new Window();
        // Create a form and use FormLayout as its layout.
        final Form form = new Form();
        // Set form caption and description texts
        form.setCaption(getInstance().getResource().getString(
                "message.requirement.create"));
        // Set the form to act immediately on user input. This is
        // necessary for the validation of the fields to occur immediately
        // when the input focus changes and not just on commit.
        form.setImmediate(true);
        final com.vaadin.ui.TextField name =
                new com.vaadin.ui.TextField(getInstance().getResource()
                .getString("general.name") + ":");
        form.addField("name", name);
        form.setFooter(new HorizontalLayout());
        // The Commit button calls form.commit().
        Button commit = new Button(
                getInstance().getResource().getString("general.create"),
                form, "commit");
        commit.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            }
        });
        form.getFooter().addComponent(commit);
        Button cancel = new Button(
                getInstance().getResource().getString("general.cancel"));
        cancel.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (createWindow != null) {
                    getMainWindow().removeWindow(createWindow);
                }
            }
        });
        form.getFooter().addComponent(cancel);
        createWindow.center();
        createWindow.setModal(true);
        createWindow.addComponent(form);
        createWindow.setWidth(25, Sizeable.UNITS_PERCENTAGE);
        getMainWindow().addWindow(createWindow);
    }

    private Form getRequirementForm(final Requirement r, final boolean edit,
            final Window createWindow) {
        // Create a form and use FormLayout as its layout.
        final Form form = new Form();
        // Set form caption and description texts
        form.setCaption(edit ? "" : getInstance().getResource().getString(
                "message.requirement.create"));
        // Set the form to act immediately on user input. This is
        // necessary for the validation of the fields to occur immediately
        // when the input focus changes and not just on commit.
        form.setImmediate(true);
        //Set field factory to create the fields of the form
        form.setFormFieldFactory(new FormFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                // Identify the fields by their Property ID.
                String pid = (String) propertyId;
                if ("uniqueId".equals(pid)) {
                    TextField textField =
                            new TextField(getInstance().getResource()
                            .getString("general.unique.id") + ":");
                    if (!edit) {
                        textField.addValidator(new Validator() {
                            @Override
                            public void validate(Object value)
                                    throws InvalidValueException {
                                if (!isValid(value)) {
                                    throw new InvalidValueException(
                                            "Invalid value: " + value);
                                }
                            }

                            @Override
                            public boolean isValid(Object value) {
                                HashMap<String, Object> parameters =
                                        new HashMap<String, Object>();
                                parameters.put("id", value);
                                parameters.put("product",
                                        new ProjectJpaController(
                                        DataBaseManager
                                        .getEntityManagerFactory())
                                        .findProject(currentProject.getId()));
                                List<Object> result =
                                        DataBaseManager.createdQuery(
                                        "select r from Requirement r where "
                                        + "r.uniqueId=:id and r.projectId=:product",
                                        parameters);
                                if (result.isEmpty()) {
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                    textField.setEnabled(!edit);
                    return textField;
                }
                if ("description".equals(pid)) {
                    TextArea textArea =
                            new TextArea(getInstance().getResource()
                            .getString("general.description") + ":");
                    textArea.setEnabled(true);
                    return textArea;
                }
                if ("notes".equals(pid)) {
                    TextArea textArea =
                            new TextArea(getInstance().getResource()
                            .getString("general.notes") + ":");
                    textArea.setEnabled(true);
                    return textArea;
                }
                if ("requirementType".equals(pid)) {
                    // Wrap them in a container for binding to a Select
                    final BeanItemContainer<RequirementType> requirementTypeContainer =
                            new BeanItemContainer<RequirementType>(RequirementType.class);
                    for (Iterator<Object> it =
                            DataBaseManager.namedQuery(
                            "RequirementType.findAll").iterator(); it.hasNext();) {
                        requirementTypeContainer.addBean(
                                (RequirementType) it.next());
                    }
                    Select select = new Select(
                            getInstance().getResource()
                            .getString("message.requirement.type") + ":",
                            requirementTypeContainer);
                    // Show the RequirementType names in the select list
                    select.setItemCaptionPropertyId("name");
                    //Allow to create new ones
                    select.setNewItemsAllowed(true);
                    //Make sure something is selected
                    select.setNullSelectionAllowed(false);
                    if (!edit) {
                        select.addValidator(new Validator() {
                            @Override
                            public void validate(Object value)
                                    throws InvalidValueException {
                                if (!isValid(value)) {
                                    throw new InvalidValueException(
                                            "Invalid value: " + value);
                                }
                            }

                            @Override
                            public boolean isValid(Object value) {
                                return value != null
                                        && !value.toString().trim().isEmpty();
                            }
                        });
                    }
                    if (edit && r.getRequirementType() != null) {
                        LOG.info(r.getRequirementType().getName());
                        select.setValue(r.getRequirementType());
                    }
                    return select;
                }
                return null;//Invalid field
            }
        });
        // Create a bean item that is bound to the bean.
        BeanItem item = new BeanItem(r);
        // Bind the bean item as the data source for the form.
        form.setItemDataSource(item);
        //Set the order of fields to display as well as which fields to hide
        ArrayList<String> fields = new ArrayList<String>();
        //Required fields
        fields.add("uniqueId");
        fields.add("description");
        fields.add("requirementType");
        for (String field : fields) {
            LOG.log(Level.INFO, "Marking field {0} as required...", field);
            form.getField(field).setRequired(true);
            form.getField(field).setRequiredError(
                    getInstance().getResource().getString(
                    "message.required.field.missing").replaceAll("%f", field));
        }
        //Non required fields
        fields.add("notes");
        form.setVisibleItemProperties(fields);
        form.setFooter(new HorizontalLayout());
        // The Commit button calls form.commit().
        Button commit = new Button(
                edit ? getInstance().getResource().getString("general.edit")
                : getInstance().getResource().getString("general.create"),
                form, "commit");
        commit.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("name",
                        ((RequirementType) form.getField("requirementTypeId")
                        .getValue()).getName());
                List result =
                        DataBaseManager.namedQuery("RequirementType.findByName",
                        parameters);
                RequirementType rt;
                if (result.isEmpty()) {
                    //Create a new one
                    rt = new RequirementType(form.getField("requirementTypeId")
                            .getValue().toString().trim());
                    new RequirementTypeJpaController(
                            DataBaseManager.getEntityManagerFactory()).create(rt);
                } else {
                    rt = (RequirementType) result.get(0);
                }
                RequirementServer reqs;
                if (edit) {
                    parameters.clear();
                    parameters.put("id", form.getField("uniqueId").getValue().toString());
                    parameters.put("projectId", currentProject.getId());
                    result = DataBaseManager.createdQuery(
                            "SELECT r FROM Requirement r WHERE "
                            + "r.uniqueId=:id and "
                            + "r.projectId.id=:projectId", parameters);
                    reqs = new RequirementServer((Requirement) result.get(0));
                    reqs.setDescription(
                            form.getField("description").getValue().toString()
                            .trim());
                    reqs.setNotes(form.getField("notes").getValue().toString()
                            .trim());
                } else {
                    reqs = new RequirementServer(
                            form.getField("uniqueId").getValue().toString()
                            .trim(),
                            form.getField("description").getValue().toString()
                            .trim(),
                            new ProjectJpaController(
                            DataBaseManager.getEntityManagerFactory())
                            .findProject(currentProject.getId()),
                            form.getField("notes").getValue().toString().trim(),
                            rt.getId(), 1);
                }
                reqs.setRequirementType(rt);
                if (createWindow != null) {
                    getMainWindow().removeWindow(createWindow);
                }
                try {
                    reqs.write2DB();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    getMainWindow().showNotification(
                            getInstance().getResource()
                            .getString("message.requirement.create.failed"),
                            ex.getLocalizedMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE);
                }
                //Update view
                showRequirementSpecWindow();
            }
        });
        form.getFooter().addComponent(commit);
        Button cancel = new Button(
                getInstance().getResource().getString("general.cancel"));
        cancel.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (createWindow != null) {
                    getMainWindow().removeWindow(createWindow);
                }
            }
        });
        form.getFooter().addComponent(cancel);
        return form;
    }

    private void showRequirementForm(Requirement r, final boolean edit) {
        final Window createWindow = new Window();
        final Form form = getRequirementForm(r, edit, createWindow);
        createWindow.center();
        createWindow.setModal(true);
        createWindow.addComponent(form);
        createWindow.setWidth(25, Sizeable.UNITS_PERCENTAGE);
        getMainWindow().addWindow(createWindow);
    }

    private Component getProjectRequirementMainComponent(final ProjectServer p) {
        com.vaadin.ui.Panel panel = new com.vaadin.ui.Panel();
        panel.setContent(new VerticalLayout());
        panel.addComponent(new Label(getInstance().getResource()
                .getString("general.menu") + ":"));
        com.vaadin.ui.Panel bpanel = new com.vaadin.ui.Panel();
        bpanel.setContent(new HorizontalLayout());
        Button create = new Button(getInstance().getResource()
                .getString("general.create"));
        create.setEnabled(loggedUser != null);
        create.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showRequirementForm();
            }
        });
        Button export = new Button(getInstance().getResource()
                .getString("message.export.requirement"));
        export.setEnabled(loggedUser != null);
        export.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                //TODO
            }
        });
        Button importer = new Button(getInstance().getResource()
                .getString("general.import"));
        importer.setEnabled(loggedUser != null);
        importer.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Wizard wizard = new Wizard();
                final com.vaadin.ui.Window wizardWindow =
                        new com.vaadin.ui.Window();
                wizard.addStep(new WizardStep() {
                    final UploadManager um = new UploadManager();
                    final Upload upload = new Upload(
                            getInstance().getResource().getString(
                            "message.import.requirement.select.file"), um);
                    final Upload.SucceededListener successListener =
                            new Upload.SucceededListener() {
                                @Override
                                public void uploadSucceeded(Upload.SucceededEvent event) {
                                    if (upload != null) {
                                        upload.setEnabled(false);
                                        LOG.log(Level.FINE,
                                                "Renaming uploaded file to: {0}",
                                                getFileName());
                                        File toImport =
                                                new File(um.getFile().getParentFile()
                                                .getAbsolutePath()
                                                + System.getProperty("file.separator")
                                                + getFileName());
                                        boolean rename = um.getFile().renameTo(toImport);
                                        if (rename && reqImportFilter.accept(toImport)) {
                                            getMainWindow().showNotification(
                                                    getInstance().getResource()
                                                    .getString(
                                                    "message.file.upload.success"),
                                                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                            //Import
                                            RequirementImporter ri = new RequirementImporter(
                                                    new ProjectJpaController(
                                                    DataBaseManager
                                                    .getEntityManagerFactory())
                                                    .findProject(p.getId()),
                                                    toImport);
                                            try {
                                                //Show import approval screen
                                                showRequirementImportApprovalWindow(
                                                        ri.importFile());
                                            } catch (UnsupportedOperationException ex) {
                                                LOG.log(Level.SEVERE, null, ex);
                                                getMainWindow().showNotification(
                                                        ex.getLocalizedMessage(),
                                                        Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                            } catch (RequirementImportException ex) {
                                                LOG.log(Level.SEVERE, null, ex);
                                                getMainWindow().showNotification(
                                                        ex.getLocalizedMessage(),
                                                        Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                            } catch (Exception ex) {
                                                LOG.log(Level.SEVERE, null, ex);
                                                getMainWindow().showNotification(
                                                        ex.getLocalizedMessage(),
                                                        Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                            }
                                            //Cleanup
                                            toImport.delete();
                                            //Update view
                                            showRequirementSpecWindow();
                                        } else {
                                            getMainWindow().showNotification(
                                                    getInstance().getResource()
                                                    .getString(
                                                    "message.file.upload.unsupportedformat"),
                                                    Window.Notification.TYPE_HUMANIZED_MESSAGE);
                                        }
                                    }
                                    getMainWindow().removeWindow(wizardWindow);
                                    LOG.log(Level.FINE, "Imported file:{0}",
                                            um.getFile().getAbsolutePath());
                                }
                            };
                    final Upload.FailedListener failureListener =
                            new Upload.FailedListener() {
                                @Override
                                public void uploadFailed(Upload.FailedEvent event) {
                                    getMainWindow().showNotification(
                                            VMWeb.getInstance().getResource().getString(
                                            "message.unable.to.load.file"),
                                            Window.Notification.TYPE_ERROR_MESSAGE);
                                }
                            };

                    @Override
                    public String getCaption() {
                        return getInstance().getResource().getString(
                                "message.import.requirement.select.file");
                    }

                    @Override
                    public Component getContent() {
                        upload.addListener((Upload.SucceededListener) um);
                        upload.addListener(
                                (Upload.SucceededListener) successListener);
                        upload.addListener((Upload.FailedListener) um);
                        upload.addListener(
                                (Upload.FailedListener) failureListener);
                        return upload;
                    }

                    @Override
                    public boolean onAdvance() {
                        if (!um.isSuccess()) {
                            getMainWindow().showNotification(
                                    getInstance().getResource()
                                    .getString("message.missing.file"),
                                    Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                        return um.isSuccess();
                    }

                    @Override
                    public boolean onBack() {
                        return true;
                    }

                    @Override
                    public String toString() {
                        return getCaption();
                    }
                });
                wizard.setSizeFull();
                wizardWindow.addComponent(wizard);
                wizardWindow.center();
                wizardWindow.setWidth(35, Sizeable.UNITS_PERCENTAGE);
                getMainWindow().addWindow(wizardWindow);
            }
        });
        bpanel.addComponent(create);
        bpanel.addComponent(export);
        bpanel.addComponent(importer);
        bpanel.setSizeFull();
        panel.addComponent(bpanel);
        panel.setSizeFull();
        return panel;
    }

    private void showRequirementImportApprovalWindow(
            List<Requirement> requirements) {
        VerticalLayout mainLayout = new VerticalLayout();
        final HashMap<String, Requirement> map = new HashMap<String, Requirement>();
        mainLayout.setSizeUndefined();
        final Window tableWindow = new Window(getInstance().getResource().getString(
                "message.requirement.import.verify"));
        final PagedTable table = new PagedTable();
        table.addContainerProperty("ID", String.class, null);
        table.addContainerProperty(getInstance().getResource().getString(
                "message.requirement.type"), String.class, null);
        table.addContainerProperty(getInstance().getResource().getString(
                "general.description"), String.class, null);
        table.addContainerProperty(getInstance().getResource().getString(
                "general.notes"), String.class, null);
        table.addContainerProperty(getInstance().getResource().getString(
                "general.import"), CheckBox.class, null);
        table.setImmediate(true);
        for (Iterator<Requirement> it = requirements.iterator(); it.hasNext();) {
            Requirement req = it.next();
            Item reqItem = table.addItem(req.getUniqueId());
            reqItem.getItemProperty("ID").setValue(req.getUniqueId());
            reqItem.getItemProperty(getInstance().getResource().getString(
                    "message.requirement.type")).setValue(
                    req.getRequirementType().getName());
            reqItem.getItemProperty(getInstance().getResource().getString(
                    "general.description")).setValue(req.getDescription());
            reqItem.getItemProperty(getInstance().getResource().getString(
                    "general.notes")).setValue(req.getNotes());
            //TODO: Check for requirements already existing and set those to false
            CustomCheckBox cb = new CustomCheckBox(req.getUniqueId(), null, true);
            reqItem.getItemProperty(getInstance().getResource().getString(
                    "general.import")).setValue(cb);
            map.put(req.getUniqueId(), req);
        }
        table.setPageLength(25);
        //Add Multiple selection controls
        HorizontalLayout selection = new HorizontalLayout();
        Button selectAll = new Button(getInstance().getResource()
                .getString("general.selectall"));
        selectAll.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                for (Iterator<?> it = table.getItemIds().iterator(); it.hasNext();) {
                    Object obj = it.next();
                    Item item = table.getItem(obj);
                    item.getItemProperty(getInstance().getResource().getString(
                            "general.import")).setValue(
                            new CustomCheckBox(
                            (String) item.getItemProperty("ID").getValue(),
                            null, true));
                }
            }
        });
        selection.addComponent(selectAll);
        Button unselectAll = new Button(getInstance().getResource()
                .getString("general.unselectall"));
        unselectAll.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                for (Iterator<?> it = table.getItemIds().iterator();
                        it.hasNext();) {
                    Object obj = it.next();
                    Item item = table.getItem(obj);
                    item.getItemProperty(getInstance().getResource().getString(
                            "general.import")).setValue(
                            new CustomCheckBox(
                            (String) item.getItemProperty("ID").getValue(),
                            null, false));
                }
            }
        });
        selection.addComponent(unselectAll);
        tableWindow.addComponent(selection);
        tableWindow.addComponent(table);
        //Setup the processing menu
        HorizontalLayout processing = new HorizontalLayout();
        Button importButton = new Button(getInstance().getResource()
                .getString("general.import"));
        importButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                for (Iterator<?> it = table.getItemIds().iterator(); it.hasNext();) {
                    String id = (String) it.next();
                    Item item = table.getItem(id);
                    String itemId = (String) item.getItemProperty("ID").getValue();
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("uniqueId", itemId);
                    parameters.put("projectId", currentProject.getId());
                    List<Object> result = DataBaseManager.createdQuery(
                            "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId "
                            + "and r.projectId.id = :projectId", parameters);
                    if (result.isEmpty()
                            && ((Boolean) ((CustomCheckBox) item
                            .getItemProperty(getInstance().getResource().getString(
                            "general.import")).getValue()).getValue())) {
                        try {
                            new RequirementJpaController(
                                    DataBaseManager.getEntityManagerFactory())
                                    .create(
                                    map.get(itemId));
                        } catch (PreexistingEntityException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    } else {
                        LOG.log(Level.WARNING,
                                "Not importing requirement with ID: {0}", id);
                    }
                }
                getMainWindow().removeWindow(tableWindow);
                getMainWindow().showNotification(
                        getInstance().getResource().getString(
                        "message.requirement.import.success"),
                        Window.Notification.TYPE_HUMANIZED_MESSAGE);
                //Refresh screen
                showRequirementSpecWindow();
            }
        });
        processing.addComponent(importButton);
        Button cancel = new Button(getInstance().getResource()
                .getString("general.cancel"));
        cancel.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow()
                        .removeWindow(tableWindow);
            }
        });
        processing.addComponent(importButton);
        processing.addComponent(cancel);
        table.setSizeFull();
        tableWindow.addComponent(processing);
        tableWindow.setWidth(50, Sizeable.UNITS_PERCENTAGE);
        tableWindow.center();
        //Force user to use buttons
        tableWindow.setClosable(false);
        getMainWindow().addWindow(tableWindow);
    }

    private Component getProjectRequirementTreeComponent(ProjectServer p) {
        com.vaadin.ui.Panel panel = new com.vaadin.ui.Panel();
        final Tree tree = new Tree(p.getName());
        tree.setImmediate(true);
        //Show the details of the selected requirement 
        tree.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (tree.getValue() != null && !tree.getValue()
                        .toString().trim().isEmpty()
                        && tree.getValue().toString()
                        .startsWith(requirementPrefix)) {
                    String id = tree.getValue().toString()
                            .substring(requirementPrefix.length(),
                            tree.getValue().toString().length());
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("id", id);
                    parameters.put("projectId", currentProject.getId());
                    List<Object> result = DataBaseManager.createdQuery(
                            "SELECT r FROM Requirement r WHERE "
                            + "r.uniqueId=:id and "
                            + "r.project.id=:projectId", parameters);
                    //Show the Values
                    Form form = getRequirementForm(result.isEmpty()
                            ? new Requirement() : (Requirement) result.get(0),
                            true);
                    setRightComponent(form);
                    updateRightComponent();
                }
            }
        });
        HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("caption", String.class, null);
        //Add icon support
        container.addContainerProperty("icon", Resource.class, null);
        tree.setContainerDataSource(container);
        tree.setItemCaptionPropertyId("caption");
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemIconPropertyId("icon");
        // Set the tree in drag source mode
        tree.setDragMode(TreeDragMode.NODE);
        tree.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                // Wrapper for the object that is dragged
                Transferable t = event.getTransferable();

                // Make sure the drag source is the same tree
                if (t.getSourceComponent() != tree) {
                    return;
                }

                TreeTargetDetails target =
                        (TreeTargetDetails) event.getTargetDetails();
                // Get ids of the dragged item and the target item
                Object sourceItemId = t.getData("itemId");
                Object targetItemId = target.getItemIdOver();

                LOG.log(Level.INFO, "Source: {0}", sourceItemId);
                LOG.log(Level.INFO, "Target: {0}", targetItemId);

                // On which side of the target the item was dropped
                VerticalDropLocation location = target.getDropLocation();

                HierarchicalContainer container =
                        (HierarchicalContainer) tree.getContainerDataSource();

                // Drop right on an item -> make it a child
                if (location == VerticalDropLocation.MIDDLE) {
                    //Moving a requirement to a section
                    if (targetItemId.toString().startsWith(folderPrefix)
                            && sourceItemId.toString().startsWith(requirementPrefix)) {
                        tree.setParent(sourceItemId, targetItemId);
                        //TODO: Make DB changes
                    }//Moving a section within another section
                    else if (targetItemId.toString().startsWith(folderPrefix)
                            && sourceItemId.toString().startsWith(folderPrefix)) {
                        tree.setParent(sourceItemId, targetItemId);
                        //TODO: Make DB changes
                    } else {
                        if (targetItemId.toString().startsWith(requirementPrefix)
                                && sourceItemId.toString()
                                .startsWith(requirementPrefix)) {
                            getMainWindow().showNotification(
                                    getInstance().getResource()
                                    .getString("message.requirement.dnd.invalid"),
                                    Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                        if (targetItemId.toString().startsWith(requirementPrefix)
                                && sourceItemId.toString().startsWith(folderPrefix)) {
                            getMainWindow().showNotification(
                                    getInstance().getResource()
                                    .getString("message.section.dnd.invalid"),
                                    Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                } // Drop at the top of a subtree -> make it previous
                else if (location == VerticalDropLocation.TOP) {
                    if (targetItemId.toString().startsWith(folderPrefix)
                            && sourceItemId.toString().startsWith(folderPrefix)) {
                        Object parentId = container.getParent(targetItemId);
                        container.setParent(sourceItemId, parentId);
                        container.moveAfterSibling(sourceItemId, targetItemId);
                        container.moveAfterSibling(targetItemId, sourceItemId);
                        //TODO: Make DB changes
                    } else {
                        getMainWindow().showNotification(
                                getInstance().getResource()
                                .getString("message.requirement.dnd.invalid"),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                } // Drop below another item -> make it next
                else if (location == VerticalDropLocation.BOTTOM) {
                    if (targetItemId.toString().startsWith(folderPrefix)
                            && sourceItemId.toString().startsWith(folderPrefix)) {
                        Object parentId = container.getParent(targetItemId);
                        container.setParent(sourceItemId, parentId);
                        container.moveAfterSibling(sourceItemId, targetItemId);
                        //TODO: Make DB changes
                    } else {
                        getMainWindow().showNotification(
                                getInstance().getResource()
                                .getString("message.requirement.dnd.invalid"),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
        });
        //Handle item clicks
        tree.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                ContextMenu menu = new ContextMenu();
                // Add menu to main window, will not be directly visible but must be added.
                getMainWindow().addComponent(menu);
                if (event.getSource() == tree) {
                    Object itemId = event.getItemId();
                    if (itemId != null && itemId.toString()
                            .startsWith(specPrefix)) {
                        final ContextMenuItem createItem = menu.addItem(
                                getInstance().getResource()
                                .getString("menu.requirement.create.section"));
                        menu.addListener(new ContextMenu.ClickListener() {
                            @Override
                            public void contextItemClick(ContextMenu.ClickEvent event) {
                                // Get reference to clicked item
                                ContextMenuItem clickedItem = event.getClickedItem();
                                if (clickedItem.equals(createItem)) {
                                    //Show popu to gather required information
                                    showCreateRequirementSectionWindow();
                                }
                            }
                        });
                        menu.setEnabled(true);
                        if (ItemClickEvent.BUTTON_RIGHT == event.getButton()) {
                            menu.show(event.getClientX(), event.getClientY());
                        }
                    } else if (itemId != null && itemId.toString()
                            .startsWith(folderPrefix)) {
                        final ContextMenuItem createItem = menu.addItem(
                                getInstance().getResource()
                                .getString("menu.requirement.create.spec"));
                        menu.addListener(new ContextMenu.ClickListener() {
                            @Override
                            public void contextItemClick(ContextMenu.ClickEvent event) {
                                // Get reference to clicked item
                                ContextMenuItem clickedItem = event.getClickedItem();
                                if (clickedItem.equals(createItem)) {
                                    //Show popu to gather required information
                                    showCreateRequirementSpecWindow();
                                }
                            }
                        });
                        menu.setEnabled(true);
                        if (ItemClickEvent.BUTTON_RIGHT == event.getButton()) {
                            menu.show(event.getClientX(), event.getClientY());
                        }
                    } else if (itemId != null && itemId.toString()
                            .startsWith(requirementPrefix)) {
                        menu.addListener(new ContextMenu.ClickListener() {
                            @Override
                            public void contextItemClick(ContextMenu.ClickEvent event) {
                                // Get reference to clicked item
                                ContextMenuItem clickedItem = event.getClickedItem();
                                if (clickedItem.getName().equals(
                                        getInstance().getResource()
                                        .getString("menu.requirement.copy"))) {
                                    getMainWindow().showNotification(clickedItem.getName());
                                } else if (clickedItem.getName().equals(
                                        getInstance().getResource()
                                        .getString("menu.requirement.delete"))) {
                                    getMainWindow().showNotification(clickedItem.getName());
                                } else if (clickedItem.getName().equals(
                                        getInstance().getResource()
                                        .getString("menu.requirement.create.section"))) {
                                    getMainWindow().showNotification(clickedItem.getName());
                                }
                            }
                        });
                        menu.addItem(getInstance().getResource()
                                .getString("menu.requirement.create.section"));
                        menu.addItem(getInstance().getResource()
                                .getString("menu.requirement.copy"));
                        menu.addItem(getInstance().getResource()
                                .getString("menu.requirement.delete"));
                        menu.setEnabled(true);
                        if (ItemClickEvent.BUTTON_RIGHT == event.getButton()) {
                            menu.show(event.getClientX(), event.getClientY());
                        }
                    }
                }
            }
        });
        Item reqs = tree.addItem(folderPrefix + "Requirements");
        tree.setChildrenAllowed("general.requirement", true);
        reqs.getItemProperty("caption").setValue(getInstance().getResource()
                .getString("general.requirement"));
        //Populate the tree
        List<RequirementSpec> requirementSpecs = p.getRequirementSpecList();
        for (Iterator<RequirementSpec> it = requirementSpecs.iterator(); it.hasNext();) {
            RequirementSpec rs = it.next();
            Item item = tree.addItem(folderPrefix + rs.getName());
            item.getItemProperty("caption").setValue(rs.getName());
            //Make it a leaf
            tree.setChildrenAllowed(folderPrefix + rs.getName(), true);
            //Set the parent
            tree.setParent(folderPrefix + rs.getName(),
                    folderPrefix + "Requirements");
            //Add icon
            item.getItemProperty("icon").setValue(
                    new ThemeResource("icons/Papermart/Folder.png"));
            for (Iterator<RequirementSpecNode> it2 =
                    rs.getRequirementSpecNodeList().iterator(); it2.hasNext();) {
                RequirementSpecNode rsn = it2.next();
                //Add the node
                Item node = tree.addItem(folderPrefix + rsn.getName());
                node.getItemProperty("caption").setValue(rsn.getName());
                //Make it a leaf
                tree.setChildrenAllowed(folderPrefix + rsn.getName(), true);
                //Set the parent
                tree.setParent(folderPrefix + rsn.getName(),
                        folderPrefix + rs.getName());
                //Add icon
                node.getItemProperty("icon").setValue(
                        new ThemeResource("icons/Papermart/Folder.png"));
                for (Iterator<Requirement> it3 = rsn.getRequirementList().iterator(); it3.hasNext();) {
                    Requirement req = (Requirement) it3.next();
                    Item requirement = tree.addItem(requirementPrefix + req.getUniqueId());
                    requirement.getItemProperty("caption").setValue(req.getUniqueId());
                    //Make it a leaf
                    tree.setChildrenAllowed(requirementPrefix + req.getUniqueId(), false);
                    //Set the parent
                    tree.setParent(requirementPrefix + req.getUniqueId(),
                            folderPrefix + rsn.getName());
                    //Add icon
                    requirement.getItemProperty("icon").setValue(
                            new ThemeResource("icons/Papermart/Text-Edit.png"));
                }
            }
        }
        tree.setSizeFull();
        panel.addComponent(tree);
        panel.setSizeFull();
        return panel;
    }

    private Component getProjectTreeComponent() {
        com.vaadin.ui.Panel panel = new com.vaadin.ui.Panel();
        final Tree tree = new Tree(getInstance().getResource()
                .getString("general.project.plural"));
        tree.setImmediate(true);
        tree.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (tree.getValue() != null && !tree.getValue()
                        .toString().isEmpty()) {
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("name", tree.getValue());
                    List<Object> result = DataBaseManager.namedQuery(
                            "Project.findByName", parameters);
                    if (!result.isEmpty()) {
                        currentProject = new ProjectServer((Project) result.get(0));
                        try {
                            updateMenu();
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        List<Object> result = DataBaseManager.namedQuery("Project.findAll");
        LOG.log(Level.INFO, "Projects found: {0}", result.size());
        for (Iterator<Object> it = result.iterator(); it.hasNext();) {
            Project product = (Project) it.next();
            tree.addItem(product.getName());
            //Make it a leaf
            tree.setChildrenAllowed(product.getName(), false);
        }
        panel.addComponent(tree);
        return panel;
    }

    private void initMenuItems() {
        VMMenuItem item;
        int i = 0;
        //FIXME: shows even after log in
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("window.connection.profile")
                .setName("general.login").setIcon(smallIcon)
                .setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {

                showLoginDialog();
            }
        }).createVMMenuItem();//Need admin access
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("window.connection.profile")
                .setName("general.logout").setIcon(smallIcon)
                .setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                loggedUser = null;
                try {
                    updateMenu();
                    setDefaultWindow();
                    showMainWindow();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }).setLoggedIn(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("window.connection.profile")
                .setName("message.admin.userProfile")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(
                    com.vaadin.ui.MenuBar.MenuItem selectedItem) {

                showUserAdminWindow(false);
            }
        }).setLoggedIn(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.project")
                .setName("menu.project.create").setIcon(smallIcon)
                .setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {

                showProjectCreationWindow();
            }
        }).setLoggedIn(true).setAdmin(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.requirement").setName("menu.requirement")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(
                    com.vaadin.ui.MenuBar.MenuItem selectedItem) {

                showRequirementSpecWindow();
            }
        }).setSelected(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.requirement").setName("menu.requirement.spec.level")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(
                    com.vaadin.ui.MenuBar.MenuItem selectedItem) {

                showRequirementSpecLevelCreationWindow();
            }
        })
                .setLoggedIn(true).setSelected(true).setAdmin(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.test").setName("menu.test")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(
                    com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                //TODO
            }
        }).setSelected(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.test.execution").setName("menu.test.execution")
                .setIcon(smallIcon).setCommand(
                new com.vaadin.ui.MenuBar.Command() {
                    @Override
                    public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                        //TODO
                    }
                }).setSelected(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.test.report").setName("menu.test.report")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(
                    com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                //TODO
            }
        }).setSelected(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.user").setName("menu.user")
                .setIcon(smallIcon).setCommand(
                new com.vaadin.ui.MenuBar.Command() {
                    @Override
                    public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                        //TODO
                    }
                }).setLoggedIn(true).setAdmin(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.admin").setName("message.admin.userAdmin")
                .setIcon(smallIcon).setCommand(
                new com.vaadin.ui.MenuBar.Command() {
                    @Override
                    public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                        showUserAdminWindow(true);
                    }
                }).setLoggedIn(true).setAdmin(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.admin").setName("message.admin.groupAdmin")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(
                    com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                showGroupAdminWindow();
            }
        }).setLoggedIn(true).setAdmin(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.admin").setName("general.audit.menu")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                try {
                    showAuditWindow();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }).setAdmin(true).createVMMenuItem();
        addItem(item);
        item = new VMMenuItemBuilder().setIndex(i += 1000)
                .setGroupName("menu.admin").setName("message.admin.settingAdmin")
                .setIcon(smallIcon).setCommand(new com.vaadin.ui.MenuBar.Command() {
            @Override
            public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
                showSettingAdminWindow();
            }
        }).setAdmin(true).createVMMenuItem();
        addItem(item);
    }

    private void showRequirementSpecLevelCreationWindow() {
        final Window createWindow = new Window();
        // Create a form and use FormLayout as its layout.
        final Form form = new Form();
        final SpecLevel sl = new SpecLevel();
        // Set form caption and description texts
        form.setCaption(getInstance().getResource()
                .getString("menu.requirement.create.spec.level"));
        // Set the form to act immediately on user input. This is
        // necessary for the validation of the fields to occur immediately
        // when the input focus changes and not just on commit.
        form.setImmediate(true);
        final com.vaadin.ui.TextField name =
                new com.vaadin.ui.TextField(getInstance()
                .getResource().getString("general.name") + ":");
        form.addField("name", name);
        // Create a bean item that is bound to the bean.
        sl.setName("");
        sl.setDescription("");
        BeanItem item = new BeanItem(sl);
        // Bind the bean item as the data source for the form.
        form.setItemDataSource(item);
        //Set the order of fields to display as well as which fields to hide
        ArrayList<String> fields = new ArrayList<String>();
        //Required fields
        fields.add("name");
        for (String field : fields) {
            form.getField(field).setRequired(true);
            form.getField(field).setRequiredError(
                    getInstance().getResource().getString(
                    "message.required.field.missing").replaceAll("%f", field));
        }
        //Non required fields
        fields.add("description");
        //Set field factory to create the fields of the form
        form.setFormFieldFactory(new FormFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                // Identify the fields by their Property ID.
                String pid = (String) propertyId;
                if ("name".equals(pid)) {
                    TextField textField = new TextField(getInstance()
                            .getResource().getString("general.name") + ":");
                    textField.addValidator(new Validator() {
                        @Override
                        public void validate(Object value)
                                throws InvalidValueException {
                            if (!isValid(value)) {
                                throw new InvalidValueException(
                                        "Invalid value: " + value);
                            }
                        }

                        @Override
                        public boolean isValid(Object value) {
                            for (Iterator<SpecLevel> it =
                                    new SpecLevelJpaController(
                                    DataBaseManager.getEntityManagerFactory())
                                    .findSpecLevelEntities().iterator();
                                    it.hasNext();) {
                                SpecLevel sl = it.next();
                                if (sl.getName().equals(form.getField("name")
                                        .getValue().toString())) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    });
                    textField.setEnabled(true);
                    return textField;
                } else if ("description".equals(pid)) {
                    TextArea textArea = new TextArea(getInstance()
                            .getResource().getString("general.description") + ":");
                    textArea.setEnabled(true);
                    return textArea;
                } else {
                    return null;//Invalid field
                }
            }
        });
        form.setVisibleItemProperties(fields);
        form.setFooter(new HorizontalLayout());
        // The Commit button calls form.commit().
        Button commit = new Button(
                getInstance().getResource().getString("general.create"),
                form, "commit");
        commit.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    new SpecLevelJpaController(
                            DataBaseManager.getEntityManagerFactory())
                            .create(sl);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } finally {
                    if (createWindow != null) {
                        getMainWindow().removeWindow(createWindow);
                        try {
                            showMainWindow();
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        form.getFooter().addComponent(commit);
        Button cancel = new Button(
                getInstance().getResource().getString("general.cancel"));
        cancel.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (createWindow != null) {
                    getMainWindow().removeWindow(createWindow);
                }
            }
        });
        form.getFooter().addComponent(cancel);
        createWindow.center();
        createWindow.setModal(true);
        createWindow.addComponent(form);
        createWindow.setWidth(25, Sizeable.UNITS_PERCENTAGE);
        getMainWindow().addWindow(createWindow);
    }

    private void showRequirementSpecWindow() {
        //Recreate the ProjectServer to make sure we have the latest info from database
        setLeftComponent(getProjectRequirementTreeComponent(
                new ProjectServer(currentProject)));
        setRightComponent(getProjectRequirementMainComponent(
                new ProjectServer(currentProject)));
        try {
            showMainWindow();
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void showProjectCreationWindow() {
        final com.vaadin.ui.Window creation = new com.vaadin.ui.Window();
        final Form form = new Form();
        form.getLayout().addComponent(logo);
        form.getLayout().addComponent(new com.vaadin.ui.Label(
                getInstance().getResource()
                .getString("general.provide.info") + ":"));
        final com.vaadin.ui.TextField name =
                new com.vaadin.ui.TextField(getInstance().getResource()
                .getString("general.name") + ":");
        form.addField("name", name);
        final com.vaadin.ui.TextArea notes =
                new com.vaadin.ui.TextArea(getInstance().getResource()
                .getString("general.notes") + ":");
        form.addField("notes", notes);
        //Used for validation purposes
        final com.vaadin.ui.Button commit = new com.vaadin.ui.Button("Submit",
                form, "commit");
        commit.addListener(new com.vaadin.ui.Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                ProjectServer ps = new ProjectServer(name.getValue().toString(),
                        notes.getValue().toString());
                try {
                    ps.write2DB();
                } catch (IllegalOrphanException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (NonexistentEntityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                getMainWindow().removeWindow(creation);
                try {
                    //Refresh the component
                    setLeftComponent(getProjectTreeComponent());
                    showMainWindow();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
        form.getFooter().setSizeUndefined();
        form.getFooter().addComponent(commit);
        creation.addComponent(form);
        creation.setModal(true);
        creation.center();
        creation.setWidth(logo.getWidth(), logo.getWidthUnits());
        WebApplicationContext context = (WebApplicationContext) getContext();
        creation.setWidth(getImageDim(context.getHttpSession()
                .getServletContext().getRealPath(
                "/VAADIN/themes/vm") + System.getProperty("file.separator")
                + "icons"
                + System.getProperty("file.separator")
                + "vm_logo.png").width + 30,
                logo.getWidthUnits());
        getMainWindow().addWindow(creation);
    }

    public static VMMenuItem addItem(VMMenuItem item) {
        return items.put(item.getIndex(), item);
    }

    // @return the current application instance
    public static VMWeb getInstance() {
        return threadLocal.get();
    }

    // Set the current application instance
    public static void setInstance(VMWeb application) {
        threadLocal.set(application);
    }

    @Override
    public void onRequestStart(HttpServletRequest request,
            HttpServletResponse response) {
        VMWeb.setInstance(this);
    }

    @Override
    public void onRequestEnd(HttpServletRequest request,
            HttpServletResponse response) {
        threadLocal.remove();
    }

    private Select getLanguageOptions() {
        final Select languages = new Select();
        ArrayList<String> locales = new ArrayList<String>();
        ResourceBundle lrb = ResourceBundle.getBundle(
                "com.validation.manager.resources.Locale", Locale.getDefault());
        locales.addAll(Arrays.asList(
                lrb.getString("AvailableLocales").split(",")));
        for (Iterator<String> it = locales.iterator(); it.hasNext();) {
            String tempLocale = it.next();
            languages.addItem(tempLocale);
            languages.setItemCaption(tempLocale,
                    lrb.getString("Locale." + tempLocale));
            try {
                FileResource flagIcon =
                        getFlagIcon(tempLocale.isEmpty() ? "us" : tempLocale);
                languages.setItemIcon(tempLocale, flagIcon);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return languages;
    }

    private FileResource getFlagIcon(String code) throws IOException {
        FileResource resource = null;
        if (code != null && !code.isEmpty()) {
            //Format if it has '_' underscore
            if (code.contains("_")) {
                String newCode = code.substring(code.lastIndexOf('_') + 1);
                Logger.getLogger(VMWeb.class.getSimpleName()).log(Level.FINE,
                        "Converting code from: {0} to {1}!",
                        new Object[]{code, newCode});
                code = newCode;
            }
            Logger.getLogger(VMWeb.class.getSimpleName()).log(Level.FINE,
                    "Requested icon for code: {0}", code);
            WebApplicationContext context = (WebApplicationContext) getContext();
            File iconsFolder = new File(context.getHttpSession()
                    .getServletContext().getRealPath(
                    "/VAADIN/themes/vm") + System.getProperty("file.separator")
                    + "icons"
                    + System.getProperty("file.separator") + "flags");
            File tempIcon = new File(iconsFolder.getAbsolutePath()
                    + System.getProperty("file.separator") + code.toLowerCase()
                    + ".png");
            if (tempIcon.exists()) {
                Logger.getLogger(VMWeb.class.getSimpleName()).log(Level.FINE,
                        "Found icon for code: {0}!", code);
                resource = new FileResource(tempIcon, VMWeb.this);
            } else {
                Logger.getLogger(VMWeb.class.getSimpleName()).log(Level.FINE,
                        "Unable to find icon for: {0}", code);
            }
        }
        return resource;
    }

    private void showLanguageSelection() {
        final com.vaadin.ui.Window lang = new com.vaadin.ui.Window();
        lang.setReadOnly(true);
        final Form form = new Form();
        form.getLayout().addComponent(logo);
        form.getLayout().addComponent(new com.vaadin.ui.Label(
                "Please choose a language:"));
        final Select languages = getLanguageOptions();
        languages.setValue("");
        form.addField("type", languages);
        //Used for validation purposes
        final com.vaadin.ui.Button commit = new com.vaadin.ui.Button("Submit",
                form, "commit");
        commit.addListener(new com.vaadin.ui.Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                Locale loc;
                try {
                    String list = languages.getValue().toString();
                    String[] locales;
                    locales = list.split("_");
                    switch (locales.length) {
                        case 1:
                            loc = new Locale(locales[0]);
                            break;
                        case 2:
                            loc = new Locale(locales[0], locales[1]);
                            break;
                        case 3:
                            loc = new Locale(locales[0], locales[1], locales[2]);
                            break;
                        default:
                            loc = Locale.getDefault();
                    }
                } catch (Exception e) {
                    loc = Locale.getDefault();
                }
                setLocale(loc);
                getMainWindow().removeWindow(lang);
                try {
                    showMainWindow();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
        form.getFooter().setSizeUndefined();
        form.getFooter().addComponent(commit);
        lang.addComponent(form);
        lang.setModal(true);
        lang.center();
        WebApplicationContext context = (WebApplicationContext) getContext();
        lang.setWidth(getImageDim(context.getHttpSession()
                .getServletContext().getRealPath(
                "/VAADIN/themes/vm") + System.getProperty("file.separator")
                + "icons"
                + System.getProperty("file.separator") + "vm_logo.png").width
                + 50,
                logo.getWidthUnits());
        getMainWindow().addWindow(lang);
    }

    public static Dimension getImageDim(final String path) {
        Dimension result = null;
        String suffix = getFileSuffix(path);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(
                        new File(path));
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new Dimension(width, height);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            } finally {
                reader.dispose();
            }
        }
        return result;
    }

    public static String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        return result;
    }

    private void setDefaultWindow() {
        setLeftComponent(getProjectTreeComponent());
        setRightComponent(null);
    }

    private void showMainWindow() throws VMException {
        getMainWindow().removeAllComponents();
        VerticalLayout vl = new VerticalLayout();
        getMainWindow().setContent(vl);
        getMainWindow().addListener(new MouseEvents.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void click(MouseEvents.ClickEvent event) {
                //Do nothing. 
                //Based on https://vaadin.com/es/forum/-/message_boards/view_message/359139
            }
        });

        splitPanel.setHeight(70, Sizeable.UNITS_PERCENTAGE);
        splitPanel.setSplitPosition(20, Sizeable.UNITS_PERCENTAGE);
        // Put two components in the container.
        updateRightComponent();
        updateLeftComponent();
        addHeader();
        getMainWindow().addComponent(splitPanel);
        vl.setExpandRatio(splitPanel, 1f);
        vl.setSizeFull();
        updateMenu();
    }

    /**
     * Updates the right component displayed
     */
    private void updateRightComponent() {
        if (splitPanel != null) {
            if (getRightComponent() != null) {
                getRightComponent().setSizeFull();
            }
            splitPanel.setSecondComponent(getRightComponent());
        }
    }

    /**
     * Updates the left component displayed
     */
    private void updateLeftComponent() {
        if (splitPanel != null) {
            if (getLeftComponent() != null) {
                getLeftComponent().setSizeFull();
            }
            splitPanel.setFirstComponent(getLeftComponent());
        }
    }

    private void addHeader() {
        final Select languages = getLanguageOptions();
        languages.setImmediate(true);
        languages.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Locale loc;
                try {
                    String list = languages.getValue().toString();
                    String[] locales;
                    locales = list.split("_");
                    switch (locales.length) {
                        case 1:
                            loc = new Locale(locales[0]);
                            break;
                        case 2:
                            loc = new Locale(locales[0], locales[1]);
                            break;
                        case 3:
                            loc = new Locale(locales[0], locales[1], locales[2]);
                            break;
                        default:
                            loc = Locale.getDefault();
                    }
                } catch (Exception e) {
                    loc = Locale.getDefault();
                }
                setLocale(loc);
                languages.setCaption(getInstance().getResource()
                        .getString("general.language") + ":");
            }
        });
        languages.setCaption(getInstance().getResource()
                .getString("general.language") + ":");
        languages.setValue(getLocale().getLanguage());
        getMainWindow().addComponent(logo);
        getMainWindow().addComponent(languages);
        getMainWindow().addComponent(currentProjectLabel);
        getMainWindow().addComponent(menuBar);
    }

    private boolean canAdd(VMMenuItem item) {
        boolean result = true;
        if ((item.isLoggedIn() || item.isAdmin()) && loggedUser == null) {
            result = false;
        }
        if (result && item.isSelected() && currentProject == null) {
            result = false;
        }
        if (result && item.isAdmin()) {
            for (Role role : loggedUser.getRoleList()) {
                if (role.getDescription().equals("admin")) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @return the Resource Bundle
     */
    public ResourceBundle getResource() {
        return vmrb;
    }

    private void showUserAdminWindow(boolean b) {
        //TODO
    }

    private void showAuditWindow() throws VMException {
        //TODO
    }

    private void showGroupAdminWindow() {
        //TODO
    }

    private void showSettingAdminWindow() {
        //TODO
    }

    private void showChangePasswordDialog() {
        //TODO
    }

    private void showLoginDialog() {
        final com.vaadin.ui.Window loginWindow = new com.vaadin.ui.Window();
        final Form form = new Form();
        form.setCaption(getInstance().getResource()
                .getString("window.connection") + ":");
        com.vaadin.ui.TextField username =
                new com.vaadin.ui.TextField(getInstance().getResource()
                .getString("general.username") + ":");
        PasswordField password =
                new PasswordField(getInstance().getResource()
                .getString("general.password") + ":");
        form.addField("username", username);
        form.addField("password", password);
        form.getField("username").setRequired(true);
        form.getField("username").focus();
        form.getField("username").setRequiredError(getInstance().getResource()
                .getString("message.missing.username"));
        form.getField("password").setRequired(true);
        form.getField("password").setRequiredError(getInstance().getResource()
                .getString("message.missing.password"));
        form.setFooter(new HorizontalLayout());
        //Used for validation purposes
        final com.vaadin.ui.Button commit = new com.vaadin.ui.Button(
                getInstance().getResource().getString("general.login"), form,
                "commit");
        final com.vaadin.ui.Button cancel = new com.vaadin.ui.Button(
                getInstance().getResource().getString("general.cancel"),
                new com.vaadin.ui.Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                        //TODO: Do something when canceled?
                        getMainWindow().removeWindow(loginWindow);
                    }
                });
        commit.addListener(new com.vaadin.ui.Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                //Disable data fields, make sure nothing gets modified after clicking save
                form.getField("username").setEnabled(false);
                form.getField("password").setEnabled(false);
                commit.setEnabled(false);
                cancel.setEnabled(false);
                if (!VMUserServer.validCredentials(
                        ((com.vaadin.ui.TextField) form.getField("username"))
                        .getValue().toString(),
                        ((PasswordField) form.getField("password")).getValue()
                        .toString(),
                        true)) {
                    getMainWindow().showNotification(
                            getInstance().getResource()
                            .getString("menu.connection.error.user"),
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    String username =
                            ((com.vaadin.ui.TextField) form.getField("username"))
                            .getValue().toString();
                    //Wrong password or username
                    java.util.List result = DataBaseManager.createdQuery(
                            "SELECT x FROM VmUser x WHERE x.username='"
                            + username + "' AND x.userStatus.id <> 2");
                    //Check if the username is correct if not just throw the wrong login message
                    if (result.isEmpty()) {
                        getMainWindow().showNotification("Login "
                                + getInstance().getResource()
                                .getString("general.fail")
                                + " Username and/or Password may be incorrect!",
                                Window.Notification.TYPE_WARNING_MESSAGE);
                    } else {
                        result = DataBaseManager.createdQuery(
                                "SELECT x FROM XincoCoreUser x WHERE x.username='"
                                + username + "'");
                        if (result.size() > 0) {
                            try {
                                VMUserServer temp_user =
                                        new VMUserServer((VmUser) result.get(0));
                                long attempts = Long.parseLong(VMSettingServer.getSetting(
                                        "password.attempts").getLongVal());
                                //If user exists increase the atempt tries in the db. If limit reached lock account
                                if (temp_user.getAttempts()
                                        >= attempts && temp_user.getId() != 1) {
                                    //The logged in admin does the locking
                                    int adminId = 1;
                                    temp_user.setModifierId(adminId);
                                    //Reason for change
                                    temp_user.setModificationReason(
                                            getInstance().getResource()
                                            .getString("password.attempt.limitReached"));
                                    //the password retrieved when you logon is already hashed...
                                    temp_user.setHashPassword(false);
                                    temp_user.setIncreaseAttempts(true);
                                    temp_user.write2DB();
                                    getMainWindow().showNotification(
                                            getInstance().getResource()
                                            .getString("password.attempt.limitReached"),
                                            Window.Notification.TYPE_WARNING_MESSAGE);
                                } else {
                                    getMainWindow().showNotification(
                                            getInstance().getResource()
                                            .getString("password.login.fail"),
                                            Window.Notification.TYPE_WARNING_MESSAGE);
                                }
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    //Enable so they can retry
                    form.getField("username").setEnabled(true);
                    form.getField("password").setEnabled(true);
                    commit.setEnabled(true);
                    cancel.setEnabled(true);
                } else {
                    try {
                        //Update logged user
                        loggedUser =
                                new VMUserServer(((com.vaadin.ui.TextField) form.getField("username")).getValue().toString(),
                                ((PasswordField) form.getField("password")).getValue().toString());
                        if (loggedUser.getUserStatus().getId() == 3) {
                            //Password aging
                            showChangePasswordDialog();
                        }
                        updateMenu();
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
                getMainWindow().removeWindow(loginWindow);
                try {
                    showMainWindow();
                } catch (VMException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
        form.getFooter().setSizeUndefined();
        form.getFooter().addComponent(commit);
        form.getFooter().addComponent(cancel);
        form.setSizeFull();
        loginWindow.addComponent(form);
        loginWindow.center();
        loginWindow.setModal(true);
        loginWindow.setWidth(300, Sizeable.UNITS_PIXELS);
        loginWindow.setReadOnly(true);
        getMainWindow().addWindow(loginWindow);
    }

    /**
     * This refreshes all components on main window. Stuff like
     * enabling/disabling menus and such
     */
    private void updateMenu() throws VMException {
        currentProjectLabel.setCaption(getInstance().getResource()
                .getString("general.project.current") + ": "
                + (currentProject == null
                ? getInstance().getResource().getString("general.no.selection")
                : currentProject.getName()));
        //Update menu bar
        menuBar.removeItems();
        groups.clear();
        for (Iterator<Integer> it = items.keySet().iterator(); it.hasNext();) {
            //TreeMap has keys already sorted
            VMMenuItem item = items.get(it.next());
            if (!groups.containsKey(item.getGroupName())) {
                //Group not in the menu bar yet
                groups.put(item.getGroupName(),
                        menuBar.addItem(item.getGroupName(), null));
            }
            if (canAdd(item)) {
                groups.get(item.getGroupName()).addItem(item.getName(),
                        item.getCommand());
            }
        }
    }

    private Component getRightComponent() {
        return rightComponent;
    }

    /**
     * @param rightComponent the rightComponent to set
     */
    private void setRightComponent(Component rightComponent) {
        this.rightComponent = rightComponent;
    }

    /**
     * @return the leftComponent
     */
    private Component getLeftComponent() {
        return leftComponent;
    }

    /**
     * @param leftComponent the leftComponent to set
     */
    private void setLeftComponent(Component leftComponent) {
        this.leftComponent = leftComponent;
    }

    public void setFileName(String filename) {
        this.fileName = filename;
    }

    public String getFileName() {
        return fileName;
    }
}
