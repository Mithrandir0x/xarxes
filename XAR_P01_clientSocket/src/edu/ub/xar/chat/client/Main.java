package edu.ub.xar.chat.client;

import java.io.*;
import java.net.InetSocketAddress;
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
            String IP_Address = args[0];
            
            int port = Integer.parseInt(args[1]);
            
            socket = new Socket();
            socket.connect(new InetSocketAddress(IP_Address, port), 1000);
            //socket.setSoTimeout(1000);
            
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
                            //System.out.println("RECVING: [" + linia + "]");
                            System.out.println(linia);
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
                //System.out.println("SENDING: [" + message + "]");
                out.println(message);
                
                if ( message.equals("/BYE") )
                {
                    Thread.sleep(1000);
                    break;
                }
            }
            
            writer.join();
            System.out.println("Disconnected.");
        }
        catch ( Exception ioe )
        {
            ioe.printStackTrace();
        }
    }

}
