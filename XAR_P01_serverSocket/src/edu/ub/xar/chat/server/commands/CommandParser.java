package edu.ub.xar.chat.server.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class CommandParser 
{
    
    public static String TEXT         = "(.*)";
    public static String BYE          = "^/BYE";
    public static String SET_USERNAME = "^/USER\\s(.*)";
    
    private Map<String, Command> commands = new HashMap<String, Command>();
    
    private String[] commandList = {
        BYE,
        SET_USERNAME,
        TEXT
    };
    
    public CommandParser()
    {
        commands.put(SET_USERNAME, new Command(null, new CommandArgumentExtractor(SET_USERNAME){
            @Override
            public boolean extract(String text, CommandRunner runner) {
                Matcher m = pattern.matcher(text);
                
                if ( m.find() )
                {
                    List<String> args = runner.getArguments();
                    args.clear();
                    args.add(text);
                    args.add(m.group(1));
                    
                    return true;
                }
                
                return false;
            }
        }));
        
        commands.put(BYE, new Command(null, new DefaultCommandArgumentExtractor(BYE)));
        
        commands.put(TEXT, new Command(null, new DefaultCommandArgumentExtractor(TEXT)));
    }
    
    public void registerCommand(String regexp, CommandRunner runner)
    {
        //runners.put(regexp, runner);
        
        Command command = commands.get(regexp);
        
        if ( command != null ) {
            command.runner = runner;
        }
    }
    
    public void parse(String text)
    {
        for ( String c : commandList ) {
            Command command = commands.get(c);
            CommandRunner runner = command.runner;
            CommandArgumentExtractor extractor = command.extractor;
            
            if ( extractor.extract(text, runner) )
            {
                System.out.println(extractor.regex);
                runner.run();
                break;
            }
        }
    }
    
    private class Command
    {
        
        CommandRunner runner;
        CommandArgumentExtractor extractor;
        
        public Command(CommandRunner cr, CommandArgumentExtractor cae)
        {
            runner = cr;
            extractor = cae;
        }
        
    }
    
    private abstract class CommandArgumentExtractor
    {
        
        public Pattern pattern;
        public String regex;
        
        public CommandArgumentExtractor(String regexp) {
            pattern = Pattern.compile(regexp);
            regex = regexp;
        }
        
        public abstract boolean extract(String text, CommandRunner runner);
        
    }
    
    private class DefaultCommandArgumentExtractor extends CommandArgumentExtractor
    {
        
        public DefaultCommandArgumentExtractor(String regexp)
        {
            super(regexp);
        }

        @Override
        public boolean extract(String text, CommandRunner runner) {
                Matcher m = pattern.matcher(text);
                
                if ( m.find() )
                {
                    List<String> args = runner.getArguments();
                    args.clear();
                    args.add(text);
                    
                    return true;
                }
                
                return false;
        }
        
    }
    
}
