package com.validation.manager.core.tool.step;

import com.validation.manager.core.ImporterInterface;
import com.validation.manager.core.tool.requirement.importer.*;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
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
public class StepImporter implements ImporterInterface<Step> {

    private File toImport;
    private List<Step> steps = new ArrayList<Step>();
    private final TestCase tc;
    private static final Logger LOG =
            Logger.getLogger(StepImporter.class.getName());
    private static final List<String> columns = new ArrayList<String>();

    static {
        columns.add("Sequence");
        columns.add("Text");
        columns.add("Related Requirements (Optional)");
        columns.add("Notes (Optional)");
    }

    public StepImporter(File toImport, TestCase tc) {
        this.toImport = toImport;
        this.tc = tc;
    }

    @Override
    public List<Step> importFile() throws
            RequirementImportException {
        try {
            return importFile(false);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Step> importFile(boolean header) throws
            RequirementImportException {
        steps.clear();
        if (toImport == null) {
            throw new RequirementImportException(
                    "message.step.import.file.null");
        } else if (!toImport.exists()) {
            throw new RequirementImportException(
                    "message.step.import.file.invalid");
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
                                    "message.step.import.missing.column");
                        }
                        Step step = new Step();
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
                                    //Sequence
                                    LOG.fine("Setting sequence");
                                    step.setStepSequence(Integer.valueOf(value));
                                    break;
                                case 1:
                                    //Text
                                    LOG.fine("Setting text");
                                    step.setText(value.getBytes("UTF-8"));
                                    break;
                                case 2:
                                    //Optional Related requirements
                                    LOG.fine("Setting related requirements");
                                    parameters.clear();
                                    parameters.put("uniqueId", value);
                                    result = DataBaseManager.namedQuery(
                                            "Requirement.findByUniqueId",
                                            parameters);
                                    if (!result.isEmpty()) {
                                        for(Object o:result){
                                            step.getRequirementList().add((Requirement)o);
                                        }
                                    }
                                    break;
                                case 3:
                                    //Optional notes
                                    LOG.fine("Setting notes");
                                    step.setNotes(value);
                                    break;
                                default:
                                    throw new RuntimeException("Invalid column detected: " + c);
                            }
                            LOG.fine(value);
                        }
                        step.setTestCase(tc);
                        steps.add(step);
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
            for (Step s : steps) {
                LOG.log(Level.FINE, "{0}: {1}",
                        new Object[]{s.getStepPK().getId(), s.getStepSequence()});
            }
            return steps;
        }
    }

    @Override
    public boolean processImport() throws PreexistingEntityException {
        if (steps.isEmpty()) {
            return false;
        } else {
            for (Iterator<Step> it = steps.iterator(); it.hasNext();) {
                Step step = it.next();
                try {
                    new StepJpaController(
                            DataBaseManager.getEntityManagerFactory())
                            .create(step);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
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
        wb.setSheetName(0, "Steps");
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
