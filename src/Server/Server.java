/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools @ Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author Nam Do
 */
import Client.Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;

public final class Server extends myFrameServer {
    
    public static int buffsize = 512;
    public static int port = 1234;
    public static DatagramSocket socket;
    public static DatagramPacket dpreceive, dpsend;
    ArrayList<String> Edges = new ArrayList<>();
    ArrayList<String> Vertexs = new ArrayList<>();
    static AES_Encryption aes = new AES_Encryption();
    static RSA_Encryption rsa = new RSA_Encryption();
    int[] ktKey = new int[10000];
    String[] keyClient = new String[10000];
    
    public Server(String title) {
        super(title);
        try {
            socket = new DatagramSocket(port);
            
            dpreceive = new DatagramPacket(new byte[buffsize], buffsize);
            int corePoolSize = 4;
            int maxPoolSize = 8;
            int queueCapacity = 20000;
            ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 1000000,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueCapacity));
            while (true) {                
                try {
                    
                    socket.receive(dpreceive);
                    System.out.println("Server:" + dpreceive.getAddress() + ":" + dpreceive.getPort());
                    executor.execute(new Worker(dpreceive));
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }            
        } catch (SocketException e) {
            System.err.println(e);
        }        
    }
    
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        Server server = new Server("Server");
        
    }
    
}
