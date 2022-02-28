package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
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


public class StatueMenu extends Menu{
    private static final String TLOTL_QUOTE = "LET FREEDOM LIGHT YOUR WAY";
    private Stat[] stats;
    private MainPanel panel;
    private Player player;
    private QuotePanel quotePanel;
    private LabelPanel labelPanel;
    private StatsPanel statsPanel;

    public StatueMenu(Player player) {
        this.player = player;
        player.getController().clear();
        stats = player.getStats();
        player.setHealth(player.getMaxHealth());
        //play sound effect

        CustomBorder customBorder = new CustomBorder();
        
        setSize(new Dimension(MainWindow.getW() - 600, MainWindow.getH() - 400));
        setLocation(300, 200);
        

        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());

        quotePanel = new QuotePanel(getWidth(),getHeight()/7, customBorder);
        labelPanel = new LabelPanel(getWidth(),getHeight()/7,customBorder);
        statsPanel = new StatsPanel(getWidth(),getHeight()*5/7,customBorder);

        panel.add(quotePanel);
        panel.add(labelPanel);
        panel.add(statsPanel);
        
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

    class QuotePanel extends JPanel {
        private JLabel quote;
        public QuotePanel(int w, int h, CustomBorder cb) {
            super();
            setPreferredSize(new Dimension(w,h));
            setMinimumSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);
            quote = new JLabel(TLOTL_QUOTE, SwingConstants.CENTER);
            quote.setPreferredSize(new Dimension(w,h));
            quote.setMinimumSize(new Dimension(w,h));
            quote.setMaximumSize(new Dimension(w,h));
            quote.setBackground(new Color(1f,1f,0f,0f));
            quote.setBorder(cb);
            add(quote);
        }
    }

    class LabelPanel extends JPanel {
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        public LabelPanel(int w, int h, CustomBorder cb) {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setPreferredSize(new Dimension(w,h));
            setMinimumSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);
            labels.add(new JLabel("STAT", SwingConstants.CENTER));
            labels.add(new JLabel("CURRENT", SwingConstants.CENTER));
            labels.add(new JLabel("NEXT", SwingConstants.CENTER));
            labels.add(new JLabel("COST", SwingConstants.CENTER));
            labels.add(new JLabel("PURCHASE", SwingConstants.CENTER));
            labels.forEach(l -> {
                l.setBackground(new Color(1f,0f,1f,0f));
                l.setPreferredSize(new Dimension(w/5,h));
                l.setMinimumSize(new Dimension(w/5,h));
                l.setMaximumSize(new Dimension(w/5,h));
                add(l);
            });
            refresh();
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public void refresh() {
            labels.get(4).setText("GOLD: " + player.getGold());
        }
    }

    class StatsPanel extends JPanel {
        private ArrayList<StatPane> statPanes = new ArrayList<StatPane>();
        public StatsPanel(int w, int h, CustomBorder cb) {
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            for (Stat s : stats) {
                StatPane sp = new StatPane(s, w, h/stats.length, cb);
                add(sp);
                statPanes.add(sp);
            }
            setBackground(new Color(1f,1f,1f,0f));
            //sp.setBorder(new EmptyBorder(10, 0, 20, 0));
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }
    }


    class StatPane extends JPanel {
        private Stat stat;
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        public StatPane(Stat s, int w, int h, CustomBorder cb) {
            this.stat = s;
            setMinimumSize(new Dimension(w,h));
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            labels.add(new JLabel("STAT", SwingConstants.CENTER));
            labels.add(new JLabel("CURRENT", SwingConstants.CENTER));
            labels.add(new JLabel("NEXT", SwingConstants.CENTER));
            labels.add(new JLabel("COST", SwingConstants.CENTER));
            labels.add(new JLabel("PURCHASE", SwingConstants.CENTER));
            labels.forEach(l -> {
                l.setBackground(new Color(1f,1f,0f,0f));
                //Dimension d = new Dimension(l.getText().equals("DETAILS") ? w*6/10 : w/10,h);
                Dimension d = new Dimension(w/5,h);
                l.setMinimumSize(d);
                l.setPreferredSize(d);
                l.setMaximumSize(d);
                add(l);
            });
            labels.get(4).addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(stat.buyNext(player.getGold())) {
                        player.spendGold(stat.getCost());
                        stat.incrementCost();
                        refresh();
                        labelPanel.refresh();
                    }
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
            refresh();
        }

        public void refresh() {
            labels.get(0).setText(stat.getName());
            labels.get(1).setText(String.valueOf(stat.getValue()));
            labels.get(2).setText(String.valueOf(stat.getNextValue()));
            labels.get(3).setText(String.valueOf(stat.getCost()));
            labels.get(4).setText("BUY");
        }
    }
}




