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
package com.validation.manager.core.tool;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.TextToPDF;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class Tool {

    private static final Logger LOG
            = Logger.getLogger(Tool.class.getSimpleName());

    public static ImageIcon createImageIcon(String path, String description) {
        return createImageIcon(path, description, null);
    }

    public static ImageIcon createImageIcon(String path, String description, Class relativeTo) {
        URL imgURL = relativeTo == null ? Tool.class.getResource(path)
                : relativeTo.getResource(path);
        return imgURL == null ? null : new ImageIcon(imgURL, description);
    }

    public static void removeDuplicates(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        list.clear();
        list.addAll(newList);
    }

    public static List<Requirement> extractRequirements(Project p) {
        ProjectServer ps;
        if (p instanceof ProjectServer) {
            ps = (ProjectServer) p;
        } else {
            ps = new ProjectServer(p);
        }
        List<Requirement> result = new ArrayList<>();
        ps.getRequirementSpecList().forEach(rs -> {
            result.addAll(extractRequirements(rs));
        });
        ps.getProjectList().forEach(sub -> {
            result.addAll(extractRequirements(sub));
        });
        return result;
    }

    public static List<Requirement> extractRequirements(RequirementSpecNode rsn) {
        ArrayList<Requirement> result = new ArrayList<>();
        rsn.getRequirementSpecNodeList().forEach(rsn2 -> {
            result.addAll(extractRequirements(rsn2));
        });
        result.addAll(rsn.getRequirementList());
        return result;
    }

    public static List<Requirement> extractRequirements(RequirementSpec rs) {
        ArrayList<Requirement> result = new ArrayList<>();
        rs.getRequirementSpecNodeList().forEach(rsn -> {
            result.addAll(extractRequirements(rsn));
        });
        return result;
    }

    public static TCEExtraction extractTCE(Object key) {
        TestCaseExecutionServer tce = null;
        TestCaseServer tcs = null;
        if (key instanceof String) {
            String item = (String) key;
            String tceIdS = item.substring(item.indexOf("-") + 1,
                    item.lastIndexOf("-"));
            try {
                int tceId = Integer.parseInt(tceIdS);
                LOG.log(Level.FINE, "{0}", tceId);
                tce = new TestCaseExecutionServer(tceId);
            }
            catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "Unable to find TCE: " + tceIdS, nfe);
            }
            try {
                int tcId = Integer.parseInt(item.substring(item
                        .lastIndexOf("-") + 1));
                LOG.log(Level.FINE, "{0}", tcId);
                tcs = new TestCaseServer(tcId);
            }
            catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "Unable to find TCE: " + tceIdS, nfe);
            }
        } else if (key instanceof TestCaseExecution) {
            //It is a TestCaseExecution
            tce = new TestCaseExecutionServer((TestCaseExecution) key);
        } else {
            LOG.log(Level.SEVERE, "Unexpected key: {0}", key);
            tce = null;
        }
        return new TCEExtraction(tce, tcs);
    }

    public static File convertToPDF(File f, String filename)
            throws FileNotFoundException,
            IOException {
        TextToPDF ttp = new TextToPDF();
        File pdf;
        try (PDDocument pdfd = ttp.createPDFFromText(new FileReader(f))) {
            pdf = new File(filename);
            if (pdf.getParentFile() != null) {
                pdf.getParentFile().mkdirs();
            }
            pdfd.save(pdf);
        }
        pdf.deleteOnExit();
        return pdf;
    }

    public static File convertToPDF(String content, String filename)
            throws FileNotFoundException,
            IOException {
        Path path = Files.createTempFile("temp-file", ".txt");
        File file = path.toFile();
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        return convertToPDF(file, filename);
    }

    public static String getMimeType(File f) throws IOException {
        return getMimeType(f.getName());
    }

    public static String getMimeType(String fileName) throws IOException {
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info = util.findMatch(fileName);
        return info.getMimeType();
    }

    public static File createZipFile(List<File> files, String zipName)
            throws FileNotFoundException, IOException {
        if (!zipName.endsWith(".zip")) {
            zipName += ".zip";
        }
        File f = new File(zipName);
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f))) {
            files.forEach(file -> {
                try {
                    ZipEntry ze = new ZipEntry(file.getName());
                    out.putNextEntry(ze);
                    byte[] data = FileUtils.readFileToByteArray(file);
                    out.write(data, 0, data.length);
                    out.closeEntry();
                }
                catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
        }
        return f;
    }
}
