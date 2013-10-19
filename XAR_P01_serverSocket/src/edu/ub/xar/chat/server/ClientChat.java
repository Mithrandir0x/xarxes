package edu.ub.xar.chat.server;

import java.io.*;
import java.net.*;

class ClientChat implements Runnable
{

    private Socket socket = null;
    private ConnectionManager managerChat = null;
    private PrintWriter out;
    private BufferedReader in;
    private int id;
    private boolean online = true;

    public ClientChat(Socket s, ConnectionManager mc, int c)
    {
        socket = s;
        id = c;
        managerChat = mc;
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
            this.send("Servidor connectat. Escriu BYE per sortir");
            while ( online )
            {
                String linia = in.readLine();
                System.out.println("RECVING: [" + linia + "]");
                
                if ( linia == null )
                    throw new Exception("Broken connection peer #" + id);
                
                if ( linia.trim().equals("BYE") )
                {
                    online = false;
                }
                else
                {
                    managerChat.broadcast(id, id + ": " + linia);
                }
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
            System.out.println("SENDING: [" + message + "]");
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