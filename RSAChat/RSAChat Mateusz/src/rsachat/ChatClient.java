package rsachat;
import java.net.*;
import java.io.*;
import java.math.BigInteger;

public class ChatClient implements Runnable
{  
    private RSA rsa = new RSA();
    private ChatWindow w=null;
    private Socket socket              = null;
    private DataInputStream  console   = null;
    private DataOutputStream streamOut = null;
    private Thread thread              = null;
    private ChatClientThread client    = null;
 
   public void run()
   {  
      BigInteger plaintext = null;
      BigInteger ciphertext = null;
      String read=null;
       while (thread != null)
      {  try
         {  
             read=console.readLine();
            plaintext = new BigInteger(read.getBytes()); //console.readLine().getBytes()
            ciphertext = rsa.encrypt(plaintext);
            streamOut.writeUTF(new String(ciphertext.toString()));
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }
   public ChatClient(String serverName, int serverPort)
   { 
       w = new ChatWindow(this);
       w.setVisible(true);
       System.out.println("Establishing connection. Please wait ...");
      try
      {  socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe)
      {  System.out.println("Host unknown: " + uhe.getMessage()); }
      catch(IOException ioe)
      {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
   }
   
   public void BntRun(String msg)
   {
      BigInteger plaintext = null;
      BigInteger ciphertext = null;
      String read=msg;
         try
         {  
            
            plaintext = new BigInteger(read.getBytes()); //console.readLine().getBytes()
            ciphertext = rsa.encrypt(plaintext);
            streamOut.writeUTF(new String(ciphertext.toString()));
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      
   }
   
   public void handle(String msg)
   { BigInteger ciphertext = null;
     BigInteger plaintext = null;
       
       //przejście wiadomości
       ciphertext = new BigInteger(msg);
       plaintext = rsa.decrypt(ciphertext);
       w.ShowMsg("Zaszyfrowany "+ciphertext);
       w.ShowMsg("Odszyfrowany "+new String(plaintext.toByteArray()));
         System.out.println("Zaszyfrowany "+ciphertext);
         System.out.println("Odszyfrowany "+new String(plaintext.toByteArray()));
   }
   /////////////////////////////////////////////////////
   public void start() throws IOException
   {  console   = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)
      {  client = new ChatClientThread(this, socket);
         thread = new Thread(this);                   
         thread.start();
      }
   }
   ////////////////////////////////////////////////////
   public void stop()
   {  if (thread != null)
      {  thread.stop();  
         thread = null;
      }
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing ..."); }
      client.close();  
      client.stop();
   }
   public static void main(String args[])
   {  ChatClient client = null;
      
         client = new ChatClient("localhost", 2);
   }
}