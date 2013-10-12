package edu.ub.xar.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Socket socket = null;
        InputStream entrada;
        OutputStream salida;
        
        try
        {
            Scanner entradaDades = new Scanner(System.in);
            
            System.out.println("Introdueix la IP del host");
            String IP_Address = entradaDades.next();
            
            System.out.println("introdueix el port");
            int port = entradaDades.nextInt();
            
            socket = new Socket(IP_Address, port);
            entrada = socket.getInputStream();
            salida = socket.getOutputStream();
            
            Scanner in = new Scanner(entrada);
            while ( in.hasNextLine() )
            {
                String linia = in.nextLine();
                salida.write(linia.getBytes());
                System.out.println(linia);
            }
        }
        catch ( Exception ioe )
        {
            ioe.printStackTrace();
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

}
