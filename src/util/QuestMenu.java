package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import java.awt.Image;
import java.awt.Dimension;

import main.MainWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;


public class QuestMenu extends Menu{
    private MainPanel panel;
    private ArrayList<Quest> quests = new ArrayList<Quest>();
    private LabelPanel labelPanel;
    private QuestsPanel questPanel;

    public QuestMenu(Player player) {
        player.getController().clear();
        player.sortQuests();
        quests = player.getQuests();

        CustomBorder customBorder = new CustomBorder();
        
        setSize(new Dimension(MainWindow.getW() - 200, MainWindow.getH() - 300));
        setLocation(100, 150);
        

        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());
        
        labelPanel = new LabelPanel(getWidth(),getHeight()/10,customBorder);
        questPanel = new QuestsPanel(getWidth(),getHeight()*9/10,customBorder);
        

        panel.add(labelPanel);
        panel.add(questPanel);
        add(panel);
        pack();
        setVisible(true);
        repaint();
    }

    public void update() {
        repaint();
    }

    class MainPanel extends JPanel {
        public MainPanel() {
            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
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
        }
    }

    class LabelPanel extends JPanel {
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        public LabelPanel(int w, int h, CustomBorder cb) {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setMinimumSize(new Dimension(w,h));
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);
            labels.add(new JLabel("TYPE", SwingConstants.CENTER));
            labels.add(new JLabel("REWARD", SwingConstants.CENTER));
            labels.add(new JLabel("QUEST GIVER", SwingConstants.CENTER));
            labels.add(new JLabel("DETAILS", SwingConstants.CENTER));
            labels.add(new JLabel("STATUS", SwingConstants.CENTER));
            labels.forEach(l -> {
                l.setBackground(new Color(1f,1f,0f,0f));
                //Dimension d = new Dimension(l.getText().equals("DETAILS") ? w*6/10 : w/10,h);
                Dimension d = new Dimension(w/5,h);
                l.setMinimumSize(d);
                l.setPreferredSize(d);
                l.setMaximumSize(d);
                add(l);
            });
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    class QuestsPanel extends JPanel {
        private JPanel tempPanel = new JPanel();
        private ArrayList<QuestPane> questPanes = new ArrayList<QuestPane>();
        private JScrollPane sp;
        public QuestsPanel(int w, int h, CustomBorder cb) {
            sp = new JScrollPane(tempPanel);
            sp.setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.Y_AXIS));
            for (Quest q : quests) {
                QuestPane questPane = new QuestPane(q, w, h/4, cb);
                tempPanel.add(questPane);
                questPanes.add(questPane);
            }
            tempPanel.setBackground(new Color(1f,1f,1f,0f));
            sp.setBackground(new Color(1f,1f,1f,0f));
            sp.getViewport().setBackground(new Color(1f,1f,1f,0f));
            sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            //sp.setBorder(new EmptyBorder(10, 0, 20, 0));
            add(sp);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }
    }


    class QuestPane extends JPanel {
        private Quest quest;
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        public QuestPane(Quest q, int w, int h, CustomBorder cb) {
            this.quest = q;
            setMinimumSize(new Dimension(w,h));
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            labels.add(new JLabel("TYPE", SwingConstants.CENTER));
            labels.add(new JLabel("REWARD", SwingConstants.CENTER));
            labels.add(new JLabel("QUEST GIVER", SwingConstants.CENTER));
            labels.add(new JLabel("DETAILS", SwingConstants.CENTER));
            labels.add(new JLabel("STATUS", SwingConstants.CENTER));
            labels.forEach(l -> {
                l.setBackground(new Color(1f,1f,0f,0f));
                //Dimension d = new Dimension(l.getText().equals("DETAILS") ? w*6/10 : w/10,h);
                Dimension d = new Dimension(w/5,h);
                l.setMinimumSize(d);
                l.setPreferredSize(d);
                l.setMaximumSize(d);
                add(l);
            });
            refresh();
        }

        public void refresh() {
            labels.get(0).setText(quest.getClass().getSimpleName());
            labels.get(1).setText(String.valueOf(quest.getReward()));
            labels.get(2).setText(quest.getQuestGiver() != null ? quest.getQuestGiver().getName() : "NULL");
            labels.get(3).setText(quest.getDetails());
            int r = quest.getQuestRelevancy();
            labels.get(4).setText(r==1 ? "COMPLETED" : r==2 ? "IN PROGRESS" : r==3 ? "REWARDED" : "FAILED");
        }
    }
}


