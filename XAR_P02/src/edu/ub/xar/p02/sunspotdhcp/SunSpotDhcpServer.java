package edu.ub.xar.p02.sunspotdhcp;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.service.BootloaderListenerService;
import java.io.IOException;
import java.util.Map;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * 
 * @author: David Mercier <david.mercier@sun.com>
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class SunSpotDhcpServer extends MIDlet
{
    
    private final static String SubnetGroup = "127";
    private final static String HostAddress = "10.0." + SubnetGroup + ".1";
    
    private Map<Byte, Spot> connectedSpots;

    @Override
    protected void startApp() throws MIDletStateChangeException
    {
        new BootloaderListenerService().getInstance().start();
        startListenerThread();
    }
    
    private void startListenerThread()
    {
        new Thread(){
            
            @Override
            public void run()
            {
                String tmp = null;
                RadiogramConnection dgConnection = null;
                Datagram dg = null;
                
                try
                {
                    dgConnection = (RadiogramConnection) Connector.open("radiogram://:37");
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                }
                catch (IOException e)
                {
                    System.out.println("Could not open radiogram receiver connection");
                    e.printStackTrace();
                    return;
                }
                
                while ( true )
                {
                    try
                    {
                        dg.reset();
                        dgConnection.receive(dg);
                        tmp = dg.readUTF();
                        String foreignAddress = dg.getAddress();
                        if ( foreignAddress != null && foreignAddress.equals("") ) // Set SunSPOT sender MAC address to filter filthy ones
                        {
                            System.out.println("Received: [" + tmp + "] from [" + dg.getAddress() + "]");
                            dg.writeUTF("Heyoo! Do you want some spam? Yeah I know you want spam, you'll love to have so much spam. Spam spam spam, lovely spaaam lovely spaaaaam!!");
                        }
                        else
                        {
                            System.out.println("Received: [" + tmp + "] from [UNKNOWN DEVICE].");
                        }
                    }
                    catch ( IOException e )
                    {
                        System.out.println("Nothing received");
                    }
                }
            }
            
        }.start();
    }

    @Override
    protected void pauseApp()
    {
    }

    @Override
    protected void destroyApp(boolean bln) throws MIDletStateChangeException
    {
    }
    
}
