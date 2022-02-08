package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Map {
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private ArrayList<Tileset> tilesets = new ArrayList<Tileset>();
    private String[] layerOrder = {"land", "river", "land_2", "walls", "crops", "windows", "sprites", "roofs","collisions"};

    public Map(File file) {
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
        Map mapLoader = new Map(file);
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

    public ArrayList<Chunk> findClosestChunks(int x, int y) {
        ArrayList<Chunk> nearestChunks = new ArrayList<Chunk>();
        int belowX = x - (x % 16);
        int belowY = y - (y % 16);
        int aboveX = belowX + 16;
        int aboveY = belowY + 16;
        int closestX, closestY;
        if(x > (aboveX - belowX)/2 + belowX) {
            closestX = aboveX;
        } else {
            closestX = belowX;
        }
        if(y > (aboveY - belowY)/2 + belowY) {
            closestY = aboveY;
        } else {
            closestY = belowY;
        }
        //System.out.println("X: " + x + " y: " + y + " closeX: " + closestX + " closeY: " + closestY);
        int[] chunkColumms = new int[] {closestX - 32, closestX - 16, closestX, closestX + 16, closestX + 32};
        int[] chunkRows = new int[] {closestY - 16, closestY, closestY + 16};
        //System.out.println("Player x=" + x + " y=" + y);
        for (int i : chunkRows) {
            for (int j : chunkColumms) {
                ArrayList<Chunk> chunks = getChunksByCoordinate(j, i);
                chunks.forEach((c) -> nearestChunks.add(c));
                //System.out.print(chunks.size()>0 ? chunks.get(0).toStringSimple() + " | ": "empty | ");
            }
            //System.out.println();
        }
        Collections.sort(nearestChunks, (a, b) -> {
            if(indexOfLayer(((Chunk)a).getLayer().getAttribute("name")) > indexOfLayer(((Chunk)b).getLayer().getAttribute("name"))) {
                return 1;
            } else if (indexOfLayer(((Chunk)a).getLayer().getAttribute("name")) < indexOfLayer(((Chunk)b).getLayer().getAttribute("name"))) {
                return -1;
            } else {
                return 0;
            }
        });
        return nearestChunks;
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

    public int indexOfLayer(String layer) {
        return Arrays.asList(layerOrder).indexOf(layer);
    }
}



