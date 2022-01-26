package util;

public class Skin {
    private String name;
    private Type type;
    private int id;

    private static int idCount = 0;
    public final static Skin[] skins = {
        new Skin("Warrior_M"),
        new Skin("Magician_M"),
        new Skin("Healer_M"),
        new Skin("Ninja_M"),
        new Skin("Ranger_M"),
        new Skin("NPC-A_M"),

        new Skin("Warrior_F"),
        new Skin("Magician_F"),
        new Skin("Healer_F"),
        new Skin("Ninja_F"),
        new Skin("Ranger_F"),
        new Skin("NPC-A_F"),

        new Skin("Monk_M"),
        new Skin("Berserk_M"),
        new Skin("Dark-Knight_M"),
        new Skin("Soldier_M"),
        new Skin("NPC-B_M"),
        new Skin("NPC-C_M"),

        new Skin("Monk_F"),
        new Skin("Berserk_F"),
        new Skin("Dark-Knight_F"),
        new Skin("Soldier_F"),
        new Skin("NPC-B_F"),
        new Skin("NPC-C_F"),

        new Skin("Fire-Elemental_U"),
        new Skin("Water-Elemental_U"),
        new Skin("Wind-Elemental_U"),
        new Skin("Earth-Elemental_U"),
        new Skin("Light-Elemental_U"),
        new Skin("Dark-Elemental_U"),

        new Skin("Priest_M"),
        new Skin("Nun_F"),
        new Skin("Merchant_M"),
        new Skin("Cultist_U"),
        new Skin("Pirate_M"),
        new Skin("Captain_M"),

        new Skin("Samurai_M"),
        new Skin("Vampire_M"),
        new Skin("Boy_M"),
        new Skin("Old-Man_M"),
        new Skin("Dancer_F"),
        new Skin("King_M"),

        new Skin("Samurai_F"),
        new Skin("Bard_M"),
        new Skin("Girl_F"),
        new Skin("Old-Lady_F"),
        new Skin("Paladin_M"),
        new Skin("Queen_F"),

        new Skin("Template-Adult_U"),
        new Skin("Template-Child_U"),
        new Skin("Angel_M", Type.ANGEL),
        new Skin("Angel_F", Type.ANGEL),
        new Skin("Bunny-Girl_F", Type.BUNNY)
    };
    public enum Type {
        DEFAULT,
        ANGEL,
        BUNNY
    }

    private Skin(String name) {
        this.name = name;
        this.type = Type.DEFAULT;
        this.id = idCount;
        idCount++;
    }

    private Skin(String name, Type t) {
        this.name = name;
        this.type = t;
        this.id = idCount;
        idCount++;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getIndex() {
        return id;
    }

    public static Skin[] getSkins() {
        return skins;
    }

    public static Skin getSkinByName(String name) {
        for (Skin skin : skins) {
            if (skin.getName() == name) {
                return skin;
            }
        }
        return null;
    }

    public int[] getNeutralCoords(int direction) {
        return getCastingCoords(0, direction);
    }

    public int[] getWalkingCoords(int animationPhase, int direction) {
        int row = id % 6;
        int column = id / 6;
        int dy2 = -1;
        switch(type) {
            case DEFAULT:   dy2 = 18; break;
            case ANGEL:     dy2 = 21; break;
            case BUNNY:     dy2 = 20; break;
            default: throw new IllegalArgumentException("Unknown type");
        }
        int dx2 = (type == Type.DEFAULT ? 16 : 20);
        int sx1 = (column * 3 * 16) + (animationPhase * dx2);
        int sy1 = (type == Type.DEFAULT ? (row * 4 * 18) : (2 * 4 * 18) + ((row - 2) * 4 * 21)) + (direction * dy2);
        int sx2 = sx1 + dx2;
        int sy2 = sy1 + dy2;
        return new int[] {dx2, dy2, sx1, sy1, sx2, sy2};
    }

    public int[] getAttackCoords(int animationPhase, int direction) {
        int row = id % 6;
        int column = id / 6;
        int dx2 = (type == Type.ANGEL ? 20 : 18);
        int dy2 = 20;
        int sx1 = (column * 3 * 16) + (animationPhase * dx2);
        int sy1 = (row * 4 * 20) + (direction * dy2);
        if(type == Type.DEFAULT) {
            sx1++;sy1++;dx2--;dy2--;
        }
        int sx2 = sx1 + dx2;
        int sy2 = sy1 + dy2;
        return new int[] {dx2, dy2, sx1, sy1, sx2, sy2};
    }

    public int[] getCastingCoords(int animationPhase, int direction) {
        int row = id % 6;
        int column = id / 6;
        int dx2 = (type == Type.ANGEL ? 20 : 18);
        int dy2 = 20;
        int sx1 = direction == 3 && column < 4  ? (column * 3 * 16) + (4 * dx2) - (animationPhase * dx2)
                                                : (column * 3 * 16) + (animationPhase * dx2);
        int sy1 = (row * 4 * 20) + (direction * dy2);
        if(type == Type.DEFAULT) {
            sx1++;sy1++;dx2--;dy2--;
        }
        int sx2 = sx1 + dx2;
        int sy2 = sy1 + dy2;
        return new int[] {dx2, dy2, sx1, sy1, sx2, sy2};
    }

    public int[] getFaceCoords() {
        int row = id % 6;
        int column = id / 6;
        int dx2 = 48, dy2 = 48;
        int sx1 = (column * 3 * 16);
        int sy1 = (row * 4 * 20);
        int sx2 = sx1 + dx2;
        int sy2 = sy1 + dy2;
        return new int[] {dx2, dy2, sx1, sy1, sx2, sy2};
    }
}
