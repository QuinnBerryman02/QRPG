package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import main.MainWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;


public class GuildMenu extends Menu{
    private MainPanel panel;
    private Player player;
    private ArrayList<Quest> quests = new ArrayList<Quest>();
    private LabelPanel labelPanel;
    private QuestsPanel questPanel;
    private static final int NUMBER_OF_QUESTS = 4;

    public GuildMenu(Player player) {
        this.player = player;
        player.getController().clear();
        refreshQuests();
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
    public void refreshQuests() {
        quests.clear();
        switch(MainWindow.getModel().getStage()) {
            case BEGINING:
                if(player.getQuests().size()>=1) return;
                quests.add(new SlayerQuest(NPCLoader.getNPCByName("John"), 100, 5, Enemy.Type.SLIME));
                //quests.add(new SlayerQuest(NPCLoader.getNPCByName("John"), 1000, 1, Enemy.Type.SLIME));
                break;
            case VICTORY:
            case MIDGAME:
                Random r = new Random();
                for(int i=0;i<NUMBER_OF_QUESTS;i++) {
                    double which;
                    if(NPCLoader.getAliveNpcs().size()<=1) {
                        which = 0.5;
                    } else {
                        which = r.nextDouble();
                    }
                    NPC giver = NPCLoader.getAliveNpcs().get(r.nextInt(NPCLoader.getAliveNpcs().size()));
                    Quest quest;
                    if(which<0.999) {
                        int amount = r.nextInt(20) + 5;
                        Enemy.Type type;
                        boolean alreadyHave = false;
                        int attempts = 0;
                        do {
                            alreadyHave = false;
                            type = Enemy.Type.values()[r.nextInt(Enemy.Type.values().length)];
                            for (Quest q : player.getQuests()) {
                                if(q instanceof SlayerQuest && !q.isComplete()) {
                                    if(((SlayerQuest)q).getTypeOfEnemy().equals(type)) {
                                        alreadyHave = true;
                                    }
                                }
                            }
                        } while(alreadyHave && attempts<Enemy.Type.values().length);
                        if(attempts>=Enemy.Type.values().length) break;
                        int reward = amount * 20 + (r.nextInt(10) * 10);
                        
                        quest = new SlayerQuest(giver,reward,amount,type);
                    } else {
                        int reward = 50 + (r.nextInt(10) * 20);
                        NPC target = null;
                        boolean alreadyHave = false;
                        int attempts = 0;
                        do {
                            alreadyHave = false;
                            target = NPCLoader.getAliveNpcs().get(r.nextInt(NPCLoader.getAliveNpcs().size()));
                            for (Quest q : player.getQuests()) {
                                if(q instanceof AssassinationQuest) {
                                    if(((AssassinationQuest)q).getTarget().equals(target) && q.getQuestGiver().equals(giver)) {
                                        alreadyHave = true;
                                    }
                                }
                            }
                        } while((alreadyHave || giver.equals(target)) && attempts < NPCLoader.getAliveNpcs().size());
                        if(attempts>=NPCLoader.getAliveNpcs().size()) break;
                        quest = new AssassinationQuest(giver, reward, target);
                    }
                    quests.add(quest);
                }
                break;
            case ENDGAME:
                if(!player.hasFinalQuest()) {
                    quests.add(new AssassinationQuest(NPCLoader.getNPCByName("John"),1000,NPCLoader.getNPCByName("John")));
                }
                break;
            default:
                break;
        }
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
        private JLabel acceptButton;
        public QuestPane(Quest q, int w, int h, CustomBorder cb) {
            this.quest = q;
            setMinimumSize(new Dimension(w,h));
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            Dimension d = new Dimension(w/5,h);
            labels.add(new JLabel("TYPE", SwingConstants.CENTER));
            labels.add(new JLabel("REWARD", SwingConstants.CENTER));
            labels.add(new JLabel("QUEST GIVER", SwingConstants.CENTER));
            labels.add(new JLabel("DETAILS", SwingConstants.CENTER));
            labels.forEach(l -> {
                l.setBackground(new Color(1f,1f,0f,0f));
                l.setMinimumSize(d);
                l.setPreferredSize(d);
                l.setMaximumSize(d);
                add(l);
            });
            acceptButton = new JLabel("ACCEPT?",SwingConstants.CENTER);
            acceptButton.setBackground(new Color(1f,1f,0f,0f));
            acceptButton.setMinimumSize(d);
            acceptButton.setPreferredSize(d);
            acceptButton.setMaximumSize(d);
            acceptButton.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    player.getQuests().add(quest);
                    acceptButton.setText("ACCEPTED!");
                    acceptButton.removeMouseListener(this);
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}

            });
            add(acceptButton);
            refresh();
        }

        public void refresh() {
            labels.get(0).setText(quest.getClass().getSimpleName());
            labels.get(1).setText(String.valueOf(quest.getReward()));
            labels.get(2).setText(quest.getQuestGiver() != null ? quest.getQuestGiver().getName() : "NULL");
            labels.get(3).setText(quest.getDetails());
        }
    }
}


