/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools @ Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author Nam Do
 */
//import de.javasoft.synthetica.dark.SyntheticaDarkLookAndFeel;
import static Client.myFrameClient.cbbBeginPoint;
import static Client.myFrameClient.fd;
import static Client.myFrameClient.pContent;
import static Client.myFrameClient.radUndirected;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import myEdge.MyEdgeWeight;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.Port;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;
//import static org.graalvm.compiler.asm.sparc.SPARCAssembler.Op3s.Add;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;
import Server.RSA_Encryption;

public final class Client extends myFrameClient {

    public static int destPort = 1234;
//    private int clientPort = setport(); //set port kết nối
    public static String hostname = "localhost";
    public static DatagramSocket socket;
    public static DatagramPacket dpsend, dpreceive;
    public static InetAddress add;
//    public static Scanner stdIn;
//    public FileChooser fc = new FileChooser();
//    public radGraphType gt = new radGraphType();
    public static ArrayList<String> arrVertex = new ArrayList();
    public static ArrayList<String> arrCompactVertex = new ArrayList();

    public static ArrayList<String> arrE = new ArrayList<>(); // variable in File Chooser

    public JGraphXAdapter<String, DefaultEdge> jgxAdapter;
    public JGraphXAdapter<String, MyEdgeWeight> x;
    MenuOption mo = new MenuOption();
    static AES_Encryption aes = new AES_Encryption(); // goi 2 giao thức mã hóa
    static RSA_Encryption rsa = new RSA_Encryption();
    String keyClient = taoKey(); //tạo key ngẫu nhiên
//    public static String temp;
    public int RADIUS = 300;

    //tao bien luu tru dia chi, port worker
    public static int workerPort;
    public static InetAddress workerAdd;

    public Client(String title) throws FileNotFoundException {

        super(title);
        try {
            connect_SendKey_ToServer();
//            String temp = receiveFromWoker();
            //kết nối vs worker
            dpreceive = new DatagramPacket(new byte[512], 512);
            socket.receive(dpreceive);
            System.out.println("Ket noi thanh cong:" + dpreceive.getAddress() + ":" + dpreceive.getPort());
            workerPort = dpreceive.getPort();
            workerAdd = dpreceive.getAddress();

            String line = "";
            FileChooser fc = new FileChooser(); // send to server data
            btnChooseFile.addActionListener(fc);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        sendToWoker("0)close connect");

                        closeConnectToServer();
                        System.exit(0);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            while (true) {

                line = receiveFromWoker();

//                if(line.equals("inputfalse")){
//                    
//                }
                System.out.println("res1:" + line);
                System.out.println("res456: " + mo.getURL());
                String[] tmpLine = line.split("<");
//                System.out.println("---" + tmpLine);
                for (String s : tmpLine) {
                    System.out.println("--" + s);

                }

                if (tmpLine[0].equals("1")) {
                    if (!tmpLine[1].equals("false") && !tmpLine[1].equals("false1")) {
                        radGraphType gt = new radGraphType();
                        radDirected.addActionListener(gt);
                        radUndirected.addActionListener(gt);

                        ArrayList<String> arrV = changeStringToArr(tmpLine[1]);
                        mo.setArrVertex(arrV);
                        if (arrV != null) {
                            buildGraph bg = new buildGraph(tmpLine[1], String.valueOf(mo.getArr()));
                            btnBuild.addActionListener(bg);
                            for (String v : arrV) {
                                cbbBeginPoint.addItem(v);
                                cbbEndPoint.addItem(v);
                            }
                            cbbBeginPoint.setSelectedIndex(0);
                            cbbEndPoint.setSelectedIndex(0);
                            mo.setVertexSourceSelected(String.valueOf(cbbBeginPoint.getSelectedItem()));
                            mo.setVertexDestinationSelected(String.valueOf(cbbEndPoint.getSelectedItem()));
                            System.out.println("check b: " + cbbBeginPoint.getSelectedItem());

                            cbbBeginPoint.repaint();
                            cbbEndPoint.repaint();
                            cbbSourceVertex csv = new cbbSourceVertex();
                            cbbBeginPoint.addActionListener(csv);
                            cbbDestinationVertex cdv = new cbbDestinationVertex();
                            cbbEndPoint.addActionListener(cdv);
//                            if(mo.getVertexSourceSelected()==null||mo.getVertexDestinationSelected()==null){
//                                JOptionPane.showMessageDialog(null, "Graph've not built yet!");
//                            }
//                            else{
                                findShortestPath fsp = new findShortestPath();
                            btnShortestPath.addActionListener(fsp);

                            exportPNG ex = new exportPNG();
                            btnExportPng.addActionListener(ex);
//                            }
                            

                            buildMatrix bMatrix = new buildMatrix();
                            btnBuildMatrix.addActionListener(bMatrix);

                        }

                    }

                    if (tmpLine[1].equals("false")) {
                        JOptionPane.showMessageDialog(null, "file data is not correct syntax \n VertexSource';'VertexDestionation';'WeightEdge");
                        lUrl.setText("File :");
                        lUrl.repaint();

                    }
                    if (tmpLine[1].equals("false1")) {
                        JOptionPane.showMessageDialog(null, "Vertex is at least 10!");
                        lUrl.setText("File :");
                        lUrl.repaint();

                    }

                }

//                if(cbbBeginPoint.is){
//                        
//                }
                if (tmpLine[0].equals("2")) {

                    if (tmpLine[1].contains("@")) {
                        
                        if(tmpLine[1]==null||x==null||mo.getVertexSourceSelected()==null||mo.getVertexDestinationSelected()==null){
                            JOptionPane.showMessageDialog(null, "Graph've not built yet!");
                        }else{
                            txtLength.removeAll();

                        System.out.println("ád:" + tmpLine[1]);
                        String[] templine = tmpLine[1].split("@");
                        System.out.println("length: " + templine[1]);
                        if ("Infinity".equals(templine[1])) {
                            JOptionPane.showMessageDialog(null, "Can not find path from "
                                    + mo.getVertexSourceSelected()
                                    + " to "
                                    + mo.getVertexDestinationSelected());

                        }

                        for (String s : templine) {
                            System.out.println("--" + s);
                        }
                        System.out.println("tyu:" + templine[0]);
                        System.out.println("qưe:" + changeStringToArr(templine[0]));
//                        System.out.println("333:"+jgxAdapter);

                        paintEdge(changeStringToArr(templine[0]), x);
                        txtLength.setText(templine[1]);
                        txtLength.setHorizontalAlignment(JTextField.CENTER);
                        txtLength.repaint();
                        }

                        
                    }


                }

                resetAll btnrs = new resetAll();
                btnReset.addActionListener(btnrs);

            }

        } catch (IOException e) {
            System.err.println(e);
        }

    }
//================================Main====================================

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        Client client = new Client("shortestpath");

    }
//=================================ActionListener for btnChooseFile ===================

    public class FileChooser implements ActionListener {

        private String url;

        public FileChooser() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fd.setFile("*.txt");

            fd.setVisible(true);
            if (fd.getFile() == null) {
                JOptionPane.showMessageDialog(null, "File have not chosen yet");

            } else {

                fileName = fd.getFile();
                lUrl.setText("File : " + fileName);
                char c = 92;
                char d = 47;

                url = fd.getDirectory().replace(c, d) + fd.getFile();

                mo.setURL(url);
                arrE.clear();
                try {
                    if (url != null) {
                        FileInputStream inputStream = null;
                        BufferedReader bufferedReader = null;

                        inputStream = new FileInputStream(url);
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        String tempLine = bufferedReader.readLine();
                        arrE.add(tempLine);

                        while ((line = bufferedReader.readLine()) != null) {
                            tempLine += ("#" + line);
                            arrE.add(line);
                        }

                        sendToWoker(tempLine);
                        System.out.println("arrE :" + arrE);
                        if (arrE != null) {
                            mo.setArr(arrE);
                        }

//                System.out.println(arrE);
                        bufferedReader.close();
                        inputStream.close();
                    }
                    // doc file va chuyen ve server

                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                lUrl.repaint();

            }
        }

    }
//=======================Actionlistener for cbb source & destination============================

    public class cbbSourceVertex implements ActionListener {
//        String vertex;

        public cbbSourceVertex() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mo.setVertexSourceSelected(String.valueOf(cbbBeginPoint.getSelectedItem()));
            System.out.println(String.valueOf(cbbBeginPoint.getSelectedItem()));
            System.out.println("reS" + mo.getVertexSourceSelected());

        }
    }

    public class cbbDestinationVertex implements ActionListener {
//        String vertex;

        public cbbDestinationVertex() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mo.setVertexDestinationSelected(String.valueOf(cbbEndPoint.getSelectedItem()));
            System.out.println(String.valueOf(cbbEndPoint.getSelectedItem()));
            System.out.println("reD" + mo.getVertexDestinationSelected());
        }
    }
//========================Actionlistener for btn shortest path================================

    public class findShortestPath implements ActionListener {

//        String re = "";
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("sd:" + mo.getVertexSourceSelected() + ">" + mo.getVertexDestinationSelected() + ">" + mo.getType());
            try {
                if (mo.getVertexSourceSelected() == null || mo.getVertexDestinationSelected() == null) {
                    JOptionPane.showMessageDialog(null, "No data available");
                } else {
                    if(mo.getVertexSourceSelected()==mo.getVertexDestinationSelected()){
                        JOptionPane.showMessageDialog(null, "Source & Destination must be different");
                    }
                    else
                    sendToWoker(mo.getVertexSourceSelected() + ">" + mo.getVertexDestinationSelected() + ">" + mo.getType());
                }

//                re = receiveFromWoker();
//                System.out.println("ress2:"+re);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//========================Actionlistener for radio btn Graph type===============================
    // xong
    public class radGraphType implements ActionListener {

        int type1 = 0;
        int type2 = 1;

        public radGraphType() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (radUndirected.isSelected()) {
                mo.setType(type1);
                System.out.println("type:" + mo.getType());
            }
            if (radDirected.isSelected()) {
                mo.setType(type2);
                System.out.println("type:" + mo.getType());
            }
        }
    }
//========================ActionListener for btnBuild =========================

    public class buildGraph implements ActionListener {

        public String vertex, edge;
//        public JGraphXAdapter x;
//        public int type = mo.getType();

        public buildGraph(String vertex, String edge) {
            this.edge = edge;
            this.vertex = vertex;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pContent.removeAll();
            pContent.setLayout(new BorderLayout());
            System.out.println("type" + mo.getType());
            if (mo.getType() == 1) {
                DirectedWeightedMultigraph dg = createDirectedGraph(vertex, edge);
                DirectedWeightedMultigraph dwm = createDirectedGraphWithMyWeigthEdge(vertex, edge);
                x = new JGraphXAdapter<>(dwm);
                jgxAdapter = new JGraphXAdapter<>(dg);
                createDirectedGraphVisualization(x, pContent);

            } else {
                WeightedMultigraph udg = createUnDirectedGraph(vertex, edge);
                WeightedMultigraph wm = createUnDirectedGraphWithMyWeigthEdge(vertex, edge);
                x = new JGraphXAdapter<>(wm);
                jgxAdapter = new JGraphXAdapter<>(udg);
                createUnDirectedGraphVisualization(x, pContent);

            }

            pContent.repaint();
        }
    }

    //====================function to build graph Visualization=========================
    public void createDirectedGraphVisualization(JGraphXAdapter x, JPanel p) {
//        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(x);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
//        p.add(component);
//        resize(DEFAULT_SIZE);

        mxCircleLayout layout = new mxCircleLayout(x);
//        mxIGraphLayout l =new 
        // center the circle
        int radius = RADIUS;
        layout.setX0((pContent.getWidth() / 2) - radius);
        layout.setY0((pContent.getHeight() / 2) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(false);
        layout.execute(x.getDefaultParent());

        p.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        p.setBackground(cContent);
        p.add(component);
        setVisible(true);

    }

    public void createUnDirectedGraphVisualization(JGraphXAdapter x, JPanel p) {
        mxGraphComponent component = new mxGraphComponent(x);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        mxGraphModel graphModel = (mxGraphModel) component.getGraph().getModel();
        Collection<Object> cells = graphModel.getCells().values();
        mxUtils.setCellStyles(component.getGraph().getModel(),
                cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);

        int radius = RADIUS;
        mxCircleLayout layout = new mxCircleLayout(x);
//        System.out.println((pContent.getWidth() / 2.0) - radius);
        // center the circle

        layout.setX0((pContent.getWidth() / 2.0) - radius);
        layout.setY0((pContent.getHeight() / 2.0) - radius);
        layout.setRadius(radius);

        layout.setMoveCircle(false);
        layout.execute(x.getDefaultParent());

//        component.setBounds(40, 40, 620, 620);
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        p.setBackground(cContent);
        p.add(component);
        setVisible(true);

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
        ArrayList<DefaultWeightedEdge> arrDefaultWeightedEdges = new ArrayList<>();

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
//            System.out.println(dg.getEdgeWeight(de[i]));
            arrDefaultWeightedEdges.add(de[i]);
        }
        mo.setArrEdge(arrDefaultWeightedEdges);
        return dg;
    }

    public DirectedWeightedMultigraph createDirectedGraphWithMyWeigthEdge(String V, String E) {
        ArrayList<String> Vertexs = new ArrayList<>();
        ArrayList<String> Edges = new ArrayList<>();
        ArrayList<MyEdgeWeight> arrWeightedEdge = new ArrayList<>();

        Vertexs = changeStringToArr(V);
        Edges = changeStringToArr(E);

        DirectedWeightedMultigraph dgtemp = new DirectedWeightedMultigraph(MyEdgeWeight.class);
        for (String v : Vertexs) {
            dgtemp.addVertex(v);
        }

        MyEdgeWeight[] we = new MyEdgeWeight[Edges.size()];
        for (int i = 0; i < Edges.size(); i++) {
            String e = Edges.get(i);
            String[] temp = e.split(";");

            we[i] = (MyEdgeWeight) dgtemp.addEdge(temp[0], temp[1]);

            dgtemp.setEdgeWeight(we[i], Integer.valueOf(temp[2]));

            arrWeightedEdge.add(we[i]);

        }
        mo.setMyWeight(arrWeightedEdge);

        return dgtemp;
    }
//============================function tao undirect graph===================

    public WeightedMultigraph createUnDirectedGraph(String V, String E) {
        ArrayList<String> Vertexs = new ArrayList<>();
        ArrayList<String> Edges = new ArrayList<>();
        ArrayList<DefaultWeightedEdge> arrDefaultWeightedEdges = new ArrayList<>();

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

            arrDefaultWeightedEdges.add(de[i]);

        }

        mo.setArrEdge(arrDefaultWeightedEdges);
        System.out.println("arrEdge :" + arrDefaultWeightedEdges);
        System.out.println("arrEdge get:" + mo.getArrEdge());
        return udg;
    }

    ///======================================asd
    public WeightedMultigraph createUnDirectedGraphWithMyWeigthEdge(String V, String E) {
        ArrayList<String> Vertexs = new ArrayList<>();
        ArrayList<String> Edges = new ArrayList<>();
        ArrayList<MyEdgeWeight> arrWeightedEdge = new ArrayList<>();

        Vertexs = changeStringToArr(V);
        Edges = changeStringToArr(E);

        WeightedMultigraph udgtemp = new WeightedMultigraph(MyEdgeWeight.class);
        for (String v : Vertexs) {
            udgtemp.addVertex(v);
        }
//        DefaultWeightedEdge[] de = new DefaultWeightedEdge[Edges.size()];
        MyEdgeWeight[] we = new MyEdgeWeight[Edges.size()];
        for (int i = 0; i < Edges.size(); i++) {
            String e = Edges.get(i);
            String[] temp = e.split(";");
            we[i] = (MyEdgeWeight) udgtemp.addEdge(temp[0], temp[1]);
            udgtemp.setEdgeWeight(we[i], Integer.valueOf(temp[2]));
            arrWeightedEdge.add(we[i]);
        }
        mo.setMyWeight(arrWeightedEdge);
        return udgtemp;
    }

//===========================painting Edge ============================================
    public void paintEdge(ArrayList<String> Edges, JGraphXAdapter x) {
        if (Edges == null || x == null) {
            JOptionPane.showMessageDialog(null, "Graph've not built yet!");
//            txtLength.setText("");
//            repaint();
        } else {
            if (mo.getArrEdge() == null || mo.getMyWeight() == null) {
                JOptionPane.showMessageDialog(null, "Graph've not built yet!");
            } else {
                System.out.println("nmo:" + Edges);
//        ArrayList <String> shortestPath1 = Edges;
//        System.out.println("ghi:"+shortestPath1);
                ArrayList<String> E = changeStringToArr(String.valueOf(mo.getArrEdge()));
                System.out.println("jkl:" + E);

                System.out.println("abc:" + mo.getArrEdge());

                ArrayList<MyEdgeWeight> Edge = mo.getMyWeight();
                /// chuwa lay duoc mang defaultEdge
                System.out.println("def:" + Edge);
                ArrayList<MyEdgeWeight> dweShortestPath = new ArrayList<>();

                for (int i = 0; i < Edges.size(); i++) {
                    for (int j = 0; j < E.size(); j++) {
                        if (Edges.get(i).equals(E.get(j))) {
                            dweShortestPath.add(Edge.get(j));
                        }
                    }

                }
                //Lay mang dinh ngan nhat
                ArrayList<String> vertexShortest = new ArrayList<>();
                for (String s : Edges) {
                    String[] tmpV = s.split(":");
                    vertexShortest.add(tmpV[0].replace("(", "").replace(")", "").trim());
                    vertexShortest.add(tmpV[1].replace("(", "").replace(")", "").trim());
                }

                vertexShortest = arrCompactV(vertexShortest);

                System.out.println("222:" + vertexShortest);
                // lay mang dinh do thi
                ArrayList<String> vertex = new ArrayList<>();
                for (String s : E) {
                    String[] tmpV = s.split(":");
                    vertex.add(tmpV[0].replace("(", "").replace(")", "").trim());
                    vertex.add(tmpV[1].replace("(", "").replace(")", "").trim());
                }

                //reset laij mau mac dinh cua do thi
                System.out.println("111:" + dweShortestPath);
                HashMap<MyEdgeWeight, com.mxgraph.model.mxICell> edgeToCellMap1 = x.getEdgeToCellMap();
                System.out.println("hashmap1 :" + edgeToCellMap1);
                for (int i = 0; i < Edge.size(); i++) {
                    mxICell cell = (mxICell) edgeToCellMap1.get(Edge.get(i));
                    if (cell.isEdge()) {
                        x.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#6482B9", new Object[]{cell});
                    }

                }

                HashMap<String, com.mxgraph.model.mxICell> vertexToCellMap1 = x.getVertexToCellMap();
                for (int i = 0; i < vertex.size(); i++) {
                    mxICell cell = (mxICell) vertexToCellMap1.get(vertex.get(i));
                    if (cell.isVertex()) {
                        x.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#6482B9", new Object[]{cell});
                    }

                }
                // to mau do thi
                HashMap<String, com.mxgraph.model.mxICell> vertexToCellMap = x.getVertexToCellMap();
                for (int i = 0; i < vertexShortest.size(); i++) {
                    mxICell cell = (mxICell) vertexToCellMap.get(vertexShortest.get(i));
                    if (cell.isVertex()) {
                        x.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", new Object[]{cell});
                    }

                }

                System.out.println("res Def shortest Edge:" + dweShortestPath);

                HashMap<MyEdgeWeight, com.mxgraph.model.mxICell> edgeToCellMap = x.getEdgeToCellMap();
                for (int i = 0; i < dweShortestPath.size(); i++) {
                    mxICell cell = (mxICell) edgeToCellMap.get(dweShortestPath.get(i));
                    if (cell.isEdge()) {
                        x.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", new Object[]{cell});
                    }

                }

            }

        }

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

    //====================create btn reset =============================================
    public class resetAll implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
//            mo.setURL(null);
//            mo.setArr(null);
//            mo.setArrEdge(null);
//            mo.setMyWeight(null);
//            mo.setVertexDestinationSelected(null);
//            mo.setVertexSourceSelected(null);
//            x.clearSelection();
////            x = null;
//            arrE.clear();
//            lUrl.setText("File:");
//            txtLength.setText(null);
//            radUndirected.setSelected(true);
            pContent.removeAll();
//            cbbBeginPoint.removeAllItems();
//            cbbEndPoint.removeAllItems();

            repaint();

        }
    }

    //====================create  btn export PNG========================================
    public class exportPNG implements ActionListener {

        public exportPNG() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fdsave.setFile("*.png");
            fdsave.setVisible(true);
            String tmpName = fdsave.getFile();

            try {
                givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist(x, tmpName);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//=========================== btn matrix===========================================
    public class buildMatrix implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            JFrame fMatrix = new JFrame();
            fMatrix.setLocationRelativeTo(null);
            fMatrix.setLayout(new FlowLayout());
            fMatrix.setSize(700, 500);
            fMatrix.setTitle(String.valueOf(cbbMatrix.getSelectedItem()));

            JScrollPane sp = new JScrollPane();
            sp.add(adjacencyMatrix());

            fMatrix.add(sp, FlowLayout.CENTER);
            fMatrix.setVisible(true);
        }
    }

    public JTable adjacencyMatrix() {

        Vector<String> tempHeader = new Vector<>();
        tempHeader.add("");
        for (String s : mo.getArrVertex()) {
            tempHeader.add(s);
        }
        DefaultTableModel model = new DefaultTableModel(tempHeader, 0);

        JTable tb = new JTable(model);
        return tb;
    }

    public void givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist(JGraphXAdapter graphAdapter, String nameFile) throws IOException {

        BufferedImage image = mxCellRenderer
                .createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);

        File imgFile = new File("src/" + nameFile + ".png");

        ImageIO.write(scale(image, pContent.getHeight(), pContent.getHeight()), "PNG", imgFile);
        imgFile.exists();
    }

    public static BufferedImage scale(BufferedImage src, int w, int h) {
        BufferedImage img
                = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++) {
            ys[y] = y * hh / h;
        }
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = src.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }

    public static String taoKey() {
        Random ran = new Random();
        int top = 6;
        char data = ' ';
        String dat = "";

        for (int i = 0; i <= top; i++) {
            data = (char) (ran.nextInt(25) + 97);
            dat = data + dat;
        }
        return dat;
    }

//    public static int setport() {
//        Random ran = new Random();
//
//        int value = ran.nextInt((10000 - 1001) - 1);
//        return value;
//    }
    //=========================== cac function connect ===================================
    public void connect_SendKey_ToServer() throws UnknownHostException, SocketException, IOException {

        add = InetAddress.getByName(hostname);//UnknownHostException
        socket = new DatagramSocket();	//SocketException
//        stdIn = new Scanner(System.in);
        System.out.println("Key: " + keyClient);
        String key = rsa.Encrpytion(keyClient);
        System.out.println("Key ma hoa: " + key);
        byte[] data = key.getBytes();
//        String key = "Hello";
//        byte[] data = key.getBytes();
        dpsend = new DatagramPacket(data, data.length, add, destPort);

        socket.send(dpsend);
    }

    public void sendToWoker(String line) throws IOException {
        line = aes.encrypt(line, keyClient);

        System.out.print("Client input: " + line);
        System.out.print("\nFrom:" + workerAdd + ":" + workerPort);
        byte[] data = line.getBytes();
        dpsend = new DatagramPacket(data, data.length, workerAdd, workerPort);
//        dpsend = new DatagramPacket(data, data.length, workerAdd, workerPort);
//        System.out.println("Client sent " + line + " to " + workerAdd.getHostAddress()
//                + " from port " + socket.getLocalPort());
        socket.send(dpsend);

    }

    public void closeConnectToServer() {

        System.out.println("Client socket closed");
//        stdIn.close();
        socket.close();

    }

    public String receiveFromWoker() throws IOException {
        dpreceive = new DatagramPacket(new byte[512], 512);
        socket.receive(dpreceive);
        String tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());

        //
//        workerPort = dpreceive.getPort();
//        workerAdd = dpreceive.getAddress();
//        System.out.println("worker" + workerAdd +":"+ workerPort);
        tmp = aes.decrypt(tmp, keyClient);
        System.out.println("Client get: " + tmp + " from server");
        return tmp;
    }
}
