import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.GregorianCalendar;

public class PingClient {
  private static final int TIMEOUT = 1000; // miliseconds
  public static void main(String[] args) throws Exception{
    
    // get server address
    //String serverName = "localhost";
    //int serverPort = 6789;

    if(args.length < 2) {
      System.out.println("Required arguments: host port");
      return;
    }

   // serverName = args[0];
    // server to ping
    InetAddress serverIPAddress = InetAddress.getByName(args[0]);
    

    // port to access
   int serverPort = Integer.parseInt(args[1]);
    
    // Create datagram socket to receive and send UDP packets
    // through port specified on command line
    DatagramSocket clientSocket = new DatagramSocket();

    long minDelay = 0;
    long maxDelay = 0;
    long averageDelay = 0;
    

    // 10 ping requests to server
    for(int sequence_num =0; sequence_num<10; sequence_num++) {
      
      // get a timestamp
      long timeSent = new GregorianCalendar().getTimeInMillis();
      long timeReceived = 0;

      System.out.println(timeSent);
      // Payload data
      String message = "PING " + sequence_num + " " + timeSent + "\r\n";
      
      // transfer message to a byte array for sending
      byte[] buf = new byte[1024];
      buf = message.getBytes();
      
      // Create ping datagram to server
      DatagramPacket packet = new DatagramPacket(buf, buf.length, serverIPAddress, serverPort);
     //   DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

      // send ping packet to server
     // clientSocket.send(packet);
        clientSocket.send(packet);

      // try to receive packet with timeout of 1000ms = 1s
      try {
    	// set timeout value for socket to 1000ms
        clientSocket.setSoTimeout(TIMEOUT);
       // DatagramPacket reply = new DatagramPacket(buf, buf.length, serverIPAddress, serverPort);
        
        // create a packet to hold incoming UDP packet
        DatagramPacket reply = new DatagramPacket(new byte[1024], 1024);
        
        // try to receive datagram packet from clientSocket
        clientSocket.receive(reply);
     //   clientSocket.close(); 
        
        timeReceived = new GregorianCalendar().getTimeInMillis();
        // RTT: difference between when packet sent an reply recieved
        long delay = timeReceived - timeSent;
       
        // update delay
        if(sequence_num == 0) {
          minDelay = delay;
          maxDelay = delay;
        }
        
        if(delay < minDelay) {
          minDelay = delay;
        }
        else if(delay > maxDelay) {
          maxDelay = delay;
        }
        averageDelay += delay / (sequence_num+1);
        
        printData(reply, sequence_num, delay);
        
      }catch(IOException e) {
        System.out.println("Timeout for packet " + sequence_num);
      }

    }
      
    System.out.println("min rtt = " + minDelay + " ms" +
        ", max rtt = " + maxDelay + " ms" +
        ", average rtt = " + averageDelay + " ms");
     
     clientSocket.close(); 
    }
    
/* 
 * Print ping data to the standard output stream.
 * modified for PingClient
 */
private static void printData(DatagramPacket request, int sequence, long delay) throws Exception
{
   // Obtain references to the packet's array of bytes.
   byte[] buf = request.getData();

   // Wrap the bytes in a byte array input stream,
   // so that you can read the data as a stream of bytes.
   ByteArrayInputStream bais = new ByteArrayInputStream(buf);

   // Wrap the byte array output stream in an input stream reader,
   // so you can read the data as a stream of characters.
   InputStreamReader isr = new InputStreamReader(bais);

   // Wrap the input stream reader in a bufferred reader,
   // so you can read the character data a line at a time.
   // (A line is a sequence of chars terminated by any combination of \r and \n.) 
   BufferedReader br = new BufferedReader(isr);

   // The message data is contained in a single line, so read this line.
   String line = br.readLine();

   // Print host address and data received from it.
   System.out.println(
      "ping to " + 
      request.getAddress().getHostAddress() + 
      ": " +
      new String(line) + "\n" +
      "seq = " + sequence +
      ", rtt = " + delay +" ms");
  }
}
