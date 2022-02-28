package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseMotionListener;

import main.MainWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.io.File;
import java.io.IOException;


public class SpellMenu extends Menu{
    private MainPanel panel;
    private ArrayList<Spell> spells = new ArrayList<Spell>();
    private Spell currentSpell;
    private LabelPanel labelPanel;
    private ChangePanels changePanels;
    private SpellPanel spellPanel;
    private AimPanel aimPanel;
    private RadiusPanel radiusPanel;
    private DamagePanel damagePanel;
    private ElementPanel elementPanel;
    private CostPanel costPanel;
    private Color defaultColor = new Color(51,51,51,255);
    private Font defaultFont = new Font("default",Font.BOLD,12);

    public SpellMenu(Player player) {
        player.getController().clear();
        spells = player.getSpells();
        currentSpell = player.getCurrentSpell();

        CustomBorder customBorder = new CustomBorder();
        
        setSize(new Dimension(MainWindow.getW() - 200, MainWindow.getH() - 300));
        setLocation(100, 150);
        

        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());

        labelPanel = new LabelPanel(getWidth(),getHeight()/10,customBorder);
        changePanels = new ChangePanels(getWidth(),getHeight()*9/10,customBorder);

        spellPanel = new SpellPanel(getWidth()/6,getHeight()*9/10, customBorder);
        aimPanel = new AimPanel(getWidth()/6,getHeight()*9/10, customBorder);
        radiusPanel = new RadiusPanel(getWidth()/6,getHeight()*9/10, customBorder);
        damagePanel = new DamagePanel(getWidth()/6,getHeight()*9/10, customBorder);
        elementPanel = new ElementPanel(getWidth()/6,getHeight()*9/10, customBorder);
        costPanel = new CostPanel(getWidth()/6,getHeight()*9/10, customBorder);

        panel.add(labelPanel);
        panel.add(changePanels);

        changePanels.add(spellPanel);
        changePanels.add(aimPanel);
        changePanels.add(radiusPanel);
        changePanels.add(damagePanel);
        changePanels.add(elementPanel);
        changePanels.add(costPanel);
        
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
        private JTextField spellNamer;
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        public LabelPanel(int w, int h, CustomBorder cb) {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);

            spellNamer = new JTextField(currentSpell.getName());
            spellNamer.setBackground(new Color(0f,1f,1f,0f));
            spellNamer.setPreferredSize(new Dimension(w/6,h));
            spellNamer.addActionListener(e -> {
                currentSpell.setName(spellNamer.getText());
                spellPanel.refresh();
            });
            spellNamer.setBorder(new EmptyBorder(0, 0, 0, 0));
            spellNamer.setHorizontalAlignment(SwingConstants.CENTER);
            spellNamer.setFont(defaultFont);
            spellNamer.setForeground(defaultColor);
            labels.add(new JLabel("AIMING", SwingConstants.CENTER));
            labels.add(new JLabel("RADIUS", SwingConstants.CENTER));
            labels.add(new JLabel("DAMAGE", SwingConstants.CENTER));
            labels.add(new JLabel("ELEMENT", SwingConstants.CENTER));
            labels.add(new JLabel("STATS", SwingConstants.CENTER));
            add(spellNamer);
            labels.forEach(l -> {
                l.setBackground(new Color(1f,0f,1f,0f));
                l.setPreferredSize(new Dimension(w/6,h));
                add(l);
            });
            refresh();
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public void refresh() {
            spellNamer.setText(currentSpell.getName());
        }
    }

    class ChangePanels extends JPanel {
        public ChangePanels(int w, int h, CustomBorder cb) {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    class SpellPanel extends JPanel {
        private JPanel buttonPanel;
        private ArrayList<SpellButton> buttons = new ArrayList<SpellButton>();
        private JScrollPane sp;
        public SpellPanel(int w, int h, CustomBorder cb) {
            buttonPanel = new JPanel();
            sp = new JScrollPane(buttonPanel);
            sp.setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
            for (Spell s : spells) {
                addSpell(s);
            }
            createNewButton();
            buttonPanel.setBackground(new Color(1f,1f,1f,0f));
            sp.setBackground(new Color(1f,1f,1f,0f));
            sp.getViewport().setBackground(new Color(1f,1f,1f,0f));
            //sp.setBorder(new EmptyBorder(10, 0, 20, 0));
            refresh();
            add(sp);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.printf("Class:%s,x:%d y:%d w:%d h:%d\n",getClass().getName(),getX(), getY(), getWidth(), getHeight());
        }

        public void addSpell(Spell s) {
            SpellButton button = new SpellButton(s);
            button.addActionListener(e -> {
                currentSpell = s;
                refreshButtons(button);
                aimPanel.refresh();
                elementPanel.refresh();
                damagePanel.refresh();
                labelPanel.refresh();
            });
            //label.setIcon(new ImageIcon());
            button.setBorder(new EmptyBorder(0,0,0,0));
            button.setBackground(s.equals(currentSpell) ? new Color(1f,1f,1f,0.3f) : new Color(1f,1f,1f,0f));
            buttonPanel.add(button);
            buttons.add(button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setAlignmentY(Component.CENTER_ALIGNMENT);
        }

        public void createNewButton() {
            SpellButton button = new SpellButton(null);
            button.addActionListener(e -> {
                currentSpell = new Spell();
                button.setSpell(currentSpell);
                spells.add(currentSpell);
                refreshButtons(button);
                aimPanel.refresh();
                elementPanel.refresh();
                damagePanel.refresh();
                labelPanel.refresh();
                createNewButton();
                spellPanel.refresh();
                button.removeActionListener(button.getActionListeners()[0]);
                button.addActionListener(t -> {
                    currentSpell = button.getSpell();
                    refreshButtons(button);
                    aimPanel.refresh();
                    elementPanel.refresh();
                    damagePanel.refresh();
                    labelPanel.refresh();
                });
            });
            //label.setIcon(new ImageIcon());
            button.setBorder(new EmptyBorder(0,0,0,0));
            button.setBackground(new Color(1f,1f,1f,0f));
            buttonPanel.add(button);
            buttons.add(button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setAlignmentY(Component.CENTER_ALIGNMENT);
        };

        public void refreshButtons(JButton on) {
            for (SpellButton b : buttons) {
                b.setBackground(b.equals(on) ? new Color(1f,1f,1f,0.3f) : new Color(1f,1f,1f,0f));
            }
        }

        public void refresh() {
            for (SpellButton b : buttons) {
                b.refresh();
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

    class SpellButton extends JButton {
        private Spell spell;
        public SpellButton(Spell s) {
            this.spell = s;
        }
        public void refresh() {
            if(spell == null) {
                setText("New Spell");
            } else if(spell.getName().trim().equals("")){
                setText("Empty");
            } else {
                setText(spell.getName());
            }
        }

        public void setSpell(Spell spell) {
            this.spell = spell;
        }

        public Spell getSpell() {
            return spell;
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
            refresh();
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
        private JSlider slider;
        public DamagePanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            slider = new JSlider(JSlider.VERTICAL ,1,25,(int)currentSpell.getDamage());
            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setSnapToTicks(true);
            slider.addChangeListener(e -> {
                JSlider js = (JSlider)e.getSource();
                int newDamage = js.getValue();
                currentSpell.setDamage(newDamage);
            });
            slider.setPreferredSize(getPreferredSize());
            slider.setBackground(new Color(1f,1f,1f,0f));
            add(slider);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public void refresh() {
            slider.setValue((int)currentSpell.getDamage());
        }
    }
    class ElementPanel extends JPanel {
        private DefaultListModel<String> model = new DefaultListModel<>();
        private JList<String> elements;
        private SelectionListener selectionListener = new SelectionListener();
        public ElementPanel(int w, int h, CustomBorder cb) {
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            for(int i=0;i<Projectile.Type.values().length;i++) {
                model.addElement(new String(Projectile.Type.values()[i].name()));
            }
            elements = new JList<>(model);
            elements.setBackground(new Color(1f,1f,1f,0f));
            elements.setSelectedIndex(currentSpell.getElement().ordinal());
            elements.addListSelectionListener(selectionListener);
            add(elements);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public void refresh() {
            elements.removeListSelectionListener(selectionListener);
            elements.setSelectedIndex(currentSpell.getElement().ordinal());
            elements.addListSelectionListener(selectionListener);
        }

        class SelectionListener implements ListSelectionListener{
            @Override
            public void valueChanged(ListSelectionEvent e) {
                JList<String> lsm = (JList)e.getSource();
                currentSpell.setElement(Projectile.Type.values()[lsm.getSelectedIndex()]);
            }
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
            g.setColor(defaultColor);
            g.setFont(defaultFont);
            char[] c = ("RADIUS: " + currentSpell.getRadius()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+16);
            c = ("TOTAL DAMAGE: " + (int)currentSpell.getDamage()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+32);
            c = ("CAST DELAY: " + currentSpell.delayOfElement()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+48);
            currentSpell.calculateCost();
            c = ("MANA COST: " + currentSpell.getManaCost()).toCharArray();
            g.drawChars(c, 0, c.length, x, y+64);
        }
    }
}




