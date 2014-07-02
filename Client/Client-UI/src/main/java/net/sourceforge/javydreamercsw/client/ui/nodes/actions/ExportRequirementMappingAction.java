package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.api.entity.manager.VMEntityManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
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

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExportRequirementMappingAction extends AbstractAction {

    private static final Logger LOG
            = Logger.getLogger(ExportRequirementMappingAction.class.getSimpleName());
    private final RequestProcessor RP
            = new RequestProcessor("Requirement Mapping Exporter", 1, true);
    private RequestProcessor.Task theTask = null;
    private ProgressHandle ph;
    private boolean valid = false;

    public ExportRequirementMappingAction() {
        super("Export Requirement Mapping",
                new ImageIcon("com/validation/manager/resources/icons/Electronics/USB.png"));
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

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ph = ProgressHandleFactory.createHandle("Requirement Mapping Exporter",
                        new Cancellable() {

                            @Override
                            public boolean cancel() {
                                return handleCancel();
                            }
                        });
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        List<Requirement> mapping = new ArrayList<>();
                        VMEntityManager manager = null;
                        for (VMEntityManager m : Lookup.getDefault().lookupAll(VMEntityManager.class)) {
                            if (m.supportEntity(Requirement.class)) {
                                manager = m;
                                break;
                            }
                        }
                        if (manager != null) {
                            for (Object o : manager.getEntities()) {
                                Requirement r = (Requirement) o;
                                //Only export approved requirements.
                                if (r.getRequirementStatusId().getStatus().equals("general.approved")) {
                                    mapping.add(r);
                                }
                            }
                        }
                        //Pick where to save the file
                        JFileChooser fc = new JFileChooser();
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnVal = fc.showOpenDialog(new JFrame());
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            try {
                                File file = fc.getSelectedFile();
                                String FS = System.getProperty("file.separator");
                                File export = new File(file.getAbsolutePath() + FS
                                        + "Export.xls");
                                if (export.exists()) {
                                    export.delete();
                                }
                                export.createNewFile();
                                //Now export
                                HSSFWorkbook workbook = new HSSFWorkbook();
                                HSSFSheet sheet = workbook.createSheet("Report Sheet");

                                Map<Integer, Object[]> data = new TreeMap<>();
                                int count = 0;
                                data.put(++count,
                                        new Object[]{"Requirement", "Children", "Test"});
                                for (Requirement r : mapping) {
                                    StringBuilder childSb = new StringBuilder();
                                    StringBuilder testSb = new StringBuilder();
                                    //Add test directly testing the requirement itself.
                                    for (Step step : r.getStepList()) {
                                        if (!testSb.toString().trim().isEmpty()) {
                                            testSb.append(";");
                                        }
                                        testSb.append("Test Case: ")
                                                .append(step.getTestCase().getName())
                                                .append(", step ")
                                                .append(step.getStepSequence());
                                    }
                                    //Now process the children
                                    for (Requirement child : r.getRequirementList1()) {
                                        if (!childSb.toString().trim().isEmpty()) {
                                            childSb.append(",");
                                        }
                                        childSb.append(child.getUniqueId().trim());
                                        for (Step step : child.getStepList()) {
                                            if (!testSb.toString().trim().isEmpty()) {
                                                testSb.append(";");
                                            }
                                            testSb.append("Test Case: ")
                                                    .append(step.getTestCase().getName())
                                                    .append(", step ")
                                                    .append(step.getStepSequence());
                                        }
                                    }
                                    LOG.log(Level.FINE, "{0}->{1}->{2}",
                                            new Object[]{r.getUniqueId().trim(),
                                                childSb.toString(),
                                                testSb.toString()});
                                    data.put(++count,
                                            new Object[]{r.getUniqueId().trim(),
                                                childSb.toString(),
                                                testSb.toString()});
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
                                        }
                                    }
                                }
                                try (FileOutputStream out
                                        = new FileOutputStream(export)) {
                                    workbook.write(out);
                                }
                                Lookup.getDefault().lookup(MessageHandler.class)
                                        .info("Mapping exported successfully!");
                                setValid(true);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
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
                                    .plain("Requirement Mapping export completed succesfully!");
                        } else {
                            Lookup.getDefault().lookup(MessageHandler.class)
                                    .error("Errors Requirement Mapping export!");
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
}
