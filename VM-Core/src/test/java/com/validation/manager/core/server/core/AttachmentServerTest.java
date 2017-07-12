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
package com.validation.manager.core.server.core;

import com.validation.manager.core.VMException;
import com.validation.manager.core.tool.Tool;
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
import org.h2.store.fs.FileUtils;
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
            //Delete the file
            FileUtils.delete(f.getAbsolutePath());
            assertEquals(1, (int) instance.getAttachmentType().getId());//Text file
            System.out.println("retrieveFile");
            AttachmentServer temp = new AttachmentServer(instance.getAttachmentPK());
            File loadedFile = temp.getAttachedFile("target/loaded/");
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
            File pdf = Tool.convertToPDF(loadedFile, "target/Text.pdf");
            pdf.deleteOnExit();
            instance = new AttachmentServer();
            instance.addFile(pdf, pdf.getName());
            instance.write2DB();
            //Delete the file
            FileUtils.delete(pdf.getAbsolutePath());
            assertEquals(2, (int) instance.getAttachmentType().getId());//PDF file
            System.out.println("retrieveFile");
            temp = new AttachmentServer(instance.getAttachmentPK());
            loadedFile = temp.getAttachedFile("target/loaded/");
            PDFTextStripper pdfStripper;
            PDDocument pdDoc = null;
            COSDocument cosDoc = null;
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
            finally {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            }
        }
        catch (IOException | VMException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
