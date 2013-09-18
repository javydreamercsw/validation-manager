package com.validation.manager.core.tool.msword.importer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TableExtractor {

    private final File source;

    public TableExtractor(File source) {
        this.source = source;
    }

    private List<XWPFTable> extractTablesFromWord() throws FileNotFoundException, IOException {
        List<XWPFTable> tables;
        //Word documents
        InputStream fis = new FileInputStream(source);
        XWPFDocument doc = new XWPFDocument(fis);
        tables = doc.getTables();
        fis.close();
        return tables;
    }

    private File writeTablesToFile() throws IOException {
        File temp = File.createTempFile("table", null);
        temp.createNewFile();
        temp.deleteOnExit();
        FileWriter fw = new FileWriter(temp);
        List<DefaultTableModel> tables = new ArrayList<DefaultTableModel>();
        for (XWPFTable table : extractTablesFromWord()) {
            //Build the table
            int rows = table.getNumberOfRows();
            int columns = table.getRow(0).getTableCells().size();
            Object[][] data = new Object[rows][columns];
            String[] title = new String[columns];
            for (int i = 0; i < columns; i++) {
                title[i] = "Column " + (i + 1);
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
        } finally {
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
            throws IOException, FileNotFoundException, ClassNotFoundException {
        List<DefaultTableModel> tables = new ArrayList<DefaultTableModel>();
        if (source.getName().endsWith(".doc")
                || source.getName().endsWith(".docx")) {
            //Word documents
            tables = loadSerializedTables();
        } else if (source.getName().endsWith(".xls")
                || source.getName().endsWith(".xlsx")
                || source.getName().endsWith(".xlsm")) {
            //Excel documents
            FileInputStream file = new FileInputStream(source);
            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            //Get first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);
            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            DefaultTableModel model = new DefaultTableModel();
            Map<Integer, Vector> data = new HashMap<Integer, Vector>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                Vector cells = new Vector();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String value = "";
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            value = cell.getBooleanCellValue() + "\t\t";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            value = cell.getNumericCellValue() + "\t\t";
                            break;
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue() + "\t\t";
                            break;
                    }
                    cells.add(value);
                }
                data.put(rowNum, cells);
                rowNum++;
            }
            //Process
            for (Entry<Integer, Vector> entry : data.entrySet()) {
                model.addRow(entry.getValue());
            }
            tables.add(model);
        } else if (source.getName().endsWith(".xlsx")
                || source.getName().endsWith(".xlsm")) {
            FileInputStream file = new FileInputStream(source);
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            DefaultTableModel model = new DefaultTableModel();
            Map<Integer, Vector> data = new HashMap<Integer, Vector>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                Vector cells = new Vector();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String value = "";
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            value = cell.getBooleanCellValue() + "\t\t";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            value = cell.getNumericCellValue() + "\t\t";
                            break;
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue() + "\t\t";
                            break;
                    }
                    cells.add(value);
                }
                data.put(rowNum, cells);
                rowNum++;
            }
            //Process
            for (Entry<Integer, Vector> entry : data.entrySet()) {
                model.addRow(entry.getValue());
            }
            tables.add(model);
        } else {
            throw new RuntimeException("Invalid import file: " + source);
        }
        return tables;
    }

    private List<DefaultTableModel> loadSerializedTables()
            throws FileNotFoundException, IOException, ClassNotFoundException {
        List<DefaultTableModel> tables;
        //use buffering
        InputStream is = new FileInputStream(writeTablesToFile());
        InputStream buffer = new BufferedInputStream(is);
        ObjectInput input = new ObjectInputStream(buffer);
        try {
            //deserialize the List
            tables = (List<DefaultTableModel>) input.readObject();
        } finally {
            input.close();
        }
        return tables;
    }

    private List<DefaultTableModel> extractTablesFromExcel(File file)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        List<DefaultTableModel> tables;
        //use buffering
        InputStream is = new FileInputStream(file);
        InputStream buffer = new BufferedInputStream(is);
        ObjectInput input = new ObjectInputStream(buffer);
        try {
            //deserialize the List
            tables = (List<DefaultTableModel>) input.readObject();
        } finally {
            input.close();
        }
        return tables;
    }
}
