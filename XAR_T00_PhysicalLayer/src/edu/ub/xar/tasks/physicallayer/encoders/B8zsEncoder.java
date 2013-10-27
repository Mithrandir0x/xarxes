package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 *
 * @author olopezsa13
 */
public class B8zsEncoder extends SignalPlot
{

    @Override
    protected void process(int bits[])
    {
        double signal = -1.0;
        int zeroes = 0;
        for ( int i = 0 ; i < bits.length ; i++ )
        {
            int bit = bits[i];
            
            if ( bit == 1 )
            {
                signal *= -1;
                
                if ( zeroes < 8 )
                {
                    // Here, we assumed that we where parsing an octet, and until now
                    // we have coded those zeroes as B8ZS indicates. But, we've found
                    // a 1 bit, and we should correct any signal value of the zeros.
                    for ( int n = i - zeroes + 3 ; n < i ; n++ )
                    {
                        int o = n * SignalPlot.DIVISIONS;
                        int p = ( n + 1 ) * SignalPlot.DIVISIONS;
                        for ( ; o < p ; o++ )
                        {
                            y[o] = 0.0;
                        }
                    }
                }
                
                zeroes = 0;
            }
            else
            {
                zeroes++;
            }
            
            int j = i * SignalPlot.DIVISIONS;
            int k = ( i + 1 ) * SignalPlot.DIVISIONS;
            for ( ; j < k ; j++ )
            {
                if ( bit == 1 )
                {
                    y[j] = signal;
                }
                else
                {
                    if ( zeroes == 4 || zeroes == 8 )
                    {
                        y[j] = signal;
                    }
                    else if ( zeroes == 5 || zeroes == 7 )
                    {
                        y[j] = -signal;
                    }
                    else
                    {
                        y[j] = 0.0;
                    }
                }
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "B8ZS";
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
