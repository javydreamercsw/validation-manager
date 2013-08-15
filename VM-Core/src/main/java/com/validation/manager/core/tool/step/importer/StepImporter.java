package com.validation.manager.core.tool.step.importer;

import com.validation.manager.core.ImporterInterface;
import com.validation.manager.core.tool.requirement.importer.*;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.StepJpaController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Import Requirements into database
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepImporter implements ImporterInterface<Step> {

    private final File toImport;
    private final List<Step> steps = new ArrayList<Step>();
    private final TestCase tc;
    private static final Logger LOG
            = Logger.getLogger(StepImporter.class.getName());
    private static final List<String> columns = new ArrayList<String>();
    private static final ResourceBundle rb
            = ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages", Locale.getDefault());

    static {
        columns.add("Sequence");
        columns.add("Text");
        columns.add("Related Requirements (Optional)");
        columns.add("Expected Result (Optional)");
        columns.add("Notes (Optional)");
    }

    public StepImporter(File toImport, TestCase tc) {
        this.toImport = toImport;
        this.tc = tc;
    }

    @Override
    public List<Step> importFile() throws
            RequirementImportException {
        List<Step> importedSteps = null;
        try {
            importedSteps = importFile(false);
        } catch (RequirementImportException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return importedSteps;
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
                    org.apache.poi.ss.usermodel.Workbook wb
                            = WorkbookFactory.create(inp);
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
                        if (row.getCell(0) == null) {
                            LOG.log(Level.WARNING,
                                    "Found an empty row on line: {0}. "
                                    + "Stopping processing", r);
                            break;
                        }
                        if (cells < 2) {
                            throw new RequirementImportException(
                                    rb.getString("message.step.import.missing.column")
                                    .replaceAll("%c", "" + cells));
                        }
                        Step step = new Step();
                        step.setRequirementList(new ArrayList<Requirement>());
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
                                    //Do nothing.
                                }
                            }
                            switch (c) {
                                case 0:
                                    if (value != null) {
                                        //Sequence
                                        LOG.fine("Setting sequence");
                                        step.setStepSequence(
                                                Integer.valueOf(value.substring(0, value.indexOf("."))));
                                    }
                                    break;
                                case 1:
                                    if (value != null) {
                                        //Text
                                        LOG.fine("Setting text");
                                        step.setText(value.getBytes("UTF-8"));
                                    }
                                    break;
                                case 2:
                                    //Optional Related requirements
                                    if (value != null && !value.trim().isEmpty()) {
                                        LOG.fine("Setting related requirements");
                                        StringTokenizer st = new StringTokenizer(value, ",");
                                        while (st.hasMoreTokens()) {
                                            String token = st.nextToken().trim();
                                            parameters.clear();
                                            parameters.put("uniqueId", token);
                                            result = DataBaseManager.namedQuery(
                                                    "Requirement.findByUniqueId",
                                                    parameters);
                                            if (!result.isEmpty()) {
                                                for (Object o : result) {
                                                    step.getRequirementList().add((Requirement) o);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case 3:
                                    if (value != null) {
                                        //Optional Expected result
                                        LOG.fine("Setting expected result");
                                        step.setExpectedResult(value.getBytes("UTF-8"));
                                    }
                                    break;
                                case 4:
                                    if (value != null) {
                                        //Optional notes
                                        LOG.fine("Setting notes");
                                        step.setNotes(value);
                                    }
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
            return steps;
        }
    }

    @Override
    public boolean processImport() throws VMException {
        boolean result = false;
        for (Step step : steps) {
            try {
                new StepJpaController(
                        DataBaseManager.getEntityManagerFactory())
                        .create(step);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
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
        JFileChooser fc = new JFileChooser();
        DataBaseManager.setPersistenceUnitName("TestVMPU");
        int returnVal = fc.showOpenDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            StepImporter si = new StepImporter(file, null);
            try {
                List<Step> imported = si.importFile(true);
                LOG.info("Imported Steps:");
                for (Step s : imported) {
                    LOG.log(Level.INFO, "Step: {0}", s.getStepSequence());
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            Map<String, Object> properties = DataBaseManager.getEntityManagerFactory().getProperties();
            DataSource ds = new JdbcDataSource();
            ((JdbcDataSource) ds).setPassword((String) properties.get("javax.persistence.jdbc.password"));
            ((JdbcDataSource) ds).setUser((String) properties.get("javax.persistence.jdbc.user"));
            ((JdbcDataSource) ds).setURL((String) properties.get("javax.persistence.jdbc.url"));
            //Load the H2 driver
            Class.forName("org.h2.Driver");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DROP ALL OBJECTS DELETE FILES");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        DataBaseManager.close();
        System.exit(0);
    }
}
