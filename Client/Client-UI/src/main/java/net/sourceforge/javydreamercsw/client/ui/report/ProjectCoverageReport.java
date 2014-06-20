package net.sourceforge.javydreamercsw.client.ui.report;

import net.sourceforge.javydreamercsw.client.ui.nodes.ProjectNode;
import net.sourceforge.javydreamercsw.vm.jasperreportviewer.AbstractReportProvider;
import net.sourceforge.javydreamercsw.vm.jasperreportviewer.ReportProviderInterface;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = ReportProviderInterface.class)
public class ProjectCoverageReport extends AbstractReportProvider {

    @Override
    public boolean supportsNode(Node node) {
        return node instanceof ProjectNode;
    }

    @Override
    public String getReportName() {
        return "Requirement Coverage";
    }
}
