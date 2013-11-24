package edu.ub.xar.p02.sunspotdhcp.server;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import edu.ub.xar.p02.sunspotdhcp.Packet;
import edu.ub.xar.p02.sunspotdhcp.Protocol;
import java.io.IOException;
import java.util.HashMap;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

/**
 * The DHCP server application.
 * 
 * WARNING!
 * THIS APPLICATION MUST NOT BE DEPLOYED ON SUNSPOT BASESTATIONS NOR SUNSPOT DEVICES!!
 * BUT SHOULD BE RUN WITH A SUNSPOT BASESTATION
 * 
 * 
 * 
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class SunSpotDhcpServer
{
    
    private final static String SubnetGroup = "5.";
    private final static String Network = "10.0.";
    private final static String HostAddress = Network + SubnetGroup + "1";
    private final static String BroadcastAddress = Network + SubnetGroup + "255";
    
    private final static String WelcomeMessage = "Welcome to home.";
    
    // String == MAC
    private HashMap<String, Spot> connectedSpots;
    
    public SunSpotDhcpServer()
    {
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
        
        connectedSpots = new HashMap<String, Spot>();
                        
        startBroadcastThread();
        startListenerThread();
    }
    
    /**
     * The Invitation Accepter Thread.
     * 
     * This thread listens to broadcast channel 37, and parses every datagram
     * it catches. If a message from a datagram does adhere to the protocol
     * message format, and it's an invitation, it will associate the sender's
     * mac address with a logical IP, and will respond to the sender with the
     * new IP assigned.
     */
    private void startListenerThread()
    {
        new Thread(){
            
            @Override
            public void run()
            {
                String packetData = null;
                RadiogramConnection dgConnection = null;
                DatagramConnection dgSendConnection = null;
                Datagram dg = null;
                Datagram dgSend = null;
                
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
                        String foreignMacAddress = dg.getAddress();
                        if ( foreignMacAddress != null ) // Verify datagram address, there's something odd here
                        {
                            System.out.println("Received: [" + packetData + "] from [" + dg.getAddress() + "]");
                            Packet p = Protocol.Deserialize(packetData);
                            if ( p != null )
                            {
                                // Valid packet format
                                String destination = p.getDestination();
                                String opcode = p.getOpcode(); // Comment if opcode is not valid to use
                                if ( destination.equals(HostAddress) )
                                {
                                    if ( opcode.equals(Protocol.OPCODE_INVITE_REQUEST) )
                                    {
                                        // New device wanting to enter the net
                                        String newIpAddress = generateNewIp(foreignMacAddress);

                                        dgSendConnection = (DatagramConnection) Connector.open("radiogram://" + dg.getAddress() + ":37");
                                        // Then, we ask for a datagram with the maximum size allowed
                                        dgSend = dgSendConnection.newDatagram(dgSendConnection.getMaximumLength());
                                        Packet r = new Packet();

                                        // If the MAC Address is already in our list of connected devices, deny the request
                                        // This can happen if the 
                                        if ( newIpAddress != null )
                                        {
                                            r.setOrigin(destination);
                                            r.setDestination(newIpAddress);
                                            r.setOpcode(Protocol.OPCODE_INVITE_ACCEPTED);
                                            r.setData(WelcomeMessage);
                                        }
                                        else
                                        {
                                            r.setOrigin(destination);
                                            r.setDestination("000.000.000.000");
                                            r.setOpcode(Protocol.OPCODE_INVITE_REJECTED);
                                            r.setData("Possible duplicated MAC Address. Please contact your administrator. You owe him/her a beer.");
                                        }

                                        dgSend.writeUTF(Protocol.Serialize(r));
                                        dgSendConnection.close();
                                    }
                                }
                                else if ( opcode.equals(Protocol.OPCODE_INVITE_DISCONECT) )
                                {
                                    removeDevice(foreignMacAddress);
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
    
    private String generateNewIp(String macAddress)
    {
        String newIpAddress = null;
        
        if ( !connectedSpots.containsKey(macAddress) )
        {
            newIpAddress = Network + SubnetGroup + Integer.toString(connectedSpots.size() + 2);
            Spot spot = new Spot(newIpAddress, macAddress);
            connectedSpots.put(macAddress, spot);
        }
        
        return newIpAddress;
    }
    
    private boolean removeDevice(String macAddress)
    {
        boolean removed = false;
        
        if ( connectedSpots.containsKey(macAddress) )
        {            
            connectedSpots.remove(macAddress);
            removed = true;
        }
        
        return removed;
    }
    
    /**
     * The Broadcast Thread.
     * 
     * This thread is in charge of broadcasting a message through the waves
     * to notify to anyone available its existence. Anyone hearing its invitation
     * should understand its protocol message format for proper communication.
     */
    private void startBroadcastThread()
    {
        new Thread(){
            
            @Override
            public void run()
            {
                // We create a DatagramConnection
                DatagramConnection dgConnection = null;
                Datagram dg = null;
                
                Packet p = new Packet();
                p.setOrigin(HostAddress);
                p.setDestination(BroadcastAddress);
                p.setOpcode(Protocol.OPCODE_INVITE_OFFER);
                p.setData("If anyone can hear me, please respond.");
                String serializedInvitation = Protocol.Serialize(p);
                try
                {
                    dgConnection = (DatagramConnection) Connector.open("radiogram://broadcast:37");
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                }
                catch ( IOException ex )
                {
                    System.out.println("Could not open radiogram broadcast connection");
                    ex.printStackTrace();
                    return;
                }
                
                while ( true )
                {
                    try
                    {
                        dg.reset();
                        dg.writeUTF(serializedInvitation);
                        dgConnection.send(dg);
                    }
                    catch ( Exception ex )
                    {
                        ex.printStackTrace();
                    }
                    Utils.sleep(1000);
                }
            }
            
        }.start();
    }
    
}
