package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import org.w3c.dom.Element;

public class Chunk {
    private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
    private static final int  FLIPPED_VERTICALLY_FLAG   = 0x40000000;
    private static final int  FLIPPED_DIAGONALLY_FLAG   = 0x20000000;
    private Element data;
    private Element layer;
    private int[] trueCoords;
    public Chunk(Element data, Element layer) {
        this.data = data;
        this.layer = layer;
    }

    public Element getData() {
        return data;
    }

    public Element getLayer() {
        return layer;
    }
    public int getTile(int i, int j) {
        int answer;
        String s = data.getTextContent();
        String[] tiles = s.split(",");
        String tile = tiles[16 * i + j].trim();
        answer =  Long.valueOf(tile).intValue();
        answer  = answer & ~(FLIPPED_HORIZONTALLY_FLAG |
             FLIPPED_VERTICALLY_FLAG |
             FLIPPED_DIAGONALLY_FLAG);
        //System.out.println("actual : " + tile + " ans : " + answer + " i : " + i + " j : " + j + "chunk: " + toString());
        return answer;
    }
    public int getXOffset() {
        String value = layer.getAttribute("offsetx");
        return value.equals("") ? 0 : Integer.valueOf(value);
    }
    public int getYOffset() {
        String value = layer.getAttribute("offsety");
        return value.equals("") ? 0 : Integer.valueOf(value);
    }

    @Override
    public String toString() {
        return "x=" + data.getAttribute("x") + 
        " y=" + data.getAttribute("y") + 
        " width=" + data.getAttribute("width") + 
        " height=" + data.getAttribute("height") +
        " from layer : " + layer.getAttribute("name");
    }
    public String toStringSimple() {
        return "x=" + data.getAttribute("x") + 
        " y=" + data.getAttribute("y");
    }

    public void setTrueCoords(int[] trueCoords) {
        this.trueCoords = trueCoords;
    }

    public int[] getTrueCoords() {
        return trueCoords;
    }
}