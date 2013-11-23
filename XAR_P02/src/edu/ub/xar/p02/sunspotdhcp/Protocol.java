package edu.ub.xar.p02.sunspotdhcp;

/**
 * This class defines our DHCP Packet Format Protocol.
 * 
 * The format of our messages should be this one:
 * 
 *      IP_ORIGIN##IP_DESTIONATION##OPCODE##DATA##
 * 
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class Protocol
{
    private final static String delimiter = "##";
    
    public final static String OPCODE_INVITE_OFFER     = "000";
    public final static String OPCODE_INVITE_REQUEST   = "001";
    public final static String OPCODE_INVITE_ACCEPTED  = "002";
    public final static String OPCODE_INVITE_REJECTED  = "003";
    public final static String OPCODE_INVITE_DISCONECT = "004";
    
    /**
     * Given a serialized message, recover a Packet from it.
     * 
     * @param m
     * @return Packet p Object representation of a message
     */
    public static Packet Deserialize(String m)
    {
        Packet p = new Packet();
        
        try
        {
            for ( int i = m.indexOf(delimiter), j = 0, k = 0 ;
                    i != -1;
                    j = i + delimiter.length(), i = m.indexOf(delimiter, i+1), k++ )
            {
                String fragment = m.substring(j, i);
                if ( k == 0 )
                {
                    p.setOrigin(fragment);
                }
                else if ( k == 1 )
                {
                    p.setDestination(fragment);
                }
                else if ( k == 2 )
                {
                    p.setOpcode(fragment);
                }
                else if ( k == 3 )
                {
                    p.setData(fragment);
                }
                else
                {
                    throw new Exception("INCORRECT FORMAT");
                }
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            p = null;
        }
        
        return p;
    }
    
    /**
     * Return a string representation of the packet.
     * 
     * @param p The packet to serialize
     * @return String m Empty string if null packet, or formatted packet.
     */
    public static String Serialize(Packet p)
    {
        String m = "";
        
        if ( p != null )
        {
            m = p.getOrigin() + delimiter + p.getDestination() + delimiter +
                    p.getOpcode() + delimiter + p.getData() + delimiter;
        }
        
        return m;
    }
    
    public static String FormatIp(Integer x, Integer y, Integer z, Integer k)
    {
        String ip = "";
        
        return ip;
    }
    
//    public static void main(String[] args)
//    {
//        String message = "10.0.127.25##10.0.127.1##000##Hello World!!!##";
//        Packet p = Deserialize(message);
//        if ( p != null )
//        {
//            System.out.println(p.getOrigin());
//            System.out.println(p.getDestination());
//            System.out.println(p.getOpcode());
//            System.out.println(p.getData());
//            
//            System.out.println(Serialize(p));
//        }
//        else
//        {
//            System.out.println("Malformed message.");
//        }
//        System.out.println(">> ENDE.");
//    }
    
}
