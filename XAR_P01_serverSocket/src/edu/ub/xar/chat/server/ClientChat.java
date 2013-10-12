package edu.ub.xar.chat.server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class ClientChat implements Runnable
{

    private Socket socket = null;
    private int contador;
    private boolean online = true;

    public ClientChat(Socket s, int c)
    {
        socket = s;
        contador = c;
    }

    @Override
    public void run()
    {
        try
        {
            InputStream entrada = socket.getInputStream();
            OutputStream salida = socket.getOutputStream();
            Scanner in = new Scanner(entrada);
            PrintWriter out = new PrintWriter(salida, true);
            out.println("Servidor connectat. Escriu BYE per sortir");
            while ( online && in.hasNextLine() )
            {
                String linia = in.nextLine();
                out.println(contador + ": " + linia);
                if ( linia.trim().equals("BYE") )
                {
                    online = false;
                }
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
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
    
    public boolean isOnline()
    {
        return online;
    }

}