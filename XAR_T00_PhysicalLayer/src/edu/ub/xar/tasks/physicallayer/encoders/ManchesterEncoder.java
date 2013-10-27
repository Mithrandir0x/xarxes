package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class ManchesterEncoder extends SignalPlot
{

    @Override
    protected void process(int bits[])
    {
        for ( int i = 0 ; i < bits.length ; i++ )
        {
            int bit = bits[i];
            
            int j = i * SignalPlot.DIVISIONS;
            int k = ( i + 1 ) * SignalPlot.DIVISIONS;
            int m = SignalPlot.DIVISIONS / 2;
            for ( int l = 0 ; j + l < k ; l++ )
            {
                if ( l < m )
                {
                    if ( bit == 1 )
                        y[j + l] = 1.0;
                    else
                        y[j + l] = -1.0;
                }
                else
                {
                    if ( bit == 1 )
                        y[j + l] = -1.0;
                    else
                        y[j + l] = 1.0;
                }
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "Manchester (BIPHASE)";
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
