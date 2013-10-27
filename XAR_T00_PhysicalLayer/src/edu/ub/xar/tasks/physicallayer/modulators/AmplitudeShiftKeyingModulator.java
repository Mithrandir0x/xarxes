package edu.ub.xar.tasks.physicallayer.modulators;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class AmplitudeShiftKeyingModulator extends SignalPlot
{
    
    private static Double AMPLITUDE = 1.0;
    private static Double F_C = 1.0;

    @Override
    protected void process(int[] bits)
    {
        for ( int i = 0 ; i < bits.length ; i++ )
        {
            int bit = bits[i];
            
            int j = i * SignalPlot.DIVISIONS;
            int k = ( i + 1 ) * SignalPlot.DIVISIONS;
            for ( ; j < k ; j++ )
            {
                if ( bit == 1 )
                {
                    y[j] = AMPLITUDE * Math.cos(2.0 * Math.PI * F_C * x[j]);
                }
                else
                {
                    y[j] = 0.0;
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Amplitude-shift Keying";
    }

    @Override
    public Double getAxisYMax()
    {
        return AMPLITUDE;
    }

    @Override
    public Double getAxisYMin() {
        return -AMPLITUDE;
    }
    
}
