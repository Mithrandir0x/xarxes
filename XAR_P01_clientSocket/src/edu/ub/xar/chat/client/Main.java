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
        final Socket socket;
        final InputStream entrada;
        final OutputStream salida;
        
        try
        {
            Scanner entradaDades = new Scanner(System.in);
            
            //System.out.println("Introdueix la IP del host");
            //String IP_Address = entradaDades.next();
            String IP_Address = "127.0.0.1";
            
            //System.out.println("introdueix el port");
            //int port = entradaDades.nextInt();
            int port = 8189;
            
            socket = new Socket(IP_Address, port);
            entrada = socket.getInputStream();
            salida = socket.getOutputStream();
            
            Thread writer = new Thread("writer"){
                
                @Override
                public void run()
                {
                    System.out.println("Listening messages...");
                    try
                    {
                        Scanner in = new Scanner(entrada);
                        while ( in.hasNextLine() )
                        {
                            String linia = in.nextLine();
                            salida.write(linia.getBytes());
                            System.out.println("RECVING: [" + linia + "]");
                        }
                    }
                    catch ( Exception ex )
                    {
                        ex.printStackTrace();
                    }
                }
                
            };
            writer.start();
            
            System.out.println("Ready to write messages...");

            String message;
            PrintWriter out = new PrintWriter(salida, true);
            Scanner input = new Scanner(System.in);
            while ( input.hasNextLine() )
            {
                message = input.nextLine();
                System.out.println("SENDING: [" + message + "]");
                out.println(message);
            }
        }
        catch ( Exception ioe )
        {
            ioe.printStackTrace();
        }
    }

}
