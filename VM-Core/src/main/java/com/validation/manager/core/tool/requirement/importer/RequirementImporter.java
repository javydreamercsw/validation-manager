package com.validation.manager.core.tool.requirement.importer;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.ImporterInterface;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementJpaController;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Import Requirements into database
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementImporter implements ImporterInterface<Requirement>{

    private File toImport;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    private final RequirementSpecNode rsn;
    private static final Logger LOG =
            Logger.getLogger(RequirementImporter.class.getName());
    private static final List<String> columns = new ArrayList<String>();

    static {
        columns.add("Unique ID");
        columns.add("Description");
        columns.add("Requirement Type");
        columns.add("Notes");
    }

    public RequirementImporter(File toImport, RequirementSpecNode rsn) {
        this.toImport = toImport;
        this.rsn = rsn;
    }

    public List<Requirement> importFile() throws RequirementImportException{
        try {
            return importFile(false);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Requirement> importFile(boolean header) throws
            VMException {
        requirements.clear();
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
                    org.apache.poi.ss.usermodel.Workbook wb =
                            WorkbookFactory.create(inp);
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

                        int cells = row.getPhysicalNumberOfCells();
                        if (cells < 3) {
                            throw new RequirementImportException(
                                    "message.requirement.import.missing.column");
                        }
                        Requirement requirement = new Requirement();
                        HashMap<String, Object> parameters = new HashMap<String, Object>();
                        List<Object> result;
                        LOG.log(Level.FINE, "Row: {0}", r);
                        for (int c = 0; c < cells; c++) {
                            Cell cell = row.getCell(c);
                            String value = null;
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
                                }
                            }
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
                                    result = DataBaseManager.namedQuery(
                                            "RequirementType.findByName",
                                            parameters);
                                    if (result.isEmpty()) {
                                        //Assume a default
                                        parameters.clear();
                                        parameters.put("name", "HW");
                                        result = DataBaseManager.namedQuery(
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
                        requirement.setRequirementSpecNode(rsn);
                        parameters.clear();
                        parameters.put("status", "general.open");
                        result = DataBaseManager.namedQuery(
                                "RequirementStatus.findByStatus", parameters);
                        requirement.setRequirementStatusId(
                                (RequirementStatus) result.get(0));
                        requirements.add(requirement);
                    }
                } catch (InvalidFormatException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        inp.close();
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
            for (Requirement r : requirements) {
                LOG.log(Level.FINE, "{0}: {1}",
                        new Object[]{r.getUniqueId(), r.getDescription()});
            }
            return requirements;
        }
    }

    public boolean processImport() throws VMException{
        if (requirements.isEmpty()) {
            return false;
        } else {
            //TODO: If requirement exists, create a new version?
            for (Iterator<Requirement> it = requirements.iterator(); it.hasNext();) {
                try {
                    Requirement requirement = it.next();
                    new RequirementJpaController(
                            DataBaseManager.getEntityManagerFactory())
                            .create(requirement);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    throw new VMException(ex);
                }
            }
            return true;
        }
    }

    public static File exportTemplate() throws FileNotFoundException, IOException, InvalidFormatException {
        File template = new File("Template.xls");
        template.createNewFile();
        org.apache.poi.ss.usermodel.Workbook wb = new HSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet();
        wb.setSheetName(0, "Requirements");
        int column = 0;
        CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 12);
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setColor((short) Font.COLOR_NORMAL);
        cs.setFont(f);
        Row newRow = sheet.createRow(0);
        for (Iterator<String> it = columns.iterator(); it.hasNext();) {
            String label = it.next();
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
            Logger.getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(RequirementImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
