package edu.ub.xar.tasks.physicallayer;

import de.erichseifert.gral.data.DataTable;
import java.util.Arrays;

/**
 *
 * @author olopezsa13
 */
public abstract class SignalPlot
{
    
    public static int DIVISIONS = 300;
    
    private int bitrate = 0;
    protected String bitString = null;
    
    protected Double[] x;
    protected Double[] y;
    
    public void setBitString(String bits)
    {
        bitString = bits;
    }
    
    public void setBitrate(int bitrate)
    {
        this.bitrate = bitrate;
    }
    
    protected abstract void process(int bits[]);
    
    public abstract String getTitle();
    public abstract Double getAxisYMax();
    public abstract Double getAxisYMin();
    
    public DataTable getDataTable() throws Exception
    {
        int l = bitString.length();
        int bits[] = new int[l];
        
        int ticks = DIVISIONS * l;
        x = new Double[ticks];
        y = new Double[ticks];
        
        double totalTime = l / bitrate;
        double tick = totalTime / ( DIVISIONS * l );
        
        for ( int i = 0 ; i < l ; i++ )
        {
            if ( bitString.charAt(i) == '0' )
                bits[i] = 0;
            else
                bits[i] = 1;
        }
        
        double t = 0;
        for ( int i = 0 ; i < x.length ; i++ )
        {
            x[i] = t;
            t += tick;
        }
        
        process(bits);
        
        DataTable dataTable = new DataTable(Double.class, Double.class);
        for ( int i = 0 ; i < ticks ; i++ )
        {
            dataTable.add(x[i], y[i]);
        }
        
        return dataTable;
    }
    
}
