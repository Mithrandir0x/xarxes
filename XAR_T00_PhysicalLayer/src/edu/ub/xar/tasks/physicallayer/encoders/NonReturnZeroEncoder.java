package edu.ub.xar.tasks.physicallayer.encoders;

import edu.ub.xar.tasks.physicallayer.SignalPlot;

/**
 * Unipolar Non-Return to Zero Code.
 *
 * @author olopezsa13
 */
public class NonReturnZeroEncoder extends SignalPlot
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
                this.y[j] = Double.valueOf(bit);
            }
        }
    }

    @Override
    public String getTitle()
    {
        return "Unipolar Nonreturn to Zero (NRZ)";
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
