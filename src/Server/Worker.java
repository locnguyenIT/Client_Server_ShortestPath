/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import static Server.myFrameServer.textArea;
import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;

public class Worker implements Runnable {

    private int buffsize = 512;
    private int port;
    private InetAddress add;
    DatagramSocket socket;
    DatagramPacket dpsend, dpreceive;

    ArrayList<String> Edges = new ArrayList<>();
    ArrayList<String> Vertexs = new ArrayList<>();

    static AES_Encryption aes = new AES_Encryption();
    static RSA_Encryption rsa = new RSA_Encryption();
    int[] ktKey = new int[10000];
    String[] keyClient = new String[10000];
    int rdkey = setkey();

    public Worker(DatagramPacket dpreceive) throws IOException {
        port = dpreceive.getPort();
        add = dpreceive.getAddress();
        System.out.println("Worker:" + dpreceive.getAddress() + ":" + dpreceive.getPort());
        
        //getKey(dpreceive);
            
        String sent = "Hello";
//        byte[] data = sent.getBytes();
//        dpsend = new DatagramPacket(data, data.length, add, port);
        
        acceptConnectToClient();
        //gửi lần 1
//        socket.send(dpsend);
        ReceiveKey(dpreceive);
        //gửi lần 2
        send_Message_ToClient(sent);
    }

    @Override
    public void run() {
        try {

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                    closeConnect();
                    System.exit(0);
                }
            });

            while (true) {
                String tmp = receiveFromClient();

                if (tmp.contains("#") || tmp.contains(">") || tmp.contains(")")) {
                    if (tmp.contains(")")) {
                        textArea.append("client: "+ add
                                                    + " at port "
                                                    + port
                                                    + " was closed \n");
                        closeConnect();

                    }
                    if (tmp.contains("#")) {
                        
                        if (checkSyntaxGraph(tmp)) {
                            Edges = getArrEdge(tmp.trim());
                            if (Edges.size() < 10) {
                                sendToClient("1<false1");
                            } else {
                                boolean checkVertex=true;
                                for (String E : Edges) {
                                    if (checkSyntaxEdge(E)) {
                                        String regex = "^[a-zA-Z0-9 aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬbBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆfFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTuUùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ]*$";
                                        
                                        String[] temp = E.split(";");
                                        
                                        if(temp[0].matches(regex)&&temp[1].matches(regex)&&temp[2].matches(regex)){
                                            Vertexs.add(temp[0]);
                                            Vertexs.add(temp[1]);
                                        }
                                        else{
                                            checkVertex=false;
                                        }
                                    }
                                }
                                
                                if(checkVertex){
                                    Vertexs = arrCompactV(Vertexs);
                                    sendToClient("1<" + String.valueOf(Vertexs));
                                }
                                else{
                                    sendToClient("1<false");
                                }
                                
                            }

                        } else {
                            sendToClient("1<false");

                        }

                    }
                    if (tmp.contains(">") && !tmp.endsWith(">") && !tmp.startsWith(">")) {
                        //chua xong
                        // timf dduongwf ddi nganws nhaats
                        String[] temp = tmp.trim().split(">");

                        if ("0".equals(temp[2])) {
                            WeightedMultigraph dg = createUnDirectedGraph(String.valueOf(Vertexs), String.valueOf(Edges));

                            DijkstraShortestPath dijkstraShortestPath1 = new DijkstraShortestPath(dg, temp[0], temp[1]);
                            ArrayList shortestPath1 = (ArrayList) dijkstraShortestPath1.getPathEdgeList();
                            String length = String.valueOf(dijkstraShortestPath1.getPathLength());
                            System.out.println(length);
                            sendToClient("2<" + String.valueOf(shortestPath1) + "@" + length);
                        }
                        if ("1".equals(temp[2])) {
                            DirectedWeightedMultigraph udg = createDirectedGraph(String.valueOf(Vertexs), String.valueOf(Edges));
                            DijkstraShortestPath dijkstraShortestPath2 = new DijkstraShortestPath(udg, temp[0], temp[1]);
                            ArrayList shortestPath2 = (ArrayList) dijkstraShortestPath2.getPathEdgeList();
                            String length = String.valueOf(dijkstraShortestPath2.getPathLength());
                            sendToClient("2<" + String.valueOf(shortestPath2) + "@" + length);
                        }

                    }

                } else {

                    sendToClient("1<false");
                }
//                if(tmp.equals("close connect")){
//                    textArea.append(tmp);
//                
//                }
                //sendToClient(String.valueOf(checkSyntaxGraph(tmp)));

            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    // lay Edge tu chuoi from client
    public ArrayList getArrEdge(String s) {
        ArrayList arrE = new ArrayList();

        String[] temp = s.split("#");
        for (String tempE : temp) {
            arrE.add(tempE);
        }

        return arrE;
    }

    public ArrayList arrCompactV(ArrayList a) {
        ArrayList<String> arrTemp = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            if (!arrTemp.contains(a.get(i))) {
                arrTemp.add((String) a.get(i));
            }
        }
        return arrTemp;
    }

    public Boolean checkSyntaxEdge(String s) {

        char a = 59;
        if (countCharInString(s, a) == 2) {
            if (!s.startsWith(";") && !s.endsWith(";")) {
                if (!s.contains(";;")) {
                    return true;
                }
            }

        }

        return false;

    }

    public Boolean checkSyntaxGraph(String s) {

        if (!s.startsWith("#") && !s.endsWith("#")) {
            if (!s.contains("##")) {
                return true;
            }
        }

        return false;
    }

    public static int countCharInString(String s, char c) {
        int count = 0;
        char[] temp = s.toCharArray();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == c) {
                count++;
            }
        }
        return count;
    }

    //======================= function to chang String to ArrayList======================
    public ArrayList changeStringToArr(String s) {
        ArrayList<String> arr = new ArrayList<>();
        char a = 91;    //[
        char b = 93;    //]
        char c = 32;    // khoang trong
        String[] temp = s.replace(a, c).replace(b, c).trim().split(",");
        for (String tmp : temp) {
            arr.add(tmp.trim());
        }
        return arr;
    }

    //============================function tao direct graph===================
    public DirectedWeightedMultigraph createDirectedGraph(String V, String E) {
        ArrayList<String> Vertexs = new ArrayList<>();
        ArrayList<String> Edges = new ArrayList<>();

        Vertexs = changeStringToArr(V);
        Edges = changeStringToArr(E);

        DirectedWeightedMultigraph dg = new DirectedWeightedMultigraph(DefaultWeightedEdge.class);

        for (String v : Vertexs) {
            dg.addVertex(v);
        }
        DefaultWeightedEdge[] de = new DefaultWeightedEdge[Edges.size()];
        for (int i = 0; i < Edges.size(); i++) {
            String e = Edges.get(i);
            String[] temp = e.split(";");

            de[i] = (DefaultWeightedEdge) dg.addEdge(temp[0], temp[1]);
            dg.setEdgeWeight(de[i], Integer.valueOf(temp[2]));
        }
        return dg;
    }
//============================function tao undirect graph===================

    public WeightedMultigraph createUnDirectedGraph(String V, String E) {
        ArrayList<String> Vertexs = new ArrayList<>();
        ArrayList<String> Edges = new ArrayList<>();

        Vertexs = changeStringToArr(V);
        Edges = changeStringToArr(E);

        WeightedMultigraph udg = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (String v : Vertexs) {
            udg.addVertex(v);
        }
        DefaultWeightedEdge[] de = new DefaultWeightedEdge[Edges.size()];
        for (int i = 0; i < Edges.size(); i++) {
            String e = Edges.get(i);
            String[] temp = e.split(";");

            de[i] = (DefaultWeightedEdge) udg.addEdge(temp[0], temp[1]);
            udg.setEdgeWeight(de[i], Integer.valueOf(temp[2]));
        }
        return udg;
    }

    public void setstart() {
        for (int i = 0; i < 10000; i++) {
            ktKey[i] = 0;
            keyClient[i] = "";
        }
    }

    public static int setkey() {
        Random ran = new Random();

        int value = ran.nextInt((10000 - 1001) - 1);
        return value;
    }

    public void acceptConnectToClient() throws SocketException, IOException {
        socket = new DatagramSocket();
        dpreceive = new DatagramPacket(new byte[buffsize], buffsize);
        setstart();

    }

    public void ReceiveKey(DatagramPacket dpreceive){
        String tmp = "";
        if (ktKey[rdkey] == 0) {
            tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());
            System.out.println("Key ma hoa: "+tmp);
            keyClient[rdkey] = rsa.Decrpytion(tmp);
            System.out.println("Key: "+keyClient[rdkey]);
            //System.out.println(tmp);

            ktKey[rdkey] = 1;

        }
    }

    public String receiveFromClient() throws IOException {
        String tmp = "";

        socket.receive(dpreceive);
        tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());
            tmp = aes.decrypt(tmp, keyClient[rdkey]);
        System.out.println("Server received: " + tmp + " from "
                + dpreceive.getAddress().getHostAddress() + " at port "
                + socket.getLocalPort());

        textArea.append("Server received: " + tmp + " from "
                + add + " at port "
                + port + "\n");

        return tmp;
    }

    public void closeConnect() {
        System.out.println("Server socket closed");
        socket.close();

    }

    public void sendToClient(String tmp) throws IOException {
        System.out.println("Server sent back: " + tmp + " to client");

        textArea.append("Server sent back: " + tmp + " to client from "
                + add + " at port "
                + port + "\n");
        tmp = aes.encrypt(tmp, keyClient[rdkey]);
        dpsend = new DatagramPacket(tmp.getBytes(), tmp.getBytes().length,
                add, port);

        socket.send(dpsend);
    }
    public void send_Message_ToClient(String tmp) throws IOException {
        System.out.println("Server sent back: " + tmp + " to client");

        textArea.append("Server sent back: " + tmp + " to client from "
                + add + " at port "
                + port + "\n");
        dpsend = new DatagramPacket(tmp.getBytes(), tmp.getBytes().length,
                add, port);

        socket.send(dpsend);
    }
}
