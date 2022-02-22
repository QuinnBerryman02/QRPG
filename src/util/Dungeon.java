package util;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {
    private static final int MAX_SIZE = 39;
    private DType type;
    private ArrayList<CTYPE[][]> layers = new ArrayList<CTYPE[][]>();

    public enum DType {  //DungeonType
        SEWER,
        CAVE,
    }
    public enum CTYPE {  //ChunkType
        WORLD_ENTRANCE,
        FLOOR_ENTRANCE,
        WORLD_ENTRANCE_CLOSED,
        FLOOR_ENTRANCE_CLOSED,
        CORNER_TL,
        CORNER_TR,
        CORNER_TL_TR,
        EMPTY,
        CENTER_A,
        CENTER_B,
        CENTER_C,
        CLOSED_LEFT,
        CLOSED_UP,
        CLOSED_RIGHT,
        CLOSED_DOWN,
        OPEN_LEFT_RIGHT,
        OPEN_UP_DOWN,
        CLOSED_LEFT_RIGHT,//might get rid off, dont know how to implement in a fair wait just yet
        CLOSED_UP_DOWN,
    }

    public Dungeon(DType type) {
        this.type = type;
    }

    public static void main(String[] args) {
        Dungeon d = new Dungeon(DType.SEWER);
        d.generateNewLayer();
        d.generateNewLayer();
        d.printLayer(0);
        d.printLayer(1);
    }

    public void generateNewLayer() {
        Random r = new Random();
        CTYPE[][] layer = new CTYPE[MAX_SIZE][MAX_SIZE];

        //Start and end
        CTYPE first = layers.isEmpty() ? CTYPE.WORLD_ENTRANCE : CTYPE.FLOOR_ENTRANCE;
        int startX = r.nextInt(MAX_SIZE/2) * 2 + 1;
        int startY = r.nextInt(MAX_SIZE/2) * 2;
        int endX;
        int endY;
        do {
            endX = r.nextInt(MAX_SIZE/2) * 2 + 1;
            endY = r.nextInt(MAX_SIZE/2) * 2;
            //maybe check to see if they are too close using pythagoras
        } while(endX == startX && endY == startY);
        layer[startY][startX] = first;
        layer[endY][endX] = CTYPE.FLOOR_ENTRANCE;
        
        //centers
        for(int y=1;y<MAX_SIZE-1;y+=2) {
            for(int x=1;x<MAX_SIZE-1;x+=2) {
                double which = r.nextDouble();
                if(layer[y-1][x] != null) {
                    if(which >=0 && which < 0.5) {
                        layer[y][x] = CTYPE.CENTER_A;
                    } else {
                        layer[y][x] = layers.size()>=1 ? CTYPE.CENTER_C : CTYPE.CENTER_B;
                    }
                    continue;
                }
                if(which >=0 && which < 0.33) {
                    layer[y][x] = CTYPE.CENTER_A;
                } else if (which >=0.33 && which < 0.66) {
                    layer[y][x] = layers.size()>=1 ? CTYPE.CENTER_C : CTYPE.CENTER_B;
                } else {
                    layer[y][x] = CTYPE.EMPTY;
                }
            }
        }

        //edges
        for(int y=0;y<MAX_SIZE;y++) {
            int xOff = (y + 1) % 2;
            for(int x=xOff;x<MAX_SIZE-xOff;x+=2) {
                if(layer[y][x] != null) {
                    if(!(y==0 || layer[y-1][x].equals(CTYPE.EMPTY))) {
                        layer[y][x] = CTYPE.values()[layer[y][x].ordinal()+2];
                    }
                    continue;
                }
                if(y==0) {
                    if(!layer[y+1][x].equals(CTYPE.EMPTY)) {
                        layer[y][x] = CTYPE.CLOSED_UP;
                    } else {
                        layer[y][x] = CTYPE.EMPTY;
                    }
                    continue;
                }
                if(y==MAX_SIZE-1) {
                    if(!layer[y-1][x].equals(CTYPE.EMPTY)) {
                        layer[y][x] = CTYPE.CLOSED_DOWN;
                    } else {
                        layer[y][x] = CTYPE.EMPTY;
                    }
                    continue;
                }
                if(x==0) {
                    if(!layer[y][x+1].equals(CTYPE.EMPTY)) {
                        layer[y][x] = CTYPE.CLOSED_LEFT;
                    } else {
                        layer[y][x] = CTYPE.EMPTY;
                    }
                    continue;
                }
                if(x==MAX_SIZE-1) {
                    if(!layer[y][x-1].equals(CTYPE.EMPTY)) {
                        layer[y][x] = CTYPE.CLOSED_RIGHT;
                    } else {
                        layer[y][x] = CTYPE.EMPTY;
                    }
                    continue;
                }
                if(xOff==0) {
                    boolean right = !layer[y][x+1].equals(CTYPE.EMPTY);
                    boolean left = !layer[y][x-1].equals(CTYPE.EMPTY);
                    if(right && left) {
                        layer[y][x] = CTYPE.OPEN_LEFT_RIGHT;
                    } else if (right) {
                        layer[y][x] = CTYPE.CLOSED_LEFT;
                    } else if (left) {
                        layer[y][x] = CTYPE.CLOSED_RIGHT;
                    } else {
                        layer[y][x] = CTYPE.EMPTY;
                    }
                    continue;
                }
                if(xOff==1) {
                    boolean up = !layer[y-1][x].equals(CTYPE.EMPTY);
                    boolean down = !layer[y+1][x].equals(CTYPE.EMPTY);
                    if(up && down) {
                        layer[y][x] = CTYPE.OPEN_UP_DOWN;
                    } else if (up) {
                        layer[y][x] = CTYPE.CLOSED_DOWN;
                    } else if (down) {
                        layer[y][x] = CTYPE.CLOSED_UP;
                    } else {
                        layer[y][x] = CTYPE.EMPTY;
                    }
                    continue;
                }
            }
        }










        layers.add(layer);
    }

    public void printLayer(int index) {
        CTYPE[][] layer = layers.get(index);
        for (CTYPE[] ctypes : layer) {
            for (CTYPE ctype : ctypes) {
                System.out.print(printType(ctype));
            }
            System.out.println();
        }
        System.out.println();
    }

    public static String printType(CTYPE t) {
        if(t == null) return "  ";
        switch(t) {
            case CENTER_A:
                return "▓▓";
            case CENTER_B:
                return "▒▒";
            case CENTER_C:
                return "░░";
            case CLOSED_DOWN:
                return "╩╩";
            case CLOSED_LEFT:
                return " ╠";
            case CLOSED_LEFT_RIGHT:
                return "╣╠";
            case CLOSED_RIGHT:
                return "╣ ";
            case CLOSED_UP:
                return "╦╦";
            case CLOSED_UP_DOWN:
                return "╬╬";
            case CORNER_TL:
                return ". ";
            case CORNER_TL_TR:
                return "..";
            case CORNER_TR:
                return " .";
            case EMPTY:
                return "  ";
            case FLOOR_ENTRANCE:
                return "FE";
            case FLOOR_ENTRANCE_CLOSED:
                return "FC";
            case OPEN_LEFT_RIGHT:
                return "══";
            case OPEN_UP_DOWN:
                return "║║";
            case WORLD_ENTRANCE:
                return "WE";
            case WORLD_ENTRANCE_CLOSED:
                return "WC";
            default:
                return "**";
        }
    }
}
