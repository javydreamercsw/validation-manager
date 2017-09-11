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
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestCasePK;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.ImageIcon;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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
            StringTokenizer st = new StringTokenizer(item, "-");
            st.nextToken();//Ignore the tce part
            String tceIdS = st.nextToken();
            try {
                int tceId = Integer.parseInt(tceIdS);
                LOG.log(Level.FINE, "{0}", tceId);
                tce = new TestCaseExecutionServer(tceId);
            }
            catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "Unable to find TCE: " + tceIdS, nfe);
            }
            try {
                int tcId = Integer.parseInt(st.nextToken());
                int tcTypeId = Integer.parseInt(st.nextToken());
                LOG.log(Level.FINE, "{0}", tcId);
                tcs = new TestCaseServer(new TestCasePK(tcId, tcTypeId));
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

    /**
     * Builder of unique id's for items.
     *
     * @param item Item to get the id from
     * @param postfix postfix to add to the key
     * @param usePrefix Use the prefix?
     * @return key for the object
     */
    public static Object buildId(Object item, Object postfix, boolean usePrefix) {
        Object pf;
        Object key = null;
        String prefix;
        if (postfix == null) {
            pf = "";
        } else {
            pf = "-" + postfix;
        }
        if (item instanceof TestCase) {
            TestCase tc = (TestCase) item;
            prefix = "tc";
            key = (usePrefix ? prefix + "-" : "") + tc.getTestCasePK().getId()
                    + "-" + tc.getTestCasePK().getTestCaseTypeId()
                    + pf;
        } else if (item instanceof Requirement) {
            Requirement r = (Requirement) item;
            key = r.getUniqueId() + pf;
        } else if (item instanceof ExecutionStep) {
            ExecutionStep es = (ExecutionStep) item;
            prefix = "es";
            key = (usePrefix ? prefix + "-" : "")
                    + es.getExecutionStepPK().getStepId() + "-"
                    + es.getExecutionStepPK().getStepTestCaseId() + "-"
                    + es.getExecutionStepPK().getTestCaseExecutionId() + pf;
        } else if (item instanceof Issue) {
            Issue issue = (Issue) item;
            prefix = "issue";
            key = (usePrefix ? prefix + "-" : "") + issue.getIssuePK().getId() + pf;
        } else if (item instanceof TestCaseExecution) {
            TestCaseExecution tce = (TestCaseExecution) item;
            prefix = "tce";
            key = (usePrefix ? prefix + "-" : "") + tce.getId() + pf;
        }
        return key;
    }

    /**
     * Builder of unique id's for items.
     *
     * @param item Item to get the id from
     * @return key for the object
     */
    public static Object buildId(Object item) {
        return buildId(item, null, true);
    }

    /**
     * Builder of unique id's for items.
     *
     * @param item Item to get the id from
     * @param postfix postfix to add to the key
     * @return key for the object
     */
    public static Object buildId(Object item, Object postfix) {
        return buildId(item, postfix, true);
    }

    public static Double evaluateEquation(FailureModeHasCauseHasRiskCategory fmhchrc) {
        RiskCategory cat = fmhchrc.getRiskCategory();
        if (cat.getCategoryEquation() != null
                && !cat.getCategoryEquation().trim().isEmpty()) {
            //Calculate based on equation
            //Format: {rc-id} where id -s the category id
            //i.e.: {rc-1} * {rc-2}
            Map<Integer, Double> map = new HashMap<>();
            fmhchrc.getFailureModeHasCause()
                    .getFailureModeHasCauseHasRiskCategoryList()
                    .forEach(temp -> {
                        //Put result or null if calculated in map.
                        map.put(temp.getRiskCategory().getId(),
                                temp.getRiskCategory().getCategoryEquation() == null
                                || temp.getRiskCategory()
                                        .getCategoryEquation().trim()
                                        .isEmpty()
                                        ? temp.getCategoryValue() : null);
                    });
            //Make the calculation. All values are in map
            String stringPattern = "rc-[0-9]+";
            String equation = fmhchrc.getRiskCategory()
                    .getCategoryEquation().trim();
            String finalEq = equation;
            Pattern pattern = Pattern.compile(stringPattern);
            Matcher matcher = pattern.matcher(equation);
            Map<String, Double> variables = new HashMap<>();
            int variableCounter = 0;
            LOG.log(Level.FINE, "Calculating equation: {0}", equation);
            finalEq = finalEq.replaceAll("\\{", "").replaceAll("\\}", "");
            while (matcher.find()) {
                //Replace with a variable
                String variable = getStringSequence(variableCounter++)
                        .toLowerCase();
                String value = matcher.group(0);
                finalEq = finalEq.replaceAll(value, variable);
                variables.put(variable,
                        map.get(Integer.parseInt(value
                                .substring(value.indexOf('-') + 1,
                                        value.length()))));
            }
            LOG.log(Level.FINE, "Final equation: {0}\n{1}",
                    new Object[]{finalEq, variables});
            Expression e = new ExpressionBuilder(finalEq)
                    .variables(variables.keySet())
                    .build()
                    .setVariables(variables);
            return e.evaluate();
        }
        return new Double(0);
    }

    /**
     * See: https://stackoverflow.com/a/32532049/198108
     *
     * @param i Integer to convert
     * @return Sequential letters.
     */
    private static String getStringSequence(int i) {
        return i < 0 ? "" : getStringSequence((i / 26) - 1) + (char) (65 + i % 26);
    }
}
