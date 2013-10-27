package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class NonReturnZeroLevelEncoder extends SignalPlot
{

    @Override
    protected void process(int bits[])
    {
        for ( int i = 0 ; i < bits.length ; i++ )
        {
            int bit = bits[i];
            
            int j = i * SignalPlot.DIVISIONS;
            int k = ( i + 1 ) * SignalPlot.DIVISIONS;
            for ( ; j < k ; j++ )
            {
                if ( bit == 1 )
                    this.y[j] = -1.0;
                else
                    this.y[j] = 0.0;
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "Nonreturn to Zero-Level (NRZ-L)";
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
