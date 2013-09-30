package net.sourceforge.javydreamercsw.vm.jasperreportviewer;

import java.sql.Connection;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.openide.util.Lookup;

public abstract class AbstractReportProvider implements ReportProviderInterface {

    protected JasperReport report;
    protected Map parameters;
    protected Connection connection;

    @Override
    public JasperReport getReport() {
        return report;
    }

    @Override
    public Map getParameterTemplate() {
        return parameters;
    }

    @Override
    public Lookup createAdditionalLookup(Lookup lkp) {
        return Lookup.EMPTY;
    }

    /**
     * @param report the report to set
     */
    public void setReport(JasperReport report) {
        this.report = report;
    }

    @Override
    public JasperPrint generatePrint(Map parameters)
            throws JRException {
        return JasperFillManager.fillReport(getReport(), parameters, connection);
    }
}
