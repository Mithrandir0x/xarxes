package edu.ub.xar.chat.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main
{
    
    private static List<ThreadChat> connections = Collections.synchronizedList(new ArrayList<ThreadChat>());
    private static List<Integer> toBeDeleted = Collections.synchronizedList(new ArrayList<Integer>());
    private static Thread watchdog = new Thread("Charlie"){
        
        @Override
        public void run()
        {
            while ( true )
            {
                for ( Integer i : toBeDeleted )
                {
                    connections.remove(i.intValue());
                }
                toBeDeleted.clear();
                
                try
                {
                    Thread.sleep(2000);
                }
                catch ( InterruptedException ex )
                {
                }
                
                // Thread timeout verification
                for ( ThreadChat th : connections )
                {
                    if ( !th.getClientChat().isOnline() )
                    {
                        try
                        {
                            th.getThread().join(2000);
                        }
                        catch (InterruptedException ex)
                        {
                            ex.printStackTrace();
                        }
                        
                        toBeDeleted.add(connections.indexOf(th));
                    }
                }
            }
        }
        
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int contador = 0;
        try
        {
            ServerSocket server = new ServerSocket(8189);
            System.out.println("Server listening at " + server.getInetAddress() + ":8189");
            
            while ( true )
            {
                Socket socket = server.accept();
                
                System.out.println("Client #" + contador + " connected...");
                ClientChat r = new ClientChat(socket, contador);
                Thread t = new Thread(r);
                t.start();
                connections.add(new ThreadChat(t, r));
                
                contador++;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                watchdog.join();
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    private static class ThreadChat
    {
        
        private Thread thread;
        private ClientChat chat;
        
        public ThreadChat(Thread t, ClientChat cc)
        {
            thread = t;
            chat = cc;
        }
        
        public Thread getThread()
        {
            return thread;
        }
        
        public ClientChat getClientChat()
        {
            return chat;
        }
        
    }

}
