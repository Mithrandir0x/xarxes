package edu.ub.xar.p02.filteredradiobroadcast;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.IProprietaryRadio;
import com.sun.spot.peripheral.radio.RadioFactory;

import com.sun.spot.util.Utils;

/**
 * 
 * @author: David Mercier <david.mercier@sun.com>
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class FilteredRadioBroadcast extends javax.microedition.midlet.MIDlet
{
    
    private IProprietaryRadio propietaryRadio;
    
    protected void startApp() throws MIDletStateChangeException
    {
        System.out.println("I'm about to filter those filthy SPOTS!");
        propietaryRadio = RadioFactory.getIProprietaryRadio();
        // Listen for downloads/commands over USB connection
        new com.sun.spot.service.BootloaderListenerService().getInstance().start();
        
        startSenderThread();
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
                        }
                        else
                        {
                            System.out.println("Received: [" + tmp + "] from [UNKNOWN DEVICE].");
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("Nothing received");
                    }
                }
            }
        }.start();
    }
    
    /**
     * The sender thread sends a string each second
     */
    synchronized public void startSenderThread()
    {
        new Thread()
        {
            public void run()
            {
                // We create a DatagramConnection
                DatagramConnection dgConnection = null;
                Datagram dg = null;
                try
                {
                    // The Connection is a broadcast so we specify it in the creation string
                    // Here we're sending datagrams to everyone, spamming them. Spaaaam lovely spaaaam, spaaam lovely spaaaaam
                    dgConnection = (DatagramConnection) Connector.open("radiogram://broadcast:37");
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                }
                catch (IOException ex)
                {
                    System.out.println("Could not open radiogram broadcast connection");
                    ex.printStackTrace();
                    return;
                }
                
                while ( true )
                {
                    try
                    {
                        // We send the message (UTF encoded)
                        dg.reset();
                        dg.setAddress(""); // Set SunSPOT sender MAC 
                        dg.writeUTF("This is commander Sheppard, and this is my favorite sunspot of the Citadel.");
                        dgConnection.send(dg);
                        System.out.println("Broadcast is going through");
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                    Utils.sleep(1000); // Why no thread.sleep??
                    
                    // Obtain some transmission statistics
                    {
                        propietaryRadio.resetErrorCounters();
                        System.out.println("           RX overflows: [" + propietaryRadio.getRxOverflow() + "]");
                        System.out.println("             CRC errors: [" + propietaryRadio.getCrcError() + "]");
                        System.out.println("Channel busy stopped TX: [" + propietaryRadio.getTxMissed() + "]");
                        System.out.println(" Short packets received: [" + propietaryRadio.getShortPacket() + "]");
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