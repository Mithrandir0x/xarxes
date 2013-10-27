package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class PseudoternaryEncoder extends SignalPlot
{

    @Override
    protected void process(int bits[])
    {
        double signal = -1.0;
        for ( int i = 0 ; i < bits.length ; i++ )
        {
            int bit = bits[i];
            
            if ( bit == 0 )
                signal *= -1;
            
            int j = i * SignalPlot.DIVISIONS;
            int k = ( i + 1 ) * SignalPlot.DIVISIONS;
            for ( ; j < k ; j++ )
            {
                if ( bit == 0 )
                    y[j] = signal;
                else
                    y[j] = 0.0;
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "Pseudoternary (MULTILEVEL)";
    }

    @Override
    public Double getAxisYMax()
    {
        return 1.0;
    }

    @Override
    public Double getAxisYMin()
    {
        return -1.0;
    }
    
}
