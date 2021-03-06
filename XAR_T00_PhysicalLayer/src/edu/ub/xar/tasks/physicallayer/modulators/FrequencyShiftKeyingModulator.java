package edu.ub.xar.tasks.physicallayer.modulators;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class FrequencyShiftKeyingModulator extends SignalPlot
{
    
    private static Double AMPLITUDE = 1.0;
    private static Double F_1 = 3.0;
    private static Double F_0 = 1.0;

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
                    y[j] = AMPLITUDE * Math.cos(2.0 * Math.PI * F_1 * x[j]);
                }
                else
                {
                    y[j] = AMPLITUDE * Math.cos(2.0 * Math.PI * F_0 * x[j]);
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Frequency-shift Keying";
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
