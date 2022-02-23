package util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Dungeon {
    private static final int MAX_SIZE = 9;
    private static final int MAX_STEP_AMOUNT = MAX_SIZE * MAX_SIZE;
    private static final float SINUOSITY_FACTOR = 0.5f;
    private int currentLayer = -1; 
    private DType type;
    private ArrayList<CTYPE[][]> layers = new ArrayList<CTYPE[][]>();
    private ArrayList<int[]> entries = new ArrayList<int[]>();
    private ArrayList<int[]> exits = new ArrayList<int[]>();

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
    }

    public Dungeon(DType type, int numLayers) {
        this.type = type;
        while(numLayers-->0) {
            generateNewLayer();
        }
    }

    public static void main(String[] args) {
        Dungeon d = new Dungeon(DType.SEWER,2);
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
        int endX, endY, dx, dy;
        int attempts = 0;
        int test = MAX_STEP_AMOUNT;
        for(;;) {
            do {
                endX = r.nextInt(MAX_SIZE/2) * 2 + 1;
                endY = r.nextInt(MAX_SIZE/2) * 2;
                dx = endX - startX;
                dy = endY - startY;
            } while((dx == 0 && dy == 0));
            layer[startY][startX] = first;
            layer[endY][endX] = CTYPE.FLOOR_ENTRANCE;
            
            //centers
            
            do {
                test = MAX_STEP_AMOUNT;
                for(int y=1;y<MAX_SIZE-1;y+=2) {
                    for(int x=1;x<MAX_SIZE-1;x+=2) {
                        double which = r.nextDouble();
                        if(layer[y-1][x] != null) {
                            if(which >=0 && which < 0.5) {
                                layer[y][x] = CTYPE.CENTER_A;
                            } else {
                                layer[y][x] = layers.size()>=5 ? CTYPE.CENTER_C : CTYPE.CENTER_B;
                            }
                            continue;
                        }
                        if(which >=0 && which < 0.33) {
                            layer[y][x] = CTYPE.CENTER_A;
                        } else if (which >=0.33 && which < 0.66) {
                            layer[y][x] = layers.size()>=5 ? CTYPE.CENTER_C : CTYPE.CENTER_B;
                        } else {
                            layer[y][x] = CTYPE.EMPTY;
                        }
                    }
                }
                test = minStepsToExit(layer, startX, startY+1, endX, endY+1);
                System.out.println("Attempt: " + attempts++);
            } while((test>MAX_STEP_AMOUNT || test < (MAX_SIZE * SINUOSITY_FACTOR)) && attempts < 10);
            if(attempts<10) break;
            attempts=0;
            System.out.println("Redoing Intial conditions");
        }
        System.out.println("Successful Attempt!");
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

        //corners
        for(int y=0;y<MAX_SIZE-2;y+=2) {
            for(int x=0;x<MAX_SIZE;x+=2) {
                boolean left = x>0 ? !layer[y+1][x-1].equals(CTYPE.EMPTY) : false;
                boolean right = x<MAX_SIZE-1 ? !layer[y+1][x+1].equals(CTYPE.EMPTY) : false;
                if(left && right) {
                    layer[y][x] = CTYPE.CORNER_TL_TR;
                } else if (left) {
                    layer[y][x] = CTYPE.CORNER_TL;
                } else if (right) {
                    layer[y][x] = CTYPE.CORNER_TR;
                } else {
                    layer[y][x] = CTYPE.EMPTY;
                }
            }
        }
        layers.add(layer);
        entries.add(new int[] {startX,startY});
        exits.add(new int[] {endX,endY});
    }

    public int minStepsToExit(CTYPE[][] layer, int sx, int sy, int dx, int dy) {
        Queue<Room> rooms = new LinkedList<>();
        boolean[][] checked = new boolean[MAX_SIZE][MAX_SIZE];
        checked[sy][sx] = true;
        Room start = new Room(sx,sy,0);
        rooms.add(start);
        while (!rooms.isEmpty()) {
            Room r = rooms.peek();
            int rx = r.x;
            int ry = r.y;

            if (rx == dx && ry == dy) return r.steps;

            rooms.remove();
            for(int i=ry-2;i<=ry+2;i+=4) {
                if(i>0 && i<MAX_SIZE && layer[i-(i-ry)/2][rx]==null && !layer[i][rx].equals(CTYPE.EMPTY)) {
                    if(!checked[i][rx]) {
                        checked[i][rx] = true;
                        Room next = new Room(rx,i,r.steps+1);
                        rooms.add(next);
                    }
                }
            }
            for(int j=rx-2;j<=rx+2;j+=4) {
                if(j>0 && j<MAX_SIZE && !layer[ry][j].equals(CTYPE.EMPTY)) {
                    if(!checked[ry][j]) {
                        checked[ry][j] = true;
                        Room next = new Room(j,ry,r.steps+1);
                        rooms.add(next);
                    }
                }
            }
        }
        return MAX_STEP_AMOUNT+1;
    }

    public int[][] createCheckArray() {
        int[][] array = new int[MAX_SIZE][MAX_SIZE];
        for (int[] sub : array) {
            for(int i=0;i<sub.length;i++) {
                sub[i] = -1;
            }
        }
        return array;
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

    public static String printCheck(int i) {
        switch(i) {
            case -2:
                return "..";    //checking
            case -1:
                return "  ";    //Not checked
            case -3:
                return "░░";    //Not found
            default:
                return "▓▓";    //found
        }
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
            case CLOSED_RIGHT:
                return "╣ ";
            case CLOSED_UP:
                return "╦╦";
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
    //credit to https://www.geeksforgeeks.org/shortest-path-in-a-binary-maze/ for helping me think of the problem in terms of queues and breadth-first instead of recursion
    class Room {
        int x;
        int y;
        int steps;
 
        public Room(int x, int y, int steps) {
            this.x = x;
            this.y = y;
            this.steps = steps;
        }
    }

    public ArrayList<int[]> getEntries() {
        return entries;
    }

    public ArrayList<int[]> getExits() {
        return exits;
    }

    public ArrayList<CTYPE[][]> getLayers() {
        return layers;
    }

    public CTYPE getChunkByCoords(int x, int y) {       //128 + x*16   //0 + y*16
        CTYPE[][] layer = layers.get(currentLayer);
        int transformX = (x - Map.DUNGEON_START_CHUNK[0]) / 16;
        int transformY = (y - Map.DUNGEON_START_CHUNK[1]) / 16;
        if(transformX<0 || transformX >= MAX_SIZE || transformY < 0 || transformY >= MAX_SIZE) {
            return CTYPE.EMPTY;
        }
        return layer[transformY][transformX];
    }

    public DType getType() {
        return type;
    }

    public int[] getChunkCoords(CTYPE c) {
        if(c==null) return new int[]{-80, 208};
        switch(type) {
            case CAVE:
                switch(c) {
                    case CENTER_A:
                        return new int[]{-112, 208};
                    case CENTER_B:
                        return new int[]{-48, 208};
                    case CENTER_C:
                        return new int[]{-16, 208};
                    case CLOSED_DOWN:
                        return new int[]{-112, 224};
                    case CLOSED_LEFT:
                        return new int[]{-128, 208};
                    case CLOSED_RIGHT:
                        return new int[]{-96, 208};
                    case CLOSED_UP:
                        return new int[]{-112, 192};
                    case CORNER_TL:
                        return new int[]{-96, 192};
                    case CORNER_TL_TR:
                        return new int[]{-80, 192};
                    case CORNER_TR:
                        return new int[]{-128, 192};
                    case EMPTY:
                        return new int[]{-80, 208};
                    case FLOOR_ENTRANCE:
                        return new int[]{-32, 224};
                    case FLOOR_ENTRANCE_CLOSED:
                        return new int[]{-96, 224};
                    case OPEN_LEFT_RIGHT:
                        return new int[]{-64, 208};
                    case OPEN_UP_DOWN:
                        return new int[]{-48, 192};
                    case WORLD_ENTRANCE:
                        return new int[]{-64, 224};
                    case WORLD_ENTRANCE_CLOSED:
                        return new int[]{-80, 224};
                    default:
                        return new int[]{-80, 208};
                }
            case SEWER:
                //
            default:
                return null;
        }
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public Dungeon isInThis() {
        return currentLayer==-1 ? null : this;
    }

    public int[] getDoorCoords(int which) {
        return new int[]{7+which, 9};
    }
}
