package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class ManchesterDifferentialEncoder extends SignalPlot
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
            int m = SignalPlot.DIVISIONS / 2;
            for ( int l = 0 ; j + l < k ; l++ )
            {
                if ( l < m )
                {
                    y[j + l] = 1.0 * signal;
                }
                else
                {
                    y[j + l] = -1.0 * signal;
                }
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "Manchester Differential (BIPHASE)";
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
