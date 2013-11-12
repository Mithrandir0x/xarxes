package edu.ub.xar.p02.sunspotdhcp;

import java.util.regex.Pattern;

/**
 * 
 * @author: Fernando Mateus
 * @author: Oriol Lopez
 */
public class Protocol
{
    
    private final static String ipPatternString = "([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})";
    private final static String opcodePatternString = "([0-9]{3})";
    private final static String dataPatternString = "(.*)";
    private final static String delimiter = "##";
    public final static Pattern ProtocolPattern = Pattern.compile("^" + ipPatternString + delimiter + ipPatternString + delimiter + opcodePatternString + delimiter + dataPatternString);
    // Protocol sans opcode
    //private final static Pattern ProtocolPattern = Pattern.compile("^([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})##([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})##(.*)");
    
}
