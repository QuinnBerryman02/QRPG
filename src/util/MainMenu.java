package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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

    public MainMenu() {
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
            MainWindow.newGame();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        });
        loadGame.addActionListener(e -> {
            //TODO
        });
        settings.addActionListener(e -> {
            //TODO
        });
        exit.addActionListener(e -> {
            System.exit(0);
        });

        panel.add(Box.createRigidArea(new Dimension(1,startY)));
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
        public CustomButton(String text) {
            super(text);
            setFont(defaultFont.deriveFont(30f));
            setBackground(new Color(0f,0f,0f,0f));
            setBorder(new EmptyBorder(0,0,0,0));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }
}


