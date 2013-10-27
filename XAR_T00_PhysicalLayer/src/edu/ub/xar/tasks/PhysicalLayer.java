package edu.ub.xar.tasks;

import de.erichseifert.gral.data.DataTable;
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
import java.util.HashMap;
import java.util.Map;
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
        if ( args.length < 2 )
        {
            printReadme();
            return;
        }
        
        Map<String, SignalPlot> plotters = new HashMap<String, SignalPlot>();
        
        plotters.put("nrz", new NonReturnZeroEncoder());
        plotters.put("nrzl", new NonReturnZeroLevelEncoder());
        plotters.put("nrzi", new NonReturnZeroInvertedEncoder());
        plotters.put("manchester", new ManchesterEncoder());
        plotters.put("manchester_diferential", new ManchesterDifferentialEncoder());
        plotters.put("pseudoternary", new PseudoternaryEncoder());
        plotters.put("bipolarami", new BipolarAmiEncoder());
        plotters.put("b8zs", new B8zsEncoder());
        plotters.put("ask", new AmplitudeShiftKeyingModulator());
        plotters.put("fsk", new FrequencyShiftKeyingModulator());
        plotters.put("psk", new PhaseShiftKeyingModulator());
        
        SignalPlot e = plotters.get(args[1]);
        if ( e != null )
        {
            e.setBitString(args[0]);
            e.setBitrate(1);

            PhysicalLayer pl = new PhysicalLayer(e);
        }
        else
        {
            printReadme();
        }
    }
    
    private static void printReadme()
    {
        System.out.println("Usage:");
        System.out.println("   java -jar XAR_T00_PhysicalLayer.jar <Bit String> <Encoder or Modulator>");
        System.out.println("");
        System.out.println("List of encoders:");
        System.out.println("   nrz                        Nonreturn to Zero");
        System.out.println("   nrzl                       Nonreturn to Zero Level");
        System.out.println("   nrzi                       Nonreturn to Zero Inverted");
        System.out.println("   manchester                 Manchester");
        System.out.println("   manchester_diferential     Manchester Diferential, or Biphase Mark Code");
        System.out.println("   pseudoternary              Pseudoternary");
        System.out.println("   bipolarami                 Bipolar-AMI");
        System.out.println("   b8zs                       Bipolar with 8 zeroes Substitution");
        System.out.println("");
        System.out.println("List of modulators:");
        System.out.println("   ask                        Amplitude-Shift Keying");
        System.out.println("   fsk                        Frequency-Shift Keying");
        System.out.println("   psk                        Phase-Shift Keying");
    }

}
