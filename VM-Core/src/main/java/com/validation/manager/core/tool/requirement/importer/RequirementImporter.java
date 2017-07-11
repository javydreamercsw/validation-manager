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
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.core.tool.message.MessageHandler;
import com.validation.manager.core.tool.table.extractor.TableExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.swing.table.DefaultTableModel;
import static org.apache.poi.hssf.usermodel.HSSFDataFormat.getBuiltinFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import static org.apache.poi.ss.usermodel.WorkbookFactory.create;
import org.openide.util.Exceptions;
import static org.openide.util.Lookup.getDefault;

/**
 * Import Requirements into database
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RequirementImporter implements ImporterInterface<Requirement> {

    private final File toImport;
    private final RequirementSpecNode rsn;
    private static final Logger LOG
            = getLogger(RequirementImporter.class.getSimpleName());
    private static final List<String> COLUMNS = new ArrayList<>();
    private final Map<String, Requirement> queue = new HashMap<>();
    private InputStream inp;

    static {
        COLUMNS.add("Unique ID");
        COLUMNS.add("Description");
        COLUMNS.add("Requirement Type");
        COLUMNS.add("Notes");
    }

    public RequirementImporter(File toImport, RequirementSpecNode rsn) {
        assert rsn != null : "Requirement Spec Node is null?";
        this.toImport = toImport;
        this.rsn = rsn;
    }

    public Workbook loadFile() throws FileNotFoundException,
            IOException, InvalidFormatException {
        if (toImport != null && toImport.exists()) {
            inp = new FileInputStream(toImport);
            Workbook wb = create(inp);
            return wb;
        }
        return null;
    }

    @Override
    public List<Requirement> importFile() throws RequirementImportException {
        List<Requirement> importedRequirements = new ArrayList<>();
        try {
            importedRequirements.addAll(importFile(false));
        }
        catch (UnsupportedOperationException | VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return importedRequirements;
    }

    @Override
    public List<Requirement> importFile(boolean header) throws
            RequirementImportException, VMException {
        queue.clear();
        List<Integer> errors = new ArrayList<>();
        HashMap<String, Object> parameters = new HashMap<>();
        List<Object> result;
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
                try {
                    Workbook wb = loadFile();
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
                        if (cells < 2) {
                            LOG.log(Level.INFO, "Processing row: {0}", r);
                            LOG.warning(
                                    ResourceBundle.getBundle(
                                            "com.validation.manager.resources.VMMessages",
                                            Locale.getDefault()).getString(
                                            "message.requirement.import.missing.column")
                                            .replaceAll("%c", "" + cells));
                            errors.add(r);
                        } else {
                            Requirement requirement = new Requirement();
                            LOG.log(Level.FINE, "Row: {0}", r);
                            for (int c = 0; c < cells; c++) {
                                Cell cell = row.getCell(c);
                                String value = "";
                                if (cell != null) {
                                    switch (cell.getCellTypeEnum()) {
                                        case FORMULA:
                                            value = cell.getCellFormula();
                                            break;
                                        case NUMERIC:
                                            value = "" + cell.getNumericCellValue();
                                            break;
                                        case STRING:
                                            value = cell.getStringCellValue();
                                            break;
                                        default:
                                            value = "";
                                            break;
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
                                        //Optional Requirement type
                                        LOG.fine("Setting requirement type");
                                        parameters.clear();
                                        parameters.put("name", value);
                                        result = namedQuery(
                                                "RequirementType.findByName",
                                                parameters);
                                        if (result.isEmpty()) {
                                            //Assume a default
                                            parameters.clear();
                                            parameters.put("name", "SW");
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
                                        throw new RequirementImportException(
                                                "Invalid column detected: " + c);
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
                            try {
                                if (!exists(requirement)
                                        && !queue.containsKey(requirement.getUniqueId())) {
                                    queue.put(requirement.getUniqueId(),
                                            requirement);
                                }
                            }
                            catch (IllegalOrphanException | NonexistentEntityException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
                catch (InvalidFormatException | IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                finally {
                    try {
                        if (inp != null) {
                            inp.close();
                        }
                    }
                    catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            } else if (toImport.getName().endsWith(".xml")) {
                throw new RequirementImportException(
                        "XML importing not supported yet.");
            } else if (toImport.getName().endsWith(".doc")
                    || toImport.getName().endsWith(".docx")) {
                try {
                    TableExtractor te = new TableExtractor(toImport);
                    List<DefaultTableModel> tables = te.extractTables();
                    Requirement requirement = new Requirement();
                    LOG.log(Level.INFO, "Imported {0} tables!", tables.size());
                    int count = 1;
                    for (DefaultTableModel model : tables) {
                        int rows = model.getRowCount();
                        int cols = model.getColumnCount();
                        LOG.log(Level.INFO, "Processing table {0} with {1} "
                                + "rows and {2} columns.",
                                new Object[]{count, rows, cols});
                        for (int r = 0; r < rows; r++) {
                            for (int c = 0; c < cols; c++) {
                                String value = (String) model.getValueAt(rows, cols);
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
                                            parameters.put("name", "SW");
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
                            }
                        }
                    }
                }
                catch (IOException | ClassNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                throw new RequirementImportException("Unsupported file format: "
                        + toImport.getName());
            }
            StringBuilder sb = new StringBuilder("Rows with erros:\n");
            errors.stream().forEach((line) -> {
                sb.append(line).append('\n');
            });
            if (!errors.isEmpty()) {
                getDefault().lookup(MessageHandler.class).info(sb.toString());
            }
            return new ArrayList(queue.values());
        }
    }

    public boolean exists(Requirement requirement) throws
            IllegalOrphanException, NonexistentEntityException {
        Project project = requirement.getRequirementSpecNode()
                .getRequirementSpec().getProject();
        List<Requirement> existing = Tool.extractRequirements(project);
        LOG.log(Level.INFO, "Processing: {0}", requirement.getUniqueId());
        boolean exists = false;
        for (Requirement r : existing) {
            if (r.getUniqueId() == null) {
                LOG.warning("Detected requirement with null unique id!");
                new RequirementJpaController(getEntityManagerFactory())
                        .destroy(r.getId());
            } else {
                if (r.getUniqueId().equals(requirement.getUniqueId())) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }

    private boolean processRequirement(Requirement requirement)
            throws IllegalOrphanException, NonexistentEntityException,
            Exception {
        boolean result = true;
        Project project = requirement.getRequirementSpecNode()
                .getRequirementSpec().getProject();
        if (exists(requirement)) {
            MessageHandler handler = getDefault().lookup(MessageHandler.class);
            if (handler != null) {
                String error = "Requirement " + requirement.getUniqueId()
                        + " already exists on project "
                        + project.getName();
                LOG.warning(error);
                handler.error(error);
            }
            result = false;
        } else {
            new RequirementJpaController(getEntityManagerFactory())
                    .create(requirement);
        }
        return result;
    }

    @Override
    public boolean processImport() throws RequirementImportException {
        try {
            for (Requirement r : queue.values()) {
                processRequirement(r);
            }
            RequirementSpecNodeServer rsns = new RequirementSpecNodeServer(rsn);
            rsns.update(rsn, rsns);
            queue.clear();
            return true;
        }
        catch (NonexistentEntityException ex) {
            Exceptions.printStackTrace(ex);
            throw new RequirementImportException(ex.getLocalizedMessage());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            throw new RequirementImportException(ex.getLocalizedMessage());
        }
    }

    public static File exportTemplate() throws FileNotFoundException,
            IOException, InvalidFormatException {
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
        f.setBold(true);
        f.setColor((short) Font.COLOR_NORMAL);
        cs.setFont(f);
        Row newRow = sheet.createRow(0);
        for (String label : COLUMNS) {
            Cell newCell = newRow.createCell(column);
            newCell.setCellStyle(cs);
            newCell.setCellValue(label);
            column++;
        }

        try (FileOutputStream out = new FileOutputStream(template)) {
            wb.write(out);
            out.close();
        }
        catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return template;
    }

    public static void main(String[] args) {
        try {
            File file = exportTemplate();
            System.out.println(file.getAbsolutePath());
        }
        catch (FileNotFoundException | InvalidFormatException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
