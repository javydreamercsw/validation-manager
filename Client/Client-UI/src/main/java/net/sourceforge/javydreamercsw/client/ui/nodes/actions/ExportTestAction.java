package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.tool.message.MessageHandler;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExportTestAction extends AbstractAction {

    private final RequestProcessor RP
            = new RequestProcessor("Test Exporter", 1, true);
    private RequestProcessor.Task theTask = null;
    private ProgressHandle ph;
    boolean valid = false;
    private static final Logger LOG
            = Logger.getLogger(ExportTestAction.class.getSimpleName());

    public ExportTestAction() {
        super("Export Test",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            Test test = Utilities.actionsGlobalContext().lookup(Test.class);

            @Override
            public void run() {
                ph = ProgressHandleFactory.createHandle("Test Exporter",
                        new Cancellable() {

                            @Override
                            public boolean cancel() {
                                return handleCancel();
                            }
                        });
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Test test = Utilities.actionsGlobalContext().lookup(Test.class);
                        VMEntityManager manager = null;
                        for (VMEntityManager m : Lookup.getDefault().lookupAll(VMEntityManager.class)) {
                            if (m.supportEntity(Requirement.class)) {
                                manager = m;
                                break;
                            }
                        }
                        if (manager != null) {
                            while (!manager.isInitialized()) {
                                try {
                                    //Wait
                                    LOG.fine("Waiting for Requirement Entity Manager.");
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                            LOG.fine("Done waiting for Requirement Entity Manager!");
                            //Pick where to save the file
                            JFileChooser fc = new JFileChooser();
                            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            int returnVal = fc.showOpenDialog(new JFrame());
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                try {
                                    File file = fc.getSelectedFile();
                                    String FS
                                            = System.getProperty("file.separator");
                                    File export = new File(file.getAbsolutePath()
                                            + FS
                                            + "Export.xls");
                                    if (export.exists()) {
                                        export.delete();
                                    }
                                    export.createNewFile();
                                    //Now export
                                    if (test != null) {
                                        HSSFWorkbook workbook
                                                = new HSSFWorkbook();
                                        List<TestCase> testCaseList
                                                = test.getTestCaseList();
                                        //Get progress state
                                        int total = 0;
                                        for (TestCase tc : testCaseList) {
                                            total += tc.getStepList().size();
                                        }
                                        ph.switchToDeterminate(total);
                                        int progress = 0;
                                        for (TestCase tc : testCaseList) {
                                            ph.setDisplayName("Exporting test Case: " + tc.getName());
                                            //Name can't have '/' on name
                                            String name = tc.getName()
                                                    .trim().replaceAll("/", "-");
                                            String mod = "";
                                            int index = 1;
                                            //Make sure to use unique name
                                            while (workbook.getSheet(name + mod) != null) {
                                                mod = "(" + (++index) + ")";
                                            }
                                            HSSFSheet sheet
                                                    = workbook.createSheet(name + mod);

                                            Map<Integer, Object[]> data
                                                    = new TreeMap<>();
                                            int count = 0;
                                            data.put(++count,
                                                    new Object[]{"Step Number",
                                                        "Description",
                                                        "Expected Result",
                                                        "Related Requirements"});
                                            for (Step s : tc.getStepList()) {
                                                StringBuilder requirements
                                                        = new StringBuilder();
                                                if (s.getRequirementList().isEmpty()) {
                                                    requirements.append("N/A");
                                                } else {
                                                    List<String> processed
                                                            = new ArrayList<>();
                                                    for (Requirement r
                                                            : s.getRequirementList()) {
                                                        Requirement latest
                                                                = ((Requirement) manager.getEntity(r.getUniqueId()));
                                                        if (!processed.contains(r.getUniqueId())) {
                                                            processed.add(latest.getUniqueId());
                                                            if (!requirements.toString().isEmpty()) {
                                                                requirements.append(", ");
                                                            }
                                                            requirements.append(latest.getUniqueId());
                                                        }
                                                    }
                                                }
                                                LOG.log(Level.INFO, "{0}->{1}->{2}->{3}",
                                                        new Object[]{s.getStepSequence(),
                                                            new String(s.getText(), "UTF8"),
                                                            new String(s.getExpectedResult(),
                                                                    "UTF8"),
                                                            requirements.toString()});
                                                data.put(++count,
                                                        new Object[]{s.getStepSequence(),
                                                            new String(s.getText(), "UTF8"),
                                                            new String(s.getExpectedResult(), "UTF8"),
                                                            requirements.toString()});
                                                ph.progress(++progress);
                                            }
                                            int rownum = 0;
                                            for (Integer key : data.keySet()) {
                                                Row row = sheet.createRow(rownum++);
                                                Object[] objArr = data.get(key);
                                                int cellnum = 0;
                                                for (Object obj : objArr) {
                                                    Cell cell = row.createCell(cellnum++);
                                                    if (obj instanceof Date) {
                                                        cell.setCellValue((Date) obj);
                                                    } else if (obj instanceof Boolean) {
                                                        cell.setCellValue((Boolean) obj);
                                                    } else if (obj instanceof String) {
                                                        cell.setCellValue((String) obj);
                                                    } else if (obj instanceof Double) {
                                                        cell.setCellValue((Double) obj);
                                                    } else if (obj instanceof Integer) {
                                                        cell.setCellValue((Integer) obj);
                                                    }
                                                }
                                            }
                                        }
                                        setValid(true);
                                        try (FileOutputStream out
                                                = new FileOutputStream(export)) {
                                            workbook.write(out);
                                        }
                                    }
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                    setValid(false);
                                }
                            }
                        }
                    }
                };
                theTask = RP.create(runnable); //the task is not started yet

                theTask.addTaskListener(new TaskListener() {
                    public void taskFinished(RequestProcessor.Task task) {
                        ph.finish();
                    }

                    @Override
                    public void taskFinished(org.openide.util.Task task) {
                        ph.finish();
                        if (isValid()) {
                            //TODO: internationalize
                            Lookup.getDefault().lookup(MessageHandler.class)
                                    .plain("Test "
                                            + (test == null ? "" : test.getName())
                                            + " export completed succesfully!");
                        } else {
                            Lookup.getDefault().lookup(MessageHandler.class)
                                    .error("Errors Test export!");
                        }
                    }
                });
                //start the progresshandle the progress UI will show 500s after
                ph.start();

                //this actually start the task
                theTask.schedule(0);
            }

            private boolean handleCancel() {
                LOG.info("handleCancel");
                if (null == theTask) {
                    return false;
                }
                return theTask.cancel();
            }
        });
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
