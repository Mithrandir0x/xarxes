package edu.ub.xar.chat.server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class ClientChat implements Runnable
{

    private Socket socket = null;
    private ConnectionManager managerChat = null;
    private PrintWriter out;
    private BufferedReader in;
    private int contador;
    private boolean online = true;

    public ClientChat(Socket s, ConnectionManager mc, int c)
    {
        socket = s;
        contador = c;
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
                    throw new Exception("Broken connection peer #" + contador);
                
                if ( linia.trim().equals("BYE") )
                {
                    online = false;
                }
                else
                {
                    managerChat.broadcast(contador + ": " + linia);
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
        System.out.println("SENDING: [" + message + "]");
        out.println(message);
        out.flush();
    }
    
    public int getContador()
    {
        return contador;
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