package util;

import java.awt.Graphics;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.border.AbstractBorder;

import main.MainWindow;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class Menu extends JFrame{
    public Menu() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.closeMenu(true);
            }
        });
    }
    public CustomBorder makeNewBorder() {
        return new CustomBorder();
    }
    public abstract void update();
}

class CustomBorder extends AbstractBorder {
    private Image img;

    public CustomBorder() {
        File source = new File("res/gui/RPG_GUI_v1.png");
        try {
            img = ImageIO.read(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        g.drawImage(img, x, y, x+15, y+15, 586, 130, 586+15, 130+15, null);
        g.drawImage(img, x+15, y, x+width-15, y+10, 612, 130, 612+48, 130+10, null);
        g.drawImage(img, x+width-15, y, x+width, y+15, 669, 130, 669+15, 130+15, null);

        g.drawImage(img, x+width-10, y+16, x+width, y+height-16, 674, 156, 674+10, 156+36, null);

        g.drawImage(img, x+width-15, y+height-15, x+width, y+height, 669, 201, 669+15, 201+15, null);
        g.drawImage(img, x+15, y+height-9, x+width-15, y+height, 612, 207, 612+48, 207+9, null);
        g.drawImage(img, x, y+height-15, x+15, y+height, 586, 201, 586+15, 201+15, null);

        g.drawImage(img, x, y+16, x+10, y+height-16, 586, 156, 586+10, 156+36, null);
    }

    @Override
    public boolean isBorderOpaque()
    {
        return true;
    }
}