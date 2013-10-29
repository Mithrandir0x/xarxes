package edu.ub.xar.tasks.physicallayer;

import de.erichseifert.gral.data.DataTable;
import java.util.Arrays;

/**
 * Base class for any signal plotter.
 * 
 * This class holds basic functionality to transform and obtain the encoded
 * or modulated signal from a string of bits.
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
    
    /**
     * Create the signal by implementing this method.
     * 
     * Given an array of integer values containing each bit of the stream,
     * it is possible to modify y-axis array values when this method is
     * executed.
     * 
     * @param bits 
     */
    protected abstract void process(int bits[]);
    
    /**
     * Return the title of the signal to be displayed.
     * 
     * @return the title
     */
    public abstract String getTitle();
    
    /**
     * Return the maximum possible value that can be y-axis values.
     * 
     * @return the max y-axis value
     */
    public abstract Double getAxisYMax();
    
    /**
     * Return the minimum possible value that can be y-axis values.
     * 
     * @return the min y-axis value
     */
    public abstract Double getAxisYMin();
    
    /**
     * Return a DataTable data structure to be used by a GRAL Plot object.
     * 
     * @return the datatable
     * @throws Exception 
     */
    public DataTable getDataTable() throws Exception
    {
        int l = bitString.length();
        int bits[] = new int[l];
        
        // Initialize both arrays that will contain
        // the voltage and the time axis.
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
