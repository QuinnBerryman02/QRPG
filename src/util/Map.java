package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import main.MainWindow;

import org.w3c.dom.Element;

public class Map {
    public static final int[] CAVE_START_CHUNK = {Dungeon.MAX_SIZE*16+96,0};
    public static final int[] SEWER_START_CHUNK = {Dungeon.MAX_SIZE*16+96,960};
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
        Dungeon dungeon = (MainWindow.getModel()==null) ? null : MainWindow.getModel().getCurrentDungeon();
        ArrayList<Chunk> chunksAtPosition = new ArrayList<Chunk>();
        if(dungeon!=null) {
            Dungeon.CTYPE c =  dungeon.getChunkByCoords(new Point3f(x,y,0));
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

    public ArrayList<Chunk> findClosestChunks(Point3f p) {
        ArrayList<Chunk> nearestChunks = new ArrayList<Chunk>();
        int[][] coords = findClosestChunkCoords(p);
        int[] chunkRows = coords[1];
        int[] chunkColumms = coords[0];
        for (int i : chunkRows) {
            for (int j : chunkColumms) {
                ArrayList<Chunk> foundChunks = getChunksByCoordinate(j, i);
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

    public static int[] findTile(Point3f p) {
        return new int[]{(int)Math.floor((double)p.getX()), (int)Math.floor((double)p.getY())};
    }

    public int getIdAudioLayer(Point3f p) {
        int[] tile = findTile(p);
        int x = tile[0], y = tile[1];
        ArrayList<Chunk> foundChunks = getChunksByCoordinate(x - (x % 16 + 16) % 16,y - (y % 16 + 16) % 16);
        for (Chunk chunk : foundChunks) {
            if(chunk.getLayer().getAttribute("name").equals("audio")) {
                return chunk.getTile((y % 16 + 16) % 16, (x % 16 + 16) % 16);
            }
        }
        return 0;
    }

    public int[][] findCollisionTilesNearbyAPoint(Point3f p, int radius) {
        int[][] collisions = new int[2 * radius + 1][2 * radius + 1];
        int[] tile = findTile(p);
        int x = tile[0];
        int y = tile[1];
        int positionInChunkX = (x % 16 + 16) % 16;
        int positionInChunkY = (y % 16 + 16) % 16;
        for(int i=0;i<collisions.length;i++) {
            int chunkY = y - positionInChunkY;
            if(i < radius - positionInChunkY)                                   chunkY -= 16;
            if(collisions.length - i - 1 < positionInChunkY - (15 - radius))    chunkY += 16;
            for(int j=0;j<collisions[i].length;j++) {
                int chunkX = x - positionInChunkX;
                if(j < radius - positionInChunkX)                                   chunkX -= 16;
                if(collisions[i].length - j - 1 < positionInChunkX - (15 - radius)) chunkX += 16;
                ArrayList<Chunk> allLayers = getChunksByCoordinate(chunkX, chunkY);
                allLayers.removeIf((c) -> !c.getLayer().getAttribute("name").equals("collisions"));
                if(allLayers.isEmpty()) {
                    collisions[i][j] = 0;
                } else {
                    Chunk chunk = allLayers.get(0);
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
        Dungeon dungeon = MainWindow.getModel().getCurrentDungeon();
        if(type.equals("caves") || type.equals("sewer")) {
            if(dungeon == null) {
                outer:
                for(Dungeon d : MainWindow.getModel().getDungeons()) {
                    System.out.println(d.getType() + " " + type);
                    switch(d.getType()) {
                        case CAVE:
                            if(type.equals("caves")) {
                                dungeon = d;
                                break outer;
                            }
                            break;
                        case SEWER:
                            if(type.equals("sewer")) {
                                dungeon = d;
                                break outer;
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown dungeon type");
                    }
                }
                dungeon.setCurrentLayer(0);
                int[] startCoords = dungeon.getEntries().get(0);
                int[] getDoorCoords = dungeon.getDoorCoords(1, true);
                int[] dst = dungeon.dungeonSpaceToWorldSpace(startCoords);
                int x = dst[0] + getDoorCoords[0];
		        int y = dst[1] + getDoorCoords[1];
                return new Point3f(x,y,0);
            } else {
                System.out.println("Inner " + dungeon.getType().name() + " door (layer " + dungeon.getCurrentLayer() + ")");
                int layer = dungeon.getCurrentLayer();
                int[] loc = dungeon.worldSpaceToDungeonSpace(p);
                boolean entry = false;
                int[] entryCoord = dungeon.getEntries().get(layer);
                int[] exitCoord = dungeon.getExits().get(layer);
                if (loc[0]==entryCoord[0] && loc[1]==entryCoord[1]) {
                    System.out.print("via Entry ");
                    entry=true;
                } else if (loc[0]==exitCoord[0] && loc[1]==exitCoord[1]) {
                    System.out.print("via Exit ");
                    entry=false;
                }
                layer += entry ? -1 : 1;
                int px = (findTile(p)[0] % 16 + 16) % 16;
                int[] doorCoords = dungeon.getDoorCoords(px - (type.equals("sewer") ? 5 : 7), false);//we dont use door coords on a world tele anyway
                int[] newCoords;
                dungeon.setCurrentLayer(layer);
                if(layer<0) {
                    System.out.println("Leaving " + dungeon.getType().name() + " (layer " + layer + ")");
                    return doorLoader.findTeleportPointByOther(type, p);
                } else if(layer>=dungeon.getLayers().size()) {
                    System.out.println("Going deeper than ever before (layer " + layer + ")");
                    dungeon.generateNewLayer();
                    newCoords = dungeon.getEntries().get(layer);
                } else {
                    System.out.println("Changing Floors (layer " + layer + ")");
                    newCoords = entry ? dungeon.getExits().get(layer) : dungeon.getEntries().get(layer);
                }
                int[] dst = dungeon.dungeonSpaceToWorldSpace(newCoords);
                int x = dst[0] + doorCoords[0];
                int y = dst[1] + doorCoords[1];
                return new Point3f(x,y,0);
            }
        } else {
            MainWindow.getAudioManager().playSoundByName("door");
            return doorLoader.findTeleportPointByOther(type,p);
        }
    }
    
    public String findTeleportTypeByPoint(Point3f p) {
        Dungeon d = MainWindow.getModel().getCurrentDungeon();
        if(d!=null) {
            if(d.getType().equals(Dungeon.DType.CAVE)) return "caves";
            else return "sewer";
        } else {
            return doorLoader.findTeleportTypeByPoint(p);
        }
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


