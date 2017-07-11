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
package com.validation.manager.core.tool.table.extractor;

import com.validation.manager.core.VMException;
import java.io.BufferedInputStream;
import java.io.File;
import static java.io.File.createTempFile;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.text.MessageFormat.format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TableExtractor {

    private final File source;
    private final static Logger LOG
            = getLogger(TableExtractor.class.getSimpleName());

    public TableExtractor(File source) {
        this.source = source;
    }

    private List<XWPFTable> extractTablesFromWord() throws
            FileNotFoundException, IOException {
        List<XWPFTable> tables;
        try ( //Word documents
                InputStream fis = new FileInputStream(source)) {
            XWPFDocument doc = new XWPFDocument(fis);
            tables = doc.getTables();
        }
        return tables;
    }

    private File writeTablesToFile() throws IOException {
        File temp = createTempFile("table", null);
        temp.createNewFile();
        temp.deleteOnExit();
        List<DefaultTableModel> tables = new ArrayList<>();
        for (XWPFTable table : extractTablesFromWord()) {
            //Build the table
            int rows = table.getNumberOfRows();
            int columns = table.getRow(0).getTableCells().size();
            Object[][] data = new Object[rows][columns];
            String[] title = new String[columns];
            for (int i = 0; i < columns; i++) {
                title[i] = format("Column {0}", i + 1);
            }
            //Row 0 for mapping field
            int rowNum = 0;
            int columnNum;
            for (XWPFTableRow row : table.getRows()) {
                columnNum = 0;
                for (XWPFTableCell cell : row.getTableCells()) {
                    data[rowNum][columnNum] = cell.getText();
                    columnNum++;
                }
                rowNum++;
            }
            //Rebuild the table model to fit this table
            tables.add(new DefaultTableModel(data, title));
        }
        FileOutputStream fileOut = null;
        ObjectOutputStream output = null;
        try {
            fileOut = new FileOutputStream(temp);
            output = new ObjectOutputStream(fileOut);
            output.writeObject(tables);
            output.flush();
        }
        finally {
            if (output != null) {
                output.close();
            }
            if (fileOut != null) {
                fileOut.close();
            }
        }
        return temp;
    }

    public List<DefaultTableModel> extractTables()
            throws IOException, FileNotFoundException, ClassNotFoundException,
            VMException {
        List<DefaultTableModel> tables = new ArrayList<>();
        if (source.getName().endsWith(".doc")
                || source.getName().endsWith(".docx")
                || source.getName().endsWith(".docm")) {
            //Word documents
            tables = loadSerializedTables();
        } else if (source.getName().endsWith(".xls")) {
            //Pre Office 2007+ XML
            //Excel documents
            FileInputStream file = new FileInputStream(source);
            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            //Get first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);
            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            int columns = 0;
            Map<Integer, ArrayList<Object>> data = new HashMap<>();
            while (rowIterator.hasNext()) {
                ArrayList<Object> cells = new ArrayList<>();
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cells.add(cell.getStringCellValue().trim());
                    if (rowNum == 0) {
                        columns++;
                    }
                }
                data.put(rowNum, cells);
                rowNum++;
            }
            //Process
            Object[][] data2 = new Object[rowNum][columns];
            String[] title = new String[columns];
            for (int i = 0; i < columns; i++) {
                title[i] = format("Column {0}", i + 1);
            }
            int row = 0;
            int col = 0;
            for (int i = 0; i < rowNum; i++) {
                for (Object obj : data.get(row)) {
                    LOG.log(Level.FINE, "r: {0} c: {1} v: {2}",
                            new Object[]{row, col, obj});
                    data2[row][col] = obj;
                    col++;
                }
                row++;
                col = 0;
            }
            tables.add(new DefaultTableModel(data2, title));
        } else if (source.getName().endsWith(".xlsx")
                || source.getName().endsWith(".xlsm")) {
            //Office 2007+ XML
            FileInputStream file = new FileInputStream(source);
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            int columns = 0;
            Map<Integer, ArrayList<Object>> data = new HashMap<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ArrayList<Object> cells = new ArrayList<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cells.add(cell.getStringCellValue().trim());
                    if (rowNum == 0) {
                        columns++;
                    }
                }
                data.put(rowNum, cells);
                rowNum++;
            }
            //Process
            Object[][] data2 = new Object[rowNum][columns];
            String[] title = new String[columns];
            for (int i = 0; i < columns; i++) {
                title[i] = format("Column {0}", i + 1);
            }
            int row = 0, col = 0;
            for (int i = 0; i < rowNum; i++) {
                for (Object obj : data.get(row)) {
                    LOG.log(Level.FINE, "r: {0} c: {1} v: {2}",
                            new Object[]{row, col, obj});
                    data2[row][col] = obj;
                    col++;
                }
                row++;
                col = 0;
            }
            tables.add(new DefaultTableModel(data2, title));
        } else {
            throw new VMException(
                    format("Invalid import file: {0}", source));
        }
        return tables;
    }

    private List<DefaultTableModel> loadSerializedTables()
            throws FileNotFoundException, IOException, ClassNotFoundException {
        List<DefaultTableModel> tables;
        //use buffering
        InputStream is = new FileInputStream(writeTablesToFile());
        InputStream buffer = new BufferedInputStream(is);
        try (ObjectInput input = new ObjectInputStream(buffer)) {
            //deserialize the List
            tables = (List<DefaultTableModel>) input.readObject();
        }
        return tables;
    }
}
