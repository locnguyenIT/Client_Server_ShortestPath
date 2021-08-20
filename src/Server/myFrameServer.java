/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import static Client.myFrameClient.blueBerry;
import static Client.myFrameClient.cContent;
import static Client.myFrameClient.indioInk;
import static Client.myFrameClient.midNightBlue;
import static Client.myFrameClient.periwinkle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Nam Do
 */
public class myFrameServer extends JFrame{
    private int WIDTH = 800, HEIGHT = 500;
    public static JTextArea textArea;
    public static Color midNightBlue , indioInk, blueBerry, periwinkle, cContent;
    public myFrameServer(String title){
        setTitle(title);
        inIt();
    }
    public void inIt(){
        midNightBlue = new Color(30, 31, 38);
        indioInk = new Color(40, 54, 85);
        blueBerry = new Color(77, 100, 141);
        periwinkle = new Color(208, 225, 249);
        cContent = new Color(124, 135, 161);
        
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setBackground( indioInk );
        textArea= new JTextArea();
//        textArea.setTabSize(WIDTH-1/4*WIDTH);
        add(createServerGui());
        
        
        setVisible(true);
    }
    
    private JPanel createServerGui(){
        JPanel panel = new JPanel(new FlowLayout());
        
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(WIDTH-100, HEIGHT-100));
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        
        panel.add(scrollPane);
//        panel.add(textArea);
        return panel;
    }
}
