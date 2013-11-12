/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ub.xar.p02.sunspotdhcp;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.service.BootloaderListenerService;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class SunSpotDhcpClient extends MIDlet
{
    
    private final static String SubnetGroup = "127";
    private final static String HostAddress = "10.0." + SubnetGroup + ".1";
    
    private Map<Byte, Spot> connectedSpots;
    
    private final static Pattern ProtocolPattern = Protocol.ProtocolPattern;

    @Override
    protected void startApp() throws MIDletStateChangeException
    {
        new BootloaderListenerService().getInstance().start();
        startSenderThread();
    }
    
    private synchronized void startSenderThread()
    {
        new Thread(){
            
            @Override
            public void run()
            {
            }
            
        }.start();
    }
    
    private void startListenerThread()
    {
        new Thread(){
            
            @Override
            public void run()
            {
                String packetData = null;
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
                        packetData = dg.readUTF();
                        String foreignAddress = dg.getAddress();
                        if ( foreignAddress != null ) // Verify datagram address, there's something odd here
                        {
                            System.out.println("Received: [" + packetData + "] from [" + dg.getAddress() + "]");
                            Matcher matcher = ProtocolPattern.matcher(packetData);
                            if ( matcher.matches() )
                            {
                                // Valid packet format
                                String origin = matcher.group(1);
                                String destination = matcher.group(2);
                                String opcode = matcher.group(3);
                                String data = matcher.group(4);
                                
                                if ( destination.equals(HostAddress) && opcode.equals("000") )
                                {
                                    // New device wanting to enter the net
                                }
                            }
                        }
                        else
                        {
                            System.out.println("Received: [" + packetData + "] from [UNKNOWN DEVICE].");
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
