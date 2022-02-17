package util;

import java.util.function.Function;

public class Spell {
    private int manaCost;
    private int castDelay;
    private Function<Segment, Void> f;

    public Spell(int manaCost, int castDelay, Function<Segment, Void> f) {
        //TODO make more spells / allow the player to make custom functions
        this.manaCost = manaCost;
        this.castDelay = castDelay;
        this.f = f;
    }

    public void cast(Entity e, Point3f direction) {
        if(e.getMana() >= manaCost) {
            e.setMana(e.getMana()-manaCost);
            (new Thread() {
                public void run() {
                    try {
                        sleep(castDelay);
                    } catch (Exception e) {}
                    f.apply(new Segment(e.getCentre(),direction));
                };
            }).start();
        }
    }
}
