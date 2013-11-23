package edu.ub.xar.p02.tinyspammer;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import javax.microedition.io.DatagramConnection;

/**
 * 
 * @author: David Mercier <david.mercier@sun.com>
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class TinySpammer extends javax.microedition.midlet.MIDlet
{
    
    protected void startApp() throws MIDletStateChangeException
    {
        System.out.println("I'm about to spam that SPOT! And that, and also that, all your base are belong to spam!");
        // Listen for downloads/commands over USB connection
        new com.sun.spot.service.BootloaderListenerService().getInstance().start();
        
        startReceiverThread();
    }
    
    /**
     * The receiver thread blocks on the receive function
     * so you don't have to sleep between each receive.
     */
    public void startReceiverThread()
    {
        new Thread()
        {
            public void run()
            {
                String tmp = null;
                RadiogramConnection dgConnection = null;
                DatagramConnection dgSpamConnection = null;
                Datagram dgSpam = null;
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
                        if ( foreignAddress != null ) // Set SunSPOT sender MAC address to filter filthy ones
                        {
                            System.out.println("Received: [" + tmp + "] from [" + dg.getAddress() + "]");
                            
                            dgSpamConnection = (DatagramConnection) Connector.open("radiogram://" + dg.getAddress() + ":37");
                            // Then, we ask for a datagram with the maximum size allowed
                            dgSpam = dgSpamConnection.newDatagram(dgSpamConnection.getMaximumLength());
                            dgSpam.writeUTF("Heyoo! Do you want some spam? Yeah I know you want spam," +
                                    "you'll love to have so much spam. Spam spam spam, lovely" +
                                    "spaaam lovely spaaaaam!!");
                            dgSpamConnection.send(dgSpam);
                            dgSpamConnection.close();
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
    
    protected void pauseApp()
    {
    }
    
    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     * 
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true when this method is called, the MIDlet must
     *    cleanup and release all resources. If false the MIDlet may throw
     *    MIDletStateChangeException  to indicate it does not want to be destroyed
     *    at this time.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException
    {
    }
    
}