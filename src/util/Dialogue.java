package util;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Adjustable;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

import main.MainWindow;
import mvc.PlayerController;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

public class Dialogue extends Menu {
    private MainPanel panel;
    private Player player;
    private NPC npc; 
    private ArrayList<TopicResponse> topics = new ArrayList<TopicResponse>();
    private ArrayList<Topic> availableTopics;
    private DetailPanel details;
    private TopicResponsePanel text;
    private TopicPanel overview;

    public Dialogue(Player player, NPC npc) {
        this.player = player;
        player.getController().clear();
        this.npc = npc;
        Topic t = Topic.getTopic("Introduction");
        topics.add(new TopicResponse(t, npc.getResponse(t)));
        Topic q = Topic.getTopic("Quest");
        availableTopics = player.findCommonTopics(npc);
        availableTopics.add(q);

        CustomBorder customBorder = new CustomBorder();
        
        setSize(new Dimension(MainWindow.getW() - 200, MainWindow.getH() - 200));
        setLocation(100, 100);

        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());

        details = new DetailPanel(getWidth()/5,getHeight(), customBorder);
        text = new TopicResponsePanel(getWidth()*3/5,getHeight(), customBorder);
        overview = new TopicPanel(getWidth()/5,getHeight(), customBorder);

        panel.add(details);
        panel.add(text);
        panel.add(overview);
        add(panel);
        pack();
        setVisible(true);
        repaint();
    }

    public void update() {
        //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        repaint();
    }

    @Override
    public String toString() {
        String s = "";
        for (TopicResponse tr : topics) {
            s += "Player asked about: " + tr.getTopic().getName() + "\n"; 
            s += npc.getName() + " responded with: " + tr.getResponse().getRaw() + "\n";
        }
        return s;
    }

    class MainPanel extends JPanel {
        public MainPanel() {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setBackground(new Color(1f,1f,1f,0f));
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                File f = new File("res/gui/paperBackground.png");
                Image myImage = ImageIO.read(f);
                g.drawImage(myImage, getX(), getY(), getWidth(), getHeight(), null); 
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
            // System.out.println("Player");
            // System.out.println(player.getKnownTopics());
            // System.out.println("NPC");
            // System.out.println(npc.getKnownTopics());
            // System.out.println("Available");
            // System.out.println(availableTopics);
        }
    }

    class DetailPanel extends JPanel {
        private static final int SCALE = 2;
        public DetailPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int[] coords = npc.getSkin().getFaceCoords();
            int x = getX() + 10;
            int y = getY() + 10;
            try {
                File f = new File("res/sprites/faces_transparent.png");
                Image myImage = ImageIO.read(f);
                g.drawImage(myImage, x, y, x+coords[0]*4,y+coords[1]*4,coords[2],coords[3],coords[4],coords[5], null); 
            
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.setColor(new Color(0f,0f,0f,1f));
            g.setFont(new Font("Details",Font.BOLD,20));
            char[] c = ("Name: " + npc.getName()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+48*4+20);
            c = ("Class: " + npc.getSkin().getName()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+48*4+40);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }
    }
    
    class TopicResponsePanel extends JPanel {
        private JPanel labelPanel;
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        private int w;
        private JScrollPane sp;
        public TopicResponsePanel(int w, int h, CustomBorder cb) {
            this.w = w;
            labelPanel = new JPanel();
            sp = new JScrollPane(labelPanel);
            sp.setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.Y_AXIS));
            for (TopicResponse tr : topics) {
                addTopicResponse(tr);
            }
            labelPanel.setBackground(new Color(1f,1f,1f,0f));
            sp.setBackground(new Color(1f,1f,1f,0f));
            sp.getViewport().setBackground(new Color(1f,1f,1f,0f));
            sp.setBorder(new EmptyBorder(10, 0, 20, 0));
            add(sp);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }

        public void addTopicResponse(TopicResponse tr) {
            String s = tr.getTopic().getName() + "<br/>" + tr.getResponse().getText(npc.getKnownTopics());
            JLabel label = new JLabel("<html>" + s + "</html>");
            labelPanel.add(label);
            labels.add(label);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setBorder(new EmptyBorder(0, 10, 0, 0));
        }

        public void bottomScroll() {
            JScrollBar vsb = sp.getVerticalScrollBar();
            vsb.addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    vsb.removeAdjustmentListener(this);
                }
            });
        }
    }
    
    class TopicPanel extends JPanel {
        private JPanel buttonPanel;
        private ArrayList<TopicButton> buttons = new ArrayList<TopicButton>();
        private int w;
        public TopicPanel(int w, int h, CustomBorder cb) {
            this.w = w;
            buttonPanel = new JPanel();
            JScrollPane sp = new JScrollPane(buttonPanel);
            sp.setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
            availableTopics.forEach(t -> addTopic(t));
            buttonPanel.setBackground(new Color(1f,1f,1f,0f));
            sp.setBackground(new Color(1f,1f,1f,0f));
            sp.getViewport().setBackground(new Color(1f,1f,1f,0f));
            add(sp);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }

        public void addTopic(Topic topic) {
            TopicButton tb = new TopicButton(topic);
            buttonPanel.add(tb);
            buttons.add(tb);
            tb.setPreferredSize(new Dimension(w - 20,20));
            tb.setAlignmentX(Component.CENTER_ALIGNMENT);
            tb.setBackground(new Color(1f,1f,1f,0f));
            tb.setBorder(new EmptyBorder(0,0,0,0));
        }
    }

    class TopicButton extends JButton {
        private Topic topic;
        public TopicButton(Topic topic) {
            this.topic = topic;
            setText(topic.getName());
            addActionListener((event -> {
                System.out.println("clicked on: " + getText());
                Response response;
                if(topic.getName().equals("Quest")) {
                    player.sortQuests();
                    Quest q1 = player.findFirstQuestByPredicate(q -> {
                        return q.getQuestGiver().equals(npc);
                    });    
                    int relevancy;              
                    if(q1 == null) {
                        relevancy = 0;
                    } else {
                        relevancy = q1.getQuestRelevancy();
                    }
                    String questResponse = "";
                    switch (relevancy) {
                        case 1:
                            questResponse = "Thanks for completing the quest, I'll pay you your reward now. What was it again? Ah yes... " + q1.getReward() + "gold.";
                            player.setGold(q1.getReward());
                            q1.setRewardCollected(true);
                            break;
                        case 2:
                            questResponse = "Thanks for taking on the quest, I hope you can complete it.";
                            break;
                        case 3:
                            questResponse = "I've already paid you for that quest...";
                            break;
                        case 4:
                            questResponse = "Worthless Adventurer... Can't even complete a simple quest like that.";
                            break;
                        default:
                            questResponse = "I haven't given you a quest yet.";
                            break;
                    }
                    response = new Response(questResponse);
                } else {
                    response = npc.getResponse(topic);
                }
                ArrayList<Topic> newTopics = response.findReferencedTopics();
                TopicResponse tr = new TopicResponse(topic, response);
                topics.add(tr);
                newTopics.forEach(t -> {
                    if(!availableTopics.contains(t)) {
                        player.getKnownTopics().add(t);
                        if(npc.getKnownTopics().contains(t)) {
                            availableTopics.add(t);
                            getOverview().addTopic(t);
                        }
                    }
                });
                getTextPanel().addTopicResponse(tr);
                getTextPanel().bottomScroll();
                pack();
                repaint();
            }));
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Topic:%s,x:%d y:%d w:%d h:%d\n",topic.toString(),getX(), getY(), getWidth(), getHeight());
        }
    }

    public TopicResponsePanel getTextPanel() {
        return text;
    }

    public TopicPanel getOverview() {
        return overview;
    }

    public DetailPanel getDetails() {
        return details;
    }
}





