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
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
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


public class SpellMenu extends Menu{
    private MainPanel panel;
    private Player player;
    private ArrayList<Spell> spells = new ArrayList<Spell>();
    private Spell currentSpell;
    private SpellPanel spellPanel;
    private AimPanel aimPanel;
    private RadiusPanel radiusPanel;
    private DamagePanel damagePanel;
    private ElementPanel elementPanel;
    private CastDelayPanel castDelayPanel; 
    private CostPanel costPanel;

    public SpellMenu(Player player) {
        this.player = player;
        player.getController().clear();
        spells = player.getSpells();
        currentSpell = (spells.size()>0) ? spells.get(0) : null; 

        CustomBorder customBorder = new CustomBorder();
        
        setSize(new Dimension(MainWindow.getW() - 200, MainWindow.getH() - 300));
        setLocation(100, 150);

        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());


        spellPanel = new SpellPanel(getWidth()/7,getHeight(), customBorder);
        aimPanel = new AimPanel(getWidth()/7,getHeight(), customBorder);
        radiusPanel = new RadiusPanel(getWidth()/7,getHeight(), customBorder);
        damagePanel = new DamagePanel(getWidth()/7,getHeight(), customBorder);
        elementPanel = new ElementPanel(getWidth()/7,getHeight(), customBorder);
        castDelayPanel = new CastDelayPanel(getWidth()/7,getHeight(), customBorder);
        costPanel = new CostPanel(getWidth()/7,getHeight(), customBorder);

        panel.add(spellPanel);
        panel.add(aimPanel);
        panel.add(radiusPanel);
        panel.add(damagePanel);
        panel.add(elementPanel);
        panel.add(castDelayPanel);
        panel.add(costPanel);
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
        }
    }

    class SpellPanel extends JPanel {
        //TODO add ability to name spells
        private JPanel buttonPanel;
        private ArrayList<JButton> buttons = new ArrayList<JButton>();
        private int w;
        private JScrollPane sp;
        public SpellPanel(int w, int h, CustomBorder cb) {
            this.w = w;
            buttonPanel = new JPanel();
            sp = new JScrollPane(buttonPanel);
            sp.setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
            for (Spell s : spells) {
                addSpell(s);
            }
            buttonPanel.setBackground(new Color(1f,1f,1f,0f));
            sp.setBackground(new Color(1f,1f,1f,0f));
            sp.getViewport().setBackground(new Color(1f,1f,1f,0f));
            //sp.setBorder(new EmptyBorder(10, 0, 20, 0));
            add(sp);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }

        public void addSpell(Spell s) {
            JButton button = new JButton(s.getName() != null ? s.getName() : "Empty");
            button.addChangeListener(e -> {
                currentSpell = s;
                refreshButtons(button);
                aimPanel.refresh();
                elementPanel.refresh();
            });
            //label.setIcon(new ImageIcon());
            button.setBorder(new EmptyBorder(0,0,0,0));
            button.setBackground(s.equals(currentSpell) ? new Color(1f,1f,1f,0.3f) : new Color(1f,1f,1f,0f));
            buttonPanel.add(button);
            buttons.add(button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setAlignmentY(Component.CENTER_ALIGNMENT);
        }

        public void refreshButtons(JButton on) {
            for (JButton b : buttons) {
                b.setBackground(b.equals(on) ? new Color(1f,1f,1f,0.3f) : new Color(1f,1f,1f,0f));
            }
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

    class AimPanel extends JPanel {
        private DefaultListModel<String> model = new DefaultListModel<>();
        private JList<String> aims;
        public AimPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            for(int i=0;i<Spell.Aim.values().length;i++) {
                model.addElement(new String(Spell.Aim.values()[i].name()));
            }
            aims = new JList<>(model);
            aims.setBackground(new Color(1f,1f,1f,0f));
            aims.addListSelectionListener(e -> {
                JList<String> lsm = (JList)e.getSource();
                currentSpell.setAim(Spell.Aim.values()[lsm.getSelectedIndex()]);
            });
            add(aims);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public void refresh() {
            aims.setSelectedIndex(currentSpell.getAim().ordinal());
        }
    }
    class RadiusPanel extends JPanel {
        private Point3f centre;
        private int scale = 100;
        public RadiusPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            centre = new Point3f(getX() + w/2f, getY() + h/2f,0f);
            RadiusChanger rc = new RadiusChanger();
            addMouseMotionListener(rc);
        }

        public Point3f getCentre() {
            return centre;
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int radius = (int)(currentSpell.getRadius() * scale);
            int x = (int)centre.getX();
            int y = (int)centre.getY();
            g.setColor(currentSpell.getColor());
            g.fillOval(x-radius, y-radius, 2* radius, 2* radius);
        }

        public int getScale() {
            return scale;
        }

        
    }

    class RadiusChanger implements MouseMotionListener {
        public void mouseDragged(MouseEvent e) {
            float dx = e.getX() - radiusPanel.getCentre().getX();
            float dy = e.getY() - radiusPanel.getCentre().getY();
            float radius = (float)Math.sqrt(dx*dx + dy*dy);
            float newRadius = radius / radiusPanel.getScale();
            currentSpell.setRadius(newRadius);
        }
        public void mouseMoved(MouseEvent e) {}
    }
    class DamagePanel extends JPanel {
        //TODO implement damage panel
        public DamagePanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
    class ElementPanel extends JPanel {
        private DefaultListModel<String> model = new DefaultListModel<>();
        private JList<String> elements;
        public ElementPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            for(int i=0;i<Projectile.Type.values().length;i++) {
                model.addElement(new String(Projectile.Type.values()[i].name()));
            }
            elements = new JList<>(model);
            elements.setBackground(new Color(1f,1f,1f,0f));
            elements.addListSelectionListener(e -> {
                JList<String> lsm = (JList)e.getSource();
                currentSpell.setType(Projectile.Type.values()[lsm.getSelectedIndex()]);
            });
            add(elements);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public void refresh() {
            elements.setSelectedIndex(currentSpell.getType().ordinal());
        }
    }
    class CastDelayPanel extends JPanel {
        //TODO implement cast delay panel
        public CastDelayPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
    class CostPanel extends JPanel {
        public CostPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 10;
            int y = 10;
            g.setColor(new Color(0f,0f,0f,1f));
            g.setFont(new Font("Details",Font.BOLD,16));
            char[] c = ("Radius: " + currentSpell.getRadius()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+16);
            c = ("Total Damage: " + currentSpell.getDamage()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+32);
            currentSpell.calculateCost();
            c = ("Mana Cost: " + currentSpell.getManaCost()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+48);
        }
    }
}




