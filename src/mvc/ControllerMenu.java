package mvc;

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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Adjustable;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseMotionListener;

import main.MainWindow;
import util.Menu;
import util.Player;
import util.Quest;
import util.Spell.Aim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.io.File;
import java.io.IOException;


public class ControllerMenu extends Menu{
    private MainPanel panel;
    private Player player;
    private ModePanel modePanel;
    private PlayerController controller;
    private ButtonPanel buttonPanel;
    private TogglePanel togglePanel;
    private Color defaultColor = new Color(51,51,51,255);
    private Font defaultFont = new Font("default",Font.BOLD,12);

    public ControllerMenu(Player player) {
        this.player = player;
        player.getController().clear();
        controller = (PlayerController)player.getController();
        
        Border customBorder = makeNewBorder();
        
        setSize(new Dimension(MainWindow.getW() - 200, MainWindow.getH() - 300));
        setLocation(100, 150);
        

        panel = new MainPanel();
        panel.setBorder(customBorder);
        panel.setPreferredSize(getSize());

        modePanel = new ModePanel(getWidth(),getHeight()/10,customBorder);
        buttonPanel = new ButtonPanel(getWidth(),getHeight()*7/10,customBorder);
        togglePanel = new TogglePanel(getWidth(),getHeight()*2/10,customBorder);

        panel.add(modePanel);
        panel.add(buttonPanel);
        panel.add(togglePanel);
        
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

    class ModePanel extends JPanel {
        private JLabel info;
        private JTextField controllerName;
        public ModePanel(int w, int h, Border cb) {
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,0f,0f));
            setBorder(cb);

            controllerName = new JTextField(controller.getControllerName());
            controllerName.setBackground(new Color(0f,1f,1f,0f));
            controllerName.setPreferredSize(new Dimension(w/2,h));
            controllerName.setBorder(new EmptyBorder(0, 0, 0, 0));
            controllerName.setHorizontalAlignment(SwingConstants.CENTER);
            controllerName.setFont(defaultFont);
            controllerName.setForeground(defaultColor);
            controllerName.addActionListener(e -> {
                controller.setControllerName(controllerName.getText());
                controller.loadController();
                controller.reEstablishComponents();
                info.setText(controller.getModeInfo());
            });
            info = new JLabel(controller.getModeInfo(), SwingConstants.CENTER);
            info.setBackground(new Color(1f,0f,1f,0f));
            info.setPreferredSize(new Dimension(w/2,h));
            add(info);
            add(controllerName);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    class ButtonPanel extends JPanel {
        private JPanel tempPanel = new JPanel();
        private ArrayList<ButtonPane> buttonPanes = new ArrayList<ButtonPane>();
        private int w;
        private JScrollPane sp;
        public ButtonPanel(int w, int h, Border cb) {
            this.w = w;
            sp = new JScrollPane(tempPanel);
            sp.setPreferredSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.Y_AXIS));
            for (Button b : controller.getButtonSet()) {
                ButtonPane buttonPane = new ButtonPane(b, w, h/6, cb);
                tempPanel.add(buttonPane);
                buttonPanes.add(buttonPane);
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


    class ButtonPane extends JPanel {
        private Button button;
        private JLabel name;
        private JLabel keyboard;
        private JLabel gameController;
        private ArrayList<JLabel> labels = new ArrayList<JLabel>();
        public ButtonPane(Button b, int w, int h, Border cb) {
            this.button = b;
            setMinimumSize(new Dimension(w,h));
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            Dimension d = new Dimension(w/3,h);
            name = new JLabel("NAME", SwingConstants.CENTER);
            keyboard = new JLabel("KEYBOARD", SwingConstants.CENTER);
            gameController = new JLabel("CONTROLLER", SwingConstants.CENTER);
            labels.add(name);
            labels.add(keyboard);
            labels.add(gameController);
            labels.forEach(l -> {
                l.setBackground(new Color(1f,1f,0f,0f));
                l.setMinimumSize(d);
                l.setPreferredSize(d);
                l.setMaximumSize(d);
                add(l);
            });
            name.setText(button.getName());
            keyboard.setText(KeyEvent.getKeyText(button.getKey()));
            gameController.setText(button.getButtonName());
            keyboard.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(controller.isControllerMode()) return;
                    keyboard.setText("PRESS A KEY");
                    controller.listenKeyboard(b);
                    refresh();
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
            gameController.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(!controller.isControllerMode()) return;
                    gameController.setText("PRESS A BUTTON");
                    controller.listenController(b);
                    refresh();
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
            name.setText(button.getName());
            keyboard.setText(KeyEvent.getKeyText(button.getKey()));
            gameController.setText(button.getButtonName());
        }
    }

    class TogglePanel extends JPanel {
        public TogglePanel(int w, int h, Border cb) {
            setMinimumSize(new Dimension(w,h));
            setPreferredSize(new Dimension(w,h));
            setMaximumSize(new Dimension(w,h));
            setBackground(new Color(1f,1f,1f,0f));
            setBorder(cb);
        }
    }
}




