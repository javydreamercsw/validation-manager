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
package com.validation.manager.core.tool.step.importer;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.ImporterInterface;
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
import static java.lang.Integer.valueOf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.util.Locale.getDefault;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;
import java.util.StringTokenizer;
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

/**
 * Import Requirements into database
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class StepImporter implements ImporterInterface<Step> {

    private final File toImport;
    private final List<Step> steps = new ArrayList<>();
    private final TestCase tc;
    private static final Logger LOG
            = getLogger(StepImporter.class.getName());
    private static final List<String> COLUMNS = new ArrayList<>();
    private static final ResourceBundle RB
            = getBundle(
                    "com.validation.manager.resources.VMMessages", getDefault());

    static {
        COLUMNS.add("Sequence");
        COLUMNS.add("Text");
        COLUMNS.add("Related Requirements (Optional)");
        COLUMNS.add("Expected Result (Optional)");
        COLUMNS.add("Notes (Optional)");
    }

    public StepImporter(File toImport, TestCase tc) {
        this.toImport = toImport;
        this.tc = tc;
    }

    @Override
    public List<Step> importFile() throws
            TestCaseImportException {
        return importFile(false);
    }

    @Override
    public List<Step> importFile(boolean header) throws
            TestCaseImportException {
        steps.clear();
        if (toImport == null) {
            throw new TestCaseImportException(
                    "message.step.import.file.null");
        } else if (!toImport.exists()) {
            throw new TestCaseImportException(
                    "message.step.import.file.invalid");
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
                        int cells = row.getPhysicalNumberOfCells();
                        if (row.getCell(0) == null) {
                            LOG.log(Level.WARNING,
                                    "Found an empty row on line: {0}. "
                                    + "Stopping processing", r);
                            break;
                        }
                        if (cells < 2) {
                            throw new TestCaseImportException(
                                    RB.getString("message.step.import.missing.column")
                                            .replaceAll("%c", "" + cells));
                        }
                        Step step = new Step();
                        step.setRequirementList(new ArrayList<>());
                        HashMap<String, Object> parameters = new HashMap<>();
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
                                        Integer val
                                                = value.contains(".")
                                                ? valueOf(value.substring(0,
                                                        value.indexOf(".")))
                                                : valueOf(value);
                                        if (!tc.getStepList().isEmpty()) {
                                            int max = 0;
                                            for (Step s : tc.getStepList()) {
                                                if (s.getStepSequence() > max) {
                                                    max = s.getStepSequence();
                                                }
                                            }
                                            //Make sure there isn't one on that sequence already
                                            val += max;
                                        }
                                        step.setStepSequence(val);
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
                                            result = namedQuery(
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
                } catch (InvalidFormatException | IOException ex) {
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
                throw new TestCaseImportException(
                        "XML importing not supported yet.");
            } else {
                throw new TestCaseImportException("Unsupported file format: "
                        + toImport.getName());
            }
            return steps;
        }
    }

    @Override
    public boolean processImport() throws TestCaseImportException {
        boolean result = false;
        for (Step step : steps) {
            try {
                new StepJpaController(
                        getEntityManagerFactory())
                        .create(step);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                throw new TestCaseImportException(ex);
            }
        }
        return result;
    }

    public static File exportTemplate() throws FileNotFoundException,
            IOException, InvalidFormatException {
        File template = new File("Template.xls");
        template.createNewFile();
        org.apache.poi.ss.usermodel.Workbook wb = new HSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet();
        wb.setSheetName(0, "Steps");
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
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return template;
    }

    public static void main(String[] args) {
        try {
            File file = exportTemplate();
            System.out.println(file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
