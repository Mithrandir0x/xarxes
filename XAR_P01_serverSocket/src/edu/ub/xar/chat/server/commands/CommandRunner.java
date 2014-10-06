package edu.ub.xar.chat.server.commands;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class CommandRunner
{
    
    private List<String> args = new ArrayList<String>();
    
    public List<String> getArguments()
    {
        return args;
    }
    
    public abstract void run();
    
}
