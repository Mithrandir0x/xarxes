package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class NonReturnZeroInvertedEncoder extends SignalPlot
{

    @Override
    protected void process(int bits[])
    {
        double signal = -1.0;
        for ( int i = 0 ; i < bits.length ; i++ )
        {
            int bit = bits[i];
            
            if ( bit == 1 )
                signal *= -1;
            
            int j = i * SignalPlot.DIVISIONS;
            int k = ( i + 1 ) * SignalPlot.DIVISIONS;
            for ( ; j < k ; j++ )
            {
                this.y[j] = signal;
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "Nonreturn to Zero Inverted (NRZI)";
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
