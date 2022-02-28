package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import java.awt.Image;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Tileset {
    private int start_id;
    private String name;
    private int width;
    private int height;
    private Image image;
    private int tileWidth;
    private int tileHeight;
    private int tileCount;
    public Tileset(Element e, DocumentBuilder b) {
        start_id = Integer.parseInt(e.getAttribute("firstgid"));
        try {
            Document doc = b.parse("res/" + e.getAttribute("source"));
            doc.getDocumentElement().normalize();
            Element root = (Element)doc.getDocumentElement();
            name = root.getAttribute("name");
            tileWidth = Integer.parseInt(root.getAttribute("tilewidth"));
            tileHeight = Integer.parseInt(root.getAttribute("tileheight"));
            tileCount = Integer.parseInt(root.getAttribute("tilecount"));
            Element imageTag = (Element)root.getElementsByTagName("image").item(0);
            width = Integer.parseInt(imageTag.getAttribute("width"));
            height = Integer.parseInt(imageTag.getAttribute("height"));
            String source = "res" + imageTag.getAttribute("source").substring(2);
            image = ImageIO.read(new File(source));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
    public int getHeight() {
        return height;
    }
    public Image getImage() {
        return image;
    }
    public int getStart_id() {
        return start_id;
    }
    public int getWidth() {
        return width;
    }
    public int getTileWidth() {
        return tileWidth;
    }
    public int getTileHeight() {
        return tileHeight;
    }
    public int getTileCount() {
        return tileCount;
    }
    public int[] getTile(int id) {
        int relativeID = id - start_id;
        if(relativeID < 0 || relativeID >= tileCount) {
            throw new IllegalArgumentException("Tile ID is not from this tileset");
        }
        int sx1, sx2, sy1, sy2;
        int columns = width / tileWidth;
        sx1 = (relativeID % columns) * tileWidth;
        sy1 = (relativeID / columns) * tileHeight;
        sx2 = sx1 + tileWidth;
        sy2 = sy1 + tileHeight;
        return new int[] {sx1, sy1, sx2, sy2};
    }
    @Override
    public String toString() {
        return "[Name = " + name + 
        "], [Start_id = " + start_id +
        "], [Image = " + image +
        "], [tileCount = " + tileCount +
        "], [tileWidth = " + tileWidth +
        "], [tileHeight = " + tileHeight +
        "], [width = " + width +
        "], [height = " + height + "]";
    }

}