package net.sourceforge.javydreamercsw.vm.jasperreportviewer;

import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.netbeans.spi.project.LookupProvider;
import org.openide.nodes.Node;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ReportProviderInterface extends LookupProvider {

    /**
     * Get report provided.
     *
     * @return report provided.
     */
    public JasperReport getReport();

    /**
     * Map containing parameters for the report. Optional parameters have
     * default values, others 'null'.
     *
     * @return parameter template
     */
    public Map getParameterTemplate();

    /**
     * Generate a print for rendering.
     *
     * @param parameters Parameters for the report.
     * @exception JRException
     * @return JasperPrint
     */
    public JasperPrint generatePrint(Map parameters) throws JRException;

    /**
     * Determines if the report supports the node type.
     *
     * @param node Node to check against
     * @return true if supported.
     */
    public boolean supportsNode(Node node);

    /**
     * Report name. To be used in menus.
     *
     * @return report name
     */
    public String getReportName();
}
