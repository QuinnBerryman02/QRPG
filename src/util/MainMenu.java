package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import java.awt.Font;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.MainWindow;

public class MainMenu extends Menu{
    private JPanel panel;
    private CustomButton newGame;
    private CustomButton loadGame;
    private CustomButton settings;
    private CustomButton exit;
    private int spacing = 20;
    private int startY = 300;
    private Color defaultColor = new Color(51,51,51,255);
    private Font defaultFont = new Font("default",Font.BOLD,12);
    boolean isGameOverMenu;

    public MainMenu() {
        isGameOverMenu = MainWindow.getModel().getPlayer().isDead();
        setUndecorated(true);
        setSize(new Dimension(MainWindow.getW()-10, MainWindow.getH()-55));
        setLocation(5, 50);
        setBackground(new Color(1f,1f,1f,0.01f));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(0,0,0,0));
        panel.setPreferredSize(getSize());
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(new Color(1f,1f,1f,0f));
        panel.setOpaque(false);
        
        newGame = new CustomButton("NEW GAME");
        loadGame = new CustomButton("LOAD GAME");
        settings = new CustomButton("SETTINGS");
        exit = new CustomButton("QUIT");
        

        newGame.addActionListener(e -> {
            if(isGameOverMenu) {
                MainWindow.newGamePrep();
            }
            MainWindow.newGame();
        });
        loadGame.addActionListener(e -> {
            MainWindow.loadGameManually();
        });
        settings.addActionListener(e -> {
            //TODO
        });
        exit.addActionListener(e -> {
            System.exit(0);
        });

        Title title = new Title(800, 200, "res/title.png");
        Title titleEng = new Title(600, 66, "res/titleEnglish.png");
        panel.add(Box.createRigidArea(new Dimension(1,spacing)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(1,spacing)));
        panel.add(titleEng);
        panel.add(Box.createRigidArea(new Dimension(1,spacing)));
        panel.add(newGame);
        panel.add(Box.createRigidArea(new Dimension(1,spacing)));
        panel.add(loadGame);
        panel.add(Box.createRigidArea(new Dimension(1,spacing)));
        panel.add(settings);
        panel.add(Box.createRigidArea(new Dimension(1,spacing)));
        panel.add(exit);
        add(panel);
        pack();
        setVisible(true);
        repaint();
    }

    public void update() {
        repaint();
    }

    class CustomButton extends JButton {
        private int w;
        private int h;
        public CustomButton(String text) {
            super(text);
            setFont(defaultFont.deriveFont(30f));
            setForeground(Color.WHITE);
            setBackground(new Color(0f,0f,0f,0f));
            setBorder(new EmptyBorder(0,0,0,0));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            int w = 200;
            int h = 50;
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setMinimumSize(new Dimension(w,h));
            this.w = w;
            this.h = h;
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(117, 204, 32));  //117 204 32 ?
            g.fillRoundRect(0, 0, w, h, 10, 10);
            super.paintComponent(g);
        }
    }

    class Title extends JLabel {
        private int w;
        private int h;
        private String s;
        public Title(int w, int h, String s) {
            super();
            setBackground(new Color(0f,0f,0f,0f));
            setBorder(new EmptyBorder(0,0,0,0));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setMinimumSize(new Dimension(w,h));
            this.w = w;
            this.h = h;
            this.s = s;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                File f = new File(s);
                if(s.equals("res/titleEnglish.png")) {
                   g.setColor(Color.WHITE); 
                   g.fillRoundRect(0, 0, w, h, 10, 10);
                }
                Image i = ImageIO.read(f);
                g.drawImage(i, 0, 0, w, h, null);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


