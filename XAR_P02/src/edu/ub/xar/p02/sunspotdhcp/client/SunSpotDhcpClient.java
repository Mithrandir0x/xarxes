package edu.ub.xar.p02.sunspotdhcp.client;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import edu.ub.xar.p02.sunspotdhcp.Packet;
import edu.ub.xar.p02.sunspotdhcp.Protocol;

/**
 * The DHCP client application for SunSpots.
 * 
 * This application listens to every message it finds and tries to
 * find anyone that applies to our DHCP protocol. If it founds one,
 * it will try to contact the sender to try to enter the network.
 * 
 * If everything goes right, and the server accepts this client,
 * it will be displayed in the SunSpots LEDs the number of the
 * IP assigned by the server.
 * 
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class SunSpotDhcpClient extends javax.microedition.midlet.MIDlet
{
    
    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    
    private static final int WAITING_FOR_INVITATION = 0;
    private static final int WAITING_FOR_DHCP_RESPONSE = 1;
    private static final int INVITATION_SUCCESSFUL = 2;
    
    private int state = WAITING_FOR_INVITATION;
    private int lightId;
    
    private String HostMacAddress = null;
    private String HostIpAddress = null;
    private String ipAddress = null;
    
    protected void startApp() throws MIDletStateChangeException
    {
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
                Packet p = null;
                
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
                        String foreignAddress = dg.getAddress();
                        tmp = dg.readUTF();
                        
                        if ( foreignAddress != null )
                        {
                            if ( state == WAITING_FOR_INVITATION )
                            {
                                // Waiting for a valid dhcp broadcast message
                                p = Protocol.Deserialize(tmp);

                                if ( p != null && p.getOpcode().equals(Protocol.OPCODE_INVITE_OFFER) )
                                {
                                    // Responding to valid broadcast message
                                    DatagramConnection dgSendConnection = (DatagramConnection) Connector.open("radiogram://" + dg.getAddress() + ":37");
                                    Datagram dgSend = dgSendConnection.newDatagram(dgSendConnection.getMaximumLength());
                                    
                                    HostIpAddress = p.getOrigin();
                                    HostMacAddress = dg.getAddress();
                                    
                                    Packet r = new Packet();
                                    r.setDestination(p.getOrigin());
                                    r.setOrigin("000.000.000.000");
                                    r.setOpcode(Protocol.OPCODE_INVITE_REQUEST);
                                    r.setData("");
                                    dgSend.writeUTF(Protocol.Serialize(r));

                                    state = WAITING_FOR_DHCP_RESPONSE;
                                }
                            }
                            else if ( state == WAITING_FOR_DHCP_RESPONSE )
                            {
                                // Waiting for dhcp registration response
                                p = Protocol.Deserialize(tmp);

                                if ( p != null )
                                {
                                    // Light on thingies
                                    String opcode = p.getOpcode();
                                    if ( opcode.equals(Protocol.OPCODE_INVITE_ACCEPTED) )
                                    {
                                        lightId = getIp(p.getDestination());
                                        state = 2;
                                    }
                                    else if ( opcode.equals(Protocol.OPCODE_INVITE_REJECTED) )
                                    {
                                        state = WAITING_FOR_INVITATION;
                                    }
                                }
                            }
                            
                            if ( state == INVITATION_SUCCESSFUL )
                            {
                                // 
                                
                                displayNumber(lightId, LEDColor.ORANGE);
                            }
                        }
                        /*else
                        {
                            System.out.println("Received: [" + tmp + "] from [UNKNOWN DEVICE].");
                        }*/
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
     * Display a number (base 2) in LEDs 1-7
     *
     * @param val the number to display
     * @param col the color to display in LEDs
     */
    private void displayNumber(int val, LEDColor col)
    {
        for (int i = 0, mask = 1; i < 7; i++, mask <<= 1)
        {
            leds.getLED(7-i).setColor(col);
            leds.getLED(7-i).setOn((val & mask) != 0);
        }
    }
    
    private int getIp(String ipAddress)
    {
        int ip = 0;
        String ipNumber = "";
        
        for ( int i = ipAddress.length() - 1 ; i > 0 ; i-- )
        {
            char c = ipAddress.charAt(i);
            if ( c != '.' )
            {
                ipNumber = c + ipNumber;
            }
            else
            {
                break;
            }
        }
        
        ip = Integer.parseInt(ipNumber);
        
        return ip;
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