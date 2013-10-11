package com.validation.manager.core.tool.table.extractor;

import java.io.File;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TableExtractorTest {

    /**
     * Test of extractTables method, of class TableExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractTables() throws Exception {
        System.out.println("extractTables");
        String name = TableExtractorTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", System.getProperty("file.separator"));
        File file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Tables.docx");
        assert file.exists();
        TableExtractor te = new TableExtractor(file);
        List<DefaultTableModel> tables = te.extractTables();
        assertEquals(2, tables.size());
        for (DefaultTableModel model : tables) {
            //Has header
            assertEquals(4, model.getRowCount());
        }
        file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Tables.xls");
        assert file.exists();
        te = new TableExtractor(file);
        tables = te.extractTables();
        assertEquals(1, tables.size());
        for (DefaultTableModel model : tables) {
            assertEquals(3, model.getRowCount());
        }
        file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Tables.xlsx");
        assert file.exists();
        te = new TableExtractor(file);
        tables = te.extractTables();
        assertEquals(1, tables.size());
        for (DefaultTableModel model : tables) {
            assertEquals(3, model.getRowCount());
        }
    }
}
