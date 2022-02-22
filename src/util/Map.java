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
    public static final int[] DUNGEON_START_CHUNK = {256,0};
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private ArrayList<Tileset> tilesets = new ArrayList<Tileset>();
    private String[] layerOrder = {"land", "river", "land_2", "walls", "crops", "windows", "sprites", "roofwalls", "roofs","roofcrops", "collisions"};
    private NodeList chunks;
    private DoorLoader doorLoader;

    public Map(File file) {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
            chunks = document.getElementsByTagName("chunk");
            doorLoader = new DoorLoader(new File("res/door.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static void main(String[] args) {
        File file = new File("res/map.tmx");
        Map mapLoader = new Map(file);
        ArrayList<Chunk> chunks = mapLoader.getChunksByCoordinate(-32, -64,null);
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
        for (int i=0;i<chunks.getLength();i++) {
            Element chunk = (Element)chunks.item(i);
            Element layer = (Element)chunk.getParentNode().getParentNode();   
            if(chunk.getAttribute("x").equals(String.valueOf(x)) && chunk.getAttribute("y").equals(String.valueOf(y))) {
                System.out.print("x=" + chunk.getAttribute("x") + " y=" + chunk.getAttribute("y") + " width=" + chunk.getAttribute("width") + " height=" + chunk.getAttribute("height"));
                System.out.println(" from layer : " + layer.getAttribute("name"));
            }
        }
    }

    public ArrayList<Chunk> getChunksByCoordinate(int x, int y, Dungeon dungeon) {
        ArrayList<Chunk> chunksAtPosition = new ArrayList<Chunk>();
        if(dungeon!=null) {
            Dungeon.CTYPE c =  dungeon.getChunkByCoords(x, y);
            int[] chunkCoords = dungeon.getChunkCoords(c);
            //System.out.println(c + " coords: " + chunkCoords);
            x = chunkCoords[0];
            y = chunkCoords[1];
        }
        for (int i=0;i<chunks.getLength();i++) {
            Element chunk = (Element)chunks.item(i);
            Element layer = (Element)chunk.getParentNode().getParentNode();   
            if(chunk.getAttribute("x").equals(String.valueOf(x)) && chunk.getAttribute("y").equals(String.valueOf(y))) {
                chunksAtPosition.add(new Chunk(chunk, layer));
            }
        }
        return chunksAtPosition;
    }

    public int[][] findClosestChunkCoords(Point3f p) {
        int[] tile = findTile(p);
        int x = tile[0];
        int y = tile[1];
        int belowX = x - (x % 16 + 16) % 16;
        int belowY = y - (y % 16 + 16) % 16;
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
        int[] chunkColumms = new int[] {closestX - 32, closestX - 16, closestX, closestX + 16};
        int[] chunkRows = new int[] {closestY - 32, closestY - 16, closestY, closestY + 16};
        return new int[][] {chunkColumms, chunkRows};
    }

    public ArrayList<Chunk> findClosestChunks(Point3f p, Dungeon dungeon) {
        ArrayList<Chunk> nearestChunks = new ArrayList<Chunk>();
        int[][] coords = findClosestChunkCoords(p);
        int[] chunkRows = coords[1];
        int[] chunkColumms = coords[0];
        for (int i : chunkRows) {
            for (int j : chunkColumms) {
                ArrayList<Chunk> foundChunks = getChunksByCoordinate(j, i, dungeon);
                foundChunks.forEach((c) -> {
                    nearestChunks.add(c);
                    c.setTrueCoords(new int[] {j,i});
                });
                //System.out.print(foundChunks.size()>0 ? foundChunks.get(0).toStringSimple() + " | ": "empty | ");
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

    public int[] findTile(Point3f p) {
        return new int[]{(int)Math.floor((double)p.getX()), (int)Math.floor((double)p.getY())};
    }

    public int getIdAudioLayer(Point3f p, Dungeon dungeon) {
        int[] tile = findTile(p);
        int x = tile[0], y = tile[1];
        ArrayList<Chunk> foundChunks = getChunksByCoordinate(x - (x % 16 + 16) % 16,y - (y % 16 + 16) % 16, dungeon);
        for (Chunk chunk : foundChunks) {
            if(chunk.getLayer().getAttribute("name").equals("audio")) {
                return chunk.getTile((y % 16 + 16) % 16, (x % 16 + 16) % 16);
            }
        }
        return 0;
    }

    public int[][] findCollisionTilesNearbyAPoint(Point3f p, int radius, Dungeon dungeon) {
        int[][] collisions = new int[2 * radius + 1][2 * radius + 1];
        int[] tile = findTile(p);
        int x = tile[0];
        int y = tile[1];
        int positionInChunkX = (x % 16 + 16) % 16;
        int positionInChunkY = (y % 16 + 16) % 16;
        //System.out.println((x - positionInChunkX) + " " + (y - positionInChunkY));
        //System.out.println("x: " + (positionInChunkX) + " y: " + (positionInChunkY));
        for(int i=0;i<collisions.length;i++) {
            for(int j=0;j<collisions[i].length;j++) {
                int chunkX = x - positionInChunkX;
                int chunkY = y - positionInChunkY;
                if(j < radius - positionInChunkX)                                   chunkX -= 16;
                if(collisions[i].length - j - 1 < positionInChunkX - (15 - radius)) chunkX += 16;
                if(i < radius - positionInChunkY)                                   chunkY -= 16;
                if(collisions.length - i - 1 < positionInChunkY - (15 - radius))    chunkY += 16;
                ArrayList<Chunk> allLayers = getChunksByCoordinate(chunkX, chunkY, dungeon);
                allLayers.removeIf((c) -> !c.getLayer().getAttribute("name").equals("collisions"));
                if(allLayers.isEmpty()) {
                    collisions[i][j] = 0;
                } else {
                    Chunk chunk = allLayers.get(0);
                    //collisions[i][j] = chunkX * 1000 + chunkY;
                    //collisions[i][j] = positionInChunkY * 100 + positionInChunkX;
                    //collisions[i][j] = (y - radius + i) * 1000 + (x - radius + j);
                    //collisions[i][j] = (((positionInChunkY - radius + i) % 16 + 16) % 16)*100 + (((positionInChunkX - radius + j) % 16 + 16) % 16);
                    collisions[i][j] = chunk.getTile(((positionInChunkY - radius + i) % 16 + 16) % 16, ((positionInChunkX - radius + j) % 16 + 16) % 16);
                }
            }
        }
        return collisions;
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
        Tileset last = tilesets.get(tilesets.size()-1);
        if (id >= last.getStart_id() && id < last.getStart_id() + last.getTileCount()) {
            return last;
        } else {
            return null;
        }
        
    }

    public int indexOfLayer(String layer) {
        return Arrays.asList(layerOrder).indexOf(layer);
    }

    public Point3f findTeleportPointByOther(String type, Point3f p) {
        return doorLoader.findTeleportPointByOther(type,p);
    }
    
    public String findTeleportTypeByPoint(Point3f p) {
        return doorLoader.findTeleportTypeByPoint(p);
    }
}

class DoorLoader {
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;

    public DoorLoader(File file) {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public String findTeleportTypeByPoint(Point3f p) {
        p = worldPointToObject(p);
        NodeList doors = document.getElementsByTagName("door");
        boolean pastFarms = false;
        for (int i=0;i<doors.getLength();i++) {
            Element e = (Element)doors.item(i);
            if(i >= 96 && !pastFarms) {
                p = objectPointToWorld(p);
                pastFarms = true;
            }
            //System.out.printf("ox:%s px:%s oy:%s py:%s\n",Integer.parseInt(e.getAttribute("x")),(int)p.getX()*16,Integer.parseInt(e.getAttribute("y")),(int)(p.getY()+1)*16);
            if(Integer.parseInt(e.getAttribute("x")) == (int)p.getX() && Integer.parseInt(e.getAttribute("y")) == (int)p.getY()) {
                return e.getTextContent();
            }
        }
        return null;
    }

    public Point3f findTeleportPointByOther(String type, Point3f p) {
        p = worldPointToObject(p);
        NodeList doors = document.getElementsByTagName("door");
        boolean pastFarms = false;
        for (int i=0;i<doors.getLength();i++) {
            Element e = (Element)doors.item(i);
            if(e.getTextContent().equals(type)) {
                if(i >= 96 && !pastFarms) {
                    p = objectPointToWorld(p);
                    pastFarms = true;
                }
                //System.out.printf("ox:%d px:%d oy:%d py:%d\n",Integer.parseInt(e.getAttribute("x")),(int)p.getX(),Integer.parseInt(e.getAttribute("y")),(int)p.getY());
                if(Integer.parseInt(e.getAttribute("x")) != (int)p.getX() || Integer.parseInt(e.getAttribute("y")) != (int)p.getY()) {
                    Point3f destination = new Point3f(Float.parseFloat(e.getAttribute("x")),Float.parseFloat(e.getAttribute("y")),0f);
                    return i >=96 ? destination : objectPointToWorld(destination);
                }
            }
        }
        return null;
    }

    public Point3f objectPointToWorld(Point3f p) {
        return new Point3f(p.getX()/16, p.getY()/16 - 1,0f);
    }

    public Point3f worldPointToObject(Point3f p) {
        return new Point3f(p.getX()*16, (p.getY()+1)*16,0f);
    }
}


