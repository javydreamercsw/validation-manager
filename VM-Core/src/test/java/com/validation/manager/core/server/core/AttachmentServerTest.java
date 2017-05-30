package com.validation.manager.core.server.core;

import com.validation.manager.test.AbstractVMTestCase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.TextToPDF;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AttachmentServerTest extends AbstractVMTestCase {

    /**
     * Test of addFile method, of class AttachmentServer.
     */
    @Test
    public void testAddRetrieveTextFile() {
        try {
            System.out.println("add text File");
            File f = new File("target/Test.txt");
            f.deleteOnExit();
            List<String> lines = Arrays.asList("The first line", "The second line");
            Path file = Paths.get(f.getAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
            AttachmentServer instance = new AttachmentServer();
            instance.addFile(f, f.getName());
            instance.write2DB();
            assertEquals(1, (int) instance.getAttachmentType().getId());//Text file
            System.out.println("retrieveFile");
            AttachmentServer temp = new AttachmentServer(instance.getAttachmentPK());
            File loadedFile = temp.getAttachedFile();
            BufferedReader br = new BufferedReader(new FileReader(loadedFile));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                assertEquals(lines.get(count), line);
                System.out.println(line);
                count++;
            }
            assertEquals(lines.size(), count);
            //Create pdf file
            System.out.println("add pdf File");
            TextToPDF ttp = new TextToPDF();
            PDDocument pdfd = ttp.createPDFFromText(new FileReader(loadedFile));
            File pdf = new File("target/Text.pdf");
            pdf.deleteOnExit();
            pdfd.save(pdf);
            instance = new AttachmentServer();
            instance.addFile(pdf, pdf.getName());
            instance.write2DB();
            assertEquals(2, (int) instance.getAttachmentType().getId());//PDF file
            System.out.println("retrieveFile");
            temp = new AttachmentServer(instance.getAttachmentPK());
            loadedFile = temp.getAttachedFile();
            PDFTextStripper pdfStripper;
            PDDocument pdDoc;
            COSDocument cosDoc;
            try {
                PDFParser parser
                        = new PDFParser(new RandomAccessBufferedFileInputStream(loadedFile));
                parser.parse();
                cosDoc = parser.getDocument();
                pdfStripper = new PDFTextStripper();
                pdDoc = new PDDocument(cosDoc);
                pdfStripper.setStartPage(1);
                pdfStripper.setEndPage(1);
                String parsedText = pdfStripper.getText(pdDoc);
                System.out.println(parsedText);
            }
            catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                fail();
            }
        }
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
