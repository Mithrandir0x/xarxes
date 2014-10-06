package edu.ub.xar.chat.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * 
 * @author olopezsa13
 */
public class ConnectionManager
{
    
    private static Map<Integer, ThreadChat> connections = Collections.synchronizedMap(new HashMap<Integer, ThreadChat>());
    
    private static final Thread watchdog = new Thread("Charlie"){
        
        @Override
        public void run()
        {
            System.out.println("Woof woof!");
            
            while ( true )
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch ( Exception ex )
                {
                }
                
                // Thread timeout verification
                for ( Entry<Integer, ThreadChat> entrySet : connections.entrySet() )
                {
                    ThreadChat th = entrySet.getValue();
                    ClientChat cc = th.getClientChat();
                    Integer key = entrySet.getKey();
                    if ( !cc.isOnline() )
                    {
                        try
                        {
                            System.out.println("Cleaning Client #" + cc.getId() + " thread...");
                            cc.close();
                            th.getThread().join(2000);
                            
                            // Concurrent modification issue
                            //connections.remove(key);
                        }
                        catch ( Exception ex )
                        {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        
    };
    
    static
    {
        watchdog.start();
    }
    
    private class ThreadChat
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
    
    private ServerSocket server;
    
    public ConnectionManager()
    {
        int contador = 0;
        try
        {
            server = new ServerSocket(8189);
            
            System.out.println("Server listening at " + server.getInetAddress() + ":8189");
            
            while ( true )
            {
                Socket socket = server.accept();
                
                ClientChat r = new ClientChat(socket, this, contador);
                Thread t = new Thread(r);
                t.start();
                connections.put(contador, new ThreadChat(t, r));
                broadcast(contador, "Client #" + contador + " connected...");
                
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
    
    public void broadcast(int id, String message)
    {
        for ( ThreadChat th : connections.values() )
        {
            ClientChat cc = th.getClientChat();
            if ( cc != null && cc.getId() != id && cc.isOnline() )
            {
                cc.send(message);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new ConnectionManager();
    }

}
