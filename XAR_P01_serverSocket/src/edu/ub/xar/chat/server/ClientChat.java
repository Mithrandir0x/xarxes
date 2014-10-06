package edu.ub.xar.chat.server;

import edu.ub.xar.chat.server.commands.CommandParser;
import edu.ub.xar.chat.server.commands.CommandRunner;
import java.io.*;
import java.net.*;
import java.util.List;

class ClientChat implements Runnable
{

    private CommandParser parser = null;
    private Socket socket = null;
    private ConnectionManager managerChat = null;
    private PrintWriter out;
    private BufferedReader in;
    private int id;
    private String username = null;
    private boolean online = true;

    public ClientChat(Socket s, ConnectionManager mc, int c)
    {
        socket = s;
        id = c;
        managerChat = mc;
        parser = new CommandParser();
        
        registerCommands();
    }
    
    private void registerCommands()
    {
        parser.registerCommand(CommandParser.BYE, new CommandRunner(){
            @Override
            public void run() {
                online = false;
            }
        });
        
        parser.registerCommand(CommandParser.SET_USERNAME, new CommandRunner(){
            @Override
            public void run() {
                List<String> args = getArguments();
                username = args.get(1);
                
                send(String.format("S'ha canviat nom d'usuari [%2d] -> [%s]", id, username));
            }
        });
        
        parser.registerCommand(CommandParser.TEXT, new CommandRunner(){
            @Override
            public void run() {
                List<String> args = getArguments();
                String linia = args.get(0);
                
                // More complex writing to support usernames
                if ( username == null )
                {
                    managerChat.broadcast(id, String.format("[anonymous:%2d]: %s", id, linia));
                }
                else
                {
                    managerChat.broadcast(id, String.format("[%s:%2d]: %s", username, id, linia));
                }
            }
        });
    }

    @Override
    public void run()
    {
        try
        {
            InputStream entrada = socket.getInputStream();
            OutputStream salida = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(entrada));
            out = new PrintWriter(new OutputStreamWriter(salida), true);
            send("Servidor connectat. Escriu BYE per sortir");
            while ( online )
            {
                String linia = in.readLine();
                System.out.println("RECVING: [" + linia + "]");
                
                if ( linia == null )
                    throw new Exception("Broken connection peer #" + id);
                
                parser.parse(linia);
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            online = false;
            managerChat.broadcast(id, "Client #" + id + " has disconnected.");
            try
            {
                if (socket != null)
                {
                    socket.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    public void send(String message)
    {
        if ( online )
        {
            System.out.println(String.format("SENDING to [%2d]: [%s]", id, message));
            out.println(message);
            out.flush();
        }
    }
    
    public int getId()
    {
        return id;
    }
    
    public boolean isOnline()
    {
        return online;
    }
    
    public void close()
    {
        online = false;
    }

}