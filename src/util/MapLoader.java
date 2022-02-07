package util;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class MapLoader {
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private ArrayList<Tileset> tilesets = new ArrayList<Tileset>();

    public MapLoader(File file) {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static void main(String[] args) {
        File file = new File("res/map.tmx");
        MapLoader mapLoader = new MapLoader(file);
        ArrayList<Chunk> chunks = mapLoader.getChunksByCoordinate(-32, -64);
        chunks.forEach((c) -> System.out.println(((Chunk)c).getData().getTextContent()));
        mapLoader.printChunksByCoordinate(-32, -64);
        System.out.println(chunks.get(3).getTile(15, 15));
        System.out.println(chunks.get(2).getXOffset());
        System.out.println(chunks.get(3).getXOffset());


        mapLoader.loadTilesets();
        for (Tileset t : mapLoader.tilesets) {
            System.out.println(t);
        }
    }



    public void printChunksByCoordinate(int x, int y) {
        NodeList chunks = document.getElementsByTagName("chunk");
        for (int i=0;i<chunks.getLength();i++) {
            Element chunk = (Element)chunks.item(i);
            Element layer = (Element)chunk.getParentNode().getParentNode();   
            if(chunk.getAttribute("x").equals(String.valueOf(x)) && chunk.getAttribute("y").equals(String.valueOf(y))) {
                System.out.print("x=" + chunk.getAttribute("x") + " y=" + chunk.getAttribute("y") + " width=" + chunk.getAttribute("width") + " height=" + chunk.getAttribute("height"));
                System.out.println(" from layer : " + layer.getAttribute("name"));
            }
        }
    }

    public ArrayList<Chunk> getChunksByCoordinate(int x, int y) {
        NodeList chunks = document.getElementsByTagName("chunk");
        ArrayList<Chunk> chunksAtPosition = new ArrayList<Chunk>();
        for (int i=0;i<chunks.getLength();i++) {
            Element chunk = (Element)chunks.item(i);
            Element layer = (Element)chunk.getParentNode().getParentNode();   
            if(chunk.getAttribute("x").equals(String.valueOf(x)) && chunk.getAttribute("y").equals(String.valueOf(y))) {
                chunksAtPosition.add(new Chunk(chunk, layer));
            }
        }
        return chunksAtPosition;
    }

    public void loadTilesets() {
        NodeList tilesetNodeList = document.getElementsByTagName("tileset");
        for (int i=0;i<tilesetNodeList.getLength();i++) {
            Element element = (Element)tilesetNodeList.item(i);
            Tileset ts = new Tileset(element, builder);
            tilesets.add(ts);
        }
    }
    public ArrayList<Tileset> getTilesets() {
        return tilesets;
    }

    public Tileset findTilesetByTileID(int id) {
        if(id == 0) return null;
        for(int i=0;i<tilesets.size()-1;i++) {
            if(id >= tilesets.get(i).getStart_id() && id < tilesets.get(i+1).getStart_id()) {
                return tilesets.get(i);
            }
        }
        return null;
    }
}



