package com.validation.manager.core.tool.msword.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
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
        assert source.getAbsolutePath().endsWith(".docx") :
                "File not supported. Only .docx files are supported.";
    }

    public List<XWPFTable> extractTables() throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(source);
        XWPFDocument doc = new XWPFDocument(fis);
        return doc.getTables();
    }

    public static void main(String[] args) {
        JFileChooser fc = new JFileChooser();
        List<XWPFTable> tables = new ArrayList<XWPFTable>();
        int returnVal = fc.showOpenDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            TableExtractor te = new TableExtractor(file);
            try {
                tables = te.extractTables();
                int i = 1;
                for (XWPFTable table : tables) {
                    System.out.println("Table: " + i);
                    for (int rowIndex = 0; rowIndex < table.getNumberOfRows(); rowIndex++) {
                        XWPFTableRow row = table.getRow(rowIndex);
                        System.out.println("Row: " + rowIndex);
                        for (int colIndex = 0; colIndex < row.getTableCells().size(); colIndex++) {
                            System.out.println("Column: " + colIndex);
                            XWPFTableCell cell = row.getCell(colIndex);
                            for (XWPFParagraph p : cell.getParagraphs()) {
                                System.out.println(p.getText());
                            }
                        }
                    }
                    i++;
                }
            } catch (IOException ex) {
                Logger.getLogger(TableExtractor.class.getName()).log(
                        Level.SEVERE, null, ex);
                System.exit(1);
            }
        }
        Logger.getLogger(TableExtractor.class.getName()).log(Level.INFO,
                "Imported {0} tables!", tables.size());
        System.exit(0);
    }
}
