package util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Dungeon {
    public static final int MAX_SIZE = 9;
    private static final int MAX_STEP_AMOUNT = MAX_SIZE * MAX_SIZE;
    private static final float SINUOSITY_FACTOR = 0.5f;
    private ArrayList<boolean[][]> cleared = new ArrayList<boolean[][]>();
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
            printLayer(layers.size()-1);
        }
        
    }

    public static void main(String[] args) {
        Dungeon d = new Dungeon(DType.SEWER,5);
        Dungeon d2 = new Dungeon(DType.CAVE,5);
    }

    public void generateNewLayer() {
        Random r = new Random();
        CTYPE[][] layer = new CTYPE[MAX_SIZE][MAX_SIZE];

        //Start and end
        CTYPE first = layers.isEmpty() ? CTYPE.WORLD_ENTRANCE : CTYPE.FLOOR_ENTRANCE;
        int startX = r.nextInt(MAX_SIZE/2) * 2 + 1;
        int startY = r.nextInt(MAX_SIZE/2) * 2;
        int endX=startX, endY=startY, dx, dy;
        int attempts = 0;
        int test = MAX_STEP_AMOUNT;
        for(;;) {
            layer[startY][startX] = null;
            layer[endY][endX] = null;
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
                //System.out.println("Attempt: " + attempts++);
            } while((test>MAX_STEP_AMOUNT || test < (MAX_SIZE * SINUOSITY_FACTOR)) && attempts++ < 10);
            if(attempts<10) break;
            attempts=0;
            //System.out.println("Redoing Intial conditions");
        }
        //System.out.println("Successful Attempt!");
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
        cleared.add(new boolean[MAX_SIZE][MAX_SIZE]);
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

    public int[] dungeonSpaceToWorldSpace(int[] d) {
        int offset[] = type.equals(DType.CAVE) ? Map.CAVE_START_CHUNK : Map.SEWER_START_CHUNK;
        int cx = d[0] * 16 + (offset[0] * (currentLayer+1));
        int cy = d[1] * 16 + (offset[1] * (currentLayer+1));
        return new int[] {cx, cy};
    }

    public int[] worldSpaceToDungeonSpace(Point3f p) {
        int offset[] = type.equals(DType.CAVE) ? Map.CAVE_START_CHUNK : Map.SEWER_START_CHUNK;
        int[] tile = Map.findTile(p);
        int x = tile[0];
        int y = tile[1];
        int cx = x - (x % 16 + 16) % 16;
        int cy = y - (y % 16 + 16) % 16;
        int dx = (cx - offset[0] * (currentLayer+1)) / 16;
        int dy = (cy - offset[1] * (currentLayer+1)) / 16;
        return new int[] {dx, dy};
    }

    public int[][] getSpawnLocations(int[] room) {
        if(type.equals(DType.CAVE)) {
            return new int[][] {{6,1}, {11,1}, {14,4}, {14,9}, {11,12}, {6,12}, {3,9}, {3,4}};
        }
        CTYPE c = layers.get(currentLayer)[room[1]][room[0]];
        switch(c) {
            case CENTER_A:
                return new int[][]{{6,2}, {6,6}, {6,10}, {6,14}, {0,11}, {14,4},{14,8},{14,12}}; 
            case CENTER_B:
                return new int[][]{{8,1},{8,5},{8,9},{0,5},{0,10},{16,5},{16,10},{7,10}};
            case CENTER_C:
                return new int[][]{{0,0},{0,15},{15,15},{15,0},{7,0},{7,15},{0,7},{15,7}};
            default:
                return new int[][]{{}}; 
        }
    }

    public Enemy[] generateEnemies(Point3f p) {
        int[] d = worldSpaceToDungeonSpace(p);
        if(cleared.get(currentLayer)[d[1]][d[0]]) {
            return null;
        }
        cleared.get(currentLayer)[d[1]][d[0]] = true;
        Random r = new Random();
        int[] c = dungeonSpaceToWorldSpace(d);
        int cx = c[0];
        int cy = c[1];
        int[][] spawns = getSpawnLocations(d);
        for (int[] is : spawns) {
            is[0]+=0.5;
            is[1]+=0.5;
        }
        Enemy[] enemies = new Enemy[spawns.length];
        for(int i=0;i<enemies.length;i++) {
            double isEnemy = r.nextDouble();
            if(isEnemy < 0.5) {
                int px = spawns[i][0];
                int py = spawns[i][1];
                Enemy.Type t = generateApropriateEnemy();
                Enemy e = new Enemy(t, 0.5f, 0.5f, new Point3f(cx+px,cy+py,0), 100 * (currentLayer/5+1), 10 * (currentLayer/5+1), 100 * (currentLayer/5+1));
                enemies[i] = e;
            }
        }
        return enemies;
    }

    public Enemy.Type generateApropriateEnemy() {
        ArrayList<Enemy.Type> validEnemies = new ArrayList<Enemy.Type>();
        if(currentLayer>=0 && currentLayer < 5) {
            validEnemies.add(Enemy.Type.BAT);
            validEnemies.add(Enemy.Type.SLIME);
            validEnemies.add(Enemy.Type.SPIDER);
            validEnemies.add(Enemy.Type.GHOST);
        }
        if(currentLayer>=2 && currentLayer < 10) {
            validEnemies.add(Enemy.Type.EARTH_ELEMENTAL);
            validEnemies.add(Enemy.Type.FIRE_ELEMENTAL);
            validEnemies.add(Enemy.Type.WATER_ELEMENTAL);
            validEnemies.add(Enemy.Type.WIND_ELEMENTAL);
            validEnemies.add(Enemy.Type.DARK_ELEMENTAL);
            validEnemies.add(Enemy.Type.LIGHT_ELEMENTAL);
            validEnemies.add(Enemy.Type.YOUNG_WITCH);
        }
        if(currentLayer>=6) {
            validEnemies.add(Enemy.Type.CULTIST);
            validEnemies.add(Enemy.Type.VAMPIRE);
            validEnemies.add(Enemy.Type.ELDER_WITCH);
        }
        if(currentLayer>=10) {
            validEnemies.add(Enemy.Type.BLACK_KNIGHT);
        }
        return validEnemies.get(new Random().nextInt(validEnemies.size()));
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

    public CTYPE getChunkByCoords(Point3f p) {       //128 + x*16   //0 + y*16
        CTYPE[][] layer = layers.get(currentLayer);
        int[] d = worldSpaceToDungeonSpace(p);
        if(d[0]<0 || d[0] >= MAX_SIZE || d[1] < 0 || d[1] >= MAX_SIZE) {
            return CTYPE.EMPTY;
        }
        return layer[d[1]][d[0]];
    }

    public ArrayList<boolean[][]> getCleared() {
        return cleared;
    }

    public DType getType() {
        return type;
    }

    public int[] getChunkCoordsHelper(CTYPE c) {
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
    }

    public int[] getChunkCoords(CTYPE c) {
        if(c==null) return getChunkCoords(CTYPE.EMPTY);
        switch(type) {
            case CAVE:
                return getChunkCoordsHelper(c);
            case SEWER:
                int[] arr = getChunkCoordsHelper(c);
                arr[0] += 8*16;
                return arr;
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

    public boolean isInThis() {
        return currentLayer==-1 ? false : true;
    }

    public int[] getDoorCoords(int which, boolean world) {
        if(type.equals(DType.CAVE)) {
            return new int[]{7+which, 9};
        } else {
            if(world) {
                return new int[]{7,8};
            } else {
                return new int[]{5+which,10};
            }
        } 
        
    }
}
