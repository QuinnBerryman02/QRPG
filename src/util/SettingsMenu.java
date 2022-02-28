package util;

import javax.imageio.ImageIO;
import javax.swing.Box;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

public class SettingsMenu extends Menu {
    private MainPanel panel;
    private LabelPanel labelPanel;
    private VolumePanel volumePanel;
    private AudioManager am = MainWindow.getAudioManager();
    private Color defaultColor = new Color(51,51,51,255);
    private Font defaultFont = new Font("default",Font.BOLD,12);

    public SettingsMenu() {
        MainWindow.getModel().getPlayer().getController().clear();

        CustomBorder customBorder = new CustomBorder();
        int w = MainWindow.getW();
        int h = MainWindow.getH();
        setBounds(w/4,h*3/8,w/2,h/4);
        
        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());

        labelPanel = new LabelPanel(getWidth(),getHeight()*1/5,customBorder);
        volumePanel = new VolumePanel(getWidth(),getHeight()*4/5,customBorder);
        labelPanel.setAlignmentX(0.5f);
        volumePanel.setAlignmentX(0.5f);

        panel.add(labelPanel);
        panel.add(volumePanel);

        add(panel);
        pack();
        revalidate();
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
        public LabelPanel(int w, int h, CustomBorder cb) {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setPreferredSize(new Dimension(w,h));
            setMinimumSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);
            Dimension d = new Dimension(w/2,h);
            JLabel save = new JLabel("SAVE", SwingConstants.CENTER);
            save.setPreferredSize(d);
            save.setMinimumSize(d);
            save.setMaximumSize(d);
            save.setBackground(new Color(1f,1f,0f,0f));
            add(save);
            JLabel load = new JLabel("LOAD", SwingConstants.CENTER);
            load.setBackground(new Color(1f,1f,0f,0f));
            load.setPreferredSize(d);
            load.setMinimumSize(d);
            load.setMaximumSize(d);
            add(load);
            //TODO add mouselisteners
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    class VolumePanel extends JPanel {
        JSlider masterSlider;
        JSlider musicSlider;
        JSlider soundSlider;
        public VolumePanel(int w, int h, CustomBorder cb) {
            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(w,h));
            setMinimumSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);
            Dimension d = new Dimension(w,h/6);
            JLabel master = new JLabel("MASTER", SwingConstants.LEFT);
            master.setBackground(new Color(1f,1f,0f,0f));
            add(master);
            JLabel music = new JLabel("MUSIC", SwingConstants.LEFT);
            music.setBackground(new Color(1f,1f,0f,0f));
            add(music);
            JLabel sound = new JLabel("SOUND", SwingConstants.LEFT);
            sound.setBackground(new Color(1f,1f,0f,0f));
            add(sound);
            masterSlider = new JSlider(0,100,50);
            masterSlider.setSnapToTicks(true);
            masterSlider.setMajorTickSpacing(20);
            masterSlider.setMinorTickSpacing(1);
            masterSlider.setBackground(new Color(1f,1f,0f,0f));

            musicSlider = new JSlider(0,101,50);
            musicSlider.setSnapToTicks(true);
            musicSlider.setMajorTickSpacing(20);
            musicSlider.setMinorTickSpacing(1);
            musicSlider.setBackground(new Color(1f,1f,0f,0f));

            soundSlider = new JSlider(0,102,50);
            soundSlider.setSnapToTicks(true);
            soundSlider.setMajorTickSpacing(20);
            soundSlider.setMinorTickSpacing(1);
            soundSlider.setBackground(new Color(1f,1f,0f,0f));
            Listener l = new Listener();
            masterSlider.addChangeListener(l);
            musicSlider.addChangeListener(l);
            soundSlider.addChangeListener(l);
            add(Box.createRigidArea(new Dimension(1,20)));
            add(master);
            add(masterSlider);
            add(music);
            add(musicSlider);
            add(sound);
            add(soundSlider);
            refresh();
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
        
        public void refresh() {
            masterSlider.setValue((int)(am.getMasterVolume()*100));
            musicSlider.setValue((int)(am.getMusicVolume()*100));
            soundSlider.setValue((int)(am.getSoundVolume()*100));
        }

        class Listener implements ChangeListener {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider src = (JSlider)(e.getSource());
                switch(src.getMaximum()) {
                    case 100:
                        am.setMasterVolume(src.getValue()/100f);
                        break;
                    case 101:
                        am.setMusicVolume(src.getValue()/100f);
                        break;
                    case 102:
                        am.setSoundVolume(src.getValue()/100f);
                        break;
                }
            }
        }
    }
}
