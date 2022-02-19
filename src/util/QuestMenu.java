package util;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Adjustable;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseMotionListener;

import main.MainWindow;
import mvc.PlayerController;
import util.Spell.Aim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.io.File;
import java.io.IOException;


public class QuestMenu extends Menu{
    private MainPanel panel;
    private Player player;
    private ArrayList<Quest> quests = new ArrayList<Quest>();
    private LabelPanel labelPanel;
    private QuestsPanel questPanel;
    private Color defaultColor = new Color(51,51,51,255);
    private Font defaultFont = new Font("default",Font.BOLD,12);

    public QuestMenu(Player player) {
        this.player = player;
        player.getController().clear();
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
        private int w;
        private JScrollPane sp;
        public QuestsPanel(int w, int h, CustomBorder cb) {
            this.w = w;
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
            labels.get(4).setText(quest.isFailed() ? "FAILED" : quest.isComplete() ? "COMPLETED" : "IN PROGRESS");
        }
    }
}


