package edu.ub.xar.tasks;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.PlotArea;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import edu.ub.xar.tasks.physicallayer.SignalPlot;
import edu.ub.xar.tasks.physicallayer.encoders.B8zsEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.BipolarAmiEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.ManchesterDifferentialEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.ManchesterEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.NonReturnZeroEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.NonReturnZeroInvertedEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.NonReturnZeroLevelEncoder;
import edu.ub.xar.tasks.physicallayer.encoders.PseudoternaryEncoder;
import edu.ub.xar.tasks.physicallayer.modulators.AmplitudeShiftKeyingModulator;
import edu.ub.xar.tasks.physicallayer.modulators.FrequencyShiftKeyingModulator;
import edu.ub.xar.tasks.physicallayer.modulators.PhaseShiftKeyingModulator;
import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author olopezsa13
 */
public class PhysicalLayer extends JFrame
{
    
    public PhysicalLayer(SignalPlot signalPlot) throws Exception
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        
        DataTable dataTable = signalPlot.getDataTable();
        
        // Create plotter and append it to the content panel
        XYPlot plot = new XYPlot(dataTable);
        getContentPane().add(new InteractivePanel(plot));
        
        // Set plot stuff
        plot.setSetting(XYPlot.TITLE, signalPlot.getTitle());
        plot.setInsets(new Insets2D.Double(20.0, 80.0, 80.0, 20.0));
        plot.getPlotArea().setSetting(PlotArea.BORDER, null);
        
        // Set plotter's axis labels
        AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        axisRendererX.setSetting(AxisRenderer.LABEL, "Time");
        axisRendererX.setSetting(AxisRenderer.SHAPE_COLOR, new Color(0.0f, 0.0f, 0.0f, 0.2f));
        
        AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        axisRendererY.setSetting(AxisRenderer.LABEL, "Voltage");
        
        Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
        axisY.setRange(signalPlot.getAxisYMin(), signalPlot.getAxisYMax());
        
        // Set line renderer stuff
        LineRenderer lines = new DefaultLineRenderer2D();
        plot.setLineRenderer(dataTable, lines);
        Color color = new Color(0, 0, 0, 0);
        Color colorLine = new Color(0.0f, 0.3f, 1.0f, 1.0f);
        plot.getPointRenderer(dataTable).setSetting(PointRenderer.COLOR, color);
        plot.getLineRenderer(dataTable).setSetting(LineRenderer.COLOR, colorLine);
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        PhaseShiftKeyingModulator e = new PhaseShiftKeyingModulator();
        e.setBitString("00110100010");
        e.setBitrate(1);
        
        PhysicalLayer pl = new PhysicalLayer(e);
    }
}
