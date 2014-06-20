package com.validation.manager.core.tool.requirement.importer;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.ImporterInterface;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import static com.validation.manager.core.server.core.ProjectServer.getRequirements;
import com.validation.manager.core.tool.message.MessageHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import static org.apache.poi.hssf.usermodel.HSSFDataFormat.getBuiltinFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import static org.apache.poi.ss.usermodel.WorkbookFactory.create;
import static org.openide.util.Lookup.getDefault;

/**
 * Import Requirements into database
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementImporter implements ImporterInterface<Requirement> {

    private final File toImport;
    private final ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    private final RequirementSpecNode rsn;
    private static final Logger LOG
            = getLogger(RequirementImporter.class.getName());
    private static final List<String> columns = new ArrayList<String>();

    static {
        columns.add("Unique ID");
        columns.add("Description");
        columns.add("Requirement Type");
        columns.add("Notes");
    }

    public RequirementImporter(File toImport, RequirementSpecNode rsn) {
        assert rsn != null : "Requirement Spec Node is null?";
        this.toImport = toImport;
        this.rsn = rsn;
    }

    @Override
    public List<Requirement> importFile() throws RequirementImportException {
        List<Requirement> importedRequirements = null;
        try {
            importedRequirements = importFile(false);
        } catch (UnsupportedOperationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return importedRequirements;
    }

    @Override
    public List<Requirement> importFile(boolean header) throws
            VMException {
        requirements.clear();
        List<Integer> errors = new ArrayList<Integer>();
        if (toImport == null) {
            throw new RequirementImportException(
                    "message.requirement.import.file.null");
        } else if (!toImport.exists()) {
            throw new RequirementImportException(
                    "message.requirement.import.file.invalid");
        } else {
            //Excel support
            if (toImport.getName().endsWith(".xls")
                    || toImport.getName().endsWith(".xlsx")) {
                InputStream inp = null;
                try {
                    inp = new FileInputStream(toImport);
                    org.apache.poi.ss.usermodel.Workbook wb
                            = create(inp);
                    org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);
                    int rows = sheet.getPhysicalNumberOfRows();
                    int r = 0;
                    if (header) {
                        //Skip header row
                        r++;
                    }
                    for (; r < rows; r++) {
                        Row row = sheet.getRow(r);
                        if (row == null) {
                            continue;
                        }
                        if (row.getCell(0) == null) {
                            LOG.log(Level.WARNING,
                                    "Found an empty row on line: {0}. "
                                    + "Stopping processing", r);
                            break;
                        }
                        int cells = row.getPhysicalNumberOfCells();
                        if (cells < 3) {
                            LOG.log(Level.INFO, "Processing row: {0}", r);
                            LOG.warning(
                                    ResourceBundle.getBundle(
                                            "com.validation.manager.resources.VMMessages",
                                            Locale.getDefault()).getString(
                                            "message.requirement.import.missing.column").replaceAll("%c", "" + cells));
                            errors.add(r);
                        } else {
                            Requirement requirement = new Requirement();
                            HashMap<String, Object> parameters = new HashMap<String, Object>();
                            List<Object> result;
                            LOG.log(Level.FINE, "Row: {0}", r);
                            for (int c = 0; c < cells; c++) {
                                Cell cell = row.getCell(c);
                                String value = "";
                                if (cell != null) {
                                    switch (cell.getCellType()) {

                                        case Cell.CELL_TYPE_FORMULA:
                                            value = cell.getCellFormula();
                                            break;

                                        case Cell.CELL_TYPE_NUMERIC:
                                            value = "" + cell.getNumericCellValue();
                                            break;

                                        case Cell.CELL_TYPE_STRING:
                                            value = cell.getStringCellValue();
                                            break;
                                        default:
                                            value = "";
                                    }
                                }
                                //Remove any extra spaces.
                                value = value.trim();
                                switch (c) {
                                    case 0:
                                        //Unique ID
                                        LOG.fine("Setting id");
                                        requirement.setUniqueId(value);
                                        break;
                                    case 1:
                                        //Description
                                        LOG.fine("Setting desc");
                                        requirement.setDescription(value);
                                        break;
                                    case 2:
                                        //Requirement type
                                        LOG.fine("Setting requirement type");
                                        parameters.clear();
                                        parameters.put("name", value);
                                        result = namedQuery(
                                                "RequirementType.findByName",
                                                parameters);
                                        if (result.isEmpty()) {
                                            //Assume a default
                                            parameters.clear();
                                            parameters.put("name", "HW");
                                            result = namedQuery(
                                                    "RequirementType.findByName",
                                                    parameters);
                                        }
                                        requirement.setRequirementTypeId(
                                                (RequirementType) result.get(0));
                                        break;
                                    case 3:
                                        //Optional notes
                                        LOG.fine("Setting notes");
                                        requirement.setNotes(value);
                                        break;
                                    default:
                                        throw new RuntimeException("Invalid column detected: " + c);
                                }
                                LOG.fine(value);
                            }
                            //This shouldn't be null
                            assert rsn != null : "Requirement Spec Node is null?";
                            requirement.setRequirementSpecNode(rsn);
                            parameters.clear();
                            parameters.put("status", "general.open");
                            result = namedQuery(
                                    "RequirementStatus.findByStatus", parameters);
                            requirement.setRequirementStatusId(
                                    (RequirementStatus) result.get(0));
                            assert requirement.getUniqueId() != null
                                    && !requirement.getUniqueId().isEmpty() :
                                    "Invalid requirement detected!";
                            requirements.add(requirement);
                        }
                    }
                } catch (InvalidFormatException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (inp != null) {
                            inp.close();
                        }
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            } else if (toImport.getName().endsWith(".xml")) {
                throw new RequirementImportException(
                        "XML importing not supported yet.");
            } else {
                throw new RequirementImportException("Unsupported file format: "
                        + toImport.getName());
            }
            StringBuilder sb = new StringBuilder("Rows with erros:\n");
            for (Integer line : errors) {
                sb.append(line).append("\n");
            }
            if (!errors.isEmpty()) {
                getDefault().lookup(MessageHandler.class).info(sb.toString());
            }
            for (Requirement r : requirements) {
                LOG.log(Level.FINE, "{0}: {1}",
                        new Object[]{r.getUniqueId(), r.getDescription()});
            }
            return requirements;
        }
    }

    @Override
    public boolean processImport() throws VMException {
        boolean result = true;
        RequirementJpaController controller = new RequirementJpaController(
                getEntityManagerFactory());
        for (Iterator<Requirement> it = requirements.iterator(); it.hasNext();) {
            try {
                Requirement requirement = it.next();
                boolean exists = false;
                Project project = requirement.getRequirementSpecNode().getRequirementSpec().getProject();
                List<Requirement> existing = getRequirements(project);
                for (Requirement r : existing) {
                    if (r.getUniqueId() == null) {
                        LOG.warning("Detected requirement with null unique id!");
                        new RequirementJpaController(getEntityManagerFactory()).destroy(r.getId());
                    } else {
                        if (r.getUniqueId().equals(requirement.getUniqueId())) {
                            exists = true;
                            result = false;
                            break;
                        }
                    }
                }
                if (exists) {
                    MessageHandler handler = getDefault().lookup(MessageHandler.class);
                    if (handler != null) {
                        handler.error(
                                "Requirement " + requirement.getUniqueId()
                                + " already exists on project "
                                + project.getName());
                    }
                } else {
                    controller.create(requirement);
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                for (Requirement requirement : requirements) {
                    Project project = requirement.getRequirementSpecNode().getRequirementSpec().getProject();
                    List<Requirement> existing = getRequirements(project);
                    for (Requirement r : existing) {
                        if (r.getUniqueId().equals(requirement.getUniqueId())) {
                            try {
                                controller.destroy(r.getId());
                            } catch (IllegalOrphanException ex1) {
                                LOG.log(Level.SEVERE, null, ex1);
                            } catch (NonexistentEntityException ex1) {
                                LOG.log(Level.SEVERE, null, ex1);
                            }
                            break;
                        }
                    }
                }
                throw new VMException(ex);
            }
        }
        return result;
    }

    public static File exportTemplate() throws FileNotFoundException, IOException, InvalidFormatException {
        File template = new File("Template.xls");
        template.createNewFile();
        org.apache.poi.ss.usermodel.Workbook wb = new HSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet();
        wb.setSheetName(0, "Requirements");
        int column = 0;
        CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(getBuiltinFormat("text"));
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 12);
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setColor((short) Font.COLOR_NORMAL);
        cs.setFont(f);
        Row newRow = sheet.createRow(0);
        for (String label : columns) {
            Cell newCell = newRow.createCell(column);
            newCell.setCellStyle(cs);
            newCell.setCellValue(label);
            column++;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(template);
            wb.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return template;
    }

    public static void main(String[] args) {
        try {
            File file = exportTemplate();
            System.out.println(file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
