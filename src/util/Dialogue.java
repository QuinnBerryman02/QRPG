package util;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import java.awt.Image;
import java.awt.Dimension;

import main.MainWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

public class Dialogue extends JPanel implements Menu {
    private Player player;
    private NPC npc; 
    private ArrayList<Topic> topics = new ArrayList<Topic>();
    private ArrayList<Topic> availableTopics;
    private Scroller text;
    private Scroller overview;
    private DetailPanel details;

    public Dialogue(Player player, NPC npc) {
        super();
        this.player = player;
        this.npc = npc;
        topics.add(Topic.getTopic("Introduction"));
        availableTopics = player.findCommonTopics(npc);
        setSize(MainWindow.getW() - 200, MainWindow.getH() - 200);
        setLocation(100, 100);
        setBackground(new Color(1f,1f,1f,0f));
        CustomBorder customBorder = new CustomBorder();
        setBorder(customBorder);

        details = new DetailPanel();
        text = new Scroller(new JLabel(), customBorder);
        overview = new Scroller(new JLabel(), customBorder);
        details.setBorder(customBorder);
        details.setBounds(getX()+10,getY()+10, getWidth()*1/5-10,getHeight()-20);
        text.setBounds(getX()+getWidth()*1/5,getY()+10, getWidth()*3/5-10,getHeight()-20);
        overview.setBounds(getX() + getWidth()*4/5,getY()+10,getWidth()*1/5-10,getHeight()-20);
        add(details);
        add(text);
        add(overview);
        setVisible(true);
    }

    public void render(Graphics g) {
        super.paintComponent(g);
        try {
            File f = new File("res/gui/paperBackground.png");
            Image myImage = ImageIO.read(f);
		    g.drawImage(myImage, getX(), getY(), getWidth(), getHeight(), null); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        getBorder().paintBorder(this, g, getX() + 5, getY() + 5, getWidth() - 10, getHeight() - 10);
        details.render(g);
        text.render(g);
        overview.render(g);
        System.out.println("Player");
        System.out.println(player.getKnownTopics());
        System.out.println("NPC");
        System.out.println(npc.getKnownTopics());
        System.out.println("Available");
        System.out.println(availableTopics);
    }
    
    @Override
    public String toString() {
        String s = "";
        for (Topic topic : topics) {
            s += "Player asked about: " + topic.getName() + "\n"; 
            s += npc.getName() + " responded with: " + npc.getResponse(topic) + "\n";
        }
        return s;
    }
}

class Scroller extends JScrollPane implements Menu{
    public Scroller(JLabel tp, CustomBorder cb) {
        super(tp);
        setBackground(new Color(1f,1f,1f,0f));
        setBorder(cb);
    }
    public void render(Graphics g) {
        super.paintComponent(g);
        //System.out.printf("x:%d y:%d w:%d h:%d\n",getX(), getY(), getWidth(), getHeight());
        // g.setColor(getForeground());
        // g.fillRect(getX(), getY(), getWidth(), getHeight());
        getBorder().paintBorder(this, g, getX() + 5, getY() + 5, getWidth() - 10, getHeight() - 10);
    }
}

class DetailPanel extends JPanel implements Menu {
    @Override
    public void render(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(1f,1f,1f,0f));
        getBorder().paintBorder(this, g, getX() + 5, getY() + 5, getWidth() - 10, getHeight() - 10);
    }
}