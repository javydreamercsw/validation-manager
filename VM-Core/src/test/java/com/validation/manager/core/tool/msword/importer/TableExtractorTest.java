package com.validation.manager.core.tool.msword.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.xwpf.usermodel.XWPFTable;
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
        //TODO: Add test for excel files
    }
}
