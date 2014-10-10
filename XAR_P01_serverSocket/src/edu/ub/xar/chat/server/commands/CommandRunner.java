package edu.ub.xar.chat.server.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Aquesta clase serverix per abstreure la lògica de negoci d'execució
 * d'una comanda.
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
